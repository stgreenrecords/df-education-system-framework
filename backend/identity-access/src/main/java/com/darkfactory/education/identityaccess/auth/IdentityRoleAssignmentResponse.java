package com.darkfactory.education.identityaccess.auth;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record IdentityRoleAssignmentResponse(
        UUID assignmentId,
        UUID userId,
        String roleCode,
        List<IdentityScopeNode> scopePath,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
    public static IdentityRoleAssignmentResponse fromRecord(IdentityRoleAssignmentRecord record) {
        return new IdentityRoleAssignmentResponse(
                record.assignmentId(),
                record.userId(),
                record.roleCode().apiValue(),
                record.scopePath().nodes(),
                record.createdAt(),
                record.updatedAt()
        );
    }
}

