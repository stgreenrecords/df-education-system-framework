package com.darkfactory.education.identityaccess.auth;

import java.util.UUID;
import java.util.List;

public record CurrentUserResponse(
        UUID userId,
        UUID tenantId,
        String username,
        String displayName,
        String authority,
        List<String> roles
) {
    public static CurrentUserResponse fromPrincipal(AuthenticatedUserPrincipal principal) {
        return new CurrentUserResponse(
                principal.userId(),
                principal.tenantId(),
                principal.username(),
                principal.displayName(),
                principal.authority().name(),
                principal.roleAssignments().stream()
                        .map(IdentityRoleAssignmentRecord::roleCode)
                        .map(IdentityRoleCode::apiValue)
                        .distinct()
                        .toList()
        );
    }
}

