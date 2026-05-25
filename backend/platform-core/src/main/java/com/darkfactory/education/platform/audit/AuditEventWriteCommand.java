package com.darkfactory.education.platform.audit;

public record AuditEventWriteCommand(
        String entityType,
        String entityId,
        String actionType,
        String actor,
        Object oldValue,
        Object newValue,
        Object metadata
) {
}

