package com.darkfactory.education.platform.configuration;

import java.util.Arrays;

public enum ConfigurationScopeType {
    COUNTRY,
    REGION,
    CITY,
    INSTITUTION,
    UNIT;

    public static ConfigurationScopeType from(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Configuration scope type must not be blank");
        }

        return Arrays.stream(values())
                .filter(candidate -> candidate.name().equalsIgnoreCase(value.trim()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unsupported configuration scope type: " + value));
    }
}

