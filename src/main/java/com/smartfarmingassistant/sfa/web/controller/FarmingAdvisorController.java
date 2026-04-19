package com.smartfarmingassistant.sfa.web.controller;

import com.smartfarmingassistant.sfa.model.dto.RecommendationRequest;
import com.smartfarmingassistant.sfa.model.dto.RecommendationResponse;
import com.smartfarmingassistant.sfa.service.domain.impl.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/advisor")
@RequiredArgsConstructor
public class FarmingAdvisorController {
    private final RecommendationService recommendationService;

    @PostMapping("/recommend")
    public ResponseEntity<RecommendationResponse> recommend(@RequestBody RecommendationRequest req) {
        return ResponseEntity.ok(
                recommendationService.generate(req.getLat(), req.getLon(), req.getCrop(), req.getSoilType(), req.getSeason())
        );
    }
}
