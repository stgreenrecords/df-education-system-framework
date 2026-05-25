package com.darkfactory.education.identityaccess.auth;

import org.springframework.stereotype.Service;

@Service
public class AuthenticatedPrincipalRoleService {

    private final IdentityRoleAssignmentRepository identityRoleAssignmentRepository;

    public AuthenticatedPrincipalRoleService(IdentityRoleAssignmentRepository identityRoleAssignmentRepository) {
        this.identityRoleAssignmentRepository = identityRoleAssignmentRepository;
    }

    public AuthenticatedUserPrincipal enrich(AuthenticatedUserPrincipal principal) {
        return principal.withRoleAssignments(
                identityRoleAssignmentRepository.findByUserId(principal.tenantId(), principal.userId())
        );
    }
}

