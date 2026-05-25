package com.darkfactory.education.platform.configuration;

import com.darkfactory.education.platform.tenant.TenantContextService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
    private final TenantContextService tenantContextService;
    private final ObjectMapper objectMapper;

    public ConfigurationService(
            ConfigurationFieldDefinitionRepository fieldDefinitionRepository,
            ConfigurationValueRepository configurationValueRepository,
            TenantContextService tenantContextService,
            ObjectMapper objectMapper
    ) {
        this.fieldDefinitionRepository = fieldDefinitionRepository;
        this.configurationValueRepository = configurationValueRepository;
        this.tenantContextService = tenantContextService;
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
        String normalizedFieldKey = normalizeFieldKey(fieldKey);
        ConfigurationFieldDefinition fieldDefinition = getFieldDefinition(normalizedFieldKey);
        UUID tenantId = tenantContextService.getActiveTenant().tenantId();
        boolean normalizedLocked = locked != null && locked;

        validateOverrideAllowed(fieldDefinition, scopePath);

        Map<ConfigurationScope, ConfigurationValueEntry> storedValuesByScope = valuesByScope(
                configurationValueRepository.findByTenantAndFieldKey(tenantId, normalizedFieldKey)
        );
        validateNoLockedAncestors(scopePath, storedValuesByScope, normalizedFieldKey);

        JsonNode normalizedValue = fieldDefinition.valueType().normalize(objectMapper.valueToTree(rawValue), objectMapper);
        return configurationValueRepository.upsert(
                tenantId,
                normalizedFieldKey,
                scopePath.targetScope(),
                normalizedValue,
                normalizedLocked
        );
    }

    public ResolvedConfigurationValue resolve(String fieldKey, ScopePath scopePath) {
        String normalizedFieldKey = normalizeFieldKey(fieldKey);
        ConfigurationFieldDefinition fieldDefinition = getFieldDefinition(normalizedFieldKey);
        UUID tenantId = tenantContextService.getActiveTenant().tenantId();
        Map<ConfigurationScope, ConfigurationValueEntry> valuesByScope = valuesByScope(
                configurationValueRepository.findByTenantAndFieldKey(tenantId, normalizedFieldKey)
        );

        return switch (fieldDefinition.mergeStrategy()) {
            case REPLACE -> resolveReplace(fieldDefinition, scopePath, valuesByScope);
            case EXTEND_SET -> resolveExtendSet(fieldDefinition, scopePath, valuesByScope);
        };
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

    private void validateOverrideAllowed(ConfigurationFieldDefinition fieldDefinition, ScopePath scopePath) {
        if (!fieldDefinition.overridesAllowed() && scopePath.targetScope().scopeType() != ConfigurationScopeType.COUNTRY) {
            throw new LockedConfigurationOverrideException(
                    "Field %s does not allow lower-scope overrides".formatted(fieldDefinition.fieldKey())
            );
        }
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
                        "Field %s is locked by ancestor scope %s(%s)".formatted(
                                fieldKey,
                                ancestorScope.scopeType().name(),
                                ancestorScope.scopeKey()
                        )
                );
            }
        }
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

    private boolean requireBoolean(Boolean value, String fieldName) {
        if (value == null) {
            throw new IllegalArgumentException(fieldName + " must not be null");
        }
        return value;
    }
}

