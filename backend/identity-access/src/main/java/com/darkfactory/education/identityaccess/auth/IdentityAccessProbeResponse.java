package com.darkfactory.education.identityaccess.auth;

import java.util.List;

public record IdentityAccessProbeResponse(
        String actorUsername,
        String permission,
        List<IdentityScopeNode> resourceScopePath,
        List<String> effectiveRoles
) {
    public static IdentityAccessProbeResponse of(
            AuthenticatedUserPrincipal principal,
            IdentityPermission permission,
            IdentityScopePath resourceScopePath
    ) {
        return new IdentityAccessProbeResponse(
                principal.username(),
                permission.name(),
                resourceScopePath.nodes(),
                principal.roleAssignments().stream()
                        .map(IdentityRoleAssignmentRecord::roleCode)
                        .map(IdentityRoleCode::apiValue)
                        .distinct()
                        .toList()
        );
    }
}

