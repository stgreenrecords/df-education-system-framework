# Handoff - STORY-081

## SA -> backend-dev

- Timestamp: 2026-05-24 23:19 local
- Task: STORY-081
- From state: ARCHITECTURE_IN_PROGRESS
- To state: READY_FOR_DEV
- Lane: backend-dev
- Summary: SA selected `STORY-081` as the next highest-priority actionable Phase 1 dependency-root story after `STORY-080` acceptance, designed a backend-only RBAC foundation that layers on the existing auth baseline, recorded `DECISION-018`, and routed the story to `backend-dev`.

## Evidence

- `df/artifacts/STORY-081/task.md`
- `df/artifacts/STORY-081/solution-design.md`
- `df/artifacts/STORY-081/decision-018-phase-1-rbac-foundation.md`
- `df/backlog/roadmap.md`
- `df/backlog/mvp-definition.md`
- `df/backlog/architecture-direction.md`
- `df/backlog/user-stories.md`
- `df/runtime/board.md`
- `df/runtime/backend-dev-board.md`
- `df/runtime/decisions.md`
- `backend/identity-access/src/main/java/com/darkfactory/education/identityaccess/auth/AuthenticatedUserPrincipal.java`
- `backend/identity-access/src/main/java/com/darkfactory/education/identityaccess/auth/IdentityUserAuthority.java`
- `backend/platform-core/src/main/java/com/darkfactory/education/platform/security/SecurityConfiguration.java`

## Tests/checks

| Check | Command/source | Result | Notes |
|---|---|---|---|
| Runtime queue review | `df/runtime/board.md`; delivery subdashboards | PASS | No active `RETURNED_TO_DEV`, `QA_FAILED`, `PO_REJECTED`, design, or implementation task outranks promoting a new backlog item after `STORY-080` reached `DONE` |
| Backlog dependency review | `df/backlog/user-stories.md` | PASS | `STORY-081` depends on `STORY-080`, which is now accepted; tenant/configuration/audit/OpenAPI foundations further reduce implementation risk |
| MVP/roadmap alignment review | `df/backlog/roadmap.md`; `df/backlog/mvp-definition.md` | PASS | User/role model and security baseline remain explicit unfinished Phase 1 MVP requirements |
| Live auth baseline review | `df/artifacts/STORY-080/task.md`; `df/artifacts/STORY-080/solution-design.md`; auth/security source files listed above | PASS | Confirms the repository now has a stable authentication base that RBAC can extend without starting from scratch |

## Known risks

- School/person domain modules are not complete yet, so the implementation must use generic scope descriptors and representative protected routes instead of inventing full domain features.
- Shared auth/security files in `backend/identity-access` and `backend/platform-core` were touched heavily by `STORY-080`; keep the RBAC changes tightly scoped and backward-compatible.
- Hardcoded country- or module-specific access rules would violate framework invariants.

## Next role instructions

- Implement the RBAC foundation primarily in `backend/identity-access`, with only the minimum request-authorization wiring in the runnable backend module.
- Add forward-only Flyway migration(s) for tenant-scoped role assignments and any minimal supporting scope metadata required by the authorization engine.
- Introduce the predefined generic roles `COUNTRY_ADMIN`, `REGION_ADMIN`, `CITY_ADMIN`, `INSTITUTION_ADMIN`, `TEACHER`, `STUDENT`, and `PARENT`, plus server-side authorization evaluation against generic scope descriptors.
- Keep the `STORY-080` login contract stable while extending authenticated principal handling so protected routes can consult persisted role assignments.
- Add minimal admin role-assignment endpoints, audit convergence, representative protected-route coverage for teacher, institution-admin, and parent visibility checks, and OpenAPI exposure.
- Keep the work backend-only, framework-generic, and free of MFA, external IdP integration, frontend role-management UI, or country-specific authorization logic.

## Blockers

- None.

