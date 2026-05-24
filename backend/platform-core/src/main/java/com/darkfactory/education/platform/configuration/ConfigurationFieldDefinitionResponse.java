package com.darkfactory.education.platform.configuration;

public record ConfigurationFieldDefinitionResponse(
        String fieldKey,
        String valueType,
        String mergeStrategy,
        boolean overridesAllowed
) {

    public static ConfigurationFieldDefinitionResponse from(ConfigurationFieldDefinition definition) {
        return new ConfigurationFieldDefinitionResponse(
                definition.fieldKey(),
                definition.valueType().name(),
                definition.mergeStrategy().name(),
                definition.overridesAllowed()
        );
    }
}

