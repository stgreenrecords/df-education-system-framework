package com.darkfactory.education.platform.configuration;

import java.time.OffsetDateTime;

public record ConfigurationFieldDefinition(
        String fieldKey,
        ConfigurationValueType valueType,
        ConfigurationMergeStrategy mergeStrategy,
        boolean overridesAllowed,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}

