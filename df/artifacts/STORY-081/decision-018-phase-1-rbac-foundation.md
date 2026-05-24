# Decision Record - DECISION-018

- Date: 2026-05-24
- Status: Accepted
- Owner role: SA
- Related task: STORY-081

## Context

`STORY-080` is accepted and provides tenant-scoped authentication, but the MVP still lacks the user/role model and least-privilege authorization called out in the roadmap and MVP definition. The repository does not yet contain the full school/class/student/parent domain model, so waiting for later module stories would force downstream features either to stay over-permissive or to invent one-off authorization logic.

## Decision

Adopt a **backend-only Phase 1 RBAC foundation** centered on `backend/identity-access` with these governing rules:

1. RBAC uses a predefined, framework-generic role catalogue: `COUNTRY_ADMIN`, `REGION_ADMIN`, `CITY_ADMIN`, `INSTITUTION_ADMIN`, `TEACHER`, `STUDENT`, and `PARENT`.
2. Role assignments are persisted per tenant and per user, with generic scope descriptors rather than country-specific or module-specific authorization code.
3. The `STORY-080` login contract remains stable; authorization layers on top of authentication without requiring a fresh auth redesign.
4. Current role assignments should be resolved server-side during authorized request handling so the model can evolve without oversized or stale JWT role payloads.
5. Phase 1 proof may use representative protected backend routes and generic resource-scope contexts until later school/person domain stories provide richer real resources.
6. Meaningful security mutations such as role assignment or revocation must converge on the generic audit foundation.

## Consequences

- `backend-dev` can implement a reusable authorization baseline now without waiting for every school/person feature to exist.
- The backend gains a stable RBAC contract that later stories can consume for real domain workflows.
- Representative authorization proof routes may be needed temporarily until downstream modules provide richer protected resources.
- MFA, external federation, and full ABAC remain deferred to later stories.

## Alternatives considered

- Delay RBAC until the school/person modules exist
- Put all role/scope data directly into JWT claims
- Build a full policy-engine/ABAC system immediately
- Hardcode teacher/parent access checks separately in each controller

## Evidence

- `df/backlog/roadmap.md`
- `df/backlog/mvp-definition.md`
- `df/backlog/user-stories.md`
- `df/backlog/architecture-direction.md`
- `df/artifacts/STORY-080/task.md`
- `df/artifacts/STORY-080/solution-design.md`
- `backend/identity-access/src/main/java/com/darkfactory/education/identityaccess/auth/AuthenticatedUserPrincipal.java`
- `backend/identity-access/src/main/java/com/darkfactory/education/identityaccess/auth/IdentityUserAuthority.java`
- `backend/platform-core/src/main/java/com/darkfactory/education/platform/security/SecurityConfiguration.java`

## Follow-up actions

- Route `STORY-081` to `backend-dev`
- Add RBAC persistence and authorization services
- Add representative protected backend coverage for teacher, institution-admin, and parent scope checks
- Preserve generic, scope-driven authorization behavior so later modules can plug in without schema replacement

