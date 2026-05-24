package com.darkfactory.education.platform.configuration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public record ConfigurationValueResponse(
        String fieldKey,
        String scopeType,
        String scopeKey,
        Object value,
        boolean locked
) {

    private static final ObjectMapper RESPONSE_MAPPER = new ObjectMapper().findAndRegisterModules();

    public static ConfigurationValueResponse from(ConfigurationValueEntry entry) {
        return new ConfigurationValueResponse(
                entry.fieldKey(),
                entry.scopeType().name(),
                entry.scopeKey(),
                RESPONSE_MAPPER.convertValue(entry.value(), new TypeReference<>() { }),
                entry.locked()
        );
    }
}

