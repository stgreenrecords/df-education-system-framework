package com.darkfactory.education.platform.configuration;

public record ConfigurationScopeResponse(String scopeType, String scopeKey) {

    public static ConfigurationScopeResponse from(ConfigurationScope scope) {
        return new ConfigurationScopeResponse(scope.scopeType().name(), scope.scopeKey());
    }
}

