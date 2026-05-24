package com.darkfactory.education.identityaccess.auth;

import java.time.OffsetDateTime;
import java.util.UUID;

public record IdentityUserResponse(
        UUID userId,
        UUID tenantId,
        String username,
        String displayName,
        String authority,
        String status,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
    public static IdentityUserResponse fromRecord(IdentityUserRecord record) {
        return new IdentityUserResponse(
                record.userId(),
                record.tenantId(),
                record.username(),
                record.displayName(),
                record.authority().name(),
                record.status().name(),
                record.createdAt(),
                record.updatedAt()
        );
    }
}

