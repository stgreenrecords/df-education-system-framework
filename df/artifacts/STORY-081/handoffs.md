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

## backend-dev -> qa

- Timestamp: 2026-05-24 23:32 local
- Task: STORY-081
- From state: DEV_IN_PROGRESS
- To state: READY_FOR_QA
- Lane: backend-dev
- Summary: `backend-dev` completed the backend-only Phase 1 RBAC foundation by adding tenant-scoped predefined role assignments, generic scope-path authorization evaluation, bootstrap-admin reconciliation into the new RBAC model, minimal role-assignment APIs, representative institution/student authorization-proof endpoints, audit convergence for role assignments, and expanded integration coverage for migration `V10`, teacher/institution-admin/parent scope checks, and `/api-docs` exposure.

## Evidence

- `df/artifacts/STORY-081/backend/dev-notes.md`
- `df/artifacts/STORY-081/backend/handoff-to-qa.md`
- `df/artifacts/STORY-081/task.md`
- `backend/platform-core/src/main/resources/db/migration/V10__create_identity_role_assignment_table.sql`
- `backend/identity-access/src/main/java/com/darkfactory/education/identityaccess/auth/IdentityRoleAssignmentService.java`
- `backend/identity-access/src/main/java/com/darkfactory/education/identityaccess/auth/IdentityAuthorizationService.java`
- `backend/identity-access/src/main/java/com/darkfactory/education/identityaccess/auth/IdentityRoleAssignmentController.java`
- `backend/identity-access/src/main/java/com/darkfactory/education/identityaccess/auth/IdentityAccessProbeController.java`
- `backend/platform-core/src/main/java/com/darkfactory/education/platform/security/JwtAuthenticationFilter.java`
- `backend/platform-core/src/test/java/com/darkfactory/education/platform/EducationSystemApplicationIT.java`

## Tests/checks

| Check | Command/source | Result | Notes |
|---|---|---|---|
| Focused RBAC integration verification | `Set-Location "C:\Users\Viach\IdeaProjects\DF Education System Framework"; .\mvnw.cmd -f backend\pom.xml -pl platform-core -am "-Dit.test=EducationSystemApplicationIT" verify` | PASS | `EducationSystemApplicationIT` passed `40/40`, including migration `V10`, bootstrap role reconciliation, role assignment audit convergence, teacher/institution-admin/parent scope checks, and `/api-docs` exposure |
| Full backend reactor verification | `Set-Location "C:\Users\Viach\IdeaProjects\DF Education System Framework"; .\mvnw.cmd -f backend\pom.xml clean verify` | PASS | Confirms the RBAC/auth changes did not break the wider backend reactor |
| IDE error scan | `get_errors` on edited/new RBAC files | PASS | Only the expected SQL datasource-assistance warning appeared for the new migration file |

## Known risks

- Representative authorization-proof endpoints remain temporary backend-only evidence paths until later domain stories provide richer real school/person resources.
- Existing non-identity endpoints were not broadly re-scoped in this story to avoid silent authorization expansion across earlier accepted work.

## Next role instructions

- Rerun the focused `EducationSystemApplicationIT` suite and inspect the new `V10` migration plus bootstrap role-assignment reconciliation.
- Verify role assignments are loaded server-side during JWT-authenticated requests through `AuthenticatedPrincipalRoleService` and `JwtAuthenticationFilter`.
- Confirm teacher/institution-admin/parent authorization boundaries behave as documented and that role-assignment mutations create audit events.
- Confirm the new RBAC endpoints appear in `/api-docs` and that the implementation remains backend-only and framework-generic.

