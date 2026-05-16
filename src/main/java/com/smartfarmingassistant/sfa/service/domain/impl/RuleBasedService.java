package com.smartfarmingassistant.sfa.service.domain.impl;

import com.smartfarmingassistant.sfa.model.dto.WeatherData;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RuleBasedService {

    /** Crop-specific tips covering common agricultural crops */
    private static final Map<String, String> CROP_TIPS;

    static {
        CROP_TIPS = new HashMap<>();
        // Row crops
        CROP_TIPS.put("tomato",      "Tomatoes: consistent watering prevents blossom-end rot and fruit cracking. Avoid wetting foliage.");
        CROP_TIPS.put("tomatoes",    "Tomatoes: consistent watering prevents blossom-end rot and fruit cracking. Avoid wetting foliage.");
        CROP_TIPS.put("wheat",       "Wheat: irrigation is critical during the heading and grain-fill stages. Monitor for rust disease in humid conditions.");
        CROP_TIPS.put("corn",        "Corn: most critical irrigation period is during silking and tasseling. Nitrogen top-dressing at V6 stage is recommended.");
        CROP_TIPS.put("maize",       "Maize: most critical irrigation period is during silking and tasseling. Nitrogen top-dressing at V6 stage is recommended.");
        CROP_TIPS.put("potato",      "Potatoes: maintain consistent soil moisture to prevent skin cracking and tuber malformation. Avoid overwatering.");
        CROP_TIPS.put("potatoes",    "Potatoes: maintain consistent soil moisture to prevent skin cracking and tuber malformation. Avoid overwatering.");
        CROP_TIPS.put("rice",        "Rice: maintain 5–10 cm standing water during vegetative stage. Drain field 2 weeks before harvest.");
        CROP_TIPS.put("soybean",     "Soybeans: most sensitive to drought at flowering and pod-fill stages. Inoculate with Rhizobium for nitrogen fixation.");
        CROP_TIPS.put("soybeans",    "Soybeans: most sensitive to drought at flowering and pod-fill stages. Inoculate with Rhizobium for nitrogen fixation.");
        CROP_TIPS.put("sunflower",   "Sunflower: deep-rooted and drought-tolerant but benefits from irrigation at flowering. Monitor for sclerotinia head rot.");
        CROP_TIPS.put("sunflowers",  "Sunflower: deep-rooted and drought-tolerant but benefits from irrigation at flowering. Monitor for sclerotinia head rot.");
        CROP_TIPS.put("barley",      "Barley: moderate water requirement; excess moisture increases lodging risk. Apply nitrogen sparingly to avoid over-vegetative growth.");
        CROP_TIPS.put("grape",       "Grapevines: regulated deficit irrigation improves berry quality. Avoid overhead irrigation to reduce fungal disease risk.");
        CROP_TIPS.put("grapes",      "Grapevines: regulated deficit irrigation improves berry quality. Avoid overhead irrigation to reduce fungal disease risk.");
        CROP_TIPS.put("apple",       "Apples: consistent moisture during fruit development reduces russeting. Apply calcium foliar spray to prevent bitter pit.");
        CROP_TIPS.put("apples",      "Apples: consistent moisture during fruit development reduces russeting. Apply calcium foliar spray to prevent bitter pit.");
        CROP_TIPS.put("onion",       "Onions: reduce irrigation 2–3 weeks before harvest to promote bulb ripening and skin quality.");
        CROP_TIPS.put("onions",      "Onions: reduce irrigation 2–3 weeks before harvest to promote bulb ripening and skin quality.");
        CROP_TIPS.put("pepper",      "Peppers: sensitive to both drought and waterlogging. Drip irrigation is preferred. Apply calcium to prevent blossom-end rot.");
        CROP_TIPS.put("peppers",     "Peppers: sensitive to both drought and waterlogging. Drip irrigation is preferred. Apply calcium to prevent blossom-end rot.");
        CROP_TIPS.put("carrot",      "Carrots: deep, even moisture promotes straight root development. Avoid nitrogen excess which causes forked roots.");
        CROP_TIPS.put("carrots",     "Carrots: deep, even moisture promotes straight root development. Avoid nitrogen excess which causes forked roots.");
        CROP_TIPS.put("cabbage",     "Cabbage: consistent watering prevents head splitting. High nitrogen demand during vegetative stage.");
        CROP_TIPS.put("cotton",      "Cotton: drought-tolerant once established but needs water during boll development. Monitor for boll weevil.");
        CROP_TIPS.put("lettuce",     "Lettuce: shallow roots require frequent light irrigation. Bolts rapidly under heat stress — consider shade cloth.");
        CROP_TIPS.put("strawberry",  "Strawberries: drip irrigation keeps fruit dry and reduces botrytis risk. Renovate beds after harvest.");
        CROP_TIPS.put("strawberries","Strawberries: drip irrigation keeps fruit dry and reduces botrytis risk. Renovate beds after harvest.");
        CROP_TIPS.put("cucumber",    "Cucumbers: maintain uniform moisture to prevent bitter fruit. Trellis training improves air circulation.");
        CROP_TIPS.put("cucumbers",   "Cucumbers: maintain uniform moisture to prevent bitter fruit. Trellis training improves air circulation.");
        CROP_TIPS.put("sunflower",   "Sunflowers: drought-tolerant but vulnerable to head rot in wet conditions. Apply fungicide preventively if humidity is high.");
    }

    /**
     * Generates a rule-based recommendation based on weather, crop type, soil type, and season.
     */
    public String getRecommendation(String crop, WeatherData weather, String soilType, String season) {
        List<String> advice = new ArrayList<>();

        // ── Temperature rules ──────────────────────────────────────────────
        if (weather.getTemperature() > 38) {
            advice.add("RISK: Extreme heat alert (" + (int)weather.getTemperature() + "°C) — irrigate early morning and evening; apply mulch to retain soil moisture and reduce stress.");
        } else if (weather.getTemperature() > 32) {
            advice.add("High temperature (" + (int)weather.getTemperature() + "°C) — increase irrigation frequency by 30–50%.");
        } else if (weather.getTemperature() > 27) {
            advice.add("Warm conditions — monitor soil moisture closely and irrigate when the top 5 cm dries out.");
        } else if (weather.getTemperature() < 2) {
            advice.add("RISK: Frost risk (" + (int)weather.getTemperature() + "°C) — cover sensitive crops immediately and avoid irrigation which can worsen frost damage.");
        } else if (weather.getTemperature() < 8) {
            advice.add("Cold conditions (" + (int)weather.getTemperature() + "°C) — reduce irrigation frequency; consider protective covering for frost-sensitive crops.");
        } else {
            advice.add("Optimal temperature range (" + (int)weather.getTemperature() + "°C) — maintain standard crop management practices.");
        }

        // ── Precipitation rules ────────────────────────────────────────────
        if (weather.getPrecipitation() > 20) {
            advice.add("Heavy rainfall (" + (int)weather.getPrecipitation() + " mm) — skip irrigation for 2–3 days; ensure drainage channels are clear to prevent waterlogging.");
        } else if (weather.getPrecipitation() > 10) {
            advice.add("Significant rainfall (" + (int)weather.getPrecipitation() + " mm) — skip irrigation for 1–2 days.");
        } else if (weather.getPrecipitation() > 3) {
            advice.add("Moderate rainfall (" + (int)weather.getPrecipitation() + " mm) — reduce scheduled irrigation by 50%.");
        } else if (weather.getPrecipitation() > 0) {
            advice.add("Light rainfall (" + (int)weather.getPrecipitation() + " mm) — supplement with irrigation as needed based on soil moisture.");
        } else {
            advice.add("No rainfall detected — maintain regular irrigation schedule.");
        }

        // ── Humidity rules ─────────────────────────────────────────────────
        if (weather.getHumidity() > 90) {
            advice.add("RISK: Very high humidity (" + (int)weather.getHumidity() + "%) — high risk of fungal diseases (blight, mildew, botrytis). Apply preventive fungicide and improve airflow.");
        } else if (weather.getHumidity() > 80) {
            advice.add("High humidity (" + (int)weather.getHumidity() + "%) — monitor closely for fungal disease outbreaks; consider fungicide application.");
        } else if (weather.getHumidity() < 25) {
            advice.add("RISK: Very low humidity (" + (int)weather.getHumidity() + "%) — severe drought stress risk; increase irrigation immediately and apply mulch.");
        } else if (weather.getHumidity() < 40) {
            advice.add("Low humidity (" + (int)weather.getHumidity() + "%) — drought stress risk; increase irrigation frequency and check for spider mites.");
        }

        // ── Soil type rules ────────────────────────────────────────────────
        if (soilType != null) {
            switch (soilType.toUpperCase()) {
                case "CLAY":
                    advice.add("Clay soil: avoid overwatering — poor drainage increases waterlogging and root rot risk. Apply organic matter to improve structure.");
                    break;
                case "SANDY":
                    advice.add("Sandy soil: fast-draining — increase irrigation frequency; apply fertilizer in smaller, more frequent doses to prevent nutrient leaching.");
                    break;
                case "LOAMY":
                    advice.add("Loamy soil: ideal water retention and drainage — maintain standard irrigation intervals.");
                    break;
                case "SILTY":
                    advice.add("Silty soil: good water retention but prone to surface crusting — use drip irrigation where possible and avoid heavy machinery when wet.");
                    break;
            }
        }

        // ── Season rules ───────────────────────────────────────────────────
        if (season != null) {
            switch (season.toUpperCase()) {
                case "SPRING":
                    advice.add("Spring: ideal time for planting and top-dressing with balanced NPK fertilizer. Check soil temperature before sowing.");
                    break;
                case "SUMMER":
                    advice.add("Summer: peak evapotranspiration — prioritize irrigation management and scout for pest pressure which increases with heat.");
                    break;
                case "AUTUMN":
                    advice.add("Autumn: reduce nitrogen applications; focus on phosphorus and potassium for root development and winter hardiness.");
                    break;
                case "WINTER":
                    advice.add("Winter: minimize irrigation; apply mulch for insulation and protect crops from frost. Avoid compacting wet soil.");
                    break;
            }
        }

        // ── Crop-specific tip ──────────────────────────────────────────────
        String tip = CROP_TIPS.get(crop != null ? crop.toLowerCase() : "");
        if (tip != null) {
            advice.add(tip);
        }

        return String.join(" | ", advice);
    }

    /** Backwards-compatible overload without soilType / season */
    public String getRecommendation(String crop, WeatherData weather) {
        return getRecommendation(crop, weather, null, null);
    }
}
