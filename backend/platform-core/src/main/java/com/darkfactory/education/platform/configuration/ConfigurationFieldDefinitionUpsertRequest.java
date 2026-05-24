package com.darkfactory.education.platform.configuration;

public record ConfigurationFieldDefinitionUpsertRequest(
        String valueType,
        String mergeStrategy,
        Boolean overridesAllowed
) {
}

