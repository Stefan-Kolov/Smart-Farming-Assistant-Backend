package com.smartfarmingassistant.sfa.model.dto;


import com.smartfarmingassistant.sfa.model.domain.User;

public record RegisterUserRequestDto(
        String name,
        String surname,
        String email,
        String username,
        String password
) {
    public User toUser() {
        return new User(name, surname, email, username, password);
    }
}
