package com.smartfarmingassistant.sfa.model.dto.farm;

import jakarta.validation.constraints.NotBlank;

public record FarmUpdateRequest(
        @NotBlank String name,
        @NotBlank String location
) {}

