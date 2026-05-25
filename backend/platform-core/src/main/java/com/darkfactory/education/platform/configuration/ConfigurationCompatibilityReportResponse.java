package com.darkfactory.education.platform.configuration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

public record ConfigurationCompatibilityReportResponse(
        String fieldKey,
        String sourceScopeType,
        String sourceScopeKey,
        Object proposedValue,
        int impactCount,
        List<ConfigurationCompatibilityImpactResponse> impacts
) {

    private static final ObjectMapper RESPONSE_MAPPER = new ObjectMapper().findAndRegisterModules();

    public static ConfigurationCompatibilityReportResponse of(
            String fieldKey,
            ConfigurationScope sourceScope,
            JsonNode proposedValue,
            List<ConfigurationCompatibilityImpactResponse> impacts
    ) {
        return new ConfigurationCompatibilityReportResponse(
                fieldKey,
                sourceScope.scopeType().name(),
                sourceScope.scopeKey(),
                RESPONSE_MAPPER.convertValue(proposedValue, new TypeReference<>() { }),
                impacts.size(),
                impacts
        );
    }

    public record ConfigurationCompatibilityImpactResponse(
            ConfigurationScopeResponse institutionScope,
            String impactLevel,
            String reason,
            String suggestedAction,
            Object currentEffectiveValue,
            Object projectedEffectiveValue
    ) {

        public static ConfigurationCompatibilityImpactResponse of(
                ConfigurationScope institutionScope,
                String impactLevel,
                String reason,
                String suggestedAction,
                JsonNode currentEffectiveValue,
                JsonNode projectedEffectiveValue
        ) {
            return new ConfigurationCompatibilityImpactResponse(
                    ConfigurationScopeResponse.from(institutionScope),
                    impactLevel,
                    reason,
                    suggestedAction,
                    RESPONSE_MAPPER.convertValue(currentEffectiveValue, new TypeReference<>() { }),
                    RESPONSE_MAPPER.convertValue(projectedEffectiveValue, new TypeReference<>() { })
            );
        }
    }
}

