package com.darkfactory.education.platform.configuration;

import java.util.Arrays;

public enum ConfigurationMergeStrategy {
    REPLACE,
    EXTEND_SET;

    public static ConfigurationMergeStrategy from(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Configuration merge strategy must not be blank");
        }

        return Arrays.stream(values())
                .filter(candidate -> candidate.name().equalsIgnoreCase(value.trim()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unsupported configuration merge strategy: " + value));
    }
}

