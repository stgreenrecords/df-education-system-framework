package com.darkfactory.education.platform.configuration;

import java.util.List;

public record ConfigurationValidationResponse(
        String fieldKey,
        boolean valid,
        String status,
        String message,
        List<ConfigurationScopeResponse> blockingScopes
) {

    public static ConfigurationValidationResponse valid(String fieldKey) {
        return new ConfigurationValidationResponse(fieldKey, true, "VALID", "Configuration change is valid", List.of());
    }

    public static ConfigurationValidationResponse invalid(String fieldKey, String status, String message, List<ConfigurationScope> blockingScopes) {
        return new ConfigurationValidationResponse(
                fieldKey,
                false,
                status,
                message,
                blockingScopes.stream().map(ConfigurationScopeResponse::from).toList()
        );
    }
}

