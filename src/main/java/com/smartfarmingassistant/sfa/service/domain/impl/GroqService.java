package com.smartfarmingassistant.sfa.service.domain.impl;

import com.smartfarmingassistant.sfa.model.dto.WeatherData;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.*;

@Service
public class GroqService {
    @Value("${groq.api.key}")
    private String groqApiKey;

    @Value("${groq.api.url:https://api.groq.com/openai/v1/chat/completions}")
    private String groqApiUrl;

    @Value("${groq.model:llama-3.1-8b-instant}")
    private String model;

    private final RestTemplate restTemplate = new RestTemplate();

    @SuppressWarnings("unchecked")
    public String getRecommendation(String crop, String soilType, WeatherData weather, String season) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(groqApiKey);

        String prompt = String.format("""
            You are an agricultural expert.
            Give short, practical farming advice for:
            Crop: %s, Soil: %s, Season: %s
            Weather: %.1f°C, %.0f%% humidity, %.1fmm precipitation
            Provide: Irrigation advice, Fertilization advice, Key risks.
            Be concise.""",
                crop, soilType, season,
                weather.getTemperature(), weather.getHumidity(), weather.getPrecipitation());

        Map<String, Object> body = new HashMap<>();
        body.put("model", model);
        body.put("messages", List.of(
                Map.of("role", "system", "content", "You are an expert agronomist."),
                Map.of("role", "user", "content", prompt)
        ));
        body.put("max_tokens", 400);
        body.put("temperature", 0.7);

        Map<String, Object> response = restTemplate.postForObject(
                groqApiUrl, new HttpEntity<>(body, headers), Map.class);

        List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
        Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
        return ((String) message.get("content")).strip();
    }
}
