package com.darkfactory.education.platform.configuration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.TextNode;

import java.util.Arrays;
import java.util.LinkedHashSet;

public enum ConfigurationValueType {
    STRING {
        @Override
        public JsonNode normalize(JsonNode rawValue, ObjectMapper objectMapper) {
            if (rawValue == null || !rawValue.isTextual()) {
                throw new IllegalArgumentException("STRING configuration values must be JSON strings");
            }

            String normalized = rawValue.asText().trim();
            if (normalized.isEmpty()) {
                throw new IllegalArgumentException("STRING configuration values must not be blank");
            }

            return TextNode.valueOf(normalized);
        }
    },
    STRING_SET {
        @Override
        public JsonNode normalize(JsonNode rawValue, ObjectMapper objectMapper) {
            if (rawValue == null || !rawValue.isArray()) {
                throw new IllegalArgumentException("STRING_SET configuration values must be JSON arrays");
            }

            ArrayNode normalized = objectMapper.createArrayNode();
            LinkedHashSet<String> seen = new LinkedHashSet<>();
            for (JsonNode element : rawValue) {
                if (!element.isTextual()) {
                    throw new IllegalArgumentException("STRING_SET configuration values must contain only JSON strings");
                }

                String value = element.asText().trim();
                if (value.isEmpty()) {
                    throw new IllegalArgumentException("STRING_SET configuration values must not contain blank entries");
                }

                if (seen.add(value)) {
                    normalized.add(value);
                }
            }
            return normalized;
        }
    };

    public abstract JsonNode normalize(JsonNode rawValue, ObjectMapper objectMapper);

    public static ConfigurationValueType from(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Configuration value type must not be blank");
        }

        return Arrays.stream(values())
                .filter(candidate -> candidate.name().equalsIgnoreCase(value.trim()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unsupported configuration value type: " + value));
    }
}

