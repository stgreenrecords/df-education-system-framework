# Backend Handoff to QA - STORY-082

## Summary

`backend-dev` completed the backend-only Phase 1 administrator MFA increment on top of the accepted authentication foundation from `STORY-080` and RBAC foundation from `STORY-081`.

The implementation adds challenge-based administrator TOTP MFA, protected MFA factor persistence, enrollment/activation/verification endpoints, purpose-bound MFA challenge tokens, protected secret storage, audit convergence, migration `V12`, and expanded backend integration coverage while preserving direct bearer-token login for non-admin users.

## Acceptance criteria mapping

1. **Given an admin account, when logging in, then MFA is required after password**
   - Verified by `EducationSystemApplicationIT#adminCredentialsReturnMfaEnrollmentChallengeBeforeAccessToken`
   - Admin login returns `mfaEnrollmentRequired=true` and an `ENROLL` challenge before any access token is issued.
   - Verified by `EducationSystemApplicationIT#adminWithActiveMfaMustVerifyBeforeAccessTokenIsIssued`
   - After enrollment, admin login returns a `VERIFY` challenge and requires successful TOTP verification before issuing an access token.

2. **Given MFA is configured, when a valid TOTP code is provided, then login succeeds**
   - Verified by `EducationSystemApplicationIT#adminActivationWithValidTotpReturnsBearerAccessToken`
   - Verified by `EducationSystemApplicationIT#adminWithActiveMfaMustVerifyBeforeAccessTokenIsIssued`
   - Valid activation/verification returns a bearer access token only after TOTP proof.

3. **Given MFA is configured, when an invalid code is provided, then login is denied**
   - Verified by `EducationSystemApplicationIT#invalidAdminTotpVerificationIsDenied`
   - Invalid verification returns `401` and no access token.

4. **Given a non-admin account, when logging in, then MFA is optional**
   - Verified by `EducationSystemApplicationIT#nonAdminCredentialsStillReturnBearerAccessTokenDirectly`
   - Non-admin login still returns a bearer access token directly without MFA challenge handling.

## Files changed / created

- `backend/identity-access/src/main/java/com/darkfactory/education/identityaccess/auth/AuthProperties.java`
- `backend/identity-access/src/main/java/com/darkfactory/education/identityaccess/auth/AuthenticationController.java`
- `backend/identity-access/src/main/java/com/darkfactory/education/identityaccess/auth/AuthenticationTokenService.java`
- `backend/identity-access/src/main/java/com/darkfactory/education/identityaccess/auth/Base32Codec.java`
- `backend/identity-access/src/main/java/com/darkfactory/education/identityaccess/auth/IdentityAuditPort.java`
- `backend/identity-access/src/main/java/com/darkfactory/education/identityaccess/auth/IdentityAuthenticationService.java`
- `backend/identity-access/src/main/java/com/darkfactory/education/identityaccess/auth/IdentityMfaController.java`
- `backend/identity-access/src/main/java/com/darkfactory/education/identityaccess/auth/IdentityMfaFactorRecord.java`
- `backend/identity-access/src/main/java/com/darkfactory/education/identityaccess/auth/IdentityMfaFactorRepository.java`
- `backend/identity-access/src/main/java/com/darkfactory/education/identityaccess/auth/IdentityMfaFactorStatus.java`
- `backend/identity-access/src/main/java/com/darkfactory/education/identityaccess/auth/IdentityMfaFactorType.java`
- `backend/identity-access/src/main/java/com/darkfactory/education/identityaccess/auth/IdentityMfaService.java`
- `backend/identity-access/src/main/java/com/darkfactory/education/identityaccess/auth/InvalidMfaChallengeException.java`
- `backend/identity-access/src/main/java/com/darkfactory/education/identityaccess/auth/InvalidMfaCodeException.java`
- `backend/identity-access/src/main/java/com/darkfactory/education/identityaccess/auth/IssuedMfaChallengeToken.java`
- `backend/identity-access/src/main/java/com/darkfactory/education/identityaccess/auth/LoginResponse.java`
- `backend/identity-access/src/main/java/com/darkfactory/education/identityaccess/auth/MfaChallengePurpose.java`
- `backend/identity-access/src/main/java/com/darkfactory/education/identityaccess/auth/MfaChallengeRequest.java`
- `backend/identity-access/src/main/java/com/darkfactory/education/identityaccess/auth/MfaCodeChallengeRequest.java`
- `backend/identity-access/src/main/java/com/darkfactory/education/identityaccess/auth/MfaEnrollmentResponse.java`
- `backend/identity-access/src/main/java/com/darkfactory/education/identityaccess/auth/MfaSecretProtectionService.java`
- `backend/identity-access/src/main/java/com/darkfactory/education/identityaccess/auth/ParsedMfaChallengeToken.java`
- `backend/identity-access/src/main/java/com/darkfactory/education/identityaccess/auth/TotpService.java`
- `backend/platform-core/src/main/java/com/darkfactory/education/platform/identity/PlatformIdentityAuditPort.java`
- `backend/platform-core/src/main/java/com/darkfactory/education/platform/security/SecurityConfiguration.java`
- `backend/platform-core/src/main/resources/application.properties`
- `backend/platform-core/src/main/resources/db/migration/V12__create_identity_mfa_factor_table.sql`
- `backend/platform-core/src/test/java/com/darkfactory/education/platform/EducationSystemApplicationIT.java`

## Tests run

| Check | Command | Result | Notes |
|---|---|---|---|
| IDE error scan | `get_errors` on edited/new MFA Java files plus `EducationSystemApplicationIT.java` and `SecurityConfiguration.java` | PASS | No file-level Java/test errors remained after final cleanup |
| Focused MFA integration verification | `cd "/Users/Viachaslau_Karnaushanka/Downloads/DF Education System Framework" && sh ./mvnw -f backend/pom.xml -pl platform-core -am -Dit.test=EducationSystemApplicationIT verify` | PASS | `EducationSystemApplicationIT` passed `48/48`, including migration `V12`, admin enrollment/activation/verification flows, invalid-code denial, non-admin direct login, auth/RBAC regression scenarios, and `/api-docs` exposure |
| Full backend regression | `cd "/Users/Viachaslau_Karnaushanka/Downloads/DF Education System Framework" && sh ./mvnw -f backend/pom.xml clean verify` | PASS | Backend reactor remained green after the MFA changes |

## QA focus areas

- Confirm `V12` applies cleanly after `V11` and enforces the intended pending/active MFA factor constraints.
- Inspect `AuthenticationTokenService` to confirm access tokens and MFA challenge tokens are purpose-separated and challenge tokens cannot authenticate protected endpoints.
- Inspect `IdentityAuthenticationService` and `IdentityMfaService` to confirm admin-role-derived MFA enforcement, protected secret handling, and enrollment/verification behavior align with the accepted design.
- Re-run the focused integration suite and confirm the administrator MFA scenarios and older auth/RBAC regression scenarios pass together.
- Confirm `/api-docs` exposes the MFA endpoints and that the change remains backend-only and framework-generic.

## Known non-blocking warnings

- Spring Boot still prints the generated development security-password warning during startup.
- SpringDoc enabled-by-default warnings remain visible.
- Mockito/Testcontainers warnings remain visible in test output.
- `sh ./mvnw` was required because the wrapper file is not executable in this macOS workspace.
- These warnings were observed during successful verification and did not affect the story acceptance criteria.

