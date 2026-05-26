package com.darkfactory.education.identityaccess.auth;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;

@Service
public class IdentityAuthenticationService {

    private static final Set<IdentityRoleCode> MFA_ENFORCED_ADMIN_ROLES = Set.of(
            IdentityRoleCode.COUNTRY_ADMIN,
            IdentityRoleCode.REGION_ADMIN,
            IdentityRoleCode.CITY_ADMIN,
            IdentityRoleCode.INSTITUTION_ADMIN
    );

    private final IdentityUserRepository identityUserRepository;
    private final ActiveTenantProvider activeTenantProvider;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationTokenService authenticationTokenService;
    private final AuthenticatedPrincipalRoleService authenticatedPrincipalRoleService;
    private final IdentityAuthorizationService identityAuthorizationService;
    private final IdentityMfaFactorRepository identityMfaFactorRepository;

    public IdentityAuthenticationService(
            IdentityUserRepository identityUserRepository,
            ActiveTenantProvider activeTenantProvider,
            PasswordEncoder passwordEncoder,
            AuthenticationTokenService authenticationTokenService,
            AuthenticatedPrincipalRoleService authenticatedPrincipalRoleService,
            IdentityAuthorizationService identityAuthorizationService,
            IdentityMfaFactorRepository identityMfaFactorRepository
    ) {
        this.identityUserRepository = identityUserRepository;
        this.activeTenantProvider = activeTenantProvider;
        this.passwordEncoder = passwordEncoder;
        this.authenticationTokenService = authenticationTokenService;
        this.authenticatedPrincipalRoleService = authenticatedPrincipalRoleService;
        this.identityAuthorizationService = identityAuthorizationService;
        this.identityMfaFactorRepository = identityMfaFactorRepository;
    }

    public LoginResponse login(String username, String password) {
        UUID tenantId = activeTenantProvider.getActiveTenantId();
        String normalizedUsername = IdentityUserNormalizer.normalizeUsername(username);
        String rawPassword = IdentityUserNormalizer.normalizePassword(password, "Password");

        IdentityUserRecord user = identityUserRepository.findByTenantAndUsername(tenantId, normalizedUsername)
                .orElseThrow(InvalidCredentialsException::new);

        if (!user.isActive() || !passwordEncoder.matches(rawPassword, user.passwordHash())) {
            throw new InvalidCredentialsException();
        }

        AuthenticatedUserPrincipal principal = authenticatedPrincipalRoleService.enrich(user.toPrincipal());
        if (!requiresAdministratorMfa(principal)) {
            return LoginResponse.fromIssuedToken(authenticationTokenService.issueAccessToken(principal));
        }

        return identityMfaFactorRepository.findActiveTotpFactor(tenantId, user.userId())
                .map(activeFactor -> LoginResponse.mfaRequired(
                        authenticationTokenService.issueMfaChallengeToken(principal, MfaChallengePurpose.VERIFY)
                ))
                .orElseGet(() -> LoginResponse.mfaEnrollmentRequired(
                        authenticationTokenService.issueMfaChallengeToken(principal, MfaChallengePurpose.ENROLL)
                ));
    }

    private boolean requiresAdministratorMfa(AuthenticatedUserPrincipal principal) {
        return identityAuthorizationService.effectiveRoleAssignments(principal).stream()
                .anyMatch(assignment -> MFA_ENFORCED_ADMIN_ROLES.contains(assignment.roleCode()));
    }
}

