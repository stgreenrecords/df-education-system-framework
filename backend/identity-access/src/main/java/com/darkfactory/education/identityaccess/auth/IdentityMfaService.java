package com.darkfactory.education.identityaccess.auth;

import io.jsonwebtoken.JwtException;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class IdentityMfaService {

    private final AuthenticationTokenService authenticationTokenService;
    private final IdentityMfaFactorRepository identityMfaFactorRepository;
    private final IdentityUserRepository identityUserRepository;
    private final MfaSecretProtectionService mfaSecretProtectionService;
    private final TotpService totpService;
    private final AuthProperties authProperties;
    private final IdentityAuditPort identityAuditPort;

    public IdentityMfaService(
            AuthenticationTokenService authenticationTokenService,
            IdentityMfaFactorRepository identityMfaFactorRepository,
            IdentityUserRepository identityUserRepository,
            MfaSecretProtectionService mfaSecretProtectionService,
            TotpService totpService,
            AuthProperties authProperties,
            IdentityAuditPort identityAuditPort
    ) {
        this.authenticationTokenService = authenticationTokenService;
        this.identityMfaFactorRepository = identityMfaFactorRepository;
        this.identityUserRepository = identityUserRepository;
        this.mfaSecretProtectionService = mfaSecretProtectionService;
        this.totpService = totpService;
        this.authProperties = authProperties;
        this.identityAuditPort = identityAuditPort;
    }

    public MfaEnrollmentResponse enroll(String challengeToken) {
        ParsedMfaChallengeToken challenge = parseChallengeToken(challengeToken, MfaChallengePurpose.ENROLL);
        String secret = totpService.generateSecret();
        IdentityMfaFactorRecord pendingFactor = identityMfaFactorRepository.savePendingTotpFactor(
                challenge.principal().tenantId(),
                challenge.principal().userId(),
                mfaSecretProtectionService.encrypt(secret)
        );

        String issuer = authProperties.requireMfaTotpIssuerLabel();
        String accountLabel = challenge.principal().username();
        String provisioningUri = totpService.provisioningUri(issuer, accountLabel, secret);

        identityAuditPort.recordMfaEnrollmentStarted(challenge.principal(), pendingFactor.factorId());

        return MfaEnrollmentResponse.forTotp(
                pendingFactor.factorId(),
                secret,
                issuer,
                accountLabel,
                provisioningUri
        );
    }

    public LoginResponse activate(String challengeToken, String totpCode) {
        ParsedMfaChallengeToken challenge = parseChallengeToken(challengeToken, MfaChallengePurpose.ENROLL);
        IdentityMfaFactorRecord pendingFactor = identityMfaFactorRepository.findPendingTotpFactor(
                        challenge.principal().tenantId(),
                        challenge.principal().userId()
                )
                .orElseThrow(() -> new InvalidMfaChallengeException("No pending MFA enrollment exists for this challenge."));

        String secret = mfaSecretProtectionService.decrypt(pendingFactor.secretCiphertext());
        if (!totpService.verifyCode(secret, totpCode)) {
            identityAuditPort.recordMfaVerificationFailed(challenge.principal(), challenge.purpose().name());
            throw new InvalidMfaCodeException();
        }

        identityMfaFactorRepository.activateFactor(pendingFactor.factorId());
        identityAuditPort.recordMfaActivated(challenge.principal(), pendingFactor.factorId());
        return LoginResponse.fromIssuedToken(authenticationTokenService.issueAccessToken(challenge.principal()));
    }

    public LoginResponse verify(String challengeToken, String totpCode) {
        ParsedMfaChallengeToken challenge = parseChallengeToken(challengeToken, MfaChallengePurpose.VERIFY);
        IdentityMfaFactorRecord activeFactor = identityMfaFactorRepository.findActiveTotpFactor(
                        challenge.principal().tenantId(),
                        challenge.principal().userId()
                )
                .orElseThrow(() -> new InvalidMfaChallengeException("No active MFA factor exists for this challenge."));

        String secret = mfaSecretProtectionService.decrypt(activeFactor.secretCiphertext());
        if (!totpService.verifyCode(secret, totpCode)) {
            identityAuditPort.recordMfaVerificationFailed(challenge.principal(), challenge.purpose().name());
            throw new InvalidMfaCodeException();
        }

        return LoginResponse.fromIssuedToken(authenticationTokenService.issueAccessToken(challenge.principal()));
    }

    public String currentTotpCodeForUser(String username, UUID tenantId) {
        IdentityUserRecord user = identityUserRepository.findByTenantAndUsername(tenantId, username)
                .orElseThrow(() -> new NoSuchElementException("Identity user not found: " + username));

        IdentityMfaFactorRecord activeFactor = identityMfaFactorRepository.findActiveTotpFactor(tenantId, user.userId())
                .orElseThrow(() -> new NoSuchElementException("No active MFA factor found for user: " + username));

        return totpService.currentCode(mfaSecretProtectionService.decrypt(activeFactor.secretCiphertext()));
    }

    private ParsedMfaChallengeToken parseChallengeToken(String challengeToken, MfaChallengePurpose expectedPurpose) {
        try {
            ParsedMfaChallengeToken parsedToken = authenticationTokenService.parseMfaChallengeToken(challengeToken);
            if (parsedToken.purpose() != expectedPurpose) {
                throw new InvalidMfaChallengeException(
                        "Expected MFA challenge purpose %s but received %s."
                                .formatted(expectedPurpose.name(), parsedToken.purpose().name())
                );
            }
            return parsedToken;
        } catch (JwtException | IllegalArgumentException exception) {
            throw new InvalidMfaChallengeException("Invalid or expired MFA challenge token.", exception);
        }
    }
}

