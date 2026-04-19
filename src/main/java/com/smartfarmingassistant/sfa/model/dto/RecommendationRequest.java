package com.smartfarmingassistant.sfa.model.dto;

import lombok.Data;

@Data
public class RecommendationRequest {
    private double lat;
    private double lon;
    private String crop;
    private String soilType;
    private String season;
}