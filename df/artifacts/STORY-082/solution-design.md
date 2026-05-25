# Solution Design - STORY-082

## Summary

Extend the accepted backend-only identity foundation with administrator-only TOTP MFA, using a challenge-based login flow that preserves the existing non-admin password login path while requiring a second factor before administrator access tokens are issued.

## Context

`STORY-080` established the first backend-only authentication baseline in `backend/identity-access`: tenant-scoped users, secure password hashing, JWT access tokens, and a bootstrap administrator path. `STORY-081` layered generic RBAC on top of that baseline and introduced the administrator role model needed to distinguish privileged accounts from standard users. The MVP and roadmap still list the security baseline as unfinished Phase 1 work, and the original product prompt explicitly requires MFA for administrators.

The current login flow is single-step and password-only for all users. That is sufficient for the early auth foundation but no longer strong enough for administrator access. The next increment must add MFA without rewriting the whole identity model, introducing UI scope, or forcing non-admin users through a more complex flow prematurely.

## Requirements and acceptance criteria

- Require MFA after password for administrator accounts
- Allow login success when a valid TOTP code is supplied for an MFA-configured admin account
- Deny login when an invalid TOTP code is supplied for an MFA-configured admin account
- Preserve optional/no-MFA behavior for non-admin accounts

## Proposed solution

Implement this story as a backend-only change routed to `backend-dev`, centered in `backend/identity-access` with minimal runtime wiring in `backend/platform-core` because that module hosts the executable Spring Boot application and integration test surface.

### 1. Keep the existing password login endpoint, but introduce a challenge-based MFA branch

Preserve `POST /api/v1/identity/auth/login` as the primary entry point. After successful password validation, branch by current effective role and MFA state:

- **Non-admin user**: issue the existing access token directly, preserving the current Phase 1 login contract for standard users.
- **Admin user with active MFA**: do **not** issue a full access token yet. Return a structured response indicating `mfaRequired = true` plus a short-lived signed MFA challenge token or equivalent challenge identifier.
- **Admin user without active MFA**: do **not** issue a full access token. Return a structured response indicating `mfaEnrollmentRequired = true` plus a short-lived signed enrollment challenge token so the user can complete initial TOTP setup before receiving administrator access.

This keeps the login endpoint stable, preserves backward compatibility for non-admin consumers, and enforces MFA for administrator sessions before privileged access is granted.

### 2. Use TOTP as the first and only Phase 1 MFA factor

The first MFA factor should be TOTP (RFC 6238) because it is open, portable, self-hostable, and does not depend on SMS/email providers or country-specific services.

Recommended behavior:

- support one factor type in Phase 1: `TOTP`
- generate a per-user shared secret during enrollment
- expose provisioning details in backend form (secret, issuer/account label, and `otpauth://` URI) suitable for later UI work or manual testing
- verify six-digit TOTP codes with a bounded clock-skew window

### 3. Add a minimal MFA enrollment and activation flow

To satisfy the admin-login requirement safely, backend-only MFA setup must be part of the story.

Recommended endpoints:

- `POST /api/v1/identity/auth/mfa/enroll` — accepts an enrollment challenge token from a successful password step and returns TOTP provisioning details for the current admin user
- `POST /api/v1/identity/auth/mfa/activate` — accepts the enrollment challenge token plus a TOTP code, verifies the code, activates the factor, and issues the normal access token
- `POST /api/v1/identity/auth/mfa/verify` — accepts an MFA challenge token plus a TOTP code for already-enrolled admin users and issues the normal access token on success

Recommended behavior:

- activation is required before an admin receives a full access token if no active MFA factor exists
- challenge tokens must be short-lived, signed, tenant-scoped, and bound to the authenticated user and challenge purpose (`ENROLL` vs `VERIFY`)
- challenge tokens should not grant general API access; they authorize only the MFA endpoints above
- audit significant events such as MFA enrollment started, activated, or failed verification where practical within scope

### 4. Determine MFA requirement from the accepted RBAC admin roles

Reuse the accepted generic RBAC model from `STORY-081` instead of inventing a separate admin flag.

Recommended MFA-enforced roles:

- `COUNTRY_ADMIN`
- `REGION_ADMIN`
- `CITY_ADMIN`
- `INSTITUTION_ADMIN`

The check should load current role assignments at login time using the accepted RBAC services so MFA requirement follows persisted authorization state rather than a duplicated configuration field.

### 5. Persist MFA factor state with protected secrets

Add forward-only persistence for per-user MFA factor state, likely with a Flyway migration in `backend/platform-core/src/main/resources/db/migration/`.

Recommended table shape:

- `factor_id` UUID primary key
- `tenant_id` UUID not null
- `user_id` UUID not null referencing `identity_user`
- `factor_type` varchar not null (`TOTP` for this story)
- `status` varchar not null (`PENDING`, `ACTIVE`, optionally `DISABLED` for future use)
- protected TOTP secret storage field (encrypted ciphertext, not plaintext)
- timestamps for created/updated/activated
- uniqueness constraint limiting one active TOTP factor per user per tenant

Because TOTP verification requires the shared secret, the secret cannot be stored as a one-way hash. The first implementation should therefore store it encrypted at the application layer with an externalized encryption key/property dedicated to MFA secret protection. This is a targeted secret-protection measure for MFA material, not a replacement for the broader encryption-at-rest story.

### 6. Externalized MFA security configuration

Extend auth configuration under `backend/identity-access` with explicit externalized settings, for example:

- MFA challenge TTL
- MFA secret encryption key
- TOTP issuer label
- optional allowed clock skew / code window

These values must remain externalized and must not be committed to source control or baked into portable artifacts.

### 7. Authentication token and controller contract evolution

