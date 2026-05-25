# QA Report - STORY-021

## QA Result: PASS

- Task: `STORY-021`
- Acceptance criteria covered: Yes
- Unit tests: `./mvnw.cmd -f backend/pom.xml -pl platform-core -am clean verify` — PASS (`TenantPropertiesTest`: 3/3)
- Integration tests: `./mvnw.cmd -f backend/pom.xml -pl platform-core -am clean verify`; `./mvnw.cmd clean verify` — PASS (`EducationSystemApplicationIT`: 20/20, Testcontainers-backed PostgreSQL)
- Manual checks: Inspected `TenantBootstrapRunner`, `TenantRepository`, `TenantController`, `V6__create_platform_tenant.sql`, and `application.properties`; confirmed one persisted deployment-local tenant model, idempotent bootstrap reconciliation, unique country-code enforcement, externalized `EDU_TENANT_*` configuration, and no request-side tenant selector
- Regression checks: Re-ran the backend-focused and full-parent Maven verification successfully; confirmed Flyway remains at version `6`, prior migration chain stays ordered `1..6`, and the new tenant endpoint is included in `/api-docs`
- Risks: `RISK-010` and `RISK-019` remain open but do not block PO review; non-failing JDK/Testcontainers/Mockito warning output was observed during test execution and is informational for future toolchain hardening
- Handoff: `READY_FOR_PO`

## Scope reviewed

- `backend/platform-core/src/main/java/com/darkfactory/education/platform/tenant/`
- `backend/platform-core/src/main/resources/application.properties`
- `backend/platform-core/src/main/resources/db/migration/V6__create_platform_tenant.sql`
- `backend/platform-core/src/test/java/com/darkfactory/education/platform/EducationSystemApplicationIT.java`
- `backend/platform-core/src/test/java/com/darkfactory/education/platform/tenant/TenantPropertiesTest.java`
- `df/artifacts/STORY-021/task.md`
- `df/artifacts/STORY-021/solution-design.md`
- `df/artifacts/STORY-021/backend/dev-notes.md`
- `df/artifacts/STORY-021/backend/handoff-to-qa.md`
- `df/runtime/board.md`
- `df/runtime/backend-dev-board.md`

## Environment

- OS: Windows
- Shell: Windows PowerShell 5.1
- Java: 25.0.2
- Maven Wrapper: 3.9.15
- Container runtime used by tests: Docker Desktop via Testcontainers
- Test database: `postgres:17-alpine`
- Branch snapshot: `master...origin/master` with many pre-existing unrelated workspace changes; QA review was scoped to the `STORY-021` backend and documentation paths

## Test cases and results

| Test case | Command/source | Result | Notes |
|---|---|---|---|
| Repository status snapshot | `git --no-pager status --short --branch` | PASS | Confirms the workspace contains many unrelated pre-existing changes; QA isolated the `STORY-021` review to the declared backend/task/runtime scope |
| Tenant migration schema inspection | `backend/platform-core/src/main/resources/db/migration/V6__create_platform_tenant.sql` | PASS | Table includes `tenant_id`, `country_code`, `display_name`, `default_timezone`, `default_locale`, timestamps, and a uniqueness constraint/index on `country_code` |
| Tenant bootstrap logic inspection | `TenantBootstrapRunner`, `TenantRepository` | PASS | Bootstrap inserts on first startup, reuses existing tenant when values match, updates mutable metadata when needed, and fails fast on sovereign-country mismatch |
| Tenant endpoint contract inspection | `TenantController`; `TenantResponse`; `/api-docs` integration assertions | PASS | Endpoint is fixed at `GET /api/v1/platform/tenant`; there is no request parameter, header, or path-based country selector |
| Backend reactor verification | `./mvnw.cmd -f backend/pom.xml -pl platform-core -am clean verify` | PASS | `BUILD SUCCESS`; 3 unit tests + 20 integration tests; finished `2026-05-24T20:44:02+02:00` |
| Full parent verification | `./mvnw.cmd clean verify` | PASS | `BUILD SUCCESS`; summarized output captured separately to avoid long-log truncation; finished `2026-05-24T20:46:00+02:00` |
| Tenant bootstrap on fresh database | `EducationSystemApplicationIT.tenantBootstrapCreatesSingleActiveDeploymentTenant` | PASS | Confirms one tenant row with expected country code, name, timezone, and locale after startup |
| Tenant bootstrap idempotency | `EducationSystemApplicationIT.rerunningTenantBootstrapRemainsIdempotent` | PASS | Explicit rerun keeps exactly one tenant row |
| Active tenant context | `EducationSystemApplicationIT.tenantContextReturnsActiveDeploymentTenant` | PASS | Confirms downstream module contract exposes the active persisted tenant metadata |
| Tenant endpoint behavior | `EducationSystemApplicationIT.tenantEndpointReturnsActiveDeploymentTenantMetadata` | PASS | Confirms API returns the active deployment tenant metadata |
| Flyway order/idempotency regression | `EducationSystemApplicationIT.flywayBootstrapMigrationsAreAppliedOnStartup`; `reRunningFlywayDoesNotReapplyExistingMigrations`; `flywayAppliesMigrationsInVersionOrder` | PASS | Confirms tenant migration integrates cleanly with existing migration baseline |
| OpenAPI regression | `EducationSystemApplicationIT.apiDocsContainsTenantEndpoint` | PASS | Confirms the tenant endpoint is exposed through the generated API docs |

## Acceptance criteria coverage

| Criterion | Result | Evidence |
|---|---|---|
| Given a new deployment, when initialized, then a tenant record is created with country code, name, timezone, locale | PASS | `V6__create_platform_tenant.sql`; `TenantBootstrapRunner`; `EducationSystemApplicationIT.tenantBootstrapCreatesSingleActiveDeploymentTenant` |
| Given a tenant, when APIs are called, then all operations are scoped to that tenant | PASS | `TenantContextService` resolves a single server-controlled active deployment tenant; `TenantController` returns only that context; no request-side country switch is implemented; `EducationSystemApplicationIT.tenantContextReturnsActiveDeploymentTenant` and `tenantEndpointReturnsActiveDeploymentTenantMetadata` passed |
| Given tenant configuration, when loaded, then it provides country-specific settings to all modules | PASS | `TenantProperties` binds and validates externalized deployment metadata, `TenantBootstrapRunner` persists/reconciles it, and `TenantContextService` exposes the active tenant metadata for module consumption |

## Notes on limitations

- QA used automated integration coverage plus direct source inspection instead of launching a separate long-running local server because the integration suite already exercises the tenant endpoint, startup bootstrap, Flyway lifecycle, and PostgreSQL persistence against a real ephemeral database.
- The test suite uses a concrete sample tenant (`PL`, `Europe/Warsaw`, `pl-PL`) to prove configuration binding and persistence behavior. QA verified the implementation itself remains generic and country-agnostic because the concrete values come from externalized properties rather than country-specific code branches.

