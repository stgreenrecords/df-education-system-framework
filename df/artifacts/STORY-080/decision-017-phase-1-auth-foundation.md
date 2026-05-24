# Decision Record - DECISION-017

- Date: 2026-05-24
- Status: Accepted
- Owner role: SA
- Related task: STORY-080

## Context

The roadmap and MVP scope both require a security baseline plus a user/role model in Phase 1. The repository now has the Spring Boot modular scaffold, PostgreSQL/Flyway foundation, tenant context, OpenAPI support, and generic audit foundation, but it still lacks any real authentication mechanism. Later RBAC, MFA, translation-management authorization, and user-bound domain features cannot proceed safely without a real identity baseline.

## Decision

Adopt a **backend-only Phase 1 authentication foundation** centered on the `identity-access` module with these governing rules:

1. user accounts are persisted locally in a tenant-scoped PostgreSQL identity store;
2. one deployment-local bootstrap administrator is created from externalized configuration so an admin can register additional users;
3. stored credentials use secure password hashing, not reversible encryption or plain-text storage;
4. successful login issues a signed bearer token (JWT) with externalized signing secret and expiry settings;
5. protected backend routes reject expired or invalid tokens with `401`;
6. full hierarchical RBAC, MFA, external IdP federation, and self-service account flows remain deferred to later stories rather than being partially mixed into the first auth baseline.

## Consequences

- `backend-dev` owns implementation in `backend/identity-access` with minimal security wiring in the running Spring Boot module.
- The first auth contract is backend/API-focused and intentionally non-visual.
- `STORY-081` should extend the identity model into full role-based authorization rather than replacing the login/token foundation.
- `STORY-082` should add MFA on top of this authentication baseline.
- Future country deployments must supply secrets through configuration and must not commit auth secrets to source control.

## Alternatives considered

- **Delay auth until RBAC is fully designed**: rejected because later security and user-bound work needs a real authentication root first.
- **Implement external OIDC/SAML integration immediately**: rejected because it adds external dependencies and configuration complexity before the local MVP auth baseline is proven.
- **Use only server-side session persistence**: rejected because a signed bearer token is the smallest portable contract for later API consumers while still satisfying the story acceptance criteria.
- **Place the entire auth domain in `platform-core`**: rejected because identity belongs in the existing `identity-access` module even if runtime security configuration stays near the Spring Boot entrypoint.

## Evidence

- `df/artifacts/STORY-080/solution-design.md`
- `df/backlog/roadmap.md`
- `df/backlog/mvp-definition.md`
- `df/backlog/architecture-direction.md`
- `df/backlog/user-stories.md`
- `backend/identity-access/pom.xml`
- `backend/identity-access/src/main/java/com/darkfactory/education/identityaccess/IdentityAccessModule.java`
- `backend/platform-core/pom.xml`

## Follow-up actions

- `backend-dev` implements the tenant-scoped user store, login endpoint, token validation, bootstrap-admin path, and admin-created registration flow.
- QA verifies login success/failure behavior, expired-token `401` behavior, OpenAPI exposure, and admin-created-user login.
- Later identity/security stories extend this foundation without introducing country-specific code or replacing the deployment-local identity boundary.