The controller and response models under `backend/identity-access` should evolve from a single login-success shape to a structured auth response that can represent:

- direct access-token success for non-admin users
- MFA required challenge for admin users with active TOTP
- MFA enrollment required challenge for admin users without active TOTP

Recommended contract characteristics:

- keep error semantics explicit (`401` for invalid credentials / invalid MFA code, `400` for malformed requests)
- make the challenge purpose explicit so clients know whether to call enroll/activate or verify
- keep the final issued access token contract compatible with current protected-route handling

### 8. Audit, OpenAPI, and package placement

Expected placement:

- `backend/identity-access/src/main/java/.../auth/**` for MFA factor records, challenge issuance/verification services, TOTP verification logic, and updated auth controllers/contracts
- minimal incremental Spring Security/runtime support in `backend/platform-core/src/main/java/.../security/**` only if required for challenge endpoint exposure or token parsing changes
- Flyway migration(s) under `backend/platform-core/src/main/resources/db/migration/`
- integration verification primarily in `backend/platform-core/src/test/java/.../EducationSystemApplicationIT.java`

All new auth endpoints and contracts must appear in `/api-docs`.

## Alternatives considered

- **Make MFA mandatory for all users now**: rejected because the backlog explicitly limits the Phase 1 requirement to administrator accounts.
- **Add SMS/email MFA first**: rejected because it introduces provider dependencies, operational cost, and country-specific integration concerns too early.
- **Issue a full admin access token after password and trust the client to finish MFA later**: rejected because it weakens the security boundary and defeats the acceptance criterion that MFA is required after password.
- **Store TOTP secrets in plaintext**: rejected because it would introduce avoidable security debt.
- **Delay MFA until broader encryption or external IdP stories are complete**: rejected because the product backlog explicitly calls out administrator MFA as a Phase 1 security requirement layered on the existing auth foundation.

## Files/components likely affected

- `df/artifacts/STORY-082/task.md`
- `df/artifacts/STORY-082/solution-design.md`
- `df/artifacts/STORY-082/decision-022-phase-1-admin-mfa-foundation.md`
- `df/artifacts/STORY-082/handoffs.md`
- `df/backlog/architecture-direction.md`
- `df/runtime/board.md`
- `df/runtime/backend-dev-board.md`
- `df/runtime/activity-log.md`
- `df/runtime/decisions.md`
- expected backend implementation targets under `backend/identity-access/src/main/java/com/darkfactory/education/identityaccess/auth/**`
- likely incremental security/runtime wiring under `backend/platform-core/src/main/java/com/darkfactory/education/platform/security/**`
- likely Flyway migration(s) under `backend/platform-core/src/main/resources/db/migration/`
- backend integration tests under `backend/platform-core/src/test/java/com/darkfactory/education/platform/EducationSystemApplicationIT.java`

## Data/API contract changes

- add persistence for user MFA factor state and protected TOTP secret storage
- extend the login response contract to represent direct success vs MFA challenge vs MFA-enrollment challenge
- add backend-only MFA enrollment, activation, and verification endpoints
- preserve compatibility of the final issued bearer access token format for protected API access

## Security/privacy considerations

- MFA challenge tokens must be purpose-bound, short-lived, signed, tenant-scoped, and insufficient for general API access
- TOTP secrets must be stored encrypted with an externalized key and excluded from logs/responses except for one-time provisioning payloads
- admin MFA enforcement must derive from generic RBAC roles, not country-specific or user-name-specific branches
- enrollment/activation and other meaningful MFA state changes should be audited through the accepted audit foundation
- non-admin login should remain simpler, but must not gain unintended access to MFA-only admin flows

## Test strategy

`backend-dev` should provide focused backend verification covering:

- migration order and creation of the MFA factor persistence after the accepted identity/RBAC migrations
- non-admin login still succeeds directly without MFA
- admin login returns MFA-required challenge after password when MFA is active
- admin enrollment-required challenge for first-time admin MFA setup
- valid TOTP activation issues an access token
- valid TOTP verification for an active factor issues an access token
- invalid TOTP verification is denied cleanly
- `/api-docs` includes the new MFA endpoints and updated login contract
- regression checks proving the existing auth and RBAC flows continue to work

## Risks and mitigations

- **Risk:** enrollment and verification flows accidentally create a privileged pre-MFA session
  - **Mitigation:** use purpose-bound MFA challenge tokens only; do not issue the standard access token until activation/verification succeeds
- **Risk:** MFA secret handling creates new sensitive-data exposure
  - **Mitigation:** encrypt secrets with an externalized key, avoid logging them, and limit provisioning responses to the enrollment step
- **Risk:** login contract changes break current non-admin consumers
  - **Mitigation:** preserve direct token issuance for non-admin users and keep final access-token semantics unchanged
- **Risk:** admin-role detection drifts from RBAC assignments
  - **Mitigation:** derive MFA requirement from the accepted RBAC role-assignment services instead of duplicating a second admin model

## Rollback plan

- revert the new MFA migration(s), challenge/enrollment/verification endpoints, and TOTP services if the change is rejected before downstream stories depend on them
- restore the simpler password-only admin login path temporarily only if the MFA branch proves unstable during development, and document the regression explicitly
- remove the temporary MFA-specific externalized settings if the implementation is rolled back

## Open questions

- Future stories can add recovery codes, factor reset, additional factor types, or external IdP MFA; this story stops at backend-only TOTP for administrator accounts.
- If implementation reveals that a small shared encryption helper is needed for protected secret storage, keep it generic and scoped to security-sensitive credential material rather than expanding into the full encryption-at-rest story.

## SA decision

Approved for development: Yes — route to `backend-dev` as a backend-only identity/security implementation story.

