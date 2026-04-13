package com.smartfarmingassistant.sfa.model.exception;

public class LocationNotFoundException extends RuntimeException {
    public LocationNotFoundException(String name) {
        super("Location not found for query: " + name);
    }
}

