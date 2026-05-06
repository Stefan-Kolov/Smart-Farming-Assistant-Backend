package com.smartfarmingassistant.sfa.model.dto.history;

import java.time.LocalDateTime;

public record WeatherRecordDto(
        Long id,
        Double temperature,
        Double humidity,
        Double rainfall,
        LocalDateTime recordedAt,
        Long farmId
) {}

