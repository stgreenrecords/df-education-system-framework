package com.darkfactory.education.platform.configuration;

import java.util.List;

public record ConfigurationInheritanceBreakRequestCreateRequest(
        String fieldKey,
        List<ConfigurationScopeRequest> targetScopePath,
        Object proposedValue,
        String justification,
        String requestedBy
) {
}

