package com.darkfactory.education.platform.identity;

import com.darkfactory.education.identityaccess.auth.AuthenticatedUserPrincipal;
import com.darkfactory.education.identityaccess.auth.IdentityAuditPort;
import com.darkfactory.education.identityaccess.auth.IdentityRoleAssignmentResponse;
import com.darkfactory.education.identityaccess.auth.IdentityUserResponse;
import com.darkfactory.education.platform.audit.AuditEventWriteCommand;
import com.darkfactory.education.platform.audit.AuditService;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class PlatformIdentityAuditPort implements IdentityAuditPort {

    private final AuditService auditService;

    public PlatformIdentityAuditPort(AuditService auditService) {
        this.auditService = auditService;
    }

    @Override
    public void recordUserCreated(AuthenticatedUserPrincipal actor, IdentityUserResponse createdUser) {
        auditService.recordEvent(new AuditEventWriteCommand(
                "IDENTITY_USER",
                createdUser.userId().toString(),
                "CREATE",
                actor.username(),
                null,
                createdUser,
                auditMetadata(actor)
        ));
    }

    @Override
    public void recordRoleAssigned(AuthenticatedUserPrincipal actor, IdentityRoleAssignmentResponse createdAssignment) {
        auditService.recordEvent(new AuditEventWriteCommand(
                "IDENTITY_ROLE_ASSIGNMENT",
                createdAssignment.assignmentId().toString(),
                "CREATE",
                actor.username(),
                null,
                createdAssignment,
                auditMetadata(actor)
        ));
    }

    private Map<String, Object> auditMetadata(AuthenticatedUserPrincipal actor) {
        Map<String, Object> metadata = new LinkedHashMap<>();
        metadata.put("registrationMode", "ADMIN_CREATED");
        metadata.put("actorUserId", actor.userId());
        metadata.put("actorAuthority", actor.authority().name());
        return metadata;
    }
}

