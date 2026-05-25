package com.darkfactory.education.platform.audit;

import com.darkfactory.education.platform.tenant.TenantContextService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class AuditService {

    private final AuditEventRepository auditEventRepository;
    private final TenantContextService tenantContextService;
    private final ObjectMapper objectMapper;

    public AuditService(
            AuditEventRepository auditEventRepository,
            TenantContextService tenantContextService,
            ObjectMapper objectMapper
    ) {
        this.auditEventRepository = auditEventRepository;
        this.tenantContextService = tenantContextService;
        this.objectMapper = objectMapper;
    }

    public AuditEventEntry recordEvent(AuditEventWriteCommand command) {
        UUID tenantId = tenantContextService.getActiveTenant().tenantId();
        return auditEventRepository.append(
                tenantId,
                normalizeStructuredValue(command.entityType(), "entityType", true),
                normalizeStructuredValue(command.entityId(), "entityId", false),
                normalizeStructuredValue(command.actionType(), "actionType", true),
                normalizeStructuredValue(command.actor(), "actor", false),
                toNullableJson(command.oldValue()),
                toNullableJson(command.newValue()),
                toNullableJson(command.metadata())
        );
    }

    public List<AuditEventEntry> findEvents(String entityType, String actor, OffsetDateTime from, OffsetDateTime to) {
        if (from != null && to != null && from.isAfter(to)) {
            throw new IllegalArgumentException("from must be before or equal to to");
        }

        UUID tenantId = tenantContextService.getActiveTenant().tenantId();
        return auditEventRepository.findFiltered(
                tenantId,
                normalizeOptionalFilter(entityType, true),
                normalizeOptionalFilter(actor, false),
                from,
                to
        );
    }

    private JsonNode toNullableJson(Object value) {
        if (value == null) {
            return null;
        }
        return objectMapper.valueToTree(value);
    }

    private String normalizeOptionalFilter(String value, boolean uppercase) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        return normalizeStructuredValue(value, "filter", uppercase);
    }

    private String normalizeStructuredValue(String value, String fieldName, boolean uppercase) {
        if (!StringUtils.hasText(value)) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        String normalized = value.trim();
        return uppercase ? normalized.toUpperCase() : normalized;
    }
}

