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

    public IdentityUserService(
            IdentityUserRepository identityUserRepository,
            ActiveTenantProvider activeTenantProvider,
            PasswordEncoder passwordEncoder,
            IdentityAuditPort identityAuditPort
    ) {
        this.identityUserRepository = identityUserRepository;
        this.activeTenantProvider = activeTenantProvider;
        this.passwordEncoder = passwordEncoder;
        this.identityAuditPort = identityAuditPort;
    }

    public IdentityUserRecord registerUser(CreateUserRequest request, AuthenticatedUserPrincipal actor) {
        if (actor.authority() != IdentityUserAuthority.ADMIN) {
            throw new IllegalArgumentException("Only ADMIN users may create new users");
        }

        UUID tenantId = activeTenantProvider.getActiveTenantId();
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

