package com.darkfactory.education.platform.audit;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.OffsetDateTime;
import java.util.UUID;

public record AuditEventResponse(
        UUID id,
        UUID tenantId,
        String entityType,
        String entityId,
        String actionType,
        String actor,
        OffsetDateTime occurredAt,
        Object oldValue,
        Object newValue,
        Object metadata
) {

    private static final ObjectMapper RESPONSE_MAPPER = new ObjectMapper().findAndRegisterModules();

    public static AuditEventResponse from(AuditEventEntry entry) {
        return new AuditEventResponse(
                entry.id(),
                entry.tenantId(),
                entry.entityType(),
                entry.entityId(),
                entry.actionType(),
                entry.actor(),
                entry.occurredAt(),
                RESPONSE_MAPPER.convertValue(entry.oldValue(), new TypeReference<>() { }),
                RESPONSE_MAPPER.convertValue(entry.newValue(), new TypeReference<>() { }),
                RESPONSE_MAPPER.convertValue(entry.metadata(), new TypeReference<>() { })
        );
    }
}

