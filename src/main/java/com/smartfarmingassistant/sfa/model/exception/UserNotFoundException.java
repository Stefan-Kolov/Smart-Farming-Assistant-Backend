package com.smartfarmingassistant.sfa.model.exception;


public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String username) {
        super("User with username '%s' does not exist.".formatted(username));
    }
}
