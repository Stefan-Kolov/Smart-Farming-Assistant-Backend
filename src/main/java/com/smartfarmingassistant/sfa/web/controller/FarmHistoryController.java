package com.smartfarmingassistant.sfa.web.controller;

import java.util.List;

import com.smartfarmingassistant.sfa.model.domain.User;
import com.smartfarmingassistant.sfa.model.dto.history.RecommendationDto;
import com.smartfarmingassistant.sfa.model.dto.history.WeatherRecordDto;
import com.smartfarmingassistant.sfa.service.domain.RecommendationHistoryService;
import com.smartfarmingassistant.sfa.service.domain.WeatherRecordHistoryService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/farms/{farmId}")
public class FarmHistoryController {
    private final RecommendationHistoryService recommendationHistoryService;
    private final WeatherRecordHistoryService weatherRecordHistoryService;

    public FarmHistoryController(
            RecommendationHistoryService recommendationHistoryService,
            WeatherRecordHistoryService weatherRecordHistoryService
    ) {
        this.recommendationHistoryService = recommendationHistoryService;
        this.weatherRecordHistoryService = weatherRecordHistoryService;
    }

    @GetMapping("/recommendations")
    public List<RecommendationDto> recommendations(@AuthenticationPrincipal User user, @PathVariable Long farmId) {
        return recommendationHistoryService.listForFarm(user, farmId);
    }

    @GetMapping("/weather-records")
    public List<WeatherRecordDto> weatherRecords(@AuthenticationPrincipal User user, @PathVariable Long farmId) {
        return weatherRecordHistoryService.listForFarm(user, farmId);
    }
}

