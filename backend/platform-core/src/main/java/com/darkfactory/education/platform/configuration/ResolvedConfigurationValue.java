package com.darkfactory.education.platform.configuration;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;

public record ResolvedConfigurationValue(
        String fieldKey,
        ConfigurationValueType valueType,
        ConfigurationMergeStrategy mergeStrategy,
        JsonNode effectiveValue,
        ConfigurationScope sourceScope,
        boolean inherited,
        boolean merged,
        List<ConfigurationScope> contributingScopes
) {
}

