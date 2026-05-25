package com.darkfactory.education.identityaccess.auth;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class IdentityRoleAssignmentService {

    private final IdentityRoleAssignmentRepository identityRoleAssignmentRepository;
    private final IdentityUserRepository identityUserRepository;
    private final ActiveTenantProvider activeTenantProvider;
    private final IdentityAuditPort identityAuditPort;
    private final IdentityAuthorizationService identityAuthorizationService;

    public IdentityRoleAssignmentService(
            IdentityRoleAssignmentRepository identityRoleAssignmentRepository,
            IdentityUserRepository identityUserRepository,
            ActiveTenantProvider activeTenantProvider,
            IdentityAuditPort identityAuditPort,
            IdentityAuthorizationService identityAuthorizationService
    ) {
        this.identityRoleAssignmentRepository = identityRoleAssignmentRepository;
        this.identityUserRepository = identityUserRepository;
        this.activeTenantProvider = activeTenantProvider;
        this.identityAuditPort = identityAuditPort;
        this.identityAuthorizationService = identityAuthorizationService;
    }

    public IdentityRoleAssignmentRecord assignRole(CreateRoleAssignmentRequest request, AuthenticatedUserPrincipal actor) {
        UUID tenantId = activeTenantProvider.getActiveTenantId();
        IdentityScopePath scopePath = IdentityScopePath.fromNodes(request.scopePath());
        IdentityRoleCode roleCode = IdentityRoleCode.parse(request.roleCode());

        if (!tenantId.equals(scopePath.tenantId())) {
            throw new IllegalArgumentException("Role assignment scope must start with the active tenant root.");
        }

        identityAuthorizationService.requirePermission(
                actor,
                IdentityPermission.ASSIGN_ROLE,
                IdentityScopePath.tenant(tenantId),
                "The current user is not allowed to assign roles in this tenant."
        );

        IdentityUserRecord targetUser = identityUserRepository.findByTenantAndUserId(tenantId, request.userId())
                .orElseThrow(() -> new NoSuchElementException("Identity user not found: " + request.userId()));

        IdentityRoleAssignmentRecord created = identityRoleAssignmentRepository.insert(
                tenantId,
                targetUser.userId(),
                roleCode,
                scopePath
        );

        identityAuditPort.recordRoleAssigned(actor, IdentityRoleAssignmentResponse.fromRecord(created));
        return created;
    }

    public List<IdentityRoleAssignmentRecord> listRoleAssignments(UUID userId, AuthenticatedUserPrincipal actor) {
        UUID tenantId = activeTenantProvider.getActiveTenantId();
        if (!actor.userId().equals(userId)) {
            identityAuthorizationService.requirePermission(
                    actor,
                    IdentityPermission.ASSIGN_ROLE,
                    IdentityScopePath.tenant(tenantId),
                    "The current user is not allowed to view another user's role assignments."
            );
        }

        return identityRoleAssignmentRepository.findByUserId(tenantId, userId);
    }

    public void ensureBootstrapCountryAdmin(IdentityUserRecord bootstrapUser) {
        UUID tenantId = activeTenantProvider.getActiveTenantId();
        IdentityScopePath tenantScope = IdentityScopePath.tenant(tenantId);
        boolean alreadyAssigned = identityRoleAssignmentRepository.findByUserId(tenantId, bootstrapUser.userId()).stream()
                .anyMatch(assignment -> assignment.roleCode() == IdentityRoleCode.COUNTRY_ADMIN
                        && assignment.scopePath().equals(tenantScope));

        if (alreadyAssigned) {
            return;
        }

        identityRoleAssignmentRepository.insert(
                tenantId,
                bootstrapUser.userId(),
                IdentityRoleCode.COUNTRY_ADMIN,
                tenantScope
        );
    }
}

