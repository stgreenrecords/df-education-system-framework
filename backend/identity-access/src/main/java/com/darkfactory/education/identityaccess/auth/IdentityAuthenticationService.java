package com.darkfactory.education.identityaccess.auth;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class IdentityAuthenticationService {

    private final IdentityUserRepository identityUserRepository;
    private final ActiveTenantProvider activeTenantProvider;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationTokenService authenticationTokenService;

    public IdentityAuthenticationService(
            IdentityUserRepository identityUserRepository,
            ActiveTenantProvider activeTenantProvider,
            PasswordEncoder passwordEncoder,
            AuthenticationTokenService authenticationTokenService
    ) {
        this.identityUserRepository = identityUserRepository;
        this.activeTenantProvider = activeTenantProvider;
        this.passwordEncoder = passwordEncoder;
        this.authenticationTokenService = authenticationTokenService;
    }

    public IssuedAccessToken login(String username, String password) {
        UUID tenantId = activeTenantProvider.getActiveTenantId();
        String normalizedUsername = IdentityUserNormalizer.normalizeUsername(username);
        String rawPassword = IdentityUserNormalizer.normalizePassword(password, "Password");

        IdentityUserRecord user = identityUserRepository.findByTenantAndUsername(tenantId, normalizedUsername)
                .orElseThrow(InvalidCredentialsException::new);

        if (!user.isActive() || !passwordEncoder.matches(rawPassword, user.passwordHash())) {
            throw new InvalidCredentialsException();
        }

        return authenticationTokenService.issueAccessToken(user.toPrincipal());
    }
}

