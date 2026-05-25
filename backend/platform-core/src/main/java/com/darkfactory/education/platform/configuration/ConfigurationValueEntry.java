package com.darkfactory.education.platform.configuration;

import com.fasterxml.jackson.databind.JsonNode;

import java.time.OffsetDateTime;
import java.util.UUID;

public record ConfigurationValueEntry(
        UUID id,
        UUID tenantId,
        String fieldKey,
        ConfigurationScopeType scopeType,
        String scopeKey,
        JsonNode value,
        boolean locked,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {

    public ConfigurationScope scope() {
        return new ConfigurationScope(scopeType, scopeKey);
    }
}

