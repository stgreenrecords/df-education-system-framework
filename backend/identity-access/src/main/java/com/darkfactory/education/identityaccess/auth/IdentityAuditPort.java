package com.darkfactory.education.identityaccess.auth;

public interface IdentityAuditPort {
    void recordUserCreated(AuthenticatedUserPrincipal actor, IdentityUserResponse createdUser);

    void recordRoleAssigned(AuthenticatedUserPrincipal actor, IdentityRoleAssignmentResponse createdAssignment);

    void recordMfaEnrollmentStarted(AuthenticatedUserPrincipal actor, java.util.UUID factorId);

    void recordMfaActivated(AuthenticatedUserPrincipal actor, java.util.UUID factorId);

    void recordMfaVerificationFailed(AuthenticatedUserPrincipal actor, String challengePurpose);
}

