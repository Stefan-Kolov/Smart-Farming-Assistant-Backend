package com.smartfarmingassistant.sfa.model.dto.weather;

public record GeocodingResultDto(
        String name,
        String country,
        String countryCode,
        String admin1,
        String admin2,
        Double latitude,
        Double longitude,
        String timezone
) {
}

