package com.darkfactory.education.platform.configuration;

public record ConfigurationScopeRequest(String scopeType, String scopeKey) {

    public ConfigurationScope toDomain() {
        return new ConfigurationScope(ConfigurationScopeType.from(scopeType), scopeKey);
    }
}

