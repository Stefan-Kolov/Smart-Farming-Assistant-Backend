package com.smartfarmingassistant.sfa.service.domain.impl;

import java.util.List;
import java.util.Optional;
import com.fasterxml.jackson.databind.JsonNode;
import com.smartfarmingassistant.sfa.model.dto.weather.CurrentWeatherResponseDto;
import com.smartfarmingassistant.sfa.model.dto.weather.GeocodingResultDto;
import com.smartfarmingassistant.sfa.model.exception.LocationNotFoundException;
import com.smartfarmingassistant.sfa.model.exception.WeatherProviderException;
import com.smartfarmingassistant.sfa.service.domain.WeatherService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

@Service
public class OpenMeteoWeatherService implements WeatherService {
    private static final List<String> CURRENT_VARIABLES = List.of(
            "temperature_2m",
            "relative_humidity_2m",
            "precipitation",
            "precipitation_probability",
            "wind_speed_10m",
            "weather_code",
            "is_day",
            "et0_fao_evapotranspiration",
            "soil_temperature_0cm",
            "soil_temperature_6cm",
            "soil_moisture_0_to_1cm",
            "soil_moisture_1_to_3cm"
    );

    private final RestClient forecastClient;
    private final RestClient geocodingClient;

    public OpenMeteoWeatherService(
            RestClient.Builder restClientBuilder,
            @Value("${openmeteo.forecast.base-url:https://api.open-meteo.com}") String forecastBaseUrl,
            @Value("${openmeteo.geocoding.base-url:https://geocoding-api.open-meteo.com}") String geocodingBaseUrl
    ) {
        this.forecastClient = restClientBuilder.baseUrl(forecastBaseUrl).build();
        this.geocodingClient = restClientBuilder.baseUrl(geocodingBaseUrl).build();
    }

    @Override
    public List<GeocodingResultDto> geocode(String name, Optional<Integer> count, Optional<String> countryCode) {
        try {
            JsonNode root = geocodingClient
                    .get()
                    .uri(uriBuilder -> {
                        uriBuilder.path("/v1/search");
                        uriBuilder.queryParam("name", name);
                        uriBuilder.queryParam("count", count.orElse(10));
                        countryCode.filter(v -> !v.isBlank()).ifPresent(v -> uriBuilder.queryParam("country_code", v));
                        uriBuilder.queryParam("format", "json");
                        return uriBuilder.build();
                    })
                    .retrieve()
                    .body(JsonNode.class);

            if (root == null || root.get("results") == null || !root.get("results").isArray()) {
                return List.of();
            }

            return OpenMeteoMapper.toGeocodingResults(root.get("results"));
        } catch (RestClientResponseException ex) {
            throw toProviderException("Open-Meteo geocoding request failed", ex);
        } catch (Exception ex) {
            throw new WeatherProviderException("Open-Meteo geocoding request failed", ex);
        }
    }

    @Override
    public CurrentWeatherResponseDto currentByCoordinates(double latitude, double longitude) {
        try {
            JsonNode root = forecastClient
                    .get()
                    .uri(uriBuilder -> {
                        uriBuilder.path("/v1/forecast");
                        uriBuilder.queryParam("latitude", latitude);
                        uriBuilder.queryParam("longitude", longitude);
                        uriBuilder.queryParam("timezone", "auto");
                        uriBuilder.queryParam("forecast_days", 1);
                        uriBuilder.queryParam("current", String.join(",", CURRENT_VARIABLES));
                        return uriBuilder.build();
                    })
                    .retrieve()
                    .body(JsonNode.class);

            if (root == null) {
                throw new WeatherProviderException("Open-Meteo forecast response was empty");
            }

            return OpenMeteoMapper.toCurrentWeatherResponse(root, latitude, longitude);
        } catch (RestClientResponseException ex) {
            throw toProviderException("Open-Meteo forecast request failed", ex);
        } catch (Exception ex) {
            throw new WeatherProviderException("Open-Meteo forecast request failed", ex);
        }
    }

    @Override
    public CurrentWeatherResponseDto currentByLocationName(String locationName, Optional<String> countryCode) {
        List<GeocodingResultDto> results = geocode(locationName, Optional.of(1), countryCode);
        if (results.isEmpty()) {
            throw new LocationNotFoundException(locationName);
        }

        GeocodingResultDto first = results.get(0);
        if (first.latitude() == null || first.longitude() == null) {
            throw new WeatherProviderException("Open-Meteo geocoding result missing coordinates for: " + locationName);
        }

        return currentByCoordinates(first.latitude(), first.longitude());
    }

    private static WeatherProviderException toProviderException(String message, RestClientResponseException ex) {
        HttpStatusCode code = ex.getStatusCode();
        return new WeatherProviderException(message + " (HTTP " + code.value() + ")", ex);
    }
}
