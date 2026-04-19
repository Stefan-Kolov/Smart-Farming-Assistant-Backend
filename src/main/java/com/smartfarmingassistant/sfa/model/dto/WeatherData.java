package com.smartfarmingassistant.sfa.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WeatherData {
    private double temperature;
    private double humidity;
    private double precipitation;
}
