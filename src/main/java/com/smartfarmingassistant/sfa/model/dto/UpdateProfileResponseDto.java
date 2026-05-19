package com.smartfarmingassistant.sfa.model.dto;

public record UpdateProfileResponseDto(
        String message,
        RegisterUserResponseDto user,
        String token
) {
    public static UpdateProfileResponseDto from(RegisterUserResponseDto user, String token) {
        return new UpdateProfileResponseDto("Profile updated successfully.", user, token);
    }
}
