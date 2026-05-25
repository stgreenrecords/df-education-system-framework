package com.darkfactory.education.platform.audit;

import com.fasterxml.jackson.databind.JsonNode;

import java.time.OffsetDateTime;
import java.util.UUID;

public record AuditEventEntry(
        UUID id,
        UUID tenantId,
        String entityType,
        String entityId,
        String actionType,
        String actor,
        JsonNode oldValue,
        JsonNode newValue,
        JsonNode metadata,
        OffsetDateTime occurredAt
) {
}

