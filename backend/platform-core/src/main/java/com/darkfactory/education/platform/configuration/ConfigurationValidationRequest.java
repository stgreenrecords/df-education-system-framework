package com.darkfactory.education.platform.configuration;

import java.util.List;

public record ConfigurationValidationRequest(
        String fieldKey,
        List<ConfigurationScopeRequest> scopePath,
        Object value,
        Boolean locked
) {
}

