package com.smartfarmingassistant.sfa.model.domain.entity;

import java.time.LocalDate;

import com.smartfarmingassistant.sfa.model.domain.User;
import com.smartfarmingassistant.sfa.model.enums.RiskLevel;
import com.smartfarmingassistant.sfa.model.enums.SoilType;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FarmAggregateTest {

    @Test
    void addAndRemoveCrop_keepsBothSidesInSync() {
        User owner = new User("A", "B", "a@b.com", "user1", "pw");
        Farm farm = new Farm("Farm", "Loc", owner);

        Crop crop = new Crop("Tomato", LocalDate.parse("2026-05-01"), SoilType.CLAY, null);

        farm.addCrop(crop);
        assertThat(farm.getCrops()).containsExactly(crop);
        assertThat(crop.getFarm()).isSameAs(farm);

        farm.removeCrop(crop);
        assertThat(farm.getCrops()).doesNotContain(crop);
        assertThat(crop.getFarm()).isNull();
    }

    @Test
    void addAndRemoveRecommendation_keepsBothSidesInSync() {
        User owner = new User("A", "B", "a2@b.com", "user2", "pw");
        Farm farm = new Farm("Farm", "Loc", owner);

        Recommendation recommendation = new Recommendation(
                "content",
                RiskLevel.LOW,
                20.0,
                60.0,
                0.0,
                null,
                null
        );

        farm.addRecommendation(recommendation);
        assertThat(farm.getRecommendations()).containsExactly(recommendation);
        assertThat(recommendation.getFarm()).isSameAs(farm);

        farm.removeRecommendation(recommendation);
        assertThat(farm.getRecommendations()).doesNotContain(recommendation);
        assertThat(recommendation.getFarm()).isNull();
    }

    @Test
    void addAndRemoveWeatherRecord_keepsBothSidesInSync() {
        User owner = new User("A", "B", "a3@b.com", "user3", "pw");
        Farm farm = new Farm("Farm", "Loc", owner);

        WeatherRecord record = new WeatherRecord(25.0, 50.0, 0.0, null);

        farm.addWeatherRecord(record);
        assertThat(farm.getWeatherRecords()).containsExactly(record);
        assertThat(record.getFarm()).isSameAs(farm);

        farm.removeWeatherRecord(record);
        assertThat(farm.getWeatherRecords()).doesNotContain(record);
        assertThat(record.getFarm()).isNull();
    }
}

