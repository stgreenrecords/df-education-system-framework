package com.darkfactory.education.platform.configuration;

import java.util.List;

public record ConfigurationResolveRequest(
        String fieldKey,
        List<ConfigurationScopeRequest> scopePath
) {
}

