package com.darkfactory.education.identityaccess.auth;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class IdentityUserService {

    private final IdentityUserRepository identityUserRepository;
    private final ActiveTenantProvider activeTenantProvider;
    private final PasswordEncoder passwordEncoder;
    private final IdentityAuditPort identityAuditPort;
    private final IdentityAuthorizationService identityAuthorizationService;

    public IdentityUserService(
            IdentityUserRepository identityUserRepository,
            ActiveTenantProvider activeTenantProvider,
            PasswordEncoder passwordEncoder,
            IdentityAuditPort identityAuditPort,
            IdentityAuthorizationService identityAuthorizationService
    ) {
        this.identityUserRepository = identityUserRepository;
        this.activeTenantProvider = activeTenantProvider;
        this.passwordEncoder = passwordEncoder;
        this.identityAuditPort = identityAuditPort;
        this.identityAuthorizationService = identityAuthorizationService;
    }

    public IdentityUserRecord registerUser(CreateUserRequest request, AuthenticatedUserPrincipal actor) {
        UUID tenantId = activeTenantProvider.getActiveTenantId();
        if (actor.authority() != IdentityUserAuthority.ADMIN
                && !identityAuthorizationService.hasPermission(
                        actor,
                        IdentityPermission.ASSIGN_ROLE,
                        IdentityScopePath.tenant(tenantId)
                )) {
            throw new AuthorizationDeniedException("The current user is not allowed to create identity users.");
        }

        String normalizedUsername = IdentityUserNormalizer.normalizeUsername(request.username());
        String rawPassword = IdentityUserNormalizer.normalizePassword(request.initialPassword(), "Initial password");
        String normalizedDisplayName = IdentityUserNormalizer.normalizeDisplayName(request.displayName());
        IdentityUserStatus status = IdentityUserNormalizer.parseStatus(request.status());

        if (identityUserRepository.findByTenantAndUsername(tenantId, normalizedUsername).isPresent()) {
            throw new DuplicateUsernameException(normalizedUsername);
        }

        IdentityUserRecord created = identityUserRepository.insert(
                tenantId,
                normalizedUsername,
                passwordEncoder.encode(rawPassword),
                status,
                normalizedDisplayName,
                IdentityUserAuthority.USER
        );

        identityAuditPort.recordUserCreated(actor, IdentityUserResponse.fromRecord(created));

        return created;
    }
}

