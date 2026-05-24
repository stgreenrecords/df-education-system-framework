# Backend Handoff to QA - STORY-080

## Summary

`backend-dev` completed the Phase 1 backend authentication foundation for `STORY-080` by fixing the `identity-access` module boundary, wiring tenant/audit adapters in `platform-core`, adding stateless JWT request authentication for the identity routes, and extending the integration suite to prove login, protected access, admin-created registration, audit recording, migration `V9`, and OpenAPI exposure.

## Acceptance criteria mapping

1. **Given valid credentials, when a user logs in, then a JWT/session token is issued**
   - Verified by `EducationSystemApplicationIT#validCredentialsReturnBearerAccessToken`
   - Endpoint: `POST /api/v1/identity/auth/login`
2. **Given invalid credentials, when login is attempted, then access is denied with appropriate error**
   - Verified by `EducationSystemApplicationIT#invalidCredentialsReturnUnauthorized`
3. **Given an expired token, when an API is called, then a 401 is returned**
   - Verified by `EducationSystemApplicationIT#expiredTokenReturnsUnauthorizedForProtectedEndpoint`
   - Protected endpoint: `GET /api/v1/identity/me`
4. **Given a new user, when registered by admin, then they can log in with provided credentials**
   - Verified by `EducationSystemApplicationIT#adminCanRegisterUserAndThatUserCanLogIn`
   - Endpoints: `POST /api/v1/identity/users`, `POST /api/v1/identity/auth/login`, `GET /api/v1/identity/me`

## Files for QA focus

- `backend/identity-access/src/main/java/com/darkfactory/education/identityaccess/auth/IdentityAuthenticationService.java`
- `backend/identity-access/src/main/java/com/darkfactory/education/identityaccess/auth/IdentityBootstrapService.java`
- `backend/identity-access/src/main/java/com/darkfactory/education/identityaccess/auth/IdentityUserService.java`
- `backend/platform-core/src/main/java/com/darkfactory/education/platform/identity/PlatformActiveTenantProvider.java`
- `backend/platform-core/src/main/java/com/darkfactory/education/platform/identity/PlatformIdentityAuditPort.java`
- `backend/platform-core/src/main/java/com/darkfactory/education/platform/identity/IdentityBootstrapRunner.java`
- `backend/platform-core/src/main/java/com/darkfactory/education/platform/security/ApiAuthenticationEntryPoint.java`
- `backend/platform-core/src/main/java/com/darkfactory/education/platform/security/JwtAuthenticationFilter.java`
- `backend/platform-core/src/main/java/com/darkfactory/education/platform/security/SecurityConfiguration.java`
- `backend/platform-core/src/main/resources/db/migration/V9__create_identity_user_table.sql`
- `backend/platform-core/src/test/java/com/darkfactory/education/platform/EducationSystemApplicationIT.java`
- `df/artifacts/STORY-080/backend/dev-notes.md`

## Commands executed

```powershell
Set-Location "C:\Users\Viach\IdeaProjects\DF Education System Framework"
.\mvnw.cmd -f backend\pom.xml -pl platform-core -am test -DskipITs

Set-Location "C:\Users\Viach\IdeaProjects\DF Education System Framework"
.\mvnw.cmd -f backend\pom.xml -pl platform-core -am "-Dit.test=EducationSystemApplicationIT" verify

Set-Location "C:\Users\Viach\IdeaProjects\DF Education System Framework"
.\mvnw.cmd -f backend\pom.xml clean verify
```

## Results

- Backend compile after boundary fix: PASS
- Focused auth integration verification: PASS (`EducationSystemApplicationIT` 37/37)
- Full backend reactor verification: PASS
- IDE error check on edited files: PASS

## Known risks / caveats

- Spring Boot still emits a default generated-password warning from fallback auto-configuration, but the story validation proves the custom JWT identity routes work correctly. This appears to be non-blocking framework noise rather than a contract failure.
- The security configuration intentionally protects only the new identity endpoints so the story does not silently broaden authorization scope across previously accepted APIs before `STORY-081`.

## Recommended QA checklist

- Rerun the focused `EducationSystemApplicationIT` auth-related integration checks.
- Inspect `V9__create_identity_user_table.sql` for tenant scoping and uniqueness constraints.
- Confirm the `identity-access` module now depends only on its own abstractions (`ActiveTenantProvider`, `IdentityAuditPort`) rather than `platform-core` classes.
- Confirm `/api-docs` includes the new identity endpoints and that `GET /api/v1/identity/me` returns `401` without a valid bearer token.

