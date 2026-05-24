# Backend Dev Notes - STORY-011

## Task

Implement PostgreSQL database configuration, connection pooling, and a forward-only migration framework for the Spring Boot backend foundation.

## Implementation summary

Implemented a generic PostgreSQL + Flyway persistence foundation in `backend/platform-core`.

### What changed

| File | Change | Notes |
|---|---|---|
| `backend/platform-core/pom.xml` | Updated | Added `spring-boot-starter-jdbc`, `flyway-core`, `flyway-database-postgresql`, PostgreSQL JDBC driver, and Testcontainers test dependencies |
| `backend/platform-core/src/main/resources/application.properties` | Updated | Added environment-driven datasource, Hikari pool, and Flyway settings while preserving OpenAPI settings |
| `backend/platform-core/src/main/resources/db/migration/V1__create_platform_bootstrap_marker.sql` | Created | Generic baseline migration creating `platform_bootstrap_marker` |
| `backend/platform-core/src/main/resources/db/migration/V2__seed_platform_bootstrap_marker.sql` | Created | Ordered follow-up migration inserting a generic baseline marker |
| `backend/platform-core/src/main/java/com/darkfactory/education/platform/config/FlywayConfiguration.java` | Created | Added explicit Flyway bean with startup `migrate()` behavior because the current Spring Boot 4 setup did not auto-create a Flyway bean in this module |
| `backend/platform-core/src/test/java/com/darkfactory/education/platform/EducationSystemApplicationIT.java` | Updated | Converted to PostgreSQL Testcontainers-backed integration coverage; now verifies connectivity, startup migrations, idempotency, ordering, and preserved OpenAPI/Swagger behavior |

## Important implementation details

### 1. Environment-driven datasource configuration

Datasource values are configured through properties backed by environment variables:

- `EDU_DB_URL`
- `EDU_DB_USERNAME`
- `EDU_DB_PASSWORD`
- `EDU_DB_POOL_MAX_SIZE`
- `EDU_DB_POOL_MIN_IDLE`
- `EDU_DB_POOL_CONNECTION_TIMEOUT_MS`
- `EDU_FLYWAY_ENABLED`

No secrets or country-specific values were committed.

### 2. Migration foundation

Added two generic, ordered Flyway migrations:

- `V1__create_platform_bootstrap_marker.sql`
- `V2__seed_platform_bootstrap_marker.sql`

These prove:

- automatic migration on startup
- forward-only ordered migration behavior
- restart idempotency

without pre-implementing domain tables that belong to later stories.

### 3. Integration test strategy

Used Docker-backed Testcontainers with `postgres:17-alpine` so tests verify a real PostgreSQL runtime instead of a simulated/in-memory substitute.

The integration test class now proves:

1. Spring context starts and connects to PostgreSQL
2. baseline Flyway migrations are applied on startup
3. rerunning Flyway does not reapply existing migrations
4. migrations are recorded in version order (`1`, `2`)
5. existing `/api-docs` and `/swagger-ui` behavior still works

## Debugging / adjustments made during implementation

### Adjustment A: Testcontainers versions were not managed

The first Maven verify failed because `org.testcontainers:junit-jupiter` and `org.testcontainers:postgresql` were not version-managed by the current dependency setup.

Resolution:
- pinned both test dependencies explicitly to `1.20.6`

### Adjustment B: No Flyway bean in the current Spring Boot 4 module setup

The second Maven verify failed because the test could not autowire `Flyway`.

Resolution:
- added `FlywayConfiguration` with a startup `Flyway` bean (`initMethod = "migrate"`)
- kept the configuration generic and controlled by `spring.flyway.enabled`

## Validation commands and results

### Backend-only verification

```text
Command: .\mvnw.cmd -f backend/pom.xml clean verify
Result: BUILD SUCCESS
Tests run: 8, Failures: 0, Errors: 0, Skipped: 0
Finished at: 2026-05-24T18:33:04+02:00
```

### Full parent verification

```text
Command: .\mvnw.cmd clean verify
Result: BUILD SUCCESS
Tests run: 8, Failures: 0, Errors: 0, Skipped: 0
Finished at: 2026-05-24T18:33:19+02:00
```

## Acceptance criteria status

| AC | Status | Evidence |
|---|---|---|
| 1. Application connects to PostgreSQL | PASS | `contextStartsAndConnectsToPostgres()` uses Testcontainers PostgreSQL + `DataSource` connection validation + `select 1` |
| 2. Migration script applies automatically on startup | PASS | `flywayBootstrapMigrationsAreAppliedOnStartup()` verifies startup-created table/row and current version `2` |
| 3. Applied migration is not re-applied on restart | PASS | `reRunningFlywayDoesNotReapplyExistingMigrations()` verifies `migrationsExecuted == 0` and unchanged history count |
| 4. New migration runs in order | PASS | `flywayAppliesMigrationsInVersionOrder()` verifies applied versions `1`, `2` in order |
| 5. No country/language-specific behavior introduced | PASS | Implementation uses generic datasource/migration config only; baseline schema is framework-generic; no country/language-specific code or schema branches added |

## Notes for QA

- Docker was available locally and Testcontainers successfully started PostgreSQL during verification.
- Springdoc warnings about `/api-docs` and `/swagger-ui` being enabled remain non-blocking and pre-existing/future-scope.
- JetBrains reports SQL assistance warnings for the migration files only because no IDE datasource is configured; these are editor warnings, not build/test failures.

