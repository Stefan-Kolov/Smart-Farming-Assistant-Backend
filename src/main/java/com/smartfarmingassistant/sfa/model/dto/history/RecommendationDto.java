package com.smartfarmingassistant.sfa.model.dto.history;

import java.time.LocalDateTime;

import com.smartfarmingassistant.sfa.model.enums.RiskLevel;

public record RecommendationDto(
        Long id,
        String content,
        RiskLevel riskLevel,
        Double temperature,
        Double humidity,
        Double rainfall,
        LocalDateTime createdAt,
        Long farmId,
        Long cropId
) {}

