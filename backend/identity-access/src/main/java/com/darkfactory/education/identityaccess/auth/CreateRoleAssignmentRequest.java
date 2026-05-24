package com.darkfactory.education.identityaccess.auth;

import java.util.List;
import java.util.UUID;

public record CreateRoleAssignmentRequest(
        UUID userId,
        String roleCode,
        List<IdentityScopeNode> scopePath
) {
}

