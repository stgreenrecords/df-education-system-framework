# Handoff - STORY-021

## SA -> backend-dev

- Timestamp: 2026-05-24 20:30 local
- Task: STORY-021
- From state: ARCHITECTURE_IN_PROGRESS
- To state: READY_FOR_DEV
- Lane: backend-dev
- Summary: SA promoted `STORY-021` as the next Critical Phase 1 foundation story, defined the sovereign deployment-tenant model as one active tenant per country-operated deployment, recorded `DECISION-014`, updated shared architecture direction, and routed implementation to the `backend-dev` lane.

## Evidence

- `df/artifacts/STORY-021/task.md`
- `df/artifacts/STORY-021/solution-design.md`
- `df/artifacts/STORY-021/decision-014-sovereign-deployment-tenant-model.md`
- `df/backlog/architecture-direction.md`
- `df/backlog/user-stories.md`
- `df/runtime/board.md`
- `df/runtime/backend-dev-board.md`
- `df/runtime/decisions.md`

## Tests/checks

| Check | Command/source | Result | Notes |
|---|---|---|---|
| Runtime queue review | `df/runtime/board.md`; delivery subdashboards | PASS | No active rejected, blocked, design, or delivery-lane tasks outranked a new `OPEN` backlog story after `STORY-023` reached `DONE` |
| Backlog priority/dependency review | `df/backlog/user-stories.md`; `df/backlog/roadmap.md` | PASS | `STORY-021` is a Critical Phase 1 story whose implementation dependencies (`STORY-010`, `STORY-011`) are accepted and which unblocks `STORY-030` plus later tenant-dependent stories |
| Sovereign deployment alignment review | `df/artifacts/STORY-020/country-sovereign-deployment-architecture.md`; `df/backlog/final-initial-prompt.md` | PASS | The selected tenant model stays aligned with the country-sovereign top-level deployment concept rather than central SaaS tenancy |
| Existing backend foundation review | `backend/platform-core/**`; `df/artifacts/STORY-011/solution-design.md`; `df/artifacts/STORY-220/solution-design.md` | PASS | The repository already has a Spring Boot/PostgreSQL/Flyway backend foundation suitable for a backend-only tenant slice in `platform-core` |

## Known risks

- `RISK-010`: migration robustness remains important as new persistent foundation tables are introduced
- `RISK-019`: shared backend/runtime files may create later cross-lane conflicts if follow-up stories touch the same platform-core scope
- The deferred schema isolation decision in `df/backlog/architecture-direction.md` may affect how later tenant-aware tables expand beyond this story

## Next role instructions

- Implement the tenant/deployment slice in `backend/platform-core` only.
- Add a Flyway migration for the tenant table and a bootstrap/initializer path using deployment configuration.
- Introduce a reusable backend tenant context abstraction and a minimal backend contract for reading the active tenant metadata.
- Keep the model sovereign-deployment-local; do not introduce request-side country switching or centralized multi-country runtime behavior.
- Run backend-focused and broader Maven verification as needed, and write lane evidence under `df/artifacts/STORY-021/backend/`.

## Blockers

- None.

## backend-dev -> qa

- Timestamp: 2026-05-24 20:39 local
- Task: STORY-021
- From state: DEV_IN_PROGRESS
- To state: READY_FOR_QA
- Lane: `backend-dev`
- Summary: Backend implementation completed the sovereign deployment tenant foundation in `backend/platform-core`, including a persisted `platform_tenant` table, deployment bootstrap properties, idempotent startup bootstrap logic, a cached tenant context abstraction, a minimal tenant metadata endpoint, and backend test coverage. Backend-focused and full-parent Maven verification both passed.

## Evidence

