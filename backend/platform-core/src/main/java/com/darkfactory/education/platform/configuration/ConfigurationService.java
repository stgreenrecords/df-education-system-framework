package com.darkfactory.education.platform.configuration;

import com.darkfactory.education.platform.audit.AuditEventWriteCommand;
import com.darkfactory.education.platform.audit.AuditService;
import com.darkfactory.education.platform.tenant.TenantContextService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class ConfigurationService {

    private final ConfigurationFieldDefinitionRepository fieldDefinitionRepository;
    private final ConfigurationValueRepository configurationValueRepository;
    private final ConfigurationInheritanceBreakRequestRepository inheritanceBreakRequestRepository;
    private final TenantContextService tenantContextService;
    private final AuditService auditService;
    private final ObjectMapper objectMapper;

    public ConfigurationService(
            ConfigurationFieldDefinitionRepository fieldDefinitionRepository,
            ConfigurationValueRepository configurationValueRepository,
            ConfigurationInheritanceBreakRequestRepository inheritanceBreakRequestRepository,
            TenantContextService tenantContextService,
            AuditService auditService,
            ObjectMapper objectMapper
    ) {
        this.fieldDefinitionRepository = fieldDefinitionRepository;
        this.configurationValueRepository = configurationValueRepository;
        this.inheritanceBreakRequestRepository = inheritanceBreakRequestRepository;
        this.tenantContextService = tenantContextService;
        this.auditService = auditService;
        this.objectMapper = objectMapper;
    }

    public ConfigurationFieldDefinition upsertFieldDefinition(
            String fieldKey,
            String valueType,
            String mergeStrategy,
            Boolean overridesAllowed
    ) {
        String normalizedFieldKey = normalizeFieldKey(fieldKey);
        ConfigurationValueType normalizedValueType = ConfigurationValueType.from(valueType);
        ConfigurationMergeStrategy normalizedMergeStrategy = ConfigurationMergeStrategy.from(mergeStrategy);
        boolean normalizedOverridesAllowed = requireBoolean(overridesAllowed, "overridesAllowed");

        validateFieldDefinition(normalizedValueType, normalizedMergeStrategy);
        return fieldDefinitionRepository.upsert(
                normalizedFieldKey,
                normalizedValueType,
                normalizedMergeStrategy,
                normalizedOverridesAllowed
        );
    }

    public ConfigurationValueEntry upsertValue(
            String fieldKey,
            ScopePath scopePath,
            Object rawValue,
            Boolean locked
    ) {
        ValidatedConfigurationChange validatedChange = validateAndNormalize(fieldKey, scopePath, rawValue, locked);
        return configurationValueRepository.upsert(
                validatedChange.tenantId(),
                validatedChange.fieldKey(),
                scopePath.targetScope(),
                validatedChange.normalizedValue(),
                validatedChange.locked()
        );
    }

    public ConfigurationValidationResponse validateValue(
            String fieldKey,
            ScopePath scopePath,
            Object rawValue,
            Boolean locked
    ) {
        String normalizedFieldKey = normalizeFieldKey(fieldKey);
        ConfigurationFieldDefinition fieldDefinition = getFieldDefinition(normalizedFieldKey);
        UUID tenantId = tenantContextService.getActiveTenant().tenantId();

        ValidationFailure overrideFailure = validateOverrideAllowed(fieldDefinition, scopePath);
        if (overrideFailure != null) {
            return ConfigurationValidationResponse.invalid(
                    normalizedFieldKey,
                    overrideFailure.code(),
                    overrideFailure.message(),
                    overrideFailure.blockingScopes()
            );
        }

        Map<ConfigurationScope, ConfigurationValueEntry> storedValuesByScope = valuesByScope(
                configurationValueRepository.findByTenantAndFieldKey(tenantId, normalizedFieldKey)
        );
        List<ConfigurationScope> lockedAncestors = findLockedAncestors(scopePath, storedValuesByScope);
        if (!lockedAncestors.isEmpty()) {
            return ConfigurationValidationResponse.invalid(
                    normalizedFieldKey,
                    "BLOCKED_BY_ANCESTOR_LOCK",
                    formatLockedAncestorMessage(normalizedFieldKey, lockedAncestors.get(0)),
                    lockedAncestors
            );
        }

        fieldDefinition.valueType().normalize(objectMapper.valueToTree(rawValue), objectMapper);
        return ConfigurationValidationResponse.valid(normalizedFieldKey);
    }

    public ConfigurationInheritanceBreakRequestRecord submitInheritanceBreakRequest(
            String fieldKey,
            ScopePath targetScopePath,
            Object proposedValue,
            String justification,
            String requestedBy
    ) {
        String normalizedFieldKey = normalizeFieldKey(fieldKey);
        ConfigurationFieldDefinition fieldDefinition = getFieldDefinition(normalizedFieldKey);
        UUID tenantId = tenantContextService.getActiveTenant().tenantId();

        if (targetScopePath.targetScope().scopeType() == ConfigurationScopeType.COUNTRY) {
            throw new IllegalArgumentException("Inheritance-break requests require a lower-than-COUNTRY target scope");
        }

        Map<ConfigurationScope, ConfigurationValueEntry> storedValuesByScope = valuesByScope(
                configurationValueRepository.findByTenantAndFieldKey(tenantId, normalizedFieldKey)
        );
        List<ConfigurationScope> lockedAncestors = findLockedAncestors(targetScopePath, storedValuesByScope);
        if (lockedAncestors.isEmpty()) {
            throw new IllegalArgumentException("No locked ancestor scope exists for the supplied field and target scope path");
        }

        JsonNode normalizedValue = fieldDefinition.valueType().normalize(objectMapper.valueToTree(proposedValue), objectMapper);
        String normalizedJustification = normalizeRequiredText(justification, "justification");
        String normalizedRequestedBy = normalizeRequiredText(requestedBy, "requestedBy");
        ConfigurationScope blockingAncestor = lockedAncestors.get(0);

        ConfigurationInheritanceBreakRequestRecord record = inheritanceBreakRequestRepository.append(
                tenantId,
                normalizedFieldKey,
                targetScopePath.scopes(),
                blockingAncestor,
                normalizedValue,
                normalizedJustification,
                normalizedRequestedBy,
                ConfigurationInheritanceBreakRequestStatus.SUBMITTED
        );

        auditService.recordEvent(new AuditEventWriteCommand(
                "CONFIGURATION_INHERITANCE_BREAK_REQUEST",
                record.requestId().toString(),
                "CREATE",
                normalizedRequestedBy,
                null,
                ConfigurationInheritanceBreakRequestResponse.from(record),
                Map.of(
                        "fieldKey", normalizedFieldKey,
                        "blockingAncestorScopeType", blockingAncestor.scopeType().name(),
                        "blockingAncestorScopeKey", blockingAncestor.scopeKey()
                )
        ));

        return record;
    }

    public ConfigurationCompatibilityReportResponse generateCompatibilityReport(
            String fieldKey,
            ScopePath scopePath,
            Object proposedValue
    ) {
        String normalizedFieldKey = normalizeFieldKey(fieldKey);
        ConfigurationFieldDefinition fieldDefinition = getFieldDefinition(normalizedFieldKey);
        UUID tenantId = tenantContextService.getActiveTenant().tenantId();

        if (scopePath.targetScope().scopeType() != ConfigurationScopeType.COUNTRY) {
            throw new IllegalArgumentException("Compatibility reports currently support COUNTRY/root scope updates only");
        }

        JsonNode normalizedProposedValue = fieldDefinition.valueType().normalize(objectMapper.valueToTree(proposedValue), objectMapper);
        Map<ConfigurationScope, ConfigurationValueEntry> storedValuesByScope = valuesByScope(
                configurationValueRepository.findByTenantAndFieldKey(tenantId, normalizedFieldKey)
        );

        Map<ConfigurationScope, ConfigurationValueEntry> projectedValuesByScope = new LinkedHashMap<>(storedValuesByScope);
        projectedValuesByScope.put(
                scopePath.targetScope(),
                new ConfigurationValueEntry(
                        null,
                        tenantId,
                        normalizedFieldKey,
                        scopePath.targetScope().scopeType(),
                        scopePath.targetScope().scopeKey(),
                        normalizedProposedValue,
                        false,
                        null,
                        null
                )
        );

        List<ConfigurationCompatibilityReportResponse.ConfigurationCompatibilityImpactResponse> impacts = storedValuesByScope.values().stream()
                .filter(entry -> entry.scopeType() == ConfigurationScopeType.INSTITUTION)
                .sorted(Comparator.comparing(ConfigurationValueEntry::scopeKey))
                .map(entry -> buildCompatibilityImpact(fieldDefinition, normalizedFieldKey, scopePath.targetScope(), storedValuesByScope, projectedValuesByScope, entry.scope()))
                .toList();

        return ConfigurationCompatibilityReportResponse.of(
                normalizedFieldKey,
                scopePath.targetScope(),
                normalizedProposedValue,
                impacts
        );
    }

    public ResolvedConfigurationValue resolve(String fieldKey, ScopePath scopePath) {
        String normalizedFieldKey = normalizeFieldKey(fieldKey);
        ConfigurationFieldDefinition fieldDefinition = getFieldDefinition(normalizedFieldKey);
        UUID tenantId = tenantContextService.getActiveTenant().tenantId();
        Map<ConfigurationScope, ConfigurationValueEntry> valuesByScope = valuesByScope(
                configurationValueRepository.findByTenantAndFieldKey(tenantId, normalizedFieldKey)
        );

        return resolve(fieldDefinition, scopePath, valuesByScope);
    }

    private ResolvedConfigurationValue resolve(
            ConfigurationFieldDefinition fieldDefinition,
            ScopePath scopePath,
            Map<ConfigurationScope, ConfigurationValueEntry> valuesByScope
    ) {

        return switch (fieldDefinition.mergeStrategy()) {
            case REPLACE -> resolveReplace(fieldDefinition, scopePath, valuesByScope);
            case EXTEND_SET -> resolveExtendSet(fieldDefinition, scopePath, valuesByScope);
        };
    }

    private ValidatedConfigurationChange validateAndNormalize(
            String fieldKey,
            ScopePath scopePath,
            Object rawValue,
            Boolean locked
    ) {
        String normalizedFieldKey = normalizeFieldKey(fieldKey);
        ConfigurationFieldDefinition fieldDefinition = getFieldDefinition(normalizedFieldKey);
        UUID tenantId = tenantContextService.getActiveTenant().tenantId();
        boolean normalizedLocked = locked != null && locked;

        ValidationFailure overrideFailure = validateOverrideAllowed(fieldDefinition, scopePath);
        if (overrideFailure != null) {
            throw new LockedConfigurationOverrideException(overrideFailure.message());
        }

        Map<ConfigurationScope, ConfigurationValueEntry> storedValuesByScope = valuesByScope(
                configurationValueRepository.findByTenantAndFieldKey(tenantId, normalizedFieldKey)
        );
        validateNoLockedAncestors(scopePath, storedValuesByScope, normalizedFieldKey);
        JsonNode normalizedValue = fieldDefinition.valueType().normalize(objectMapper.valueToTree(rawValue), objectMapper);

        return new ValidatedConfigurationChange(normalizedFieldKey, tenantId, normalizedLocked, normalizedValue);
    }

    private ResolvedConfigurationValue resolveReplace(
            ConfigurationFieldDefinition fieldDefinition,
            ScopePath scopePath,
            Map<ConfigurationScope, ConfigurationValueEntry> valuesByScope
    ) {
        ConfigurationValueEntry winner = null;
        for (ConfigurationScope scope : scopePath.scopes()) {
            ConfigurationValueEntry candidate = valuesByScope.get(scope);
            if (candidate != null) {
                winner = candidate;
            }
        }

        if (winner == null) {
            throw new NoSuchElementException("No configuration value found for field " + fieldDefinition.fieldKey());
        }

        return new ResolvedConfigurationValue(
                fieldDefinition.fieldKey(),
                fieldDefinition.valueType(),
                fieldDefinition.mergeStrategy(),
                winner.value().deepCopy(),
                winner.scope(),
                !winner.scope().equals(scopePath.targetScope()),
                false,
                List.of(winner.scope())
        );
    }

    private ResolvedConfigurationValue resolveExtendSet(
            ConfigurationFieldDefinition fieldDefinition,
            ScopePath scopePath,
            Map<ConfigurationScope, ConfigurationValueEntry> valuesByScope
    ) {
        ArrayNode mergedValue = objectMapper.createArrayNode();
        LinkedHashSet<String> seen = new LinkedHashSet<>();
        List<ConfigurationScope> contributingScopes = new ArrayList<>();
        ConfigurationScope sourceScope = null;

        for (ConfigurationScope scope : scopePath.scopes()) {
            ConfigurationValueEntry entry = valuesByScope.get(scope);
            if (entry == null) {
                continue;
            }

            contributingScopes.add(scope);
            sourceScope = scope;
            for (JsonNode valueNode : entry.value()) {
                String value = valueNode.asText();
                if (seen.add(value)) {
                    mergedValue.add(value);
                }
            }
        }

        if (sourceScope == null) {
            throw new NoSuchElementException("No configuration value found for field " + fieldDefinition.fieldKey());
        }

        return new ResolvedConfigurationValue(
                fieldDefinition.fieldKey(),
                fieldDefinition.valueType(),
                fieldDefinition.mergeStrategy(),
                mergedValue,
                sourceScope,
                !sourceScope.equals(scopePath.targetScope()),
                contributingScopes.size() > 1,
                List.copyOf(contributingScopes)
        );
    }

    private void validateFieldDefinition(ConfigurationValueType valueType, ConfigurationMergeStrategy mergeStrategy) {
        if (mergeStrategy == ConfigurationMergeStrategy.EXTEND_SET && valueType != ConfigurationValueType.STRING_SET) {
            throw new IllegalArgumentException("EXTEND_SET merge strategy requires STRING_SET value type");
        }
    }

    private ValidationFailure validateOverrideAllowed(ConfigurationFieldDefinition fieldDefinition, ScopePath scopePath) {
        if (!fieldDefinition.overridesAllowed() && scopePath.targetScope().scopeType() != ConfigurationScopeType.COUNTRY) {
            return new ValidationFailure(
                    "BLOCKED_BY_FIELD_POLICY",
                    "Field %s does not allow lower-scope overrides".formatted(fieldDefinition.fieldKey()),
                    List.of(ConfigurationScope.country())
            );
        }
        return null;
    }

    private void validateNoLockedAncestors(
            ScopePath scopePath,
            Map<ConfigurationScope, ConfigurationValueEntry> storedValuesByScope,
            String fieldKey
    ) {
        for (ConfigurationScope ancestorScope : scopePath.ancestorScopes()) {
            ConfigurationValueEntry ancestorValue = storedValuesByScope.get(ancestorScope);
            if (ancestorValue != null && ancestorValue.locked()) {
                throw new LockedConfigurationOverrideException(
                        formatLockedAncestorMessage(fieldKey, ancestorScope)
                );
            }
        }
    }

    private List<ConfigurationScope> findLockedAncestors(
            ScopePath scopePath,
            Map<ConfigurationScope, ConfigurationValueEntry> storedValuesByScope
    ) {
        List<ConfigurationScope> lockedAncestors = new ArrayList<>();
        for (ConfigurationScope ancestorScope : scopePath.ancestorScopes()) {
            ConfigurationValueEntry ancestorValue = storedValuesByScope.get(ancestorScope);
            if (ancestorValue != null && ancestorValue.locked()) {
                lockedAncestors.add(ancestorScope);
            }
        }
        return List.copyOf(lockedAncestors);
    }

    private String formatLockedAncestorMessage(String fieldKey, ConfigurationScope ancestorScope) {
        return "Field %s is locked by ancestor scope %s(%s)".formatted(
                fieldKey,
                ancestorScope.scopeType().name(),
                ancestorScope.scopeKey()
        );
    }

    private Map<ConfigurationScope, ConfigurationValueEntry> valuesByScope(List<ConfigurationValueEntry> values) {
        Map<ConfigurationScope, ConfigurationValueEntry> valuesByScope = new LinkedHashMap<>();
        for (ConfigurationValueEntry value : values) {
            valuesByScope.put(value.scope(), value);
        }
        return valuesByScope;
    }

    private ConfigurationFieldDefinition getFieldDefinition(String fieldKey) {
        return fieldDefinitionRepository.findByFieldKey(fieldKey)
                .orElseThrow(() -> new NoSuchElementException("No configuration field definition found for key " + fieldKey));
    }

    private String normalizeFieldKey(String fieldKey) {
        if (fieldKey == null || fieldKey.isBlank()) {
            throw new IllegalArgumentException("Configuration field key must not be blank");
        }
        return fieldKey.trim();
    }

    private String normalizeRequiredText(String value, String fieldName) {
        if (!StringUtils.hasText(value)) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return value.trim();
    }

    private boolean requireBoolean(Boolean value, String fieldName) {
        if (value == null) {
            throw new IllegalArgumentException(fieldName + " must not be null");
        }
        return value;
    }

    private ConfigurationCompatibilityReportResponse.ConfigurationCompatibilityImpactResponse buildCompatibilityImpact(
            ConfigurationFieldDefinition fieldDefinition,
            String fieldKey,
            ConfigurationScope sourceScope,
            Map<ConfigurationScope, ConfigurationValueEntry> currentValuesByScope,
            Map<ConfigurationScope, ConfigurationValueEntry> projectedValuesByScope,
            ConfigurationScope institutionScope
    ) {
        ScopePath institutionScopePath = ScopePath.fromRequests(List.of(
                new ConfigurationScopeRequest(sourceScope.scopeType().name(), sourceScope.scopeKey()),
                new ConfigurationScopeRequest(institutionScope.scopeType().name(), institutionScope.scopeKey())
        ));

        ResolvedConfigurationValue currentResolved = resolve(fieldDefinition, institutionScopePath, currentValuesByScope);
        ResolvedConfigurationValue projectedResolved = resolve(fieldDefinition, institutionScopePath, projectedValuesByScope);

        boolean changed = !currentResolved.effectiveValue().equals(projectedResolved.effectiveValue());
        String impactLevel = changed ? "WARNING" : "INFO";
        String reason = changed
                ? "Proposed ancestor update changes the effective institution configuration"
                : "Institution override remains in effect and should be reviewed against the proposed ancestor update";
        String suggestedAction = changed
                ? "Review the institution override and migration steps before applying the ancestor update"
                : "Review whether the institution override should remain after the ancestor update";

        return ConfigurationCompatibilityReportResponse.ConfigurationCompatibilityImpactResponse.of(
                institutionScope,
                impactLevel,
                reason,
                suggestedAction,
                currentResolved.effectiveValue(),
                projectedResolved.effectiveValue()
        );
    }

    private record ValidatedConfigurationChange(
            String fieldKey,
            UUID tenantId,
            boolean locked,
            JsonNode normalizedValue
    ) {
    }

    private record ValidationFailure(
            String code,
            String message,
            List<ConfigurationScope> blockingScopes
    ) {
    }
}

