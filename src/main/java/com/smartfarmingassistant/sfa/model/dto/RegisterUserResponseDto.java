package com.smartfarmingassistant.sfa.model.dto;


import com.smartfarmingassistant.sfa.model.domain.User;
import com.smartfarmingassistant.sfa.model.enums.Role;

public record RegisterUserResponseDto(
        String username,
        String name,
        String surname,
        String email,
        Role role
) {
    public static RegisterUserResponseDto from(User user) {
        return new RegisterUserResponseDto(
                user.getUsername(),
                user.getName(),
                user.getSurname(),
                user.getEmail(),
                user.getRole()
        );
    }
}
