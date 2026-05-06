package com.smartfarmingassistant.sfa.model.dto.crop;

import java.time.LocalDate;

import com.smartfarmingassistant.sfa.model.enums.SoilType;

public record CropDto(
        Long id,
        String name,
        LocalDate plantingDate,
        SoilType soilType,
        Long farmId
) {}