- `df/artifacts/STORY-021/task.md`
- `df/artifacts/STORY-021/backend/dev-notes.md`
- `df/artifacts/STORY-021/backend/handoff-to-qa.md`
- `backend/platform-core/src/main/java/com/darkfactory/education/platform/tenant/`
- `backend/platform-core/src/main/resources/application.properties`
- `backend/platform-core/src/main/resources/db/migration/V6__create_platform_tenant.sql`
- `backend/platform-core/src/test/java/com/darkfactory/education/platform/EducationSystemApplicationIT.java`
- `backend/platform-core/src/test/java/com/darkfactory/education/platform/tenant/TenantPropertiesTest.java`
- `df/runtime/board.md`
- `df/runtime/backend-dev-board.md`

## Tests/checks

| Check | Command/source | Result | Notes |
|---|---|---|---|
| Backend reactor verify | `.\mvnw.cmd -f backend\pom.xml -pl platform-core -am clean verify` | PASS | `BUILD SUCCESS`; 23 tests total (3 unit + 20 integration) |
| Full parent verify | `.\mvnw.cmd clean verify` | PASS | `BUILD SUCCESS`; broader multi-module build stayed green |
| Focused workspace status snapshot | `git --no-pager status --short --branch -- backend\platform-core df\artifacts\STORY-021 df\runtime` | PASS | Workspace contains unrelated pre-existing changes outside `STORY-021`; implementation itself is scoped to backend files plus runtime/task docs |

## Known risks

- `RISK-010`: migration robustness remains important as platform persistence expands.
- `RISK-019`: later backend stories may touch shared `platform-core` runtime and persistence scope.
- The broader schema-isolation strategy remains deferred; this story intentionally stops at the first deployment-tenant baseline.

## Next role instructions

- Re-run backend-focused and full-parent Maven verification.
- Confirm Flyway `V6` creates the tenant table and that bootstrap produces exactly one active tenant row.
- Confirm `GET /api/v1/platform/tenant` returns the persisted active tenant metadata.
- Confirm the implementation remains deployment-local and does not introduce request-side cross-country tenant switching.
- Confirm the changed backend scope remains framework-generic and country-agnostic.

## qa -> po

- Timestamp: 2026-05-24 20:46 local
- Task: STORY-021
- From state: QA_IN_PROGRESS
- To state: READY_FOR_PO
- Lane: `backend-dev` delivery verified by `qa`
- Summary: QA independently reran backend-focused and full-parent Maven verification, inspected the tenant migration/bootstrap/controller/configuration sources directly, and confirmed the story meets all three acceptance criteria. The implementation persists one active sovereign deployment tenant, keeps bootstrap idempotent, exposes `GET /api/v1/platform/tenant`, and does not introduce request-side cross-country tenant switching.

## Evidence

- `df/artifacts/STORY-021/qa-report.md`
- `df/artifacts/STORY-021/task.md`
- `df/artifacts/STORY-021/backend/dev-notes.md`
- `df/artifacts/STORY-021/backend/handoff-to-qa.md`
- `backend/platform-core/src/main/java/com/darkfactory/education/platform/tenant/`
- `backend/platform-core/src/main/resources/application.properties`
- `backend/platform-core/src/main/resources/db/migration/V6__create_platform_tenant.sql`
- `backend/platform-core/src/test/java/com/darkfactory/education/platform/EducationSystemApplicationIT.java`
- `backend/platform-core/src/test/java/com/darkfactory/education/platform/tenant/TenantPropertiesTest.java`
- `df/runtime/board.md`
- `df/runtime/backend-dev-board.md`

## Tests/checks

| Check | Command/source | Result | Notes |
|---|---|---|---|
| Backend reactor verify | `./mvnw.cmd -f backend/pom.xml -pl platform-core -am clean verify` | PASS | `BUILD SUCCESS`; 3 unit tests + 20 integration tests; Testcontainers PostgreSQL path succeeded |
| Full parent verify | `./mvnw.cmd clean verify` | PASS | `BUILD SUCCESS`; summary captured separately to avoid long-log truncation |
| Tenant migration inspection | `V6__create_platform_tenant.sql` | PASS | Schema includes required metadata fields and uniqueness on `country_code` |
| Tenant bootstrap/context inspection | `TenantBootstrapRunner`; `TenantRepository`; `TenantContextService` | PASS | Confirms insert/reconcile behavior, single active tenant enforcement, and deployment-local scoping |
| Tenant endpoint inspection | `TenantController`; `EducationSystemApplicationIT.tenantEndpointReturnsActiveDeploymentTenantMetadata` | PASS | Confirms the endpoint returns the active tenant metadata and has no request-side tenant selector |

