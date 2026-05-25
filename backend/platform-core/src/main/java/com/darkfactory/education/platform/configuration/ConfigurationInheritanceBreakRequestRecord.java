package com.darkfactory.education.platform.configuration;

import com.fasterxml.jackson.databind.JsonNode;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record ConfigurationInheritanceBreakRequestRecord(
        UUID requestId,
        UUID tenantId,
        String fieldKey,
        List<ConfigurationScope> targetScopePath,
        ConfigurationScope blockingAncestorScope,
        JsonNode proposedValue,
        String justification,
        String requestedBy,
        ConfigurationInheritanceBreakRequestStatus status,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}

