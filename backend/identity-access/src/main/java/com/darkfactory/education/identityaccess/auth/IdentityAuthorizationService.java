package com.darkfactory.education.identityaccess.auth;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class IdentityAuthorizationService {

    public boolean hasPermission(
            AuthenticatedUserPrincipal principal,
            IdentityPermission permission,
            IdentityScopePath resourceScopePath
    ) {
        if (!principal.tenantId().equals(resourceScopePath.tenantId())) {
            return false;
        }

        return effectiveRoleAssignments(principal).stream()
                .filter(assignment -> assignment.roleCode().permissions().contains(permission))
                .anyMatch(assignment -> assignment.scopePath().isPrefixOf(resourceScopePath));
    }

    public void requirePermission(
            AuthenticatedUserPrincipal principal,
            IdentityPermission permission,
            IdentityScopePath resourceScopePath,
            String denialMessage
    ) {
        if (!hasPermission(principal, permission, resourceScopePath)) {
            throw new AuthorizationDeniedException(denialMessage);
        }
    }

    public List<IdentityRoleAssignmentRecord> effectiveRoleAssignments(AuthenticatedUserPrincipal principal) {
        List<IdentityRoleAssignmentRecord> assignments = new ArrayList<>(principal.roleAssignments());
        boolean hasCountryAdminAtTenantRoot = assignments.stream().anyMatch(assignment ->
                assignment.roleCode() == IdentityRoleCode.COUNTRY_ADMIN
                        && assignment.scopePath().equals(IdentityScopePath.tenant(principal.tenantId()))
        );

        if (!hasCountryAdminAtTenantRoot && principal.authority() == IdentityUserAuthority.ADMIN) {
            assignments.add(new IdentityRoleAssignmentRecord(
                    bootstrapAssignmentId(principal.userId()),
                    principal.tenantId(),
                    principal.userId(),
                    IdentityRoleCode.COUNTRY_ADMIN,
                    IdentityScopePath.tenant(principal.tenantId()),
                    null,
                    null
            ));
        }

        return List.copyOf(assignments);
    }

    private UUID bootstrapAssignmentId(UUID userId) {
        return UUID.nameUUIDFromBytes(("bootstrap-country-admin-" + userId).getBytes());
    }
}

