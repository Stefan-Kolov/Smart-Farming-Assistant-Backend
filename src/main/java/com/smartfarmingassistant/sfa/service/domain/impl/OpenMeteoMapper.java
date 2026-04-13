package com.smartfarmingassistant.sfa.service.domain.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import com.fasterxml.jackson.databind.JsonNode;
import com.smartfarmingassistant.sfa.model.dto.weather.CurrentWeatherResponseDto;
import com.smartfarmingassistant.sfa.model.dto.weather.GeocodingResultDto;
import com.smartfarmingassistant.sfa.model.exception.WeatherProviderException;

final class OpenMeteoMapper {
    private OpenMeteoMapper() {
    }

    static List<GeocodingResultDto> toGeocodingResults(JsonNode resultsArray) {
        if (resultsArray == null || !resultsArray.isArray()) {
            return List.of();
        }

        return stream(resultsArray)
                .map(node -> new GeocodingResultDto(
                        text(node, "name"),
                        text(node, "country"),
                        text(node, "country_code"),
                        text(node, "admin1"),
                        text(node, "admin2"),
                        number(node, "latitude"),
                        number(node, "longitude"),
                        text(node, "timezone")
                ))
                .toList();
    }

    static CurrentWeatherResponseDto toCurrentWeatherResponse(JsonNode root, double requestedLat, double requestedLon) {
        JsonNode current = root.get("current");
        JsonNode units = root.get("current_units");

        if (current == null || !current.isObject()) {
            throw new WeatherProviderException("Open-Meteo forecast response missing 'current' object");
        }

        return new CurrentWeatherResponseDto(
                requestedLat,
                requestedLon,
                number(root, "latitude"),
                number(root, "longitude"),
                text(root, "timezone"),
                text(current, "time"),
                integer(current, "interval"),
                number(current, "temperature_2m"),
                number(current, "relative_humidity_2m"),
                number(current, "precipitation"),
                number(current, "precipitation_probability"),
                number(current, "wind_speed_10m"),
                integer(current, "weather_code"),
                integer(current, "is_day"),
                number(current, "et0_fao_evapotranspiration"),
                number(current, "soil_temperature_0cm"),
                number(current, "soil_temperature_6cm"),
                number(current, "soil_moisture_0_to_1cm"),
                number(current, "soil_moisture_1_to_3cm"),
                units != null && units.isObject() ? toUnitsMap(units) : Map.of()
        );
    }

    private static Map<String, String> toUnitsMap(JsonNode units) {
        Map<String, String> out = new HashMap<>();
        putIfNotNull(out, "temperature_2m", text(units, "temperature_2m"));
        putIfNotNull(out, "relative_humidity_2m", text(units, "relative_humidity_2m"));
        putIfNotNull(out, "precipitation", text(units, "precipitation"));
        putIfNotNull(out, "precipitation_probability", text(units, "precipitation_probability"));
        putIfNotNull(out, "wind_speed_10m", text(units, "wind_speed_10m"));
        putIfNotNull(out, "weather_code", text(units, "weather_code"));
        putIfNotNull(out, "is_day", text(units, "is_day"));
        putIfNotNull(out, "et0_fao_evapotranspiration", text(units, "et0_fao_evapotranspiration"));
        putIfNotNull(out, "soil_temperature_0cm", text(units, "soil_temperature_0cm"));
        putIfNotNull(out, "soil_temperature_6cm", text(units, "soil_temperature_6cm"));
        putIfNotNull(out, "soil_moisture_0_to_1cm", text(units, "soil_moisture_0_to_1cm"));
        putIfNotNull(out, "soil_moisture_1_to_3cm", text(units, "soil_moisture_1_to_3cm"));
        return Map.copyOf(out);
    }

    private static Stream<JsonNode> stream(JsonNode array) {
        return StreamSupport.stream(array.spliterator(), false);
    }

    private static void putIfNotNull(Map<String, String> map, String key, String value) {
        if (value != null) {
            map.put(key, value);
        }
    }

    private static String text(JsonNode node, String field) {
        JsonNode v = node.get(field);
        return v != null && !v.isNull() ? v.asText() : null;
    }

    private static Double number(JsonNode node, String field) {
        JsonNode v = node.get(field);
        return v != null && v.isNumber() ? v.asDouble() : null;
    }

    private static Integer integer(JsonNode node, String field) {
        JsonNode v = node.get(field);
        return v != null && v.isNumber() ? v.asInt() : null;
    }
}

