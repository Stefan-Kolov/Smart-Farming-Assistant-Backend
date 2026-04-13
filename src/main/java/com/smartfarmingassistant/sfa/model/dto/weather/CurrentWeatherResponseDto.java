package com.smartfarmingassistant.sfa.model.dto.weather;

import java.util.Map;

public record CurrentWeatherResponseDto(
        Double requestedLatitude,
        Double requestedLongitude,
        Double latitude,
        Double longitude,
        String timezone,
        String time,
        Integer intervalSeconds,
        Double temperature2m,
        Double relativeHumidity2m,
        Double precipitation,
        Double precipitationProbability,
        Double windSpeed10m,
        Integer weatherCode,
        Integer isDay,
        Double et0FaoEvapotranspiration,
        Double soilTemperature0cm,
        Double soilTemperature6cm,
        Double soilMoisture0To1cm,
        Double soilMoisture1To3cm,
        Map<String, String> units
) {
}

