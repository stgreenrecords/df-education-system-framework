package com.darkfactory.education.identityaccess.auth;

import org.springframework.util.StringUtils;

public record IdentityScopeNode(
        IdentityScopeType scopeType,
        String scopeKey
) {
    public IdentityScopeNode {
        if (scopeType == null) {
            throw new IllegalArgumentException("Scope type is required.");
        }
        if (!StringUtils.hasText(scopeKey)) {
            throw new IllegalArgumentException("Scope key is required.");
        }

        scopeKey = scopeKey.trim();
        if (scopeKey.contains("/")) {
            throw new IllegalArgumentException("Scope key must not contain '/'.");
        }
    }

    public String toStorageSegment() {
        return scopeType.name() + ":" + scopeKey;
    }
}

