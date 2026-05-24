package com.darkfactory.education.identityaccess.auth;

import java.time.OffsetDateTime;
import java.util.UUID;

public record IdentityUserRecord(
        UUID userId,
        UUID tenantId,
        String username,
        String passwordHash,
        IdentityUserStatus status,
        String displayName,
        IdentityUserAuthority authority,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
    public boolean isActive() {
        return status == IdentityUserStatus.ACTIVE;
    }

    public AuthenticatedUserPrincipal toPrincipal() {
        return new AuthenticatedUserPrincipal(userId, tenantId, username, displayName, authority);
    }
}

