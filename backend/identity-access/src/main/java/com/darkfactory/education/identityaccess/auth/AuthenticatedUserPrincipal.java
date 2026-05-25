package com.darkfactory.education.identityaccess.auth;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.UUID;

public record AuthenticatedUserPrincipal(
        UUID userId,
        UUID tenantId,
        String username,
        String displayName,
        IdentityUserAuthority authority,
        List<IdentityRoleAssignmentRecord> roleAssignments
) {
    public AuthenticatedUserPrincipal(
            UUID userId,
            UUID tenantId,
            String username,
            String displayName,
            IdentityUserAuthority authority
    ) {
        this(userId, tenantId, username, displayName, authority, List.of());
    }

    public AuthenticatedUserPrincipal {
        roleAssignments = List.copyOf(roleAssignments);
    }

    public AuthenticatedUserPrincipal withRoleAssignments(List<IdentityRoleAssignmentRecord> roleAssignments) {
        return new AuthenticatedUserPrincipal(userId, tenantId, username, displayName, authority, roleAssignments);
    }

    public List<? extends GrantedAuthority> grantedAuthorities() {
        LinkedHashSet<String> grantedAuthorities = new LinkedHashSet<>();
        grantedAuthorities.add(authority.name());

        for (IdentityRoleAssignmentRecord assignment : roleAssignments) {
            grantedAuthorities.add(assignment.roleCode().name());
            assignment.roleCode().permissions().forEach(permission -> grantedAuthorities.add(permission.name()));
        }

        return grantedAuthorities.stream()
                .map(SimpleGrantedAuthority::new)
                .toList();
    }
}

