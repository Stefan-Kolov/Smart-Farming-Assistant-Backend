package com.smartfarmingassistant.sfa.service.domain.impl;

import com.smartfarmingassistant.sfa.model.dto.WeatherData;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RuleBasedServiceTest {

    private final RuleBasedService ruleBasedService = new RuleBasedService();

    @Test
    void recommendation_includesExpectedSegmentsForExtremeHeatAndLowHumidity() {
        WeatherData weather = new WeatherData(36.0, 20.0, 0.0);

        String rec = ruleBasedService.getRecommendation("tomato", weather);

        assertThat(rec).contains("Extreme heat");
        assertThat(rec).contains("No rainfall");
        assertThat(rec).contains("Low humidity");
        assertThat(rec).contains("Tomato:");
    }

    @Test
    void cropTips_areCaseInsensitive() {
        WeatherData weather = new WeatherData(25.0, 50.0, 0.0);

        String recLower = ruleBasedService.getRecommendation("tomato", weather);
        String recUpper = ruleBasedService.getRecommendation("ToMaTo", weather);

        assertThat(recLower).contains("Tomato:");
        assertThat(recUpper).contains("Tomato:");
    }

    @Test
    void nullCrop_throwsNullPointerException_documentedBehavior() {
        WeatherData weather = new WeatherData(25.0, 50.0, 0.0);

        assertThatThrownBy(() -> ruleBasedService.getRecommendation(null, weather))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void blankCrop_doesNotThrow_andReturnsWeatherAdvice() {
        WeatherData weather = new WeatherData(25.0, 50.0, 0.0);

        String rec = ruleBasedService.getRecommendation("", weather);

        assertThat(rec).contains("No rainfall");
    }
}

