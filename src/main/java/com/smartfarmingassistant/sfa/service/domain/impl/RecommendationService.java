package com.smartfarmingassistant.sfa.service.domain.impl;

import com.smartfarmingassistant.sfa.model.dto.RecommendationResponse;
import com.smartfarmingassistant.sfa.model.dto.WeatherData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final WeatherService weatherService;
    private final GroqService groqService;
    private final RuleBasedService ruleBasedService;

    public RecommendationResponse generate(double lat, double lon, String crop, String soilType, String season) {
        RecommendationResponse result = new RecommendationResponse();

        WeatherData weather;
        try {
            weather = weatherService.getWeather(lat, lon);
            result.setWeather(weather);
        } catch (Exception e) {
            result.setSource("ERROR");
            result.setRecommendation("Weather fetch failed: " + e.getMessage());
            return result;
        }

        try {
            result.setSource("AI");
            result.setRecommendation(groqService.getRecommendation(crop, soilType, weather, season));
        } catch (Exception e) {
            log.warn("[FALLBACK] Groq failed: {}", e.getMessage());
            result.setSource("RULE_BASED");
            result.setRecommendation(ruleBasedService.getRecommendation(crop, weather));
            result.setNote("AI unavailable — fallback recommendation used.");
        }

        return result;
    }
}
