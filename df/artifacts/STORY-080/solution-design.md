# Solution Design - STORY-080

## Summary

Add the first backend-only authentication foundation in `backend/identity-access` using tenant-scoped local user accounts, admin-created registration, password hashing, and signed bearer-token authentication so protected APIs can require authenticated access before the later RBAC and MFA stories build on top.

## Context

The Phase 1 roadmap and MVP definition explicitly require a security baseline and user/role model. The repository already contains the modular backend scaffold from `STORY-010`, PostgreSQL/Flyway persistence from `STORY-011`, OpenAPI exposure from `STORY-012`, deployment-tenant context from `STORY-021`, and a generic audit foundation from `STORY-013`. The next missing dependency root is authentication itself: without it, later RBAC (`STORY-081`), MFA (`STORY-082`), translation-management authorization (`STORY-222`), and any user-bound domain workflows have no real identity foundation.

The first deliverable should stay intentionally small and backend-only: deployment-local credentials, password validation, token issuance and expiry checks, an admin-only user-registration path, and integration tests proving the contract.

## Requirements and acceptance criteria

- Issue a JWT/session token for valid credentials
- Reject invalid login attempts with appropriate access-denied behavior
- Return `401` for API access using expired authentication tokens
- Allow an administrator to register a new user who can then log in successfully

## Proposed solution

Implement the first identity foundation mainly in `backend/identity-access`, with the application-security wiring living in `backend/platform-core` because that module hosts the Spring Boot runtime.

### 1. Tenant-scoped local user persistence

Add an identity persistence model backed by PostgreSQL and Flyway. The first version should include at least:

- `identity_user`
  - `user_id` UUID primary key
  - `tenant_id` UUID not null referencing the deployment tenant from `STORY-021`
  - `username` varchar not null
  - `password_hash` varchar not null
  - `status` varchar not null (`ACTIVE`, `DISABLED`)
  - `display_name` varchar null
  - `created_at` timestamptz not null
  - `updated_at` timestamptz not null
- a uniqueness constraint on `(tenant_id, username)`

A minimal authority field or companion table may be added only as needed to distinguish the bootstrap/admin registration actor from normal authenticated users. Full hierarchical RBAC tables and permissions remain deferred to `STORY-081`.

### 2. Bootstrap administrator

Because the story requires “registered by admin” while no prior user-management UI or provisioning flow exists, initialize one deployment-local bootstrap administrator from externalized application properties when the application starts. The bootstrap path must be idempotent and must not hardcode secrets in the repository.

Recommended behavior:

- properties such as bootstrap username/password/display name live in environment-driven configuration
- startup creates the bootstrap admin only when no matching account exists for the active deployment tenant
- the bootstrap password is stored only as a password hash after initialization

### 3. Password hashing and credential validation

Use Spring Security password hashing (for example `BCryptPasswordEncoder`) for stored local credentials. Plain-text passwords must never be persisted or logged.

The login flow should:

- load the tenant-scoped user by username
- verify the account is active
- validate the submitted password against the stored hash
- issue an authentication token only on success
- return a clear unauthorized response on invalid credentials without exposing whether the username or password was wrong

### 4. Signed bearer-token authentication

Use a signed bearer token contract for the first implementation so later frontend and API consumers have a stateless, portable auth mechanism. A JWT access token is the smallest viable choice because it satisfies the story wording (`JWT/session token`) while avoiding a server-side session store in the first implementation.

Recommended contract:

- `POST /api/v1/identity/auth/login` returns `{ accessToken, tokenType, expiresAt }`
- JWT contains deployment-tenant-aware subject/username and minimal authority claims
- signing secret and token TTL are externalized configuration
- expired or invalid tokens must be rejected by Spring Security with `401`

### 5. Admin-only user registration endpoint

Expose a minimal backend endpoint for administrator-created accounts, such as:

- `POST /api/v1/identity/users`

The endpoint should:

- require an authenticated bootstrap/admin principal
- accept the minimum registration fields needed for this story (username, initial password, display name, optional initial account status)
- create the user within the active deployment tenant
- reject duplicate usernames within the same tenant
- record the creation through the shared audit foundation from `STORY-013`

Do not expand this story into self-service registration, invitations, email verification, password reset, or final role assignment UX.

### 6. Minimal protected test endpoint or existing protected route

To prove the expired-token `401` behavior and real request authentication, protect at least one backend endpoint with the new authentication filter chain. The preferred minimal path is a small identity-local endpoint such as `GET /api/v1/identity/me` or another auth-protected route that returns the authenticated principal summary.

### 7. Module and package placement

Expected code placement:

- `backend/identity-access/src/main/java/.../auth/**` for token service, authentication controller, user services, repositories, and DTOs
- `backend/platform-core/src/main/java/.../security/**` or equivalent only for Spring Boot security wiring shared by the running application
- Flyway migration(s) under the runtime module that owns database startup for the executable application, most likely `backend/platform-core/src/main/resources/db/migration/`

