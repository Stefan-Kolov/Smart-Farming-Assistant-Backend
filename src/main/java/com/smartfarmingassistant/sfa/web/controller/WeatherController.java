package com.smartfarmingassistant.sfa.web.controller;

import java.util.List;
import java.util.Optional;
import com.smartfarmingassistant.sfa.model.dto.weather.CurrentWeatherResponseDto;
import com.smartfarmingassistant.sfa.model.dto.weather.GeocodingResultDto;
import com.smartfarmingassistant.sfa.model.exception.InvalidLocationException;
import com.smartfarmingassistant.sfa.service.domain.WeatherService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/weather")
public class WeatherController {
    private final WeatherService weatherService;

    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @GetMapping("/geocode")
    public List<GeocodingResultDto> geocode(
            @RequestParam String name,
            @RequestParam(required = false) Integer count,
            @RequestParam(required = false) String countryCode
    ) {
        if (name == null || name.isBlank()) {
            throw new InvalidLocationException("Query parameter 'name' is required");
        }
        return weatherService.geocode(name, Optional.ofNullable(count), Optional.ofNullable(countryCode));
    }

    @GetMapping("/current")
    public CurrentWeatherResponseDto current(
            @RequestParam(required = false) Double lat,
            @RequestParam(required = false) Double lon,
            @RequestParam(required = false) String locationName,
            @RequestParam(required = false) String countryCode
    ) {
        boolean hasLat = lat != null;
        boolean hasLon = lon != null;
        boolean hasName = locationName != null && !locationName.isBlank();

        if (hasLat ^ hasLon) {
            throw new InvalidLocationException("Both 'lat' and 'lon' must be provided together");
        }
        if (!hasName && (!hasLat || !hasLon)) {
            throw new InvalidLocationException("Provide either 'lat' and 'lon', or 'locationName'");
        }
        if (hasLat && (lat < -90 || lat > 90)) {
            throw new InvalidLocationException("'lat' must be between -90 and 90");
        }
        if (hasLon && (lon < -180 || lon > 180)) {
            throw new InvalidLocationException("'lon' must be between -180 and 180");
        }

        if (hasLat) {
            return weatherService.currentByCoordinates(lat, lon);
        }
        return weatherService.currentByLocationName(locationName, Optional.ofNullable(countryCode));
    }
}

