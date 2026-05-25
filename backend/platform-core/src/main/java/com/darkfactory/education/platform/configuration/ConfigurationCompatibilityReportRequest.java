package com.darkfactory.education.platform.configuration;

import java.util.List;

public record ConfigurationCompatibilityReportRequest(
        String fieldKey,
        List<ConfigurationScopeRequest> scopePath,
        Object proposedValue
) {
}

