package com.darkfactory.education.platform.configuration;

import java.util.Arrays;

public enum ConfigurationInheritanceBreakRequestStatus {
    SUBMITTED;

    public static ConfigurationInheritanceBreakRequestStatus from(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Inheritance-break request status must not be blank");
        }

        return Arrays.stream(values())
                .filter(candidate -> candidate.name().equalsIgnoreCase(value.trim()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unsupported inheritance-break request status: " + value));
    }
}

