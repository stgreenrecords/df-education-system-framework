package com.darkfactory.education.identityaccess.auth;

import java.time.OffsetDateTime;
import java.util.UUID;

public record IdentityMfaFactorRecord(
        UUID factorId,
        UUID tenantId,
        UUID userId,
        IdentityMfaFactorType factorType,
        IdentityMfaFactorStatus status,
        String secretCiphertext,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt,
        OffsetDateTime activatedAt
) {
}

