package com.darkfactory.education.identityaccess.auth;

import java.time.OffsetDateTime;
import java.util.UUID;

public record IdentityRoleAssignmentRecord(
        UUID assignmentId,
        UUID tenantId,
        UUID userId,
        IdentityRoleCode roleCode,
        IdentityScopePath scopePath,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}

