# Decision Record - DECISION-010

- Date: 2026-05-24
- Status: Accepted
- Owner role: SA
- Related task: STORY-011

## Context

The repository now has a Spring Boot backend scaffold (`STORY-010`) and OpenAPI support (`STORY-012`), but no PostgreSQL datasource configuration or migration framework. `STORY-220` and other database-backed stories cannot proceed until a generic persistence foundation exists.

The backlog allows Flyway or Liquibase. Architecture direction already chooses PostgreSQL and requires configuration-driven, container-aware, country-agnostic behavior.

## Decision

Use PostgreSQL as the backend database with Spring Boot JDBC datasource configuration, Hikari-backed connection pooling through Spring Boot defaults, and Flyway as the migration framework in `backend/platform-core`.

Additional implementation constraints:

- datasource values must be environment/configuration driven
- migrations must be forward-only and generic
- baseline migrations must remain framework-level and must not introduce country-specific or language-specific schema behavior
- automated verification should use an isolated PostgreSQL runtime rather than require a manually prepared shared database

## Consequences

- Backend stories depending on PostgreSQL and migrations can now target one clear foundation.
- `STORY-220` has a concrete unblocking path once backend-dev completes `STORY-011`.
- Root/shared Maven build files may be touched, so the task must remain serialized while those edits happen.
- Liquibase is deferred unless a later requirement justifies switching.

## Alternatives considered

- Liquibase: viable but not chosen because Flyway is the smaller initial path for this foundation story.
- Manual migration scripts/runbooks outside application startup: rejected because they do not satisfy the automatic migration acceptance criteria.
- Hardcoded local database configuration: rejected because it is insecure, non-portable, and conflicts with sovereign/container-aware deployment needs.

## Evidence

- `df/backlog/user-stories.md` (`STORY-011`)
- `df/backlog/architecture-direction.md`
- `df/runtime/risks.md` (`RISK-010`, `BLOCKER-014`, `RISK-015`)
- `df/artifacts/STORY-010/decision-003-spring-boot-foundation-build.md`
- `df/artifacts/STORY-011/solution-design.md`

## Follow-up actions

- Backend-dev implements datasource, Flyway, baseline migrations, and automated integration tests.
- QA verifies connectivity, migration idempotency, migration ordering, and generic behavior.
- `STORY-220` remains blocked until this foundation is delivered.