## Known risks

- `RISK-010`: migration robustness remains important as platform persistence expands.
- `RISK-019`: later backend stories may touch shared `platform-core` runtime and persistence scope.
- Informational JDK/Testcontainers/Mockito warnings were observed during test execution; they do not affect current pass/fail status but should be monitored during future toolchain upgrades.

## Next role instructions

- Review `df/artifacts/STORY-021/qa-report.md` against the task intent and acceptance criteria.
- Confirm the sovereign single-deployment-tenant model matches the product expectation from `STORY-020`.
- If accepted, move the task to `DONE` and record PO acceptance evidence.

## po -> factory/sa

- Timestamp: 2026-05-24 20:50 local
- Task: STORY-021
- From state: READY_FOR_PO
- To state: DONE
- Lane: backend delivery accepted by `po`
- Summary: PO completed product review of the QA-approved tenant/deployment foundation, confirmed the implementation stays aligned with the sovereign deployment boundary from `STORY-020`, and accepted the story after a focused rerun of the tenant bootstrap + tenant endpoint contract tests passed.

## Evidence

- `df/artifacts/STORY-021/po-review.md`
- `df/artifacts/STORY-021/qa-report.md`
- `df/artifacts/STORY-021/task.md`
- `df/artifacts/STORY-021/solution-design.md`
- `df/artifacts/STORY-021/handoffs.md`
- `df/artifacts/STORY-020/country-sovereign-deployment-architecture.md`
- `backend/platform-core/src/test/java/com/darkfactory/education/platform/EducationSystemApplicationIT.java`
- `backend/platform-core/src/main/java/com/darkfactory/education/platform/tenant/`
- `backend/platform-core/src/main/resources/db/migration/V6__create_platform_tenant.sql`
- `./mvnw.cmd -f backend/pom.xml -pl platform-core "-Dtest=EducationSystemApplicationIT#tenantBootstrapCreatesSingleActiveDeploymentTenant,EducationSystemApplicationIT#tenantEndpointReturnsActiveDeploymentTenantMetadata,EducationSystemApplicationIT#apiDocsContainsTenantEndpoint" test`
- `df/runtime/board.md`
- `df/runtime/backend-dev-board.md`

## Tests/checks

| Check | Command/source | Result | Notes |
|---|---|---|---|
| QA report review | `df/artifacts/STORY-021/qa-report.md` | PASS | Confirms QA independently passed backend-focused and full-parent verification plus direct code/contract inspection |
| Sovereign deployment intent review | `df/artifacts/STORY-020/country-sovereign-deployment-architecture.md`; `df/artifacts/STORY-021/task.md` | PASS | Confirms product intent remains one active tenant per country-operated deployment, not centralized multi-country SaaS tenancy |
| Focused product contract verification | `./mvnw.cmd -f backend/pom.xml -pl platform-core "-Dtest=EducationSystemApplicationIT#tenantBootstrapCreatesSingleActiveDeploymentTenant,EducationSystemApplicationIT#tenantEndpointReturnsActiveDeploymentTenantMetadata,EducationSystemApplicationIT#apiDocsContainsTenantEndpoint" test` | PASS | `BUILD SUCCESS`; 3 tests, 0 failures, 0 errors |

## Known risks

- `RISK-010`: migration robustness remains important as platform persistence expands.
- `RISK-019`: later shared runtime/build/documentation files may still need careful lane sequencing.

## Next role instructions

- `STORY-021` is complete.
- New session: `sa` should inspect the runtime board/backlog and promote the next highest-priority actionable task.

