package com.darkfactory.education.identityaccess.auth;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.UUID;

@Service
public class IdentityBootstrapService {

    private final AuthProperties authProperties;
    private final IdentityUserRepository identityUserRepository;
    private final ActiveTenantProvider activeTenantProvider;
    private final PasswordEncoder passwordEncoder;
    private final IdentityRoleAssignmentService identityRoleAssignmentService;

    public IdentityBootstrapService(
            AuthProperties authProperties,
            IdentityUserRepository identityUserRepository,
            ActiveTenantProvider activeTenantProvider,
            PasswordEncoder passwordEncoder,
            IdentityRoleAssignmentService identityRoleAssignmentService
    ) {
        this.authProperties = authProperties;
        this.identityUserRepository = identityUserRepository;
        this.activeTenantProvider = activeTenantProvider;
        this.passwordEncoder = passwordEncoder;
        this.identityRoleAssignmentService = identityRoleAssignmentService;
    }

    public void ensureBootstrapAdmin() {
        AuthProperties.BootstrapAdminConfiguration configuration = authProperties.bootstrapAdminConfigurationOrNull();
        if (configuration == null) {
            return;
        }

        UUID tenantId = activeTenantProvider.getActiveTenantId();
        String normalizedUsername = IdentityUserNormalizer.normalizeUsername(configuration.username());
        String rawPassword = IdentityUserNormalizer.normalizePassword(configuration.password(), "Bootstrap admin password");
        String normalizedDisplayName = Objects.requireNonNullElse(
                IdentityUserNormalizer.normalizeDisplayName(configuration.displayName()),
                "Bootstrap Administrator"
        );

        IdentityUserRecord bootstrapAdmin = identityUserRepository.findByTenantAndUsername(tenantId, normalizedUsername)
                .map(existing -> reconcileExistingBootstrapAdmin(existing, rawPassword, normalizedDisplayName))
                .orElseGet(() -> identityUserRepository.insert(
                        tenantId,
                        normalizedUsername,
                        passwordEncoder.encode(rawPassword),
                        IdentityUserStatus.ACTIVE,
                        normalizedDisplayName,
                        IdentityUserAuthority.ADMIN
                ));

        identityRoleAssignmentService.ensureBootstrapCountryAdmin(bootstrapAdmin);
    }

    private IdentityUserRecord reconcileExistingBootstrapAdmin(
            IdentityUserRecord existing,
            String rawPassword,
            String normalizedDisplayName
    ) {
        if (existing.authority() != IdentityUserAuthority.ADMIN) {
            throw new IllegalStateException(
                    "Configured bootstrap admin username already exists without ADMIN authority: " + existing.username()
            );
        }

        boolean passwordMatches = passwordEncoder.matches(rawPassword, existing.passwordHash());
        boolean displayNameMatches = Objects.equals(existing.displayName(), normalizedDisplayName);
        boolean statusMatches = existing.status() == IdentityUserStatus.ACTIVE;

        if (passwordMatches && displayNameMatches && statusMatches) {
            return existing;
        }

        return identityUserRepository.updateBootstrapAdmin(
                existing.userId(),
                passwordEncoder.encode(rawPassword),
                normalizedDisplayName,
                IdentityUserStatus.ACTIVE
        );
    }
}

