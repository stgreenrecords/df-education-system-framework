# Backend Notes - STORY-021

## Task

Implement the first sovereign tenant/deployment configuration foundation in `backend/platform-core`.

## Backend start note

Implementation started on 2026-05-24 20:30 local after reviewing the task artifact, solution design, SA handoff, backend runtime subdashboard, repository status, and the existing Spring Boot/PostgreSQL/Flyway platform-core foundation.

## Planned scope

- add a persisted tenant/deployment metadata table via Flyway
- add deployment tenant configuration properties and bootstrap logic
- add a reusable backend tenant context abstraction/service
- add a minimal backend endpoint for the active tenant metadata
- add backend tests for bootstrap, endpoint behavior, and migration/version expectations

## Constraints

- backend-only scope in `backend/platform-core`
- no centralized multi-country runtime routing
- no frontend, DevOps, or data-engineering file edits for implementation scope
- keep the model generic and country-agnostic

## Implementation summary

Implemented the first sovereign deployment tenant slice in `backend/platform-core`.

### What changed

| File/path | Change | Notes |
|---|---|---|
| `backend/platform-core/src/main/java/com/darkfactory/education/platform/tenant/TenantProperties.java` | Created | Added strongly typed deployment-tenant bootstrap properties with normalization/validation for country code, timezone, and locale |
| `backend/platform-core/src/main/java/com/darkfactory/education/platform/tenant/TenantBootstrapRunner.java` | Created | Added idempotent startup bootstrap logic that creates or reconciles the active deployment tenant record |
| `backend/platform-core/src/main/java/com/darkfactory/education/platform/tenant/TenantRepository.java` | Created | Added JDBC-backed persistence access for the single active deployment tenant record |
| `backend/platform-core/src/main/java/com/darkfactory/education/platform/tenant/TenantContextService.java` | Created | Added reusable backend tenant context abstraction with cached active-tenant resolution |
| `backend/platform-core/src/main/java/com/darkfactory/education/platform/tenant/TenantController.java` | Created | Added `GET /api/v1/platform/tenant` endpoint for active tenant metadata |
| `backend/platform-core/src/main/java/com/darkfactory/education/platform/tenant/*.java` | Created | Added tenant records/context/response/configuration support types |
| `backend/platform-core/src/main/resources/db/migration/V6__create_platform_tenant.sql` | Created | Added Flyway migration for the persisted sovereign deployment tenant table |
| `backend/platform-core/src/main/resources/application.properties` | Updated | Added externalized tenant bootstrap properties using `EDU_TENANT_*` environment variables with generic safe defaults |
| `backend/platform-core/src/test/java/com/darkfactory/education/platform/EducationSystemApplicationIT.java` | Updated | Added integration tests for tenant bootstrap, context resolution, endpoint behavior, OpenAPI exposure, and Flyway version expectations |
| `backend/platform-core/src/test/java/com/darkfactory/education/platform/tenant/TenantPropertiesTest.java` | Created | Added unit tests for tenant property normalization and invalid timezone/locale handling |

## Important implementation details

### 1. Single active deployment tenant model

The backend implementation deliberately treats the sovereign deployment as one active runtime tenant:

- one persisted row in `platform_tenant`
- no request-side country switching
- no centralized SaaS tenant router
- one shared `TenantContextService` for current and later modules

### 2. Idempotent startup bootstrap

`TenantBootstrapRunner` uses deployment configuration properties to ensure the tenant record exists on startup:

- creates the row when the deployment starts against a fresh database
- reuses the row when values already match
- updates non-identity metadata (`display_name`, timezone, locale) when the same tenant is reconfigured
- fails fast if the persisted tenant country code does not match the configured sovereign deployment

### 3. Generic externalized configuration

Tenant bootstrap metadata is externalized through runtime properties:

- `EDU_TENANT_COUNTRY_CODE`
- `EDU_TENANT_DISPLAY_NAME`
- `EDU_TENANT_TIMEZONE`
- `EDU_TENANT_LOCALE`

This keeps deployment identity configurable without introducing country-specific code paths.

### 4. Backend contract for later modules

The story introduces two reusable backend contracts:

- `TenantContextService` for runtime tenant resolution inside backend modules
- `GET /api/v1/platform/tenant` for validation/operator-facing metadata inspection

This is the intended foundation for later configuration, organization, security, and audit work.

## Validation commands and results

### Backend reactor verify

```text
Command: .\mvnw.cmd -f backend\pom.xml -pl platform-core -am clean verify
Result: BUILD SUCCESS
Tests run: 23 total
- Unit tests: 3
- Integration tests: 20
Finished at: 2026-05-24T20:39:29+02:00
```

### Full parent verify

```text
Command: .\mvnw.cmd clean verify
Result: BUILD SUCCESS
Tests run: 23 total
- Unit tests: 3
- Integration tests: 20
Finished at: 2026-05-24T20:39:47+02:00
```

### Focused repository status snapshot

```text
Command: git --no-pager status --short --branch -- backend\platform-core df\artifacts\STORY-021 df\runtime
Result: PASS
Observed branch: ## master...origin/master
Notes: The workspace contains unrelated pre-existing changes from earlier accepted stories; `STORY-021` implementation itself is scoped to `backend/platform-core` plus task/runtime documentation.
```

## Acceptance criteria status

| AC | Status | Evidence |
|---|---|---|
| 1. Given a new deployment, when initialized, then a tenant record is created with country code, name, timezone, locale | PASS | `V6__create_platform_tenant.sql`; `TenantBootstrapRunner`; integration test `tenantBootstrapCreatesSingleActiveDeploymentTenant` |
| 2. Given a tenant, when APIs are called, then all operations are scoped to that tenant | PASS | `TenantContextService` provides one server-controlled active deployment tenant for the runtime; `TenantController` exposes that scoped context; integration tests `tenantContextReturnsActiveDeploymentTenant` and `tenantEndpointReturnsActiveDeploymentTenantMetadata` verify the resolved tenant |
| 3. Given tenant configuration, when loaded, then it provides country-specific settings to all modules | PASS | `TenantProperties` binds deployment config, normalizes/validates it, and `TenantContextService` exposes the configured/persisted country code, name, timezone, and locale for module consumption |

## Environment notes

- OS: Windows
- Java: 25.0.2
- Maven Wrapper: 3.9.15
- Test database runtime: Testcontainers + `postgres:17-alpine`
- Local Docker runtime available for Testcontainers-backed integration tests

## Risks and limitations

- `RISK-010` remains open: migration robustness is increasingly important as new platform tables are added.
- `RISK-019` remains relevant: later backend stories may touch shared `platform-core` runtime and persistence files.
- The broader schema isolation decision remains deferred; this story intentionally establishes the deployment-tenant baseline without expanding to schema-per-tenant or request-selectable multitenancy.

