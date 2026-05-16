package com.smartfarmingassistant.sfa.service.domain.impl;

import com.smartfarmingassistant.sfa.model.domain.entity.Crop;
import com.smartfarmingassistant.sfa.model.domain.entity.Farm;
import com.smartfarmingassistant.sfa.model.domain.entity.Recommendation;
import com.smartfarmingassistant.sfa.model.dto.RecommendationResponse;
import com.smartfarmingassistant.sfa.model.dto.WeatherData;
import com.smartfarmingassistant.sfa.model.enums.RiskLevel;
import com.smartfarmingassistant.sfa.repository.CropRepository;
import com.smartfarmingassistant.sfa.repository.FarmRepository;
import com.smartfarmingassistant.sfa.repository.RecommendationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final WeatherService weatherService;
    private final GroqService groqService;
    private final RuleBasedService ruleBasedService;
    private final FarmRepository farmRepository;
    private final CropRepository cropRepository;
    private final RecommendationRepository recommendationRepository;
    private final EmailService emailService;

    @Transactional
    public RecommendationResponse generate(double lat, double lon, String crop, String soilType, String season,
                                           Long farmId, Long cropId) {
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
            result.setRecommendation(ruleBasedService.getRecommendation(crop, weather, soilType, season));
            result.setNote("AI unavailable — fallback recommendation used.");
        }

        // Persist and send email alert if farmId was provided
        if (farmId != null) {
            try {
                Farm farm = farmRepository.findById(farmId)
                        .orElseThrow(() -> new RuntimeException("Farm not found: " + farmId));

                Crop cropEntity = null;
                if (cropId != null) {
                    cropEntity = cropRepository.findById(cropId).orElse(null);
                }

                RiskLevel riskLevel = deriveRiskLevel(result.getRecommendation());

                Recommendation entity = new Recommendation(
                        result.getRecommendation(),
                        riskLevel,
                        weather.getTemperature(),
                        weather.getHumidity(),
                        weather.getPrecipitation(),
                        farm,
                        cropEntity
                );
                recommendationRepository.save(entity);
                log.info("Saved recommendation for farmId={} cropId={} riskLevel={}", farmId, cropId, riskLevel);

                // UC-13: Send email alert when a risk is detected
                String detectedRisk = detectSpecificRisk(result.getRecommendation());
                if (detectedRisk != null) {
                    String userEmail   = farm.getUser().getEmail();
                    String userName    = farm.getUser().getName();
                    String farmName    = farm.getName();
                    String cropName    = cropEntity != null ? cropEntity.getName() : null;
                    emailService.sendRiskAlert(userEmail, userName, farmName, cropName, detectedRisk, result.getRecommendation());
                }

            } catch (Exception e) {
                log.error("Failed to persist recommendation: {}", e.getMessage());
            }
        }

        return result;
    }

    /** Backwards-compatible overload for callers without farmId/cropId */
    public RecommendationResponse generate(double lat, double lon, String crop, String soilType, String season) {
        return generate(lat, lon, crop, soilType, season, null, null);
    }

    /**
     * Detects a specific named risk in the recommendation text.
     * Returns a human-readable risk label, or null if no specific risk is found.
     */
    private String detectSpecificRisk(String text) {
        if (text == null) return null;
        String lower = text.toLowerCase();

        if (lower.contains("drought") || lower.contains("water stress") || lower.contains("dry condition")) {
            return "Drought Risk";
        }
        if (lower.contains("disease") || lower.contains("fungal") || lower.contains("blight") || lower.contains("mildew")) {
            return "Disease Risk";
        }
        if (lower.contains("pest") || lower.contains("insect") || lower.contains("infestation") || lower.contains("aphid")) {
            return "Pest Infestation Risk";
        }
        if (lower.contains("flood") || lower.contains("waterlog") || lower.contains("excess water")) {
            return "Flood Risk";
        }
        if (lower.contains("frost") || lower.contains("freeze") || lower.contains("freezing temperature")) {
            return "Frost Risk";
        }
        if (lower.contains("heat stress") || lower.contains("extreme heat") || lower.contains("high temperature stress")) {
            return "Heat Stress Risk";
        }
        // Also send for any HIGH risk level even without a specific keyword
        RiskLevel level = deriveRiskLevel(text);
        if (level == RiskLevel.HIGH) {
            return "High Risk Condition";
        }

        return null; // No significant risk detected — no email needed
    }

    private RiskLevel deriveRiskLevel(String text) {
        if (text == null) return RiskLevel.MEDIUM;
        String lower = text.toLowerCase();
        if (lower.contains("high risk") || lower.contains("severe") || lower.contains("critical")) {
            return RiskLevel.HIGH;
        } else if (lower.contains("low risk") || lower.contains("favorable") || lower.contains("good conditions")) {
            return RiskLevel.LOW;
        }
        return RiskLevel.MEDIUM;
    }
}
