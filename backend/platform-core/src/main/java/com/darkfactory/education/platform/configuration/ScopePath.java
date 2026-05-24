package com.darkfactory.education.platform.configuration;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public final class ScopePath {

    private final List<ConfigurationScope> scopes;

    private ScopePath(List<ConfigurationScope> scopes) {
        this.scopes = List.copyOf(scopes);
    }

    public static ScopePath fromRequests(List<ConfigurationScopeRequest> requests) {
        if (requests == null || requests.isEmpty()) {
            throw new IllegalArgumentException("Configuration scope path must not be empty");
        }

        List<ConfigurationScope> normalized = new ArrayList<>();
        for (ConfigurationScopeRequest request : requests) {
            if (request == null) {
                throw new IllegalArgumentException("Configuration scope path must not contain null entries");
            }
            normalized.add(request.toDomain());
        }

        if (normalized.get(0).scopeType() != ConfigurationScopeType.COUNTRY) {
            normalized.add(0, ConfigurationScope.country());
        }

        validate(normalized);
        return new ScopePath(normalized);
    }

    private static void validate(List<ConfigurationScope> scopes) {
        if (scopes.isEmpty()) {
            throw new IllegalArgumentException("Configuration scope path must not be empty");
        }
        if (scopes.get(0).scopeType() != ConfigurationScopeType.COUNTRY) {
            throw new IllegalArgumentException("Configuration scope path must start at COUNTRY scope");
        }

        Set<ConfigurationScopeType> seenScopeTypes = new LinkedHashSet<>();
        ConfigurationScopeType previous = null;
        for (ConfigurationScope scope : scopes) {
            if (!seenScopeTypes.add(scope.scopeType())) {
                throw new IllegalArgumentException("Configuration scope path must not contain duplicate scope types");
            }
            if (previous != null && scope.scopeType().ordinal() <= previous.ordinal()) {
                throw new IllegalArgumentException("Configuration scope path must be ordered from COUNTRY to the target scope");
            }
            previous = scope.scopeType();
        }
    }

    public List<ConfigurationScope> scopes() {
        return scopes;
    }

    public ConfigurationScope targetScope() {
        return scopes.get(scopes.size() - 1);
    }

    public List<ConfigurationScope> ancestorScopes() {
        if (scopes.size() <= 1) {
            return List.of();
        }
        return scopes.subList(0, scopes.size() - 1);
    }
}

