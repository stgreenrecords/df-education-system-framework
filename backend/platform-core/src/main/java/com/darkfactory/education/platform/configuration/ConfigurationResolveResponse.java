package com.darkfactory.education.platform.configuration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

public record ConfigurationResolveResponse(
        String fieldKey,
        String valueType,
        String mergeStrategy,
        Object effectiveValue,
        String sourceScopeType,
        String sourceScopeKey,
        boolean inherited,
        boolean merged,
        List<ConfigurationScopeResponse> contributingScopes
) {

    private static final ObjectMapper RESPONSE_MAPPER = new ObjectMapper().findAndRegisterModules();

    public static ConfigurationResolveResponse from(ResolvedConfigurationValue resolved) {
        return new ConfigurationResolveResponse(
                resolved.fieldKey(),
                resolved.valueType().name(),
                resolved.mergeStrategy().name(),
                RESPONSE_MAPPER.convertValue(resolved.effectiveValue(), new TypeReference<>() { }),
                resolved.sourceScope().scopeType().name(),
                resolved.sourceScope().scopeKey(),
                resolved.inherited(),
                resolved.merged(),
                resolved.contributingScopes().stream().map(ConfigurationScopeResponse::from).toList()
        );
    }
}

