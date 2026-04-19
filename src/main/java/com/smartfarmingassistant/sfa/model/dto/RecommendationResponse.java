package com.smartfarmingassistant.sfa.model.dto;

import lombok.Data;

@Data
public class RecommendationResponse {
    private String source;
    private String recommendation;
    private WeatherData weather;
    private String note;
}
