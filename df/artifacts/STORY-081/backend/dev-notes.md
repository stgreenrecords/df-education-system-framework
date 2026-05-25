# Backend Dev Notes - STORY-081

## Session

- Timestamp: 2026-05-24 23:25 local
- Role: `backend-dev`
- Task: `STORY-081`
- State: `DEV_IN_PROGRESS -> READY_FOR_QA`

## Inputs reviewed

- `df/artifacts/STORY-081/task.md`
- `df/artifacts/STORY-081/solution-design.md`
- `df/artifacts/STORY-081/handoffs.md`
- `df/runtime/board.md`
- `df/runtime/backend-dev-board.md`
- Existing auth/security/test sources under `backend/identity-access` and `backend/platform-core`

## Scope confirmation

- The task is backend-only and routed correctly to `backend-dev`.
- The implementation must stay framework-generic and must not introduce country-specific authorization logic.
- The existing `STORY-080` login contract must remain stable while RBAC layers on top.
- Because school/person modules are not fully implemented yet, representative backend authorization routes are acceptable evidence if they use real generic scope-aware authorization logic.

## Initial implementation plan

1. Move RBAC task state to `DEV_IN_PROGRESS` in runtime/task artifacts.
2. Add tenant-scoped role-assignment persistence and a forward-only migration after `V9`.
3. Add generic predefined roles, scope descriptors, role-assignment services, and authorization evaluation in `backend/identity-access`.
4. Enrich authenticated principals with current persisted role assignments during request authentication.
5. Add minimal admin role-assignment endpoints and representative protected backend authorization-proof endpoints.
6. Extend integration coverage for role assignment, teacher/institution/parent scope checks, audit convergence, migration ordering, and `/api-docs` exposure.
7. Run focused backend verification, then broader backend regression, document results, and hand off to QA.

## Start evidence

- Timestamp/status snapshot command:
  - `Set-Location "C:\Users\Viach\IdeaProjects\DF Education System Framework"; Get-Date -Format "yyyy-MM-dd HH:mm 'local'"; Write-Host '---'; git --no-pager status --short --branch -- backend df/artifacts/STORY-081 df/runtime/board.md df/runtime/backend-dev-board.md df/runtime/activity-log.md | Out-String`
- Result highlights:
  - `2026-05-24 23:25 local`
  - `STORY-081` artifacts and runtime files are present as expected for the new backend task

## Risks to watch

- The accepted `STORY-080` auth path must remain backward-compatible.
- Shared auth/security files were just introduced and should be changed narrowly.
- The RBAC proof should not sprawl into full school/person module implementation.

## Implementation completed

- Added forward-only migration `V10__create_identity_role_assignment_table.sql` for tenant-scoped predefined role assignments with generic scope-path storage.
- Added RBAC domain types in `backend/identity-access` for predefined roles, permissions, generic scope descriptors, role-assignment persistence, authorization evaluation, and principal role enrichment during JWT request handling.
- Reconciled the existing bootstrap administrator into the new RBAC model by ensuring a tenant-root `COUNTRY_ADMIN` assignment is present after bootstrap creation/update.
- Added minimal backend-only RBAC APIs for role assignment and role listing plus representative authorization-proof endpoints for institution teaching-view, institution management, and student-view access checks.
- Extended the existing user-registration authorization to rely on the new RBAC permission model while preserving compatibility with the accepted `STORY-080` auth baseline.
- Extended integration coverage in `EducationSystemApplicationIT` for migration `V10`, bootstrap role assignment, role assignment audit creation, teacher/institution-admin/parent scope-aware authorization behavior, and `/api-docs` exposure for the new RBAC endpoints.

## Validation evidence

| Check | Command | Result | Notes |
|---|---|---|---|
| File-level error scan | `get_errors` on edited/new RBAC Java, SQL, and test files | PASS | Only the expected IDE SQL datasource-assistance warning appeared for the new migration file; no Java/test errors remained |
| Focused RBAC integration verification | `Set-Location "C:\Users\Viach\IdeaProjects\DF Education System Framework"; .\mvnw.cmd -f backend\pom.xml -pl platform-core -am "-Dit.test=EducationSystemApplicationIT" verify` | PASS | `EducationSystemApplicationIT` passed `40/40`, covering migration `V10`, bootstrap RBAC assignment, role assignment audit convergence, teacher/institution-admin/parent scope checks, and `/api-docs` exposure against Testcontainers PostgreSQL |
| Full backend reactor verification | `Set-Location "C:\Users\Viach\IdeaProjects\DF Education System Framework"; .\mvnw.cmd -f backend\pom.xml clean verify` | PASS | Confirms the RBAC/auth/security changes did not break the broader backend reactor |

## Focused status snapshot

- Snapshot command:
  - `Set-Location "C:\Users\Viach\IdeaProjects\DF Education System Framework"; Get-Date -Format "yyyy-MM-dd HH:mm 'local'"; Write-Host '---'; git --no-pager status --short --branch -- backend df/artifacts/STORY-081 df/runtime/board.md df/runtime/backend-dev-board.md df/runtime/activity-log.md | Out-String`
- Snapshot timestamp:
  - `2026-05-24 23:32 local`
- Snapshot highlights:
  - new RBAC/auth classes under `backend/identity-access/src/main/java/com/darkfactory/education/identityaccess/auth/`
  - `backend/platform-core/src/main/resources/db/migration/V10__create_identity_role_assignment_table.sql`
  - focused auth/security updates in `backend/platform-core/src/main/java/com/darkfactory/education/platform/security/`
  - expanded `backend/platform-core/src/test/java/com/darkfactory/education/platform/EducationSystemApplicationIT.java`

## Known risks / notes for QA

- Spring Boot still emits the generated fallback security-password warning during startup; the focused and full verification passes show that the custom JWT + RBAC path works correctly despite that framework noise.
- The representative RBAC proof endpoints are intentionally backend-only placeholders for scope-aware authorization evidence until later school/person domain stories provide richer real resources.
- Existing non-identity APIs were not broadly re-scoped in this story; authorization proof remains intentionally concentrated in the RBAC-specific backend paths to avoid silent scope expansion across earlier accepted stories.

## Ready for QA

- Backend implementation, test evidence, and focused runtime documentation for `STORY-081` are complete.
- Next artifact: `df/artifacts/STORY-081/backend/handoff-to-qa.md`.

