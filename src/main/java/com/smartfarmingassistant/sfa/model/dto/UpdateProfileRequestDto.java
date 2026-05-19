package com.smartfarmingassistant.sfa.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record UpdateProfileRequestDto(
        @NotBlank String username,
        @NotBlank String name,
        @NotBlank String surname,
        @NotBlank @Email String email,
        @Pattern(regexp = "^$|(?=.*\\S).{8,100}", message = "password must be blank or between 8 and 100 characters") String password
) {
}
