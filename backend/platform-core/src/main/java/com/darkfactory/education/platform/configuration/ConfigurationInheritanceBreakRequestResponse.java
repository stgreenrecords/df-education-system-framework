package com.darkfactory.education.platform.configuration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record ConfigurationInheritanceBreakRequestResponse(
        UUID requestId,
        String fieldKey,
        List<ConfigurationScopeResponse> targetScopePath,
        String blockingAncestorScopeType,
        String blockingAncestorScopeKey,
        Object proposedValue,
        String justification,
        String requestedBy,
        String status,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {

    private static final ObjectMapper RESPONSE_MAPPER = new ObjectMapper().findAndRegisterModules();

    public static ConfigurationInheritanceBreakRequestResponse from(ConfigurationInheritanceBreakRequestRecord record) {
        return new ConfigurationInheritanceBreakRequestResponse(
                record.requestId(),
                record.fieldKey(),
                record.targetScopePath().stream().map(ConfigurationScopeResponse::from).toList(),
                record.blockingAncestorScope().scopeType().name(),
                record.blockingAncestorScope().scopeKey(),
                RESPONSE_MAPPER.convertValue(record.proposedValue(), new TypeReference<>() { }),
                record.justification(),
                record.requestedBy(),
                record.status().name(),
                record.createdAt(),
                record.updatedAt()
        );
    }
}

