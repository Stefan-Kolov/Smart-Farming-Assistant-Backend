package com.smartfarmingassistant.sfa.service.domain.impl;

import com.smartfarmingassistant.sfa.model.dto.WeatherData;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class RuleBasedService {
    private static final Map<String, String> CROP_TIPS = Map.of(
            "tomato", "Tomato: consistent watering prevents fruit cracking.",
            "wheat",  "Wheat: irrigation critical during heading stage.",
            "corn",   "Corn: most critical irrigation during silking stage.",
            "potato", "Potato: maintain moisture to prevent skin cracking."
    );

    public String getRecommendation(String crop, WeatherData weather) {
        List<String> advice = new ArrayList<>();

        if (weather.getTemperature() > 35)       advice.add("Extreme heat — irrigate early morning and evening.");
        else if (weather.getTemperature() > 28)  advice.add("High temperature — increase irrigation frequency.");
        else if (weather.getTemperature() < 5)   advice.add("Frost risk — consider protective covering.");

        if (weather.getPrecipitation() > 10)     advice.add("Heavy rainfall — skip irrigation for 1-2 days.");
        else if (weather.getPrecipitation() > 3) advice.add("Moderate rainfall — reduce irrigation by 50%.");
        else                                     advice.add("No rainfall — maintain regular irrigation.");

        if (weather.getHumidity() > 85)          advice.add("High humidity — risk of fungal diseases.");
        else if (weather.getHumidity() < 30)     advice.add("Low humidity — risk of drought stress.");

        String tip = CROP_TIPS.get(crop.toLowerCase());
        if (tip != null) advice.add(tip);

        return String.join(" | ", advice);
    }
}
