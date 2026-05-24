package com.darkfactory.education.identityaccess.auth;

public interface IdentityAuditPort {
    void recordUserCreated(AuthenticatedUserPrincipal actor, IdentityUserResponse createdUser);

    void recordRoleAssigned(AuthenticatedUserPrincipal actor, IdentityRoleAssignmentResponse createdAssignment);
}

