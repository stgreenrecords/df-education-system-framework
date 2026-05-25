package com.darkfactory.education.platform.configuration;

public record ConfigurationScope(ConfigurationScopeType scopeType, String scopeKey) {

    public static final String COUNTRY_SCOPE_KEY = "country";

    public ConfigurationScope {
        if (scopeType == null) {
            throw new IllegalArgumentException("Configuration scope type must not be null");
        }
        if (scopeKey == null || scopeKey.isBlank()) {
            throw new IllegalArgumentException("Configuration scope key must not be blank");
        }

        scopeKey = scopeKey.trim();
        if (scopeType == ConfigurationScopeType.COUNTRY && !COUNTRY_SCOPE_KEY.equals(scopeKey)) {
            throw new IllegalArgumentException("COUNTRY scope must use the reserved key 'country'");
        }
    }

    public static ConfigurationScope country() {
        return new ConfigurationScope(ConfigurationScopeType.COUNTRY, COUNTRY_SCOPE_KEY);
    }
}

