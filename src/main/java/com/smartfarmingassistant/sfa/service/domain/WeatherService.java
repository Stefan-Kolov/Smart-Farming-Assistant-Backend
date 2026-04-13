package com.smartfarmingassistant.sfa.service.domain;

import java.util.List;
import java.util.Optional;
import com.smartfarmingassistant.sfa.model.dto.weather.CurrentWeatherResponseDto;
import com.smartfarmingassistant.sfa.model.dto.weather.GeocodingResultDto;

public interface WeatherService {
    List<GeocodingResultDto> geocode(String name, Optional<Integer> count, Optional<String> countryCode);

    CurrentWeatherResponseDto currentByCoordinates(double latitude, double longitude);

    CurrentWeatherResponseDto currentByLocationName(String locationName, Optional<String> countryCode);
}

