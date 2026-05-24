# Solution Design - STORY-011

## Summary

Add a generic PostgreSQL persistence foundation to `backend/platform-core` using Spring Boot JDBC datasource configuration, Hikari-backed connection pooling, and Flyway-managed forward-only migrations.

## Context

`STORY-010` established the Maven multi-module Spring Boot scaffold and `STORY-012` added web/OpenAPI capabilities in `backend/platform-core`, but the repository still has no datasource configuration, PostgreSQL driver, migration framework, or migration scripts. `STORY-220` is blocked specifically because that substrate is missing.

Architecture direction already selects PostgreSQL as the primary database, requires configuration-driven behavior, and explicitly states that `STORY-011` should make database configuration container-aware as part of the Phase 1 foundation.

## Requirements and acceptance criteria

See `df/artifacts/STORY-011/task.md`.

## Proposed solution

1. Add JDBC/database foundation dependencies to `backend/platform-core`:
   - `spring-boot-starter-jdbc`
   - PostgreSQL JDBC driver
   - Flyway core support and the PostgreSQL Flyway database support artifact if required by the resolved Flyway version
2. Keep the Spring Boot application entry point in `backend/platform-core`; do not create a country-specific or module-specific database application.
3. Configure datasource properties through environment-backed Spring configuration only, for example:
   - `spring.datasource.url`
   - `spring.datasource.username`
   - `spring.datasource.password`
   - `spring.datasource.hikari.*` for generic pool sizing/timeouts
   - `spring.flyway.enabled=true`
   - `spring.flyway.locations=classpath:db/migration`
4. Use generic defaults only where safe for local development (for example host/db name placeholders), but never commit real credentials or country-specific values.
5. Use Flyway for forward-only migrations. Place migrations under `backend/platform-core/src/main/resources/db/migration/`.
6. Add a minimal generic bootstrap migration set to prove the foundation without pre-empting domain stories. Recommended example:
   - `V1__create_platform_bootstrap_marker.sql` creates a generic `platform_bootstrap_marker` table
   - `V2__seed_platform_bootstrap_marker.sql` inserts or records a generic baseline marker
   These two migrations provide a concrete ordered-migration proof point while remaining framework-generic.
7. Prefer keeping the schema generic and minimal for now. Do not introduce country-specific schemas, tenant-specific schema forks, or domain tables that belong to later stories.
8. Add automated integration tests that boot the application against an isolated PostgreSQL runtime and verify:
   - datasource connectivity succeeds
   - migrations are applied automatically on first start
   - already-applied migrations are not re-applied on restart
   - multiple migrations are recorded/applied in version order
9. Prefer an isolated automated PostgreSQL runtime for tests (for example, Testcontainers with a compatible local container runtime) so QA/dev do not depend on a manually prepared shared database.
10. Preserve container awareness and cloud neutrality:
   - datasource values come from environment/configuration
   - no Docker-daemon-only assumptions are embedded in application code or docs
   - no secrets are hardcoded in source, test logs, or markdown
11. Because root `pom.xml` is a shared file, this task is intentionally serialized through the backend lane now; no parallel lane task is currently active that would conflict with the required build-file edits.

## Alternatives considered

- Liquibase instead of Flyway: viable, but rejected for this story because Flyway is the smallest viable migration path and already aligns with the current risk/architecture notes.
- Manual SQL runbooks without application-integrated migration: rejected because it would not satisfy automatic-on-start migration behavior.
- Requiring a manually managed PostgreSQL instance for tests: rejected because it creates brittle, non-reproducible verification.

## Files/components likely affected

- `pom.xml`
- `backend/platform-core/pom.xml`
- `backend/platform-core/src/main/resources/application.properties`
- `backend/platform-core/src/main/resources/db/migration/*`
- `backend/platform-core/src/test/java/com/darkfactory/education/platform/*`
- Optional test support/configuration files for isolated PostgreSQL integration testing

## Data/API contract changes

- Adds generic datasource and migration configuration to the backend application.
- Adds generic baseline PostgreSQL schema objects required only to prove the migration foundation.
- Adds Flyway migration history tracking in the application database.
- No public REST/API contract changes are required by this story.

## Security/privacy considerations

- Do not commit real credentials, connection strings with secrets, or country-specific database details.
- Use environment-backed configuration for all sensitive values.
- Test evidence must avoid printing secrets.
- The bootstrap migration must not seed personal data, country data, or language-specific data.

## Test strategy

Backend-dev should add automated tests for:

- application startup with a reachable PostgreSQL instance
- first-start migration application
- restart idempotency (previous migrations are not re-applied)
- ordered execution of at least two migrations
- full backend Maven verification and full parent Maven verification
- source inspection/assertions proving no country-specific or language-specific configuration/schema branching was introduced

## Risks and mitigations

| Risk | Mitigation |
|---|---|
| Flyway/PostgreSQL compatibility may drift with the Spring Boot 4 baseline | Use Spring Boot-managed versions where possible and prove compatibility with automated integration tests. |
| Local machines may not have a ready PostgreSQL instance | Use an isolated automated PostgreSQL test runtime and document any container-runtime requirement clearly. |
| Shared root `pom.xml` can conflict with parallel lane work | This task is serialized now; SA has routed it as the only active delivery task touching the shared build files. |
| Deferred schema-per-module vs schema-per-tenant decision could affect later persistence work | Keep `STORY-011` generic and minimal; defer advanced schema partitioning to later architecture when domain tables arrive. |

## Rollback plan

- Revert the datasource/Flyway dependency and configuration changes if the foundation fails before production data exists.
- Remove the generic bootstrap migrations only before any shared environment persists them.
- If migrations have already been applied in a shared environment, use a forward corrective migration instead of deleting migration history.

## Open questions

- None blocking backend-dev. Advanced schema isolation remains a later architecture decision and should not block this minimal database foundation.

## Implementation lane

- Lane: `backend-dev`
- Subdashboard: `df/runtime/backend-dev-board.md`
- Artifact folder for implementation notes: `df/artifacts/STORY-011/backend/`

