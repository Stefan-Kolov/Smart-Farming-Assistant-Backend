package com.smartfarmingassistant.sfa.model.dto.crop;

import java.time.LocalDate;

import com.smartfarmingassistant.sfa.model.enums.SoilType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CropUpdateRequest(
        @NotBlank String name,
        @NotNull LocalDate plantingDate,
        @NotNull SoilType soilType
) {}