This keeps the identity domain in `identity-access` while respecting the current single Spring Boot runtime shape.

## Alternatives considered

- **Delay authentication until RBAC is designed**: rejected because later RBAC and user-bound features need a real identity foundation first.
- **Implement full OAuth2/OIDC federation now**: rejected because it adds unnecessary external-system and credential complexity for the first MVP auth baseline.
- **Use server-side session persistence instead of JWT**: rejected because the first implementation can meet the acceptance criteria more simply with stateless signed tokens while still allowing later evolution if needed.
- **Place all auth code directly in `platform-core`**: rejected because the repository already defines an `identity-access` module and the identity domain should live there even if runtime wiring remains in `platform-core`.

## Files/components likely affected

- `df/artifacts/STORY-080/task.md`
- `df/artifacts/STORY-080/solution-design.md`
- `df/artifacts/STORY-080/decision-017-phase-1-auth-foundation.md`
- `df/artifacts/STORY-080/handoffs.md`
- `df/backlog/architecture-direction.md`
- `df/runtime/board.md`
- `df/runtime/backend-dev-board.md`
- `df/runtime/activity-log.md`
- `df/runtime/decisions.md`
- expected backend implementation targets under `backend/identity-access/src/main/java/**`
- likely Spring Security wiring under `backend/platform-core/src/main/java/**`
- likely Flyway migration(s) under `backend/platform-core/src/main/resources/db/migration/`
- backend tests under `backend/platform-core/src/test/**` and/or `backend/identity-access/src/test/**`

## Data model changes

- Add tenant-scoped identity persistence tables for local user credentials
- Add uniqueness and status constraints sufficient for secure login behavior
- Reuse the existing deployment-tenant model rather than inventing central multi-country identity scoping

## API/contract changes

- Add `POST /api/v1/identity/auth/login` for credential exchange
- Add `POST /api/v1/identity/users` for admin-created account registration
- Optionally add a minimal authenticated identity endpoint such as `GET /api/v1/identity/me` to prove protected access behavior
- Apply authentication checks to protected routes so expired or invalid tokens produce `401`
- Expose the new identity contract through OpenAPI

## UI/UX impact

- None in this story. The accepted scope is backend-only authentication foundation with no frontend login screens or designer deliverables.

## Security and privacy considerations

- Store only password hashes, never plain-text passwords
- Externalize JWT signing secret, bootstrap-admin credentials, and token TTL configuration
- Do not log raw passwords, raw tokens, or sensitive credential fields
- Keep accounts tenant-scoped to the active sovereign deployment
- Defer MFA, external IdP, full role hierarchy, and password-reset flows explicitly rather than inventing partial versions here

## Performance/scalability considerations

- The first login path is low-volume and can remain database-backed without a separate auth cache
- Use indexed lookup on `(tenant_id, username)` to keep login resolution efficient
- Stateless JWT validation avoids session-store overhead in the first implementation

## Test strategy

`backend-dev` should provide strong backend verification, including:

- migration tests proving the identity tables are created correctly and idempotently
- login success test returning a token for valid credentials
- login failure test returning unauthorized behavior for invalid credentials
- expired-token access test returning `401` on a protected endpoint
- admin user-registration test proving an authenticated bootstrap/admin user can create a new user who can then log in
- regression checks proving the new endpoints appear in `/api-docs`
- audit verification proving meaningful user-creation mutations produce audit records where feasible within scope
- backend reactor and, if shared scope is touched broadly, full-parent Maven verification

## Deployment/migration plan

- Add forward-only Flyway migration(s) for identity tables
- Introduce externalized auth configuration properties for bootstrap-admin and JWT settings
- Keep defaults safe for local development while requiring secrets to come from environment/configuration in real deployments

## Rollback plan

- Revert the new identity migration(s), auth package, and security wiring if the auth foundation design is rejected before downstream stories depend on it
- Disable the identity endpoints and revert to unauthenticated local development only as a temporary redesign fallback; do not keep partially secure production behavior

## Risks and mitigations

- **Risk:** scope expands into full user/role platform design
  - **Mitigation:** keep this story to authentication, admin-created registration, and protected-token verification only; defer full RBAC to `STORY-081`
- **Risk:** secrets or bootstrap credentials are handled unsafely
  - **Mitigation:** require externalized configuration and verify no secrets are committed or logged
- **Risk:** the chosen token contract blocks future authorization work
  - **Mitigation:** keep claims minimal and role modeling extensible so `STORY-081` can layer on without breaking login consumers

## Open questions

- None blocking `backend-dev`. If implementation discovers a need for an explicit refresh-token or revocation store to keep the first acceptance criteria secure in practice, document the trade-off in backend notes and keep any added scope minimal.

## Implementation lane

- Lane: `backend-dev`
- Subdashboard: `df/runtime/backend-dev-board.md`
- Artifact folder for implementation notes: `df/artifacts/STORY-080/backend/`

## SA decision

Approved for development: Yes

