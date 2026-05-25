# Backend Handoff to QA - STORY-011

## Task

STORY-011 — Implement PostgreSQL database configuration and migration framework in `backend/platform-core`

## From state

`DEV_IN_PROGRESS`

## To state

`READY_FOR_QA`

## Lane

`backend-dev`

## Summary

Implementation is complete. `backend/platform-core` now has a generic PostgreSQL datasource foundation, Hikari-backed pooling, forward-only Flyway migrations, and Docker/Testcontainers-backed PostgreSQL integration tests. Two generic baseline migrations prove automatic-on-start migration behavior, idempotency, and ordering without introducing domain-specific schema.

Existing OpenAPI/Swagger integration coverage continues to pass under the database-enabled application context.

## Files changed

| File | Change |
|---|---|
| `backend/platform-core/pom.xml` | Added JDBC, Flyway, PostgreSQL, and Testcontainers dependencies |
| `backend/platform-core/src/main/resources/application.properties` | Added environment-driven datasource, Hikari, and Flyway properties |
| `backend/platform-core/src/main/resources/db/migration/V1__create_platform_bootstrap_marker.sql` | Created baseline schema migration |
| `backend/platform-core/src/main/resources/db/migration/V2__seed_platform_bootstrap_marker.sql` | Created ordered follow-up seed migration |
| `backend/platform-core/src/main/java/com/darkfactory/education/platform/config/FlywayConfiguration.java` | Added explicit startup Flyway bean |
| `backend/platform-core/src/test/java/com/darkfactory/education/platform/EducationSystemApplicationIT.java` | Expanded to 8 PostgreSQL-backed integration tests |
| `df/artifacts/STORY-011/backend/dev-notes.md` | Added backend implementation evidence |

## Test evidence

### Backend build

```text
Command: .\mvnw.cmd -f backend/pom.xml clean verify
Result: BUILD SUCCESS
Tests run: 8, Failures: 0, Errors: 0, Skipped: 0
Timestamp: 2026-05-24T18:33:04+02:00
```

### Full parent build

```text
Command: .\mvnw.cmd clean verify
Result: BUILD SUCCESS
Tests run: 8, Failures: 0, Errors: 0, Skipped: 0
Timestamp: 2026-05-24T18:33:19+02:00
```

## Acceptance criteria evidence

| AC | Evidence |
|---|---|
| 1. PostgreSQL connection established | `contextStartsAndConnectsToPostgres()` validates `DataSource` connection and `select 1` against a real PostgreSQL container |
| 2. Migration script applied automatically | `flywayBootstrapMigrationsAreAppliedOnStartup()` verifies the marker table/row and current version `2` after app startup |
| 3. Migration not re-applied on restart | `reRunningFlywayDoesNotReapplyExistingMigrations()` verifies `migrationsExecuted == 0` and unchanged migration history count |
| 4. New migrations run in order | `flywayAppliesMigrationsInVersionOrder()` verifies versions `1`, `2` in order |
| 5. No country/language-specific behavior | Code/config inspection shows only generic datasource and migration behavior; no country/language-specific schema or API branch was added |

## Known risks for QA

- Docker/Testcontainers availability is part of the chosen automated verification path; Docker was available locally during dev verification.
- Springdoc warnings about docs endpoints being enabled remain non-blocking and pre-existing/future-scope.
- IDE SQL warnings on migration files are only editor-assistance warnings caused by no configured IDE datasource; Maven verification passed.

## QA focus areas

1. Re-run backend and full-parent builds
2. Confirm PostgreSQL Testcontainers startup succeeds in QA environment
3. Verify Flyway startup migration, idempotency, and ordering evidence
4. Confirm no country-specific or language-specific behavior/schema was introduced
5. Confirm existing OpenAPI/Swagger coverage still passes with the new DB foundation

## Blockers

None.

