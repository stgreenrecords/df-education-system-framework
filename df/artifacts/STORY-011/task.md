# Task - STORY-011

## Summary

Implement PostgreSQL database configuration, connection pooling, and a forward-only migration framework for the Spring Boot backend foundation.

## Business goal

Provide the generic persistence substrate required for all later database-backed backend stories, including the blocked translation-storage work in `STORY-220`, without introducing country-specific or language-specific behavior.

## Source

- Backlog: `df/backlog/user-stories.md`
- Epic: `EPIC-01`
- Dependency: `STORY-010` accepted on 2026-05-23
- Blocker linkage: `STORY-220` currently depends on this story

## Refinement

Refinement skipped. The backlog story already defines explicit, testable acceptance criteria, and the remaining choices are technical architecture decisions appropriate for SA.

## Acceptance criteria

1. Given the application starts, when connecting to PostgreSQL, then the connection is established successfully.
2. Given a migration script exists, when the application starts, then the migration is applied automatically.
3. Given a migration has already been applied, when the application restarts, then it is not re-applied.
4. Given migration versioning, when a new migration is added, then it runs in order.
5. Given the implementation is inspected, then no country-specific or language-specific behavior, schema fork, or API contract is introduced.

## Scope

In scope:

- Backend datasource configuration for PostgreSQL in `backend/platform-core`
- Connection pooling through Spring Boot's JDBC stack
- Forward-only migration framework selection and baseline generic migrations
- Environment-driven configuration suitable for local, CI, and container-aware workflows
- Automated backend integration tests proving connectivity and migration behavior

Out of scope:

- Domain-specific tables beyond the minimal generic bootstrap migration proof point
- Production infrastructure provisioning of PostgreSQL instances
- Authentication, authorization, tenant scoping, or business-domain persistence models
- Country template data, language-specific behavior, or any country-specific schema variant

## Assumptions

- PostgreSQL remains the primary application database per `df/backlog/architecture-direction.md`.
- Flyway is the preferred migration framework for MVP because it aligns with existing architecture/risk notes and provides the smallest viable Spring Boot integration path.
- Automated verification should use an isolated PostgreSQL runtime (for example, Testcontainers with a compatible local container runtime) rather than require a manually prepared shared database.
- Datasource credentials and host details must come from environment/configuration, not from committed secrets.
- A minimal generic bootstrap migration is acceptable for proving migration ordering before domain schemas exist.

## Dependencies

- `STORY-010` - Initialize Spring Boot project with modular structure.
- Root/shared build files may be touched in a serialized backend-only task; no parallel lane work is currently active.

## Validation approach

- Run `./mvnw.cmd -f backend/pom.xml clean verify`.
- Run `./mvnw.cmd clean verify` when feasible to ensure the full parent build still passes.
- Start the application against an isolated PostgreSQL instance and verify datasource connectivity.
- Verify baseline migrations are applied on first start, not re-applied on restart, and tracked in migration history in order.
- Inspect changed files to confirm the design remains generic and framework-level.

## Runtime history

| Timestamp | Role | State change | Notes |
|---|---|---|---|
| 2026-05-24 18:21 local | sa | OPEN -> NEEDS_ARCHITECTURE -> ARCHITECTURE_IN_PROGRESS -> READY_FOR_DEV | Promoted the missing PostgreSQL/migration foundation dependency from backlog into runtime, selected a generic PostgreSQL + Flyway architecture, and routed implementation to `backend-dev`. |
| 2026-05-24 18:39 local | qa | READY_FOR_QA -> QA_IN_PROGRESS -> READY_FOR_PO | Re-ran backend and full parent Maven verification, confirmed PostgreSQL Testcontainers startup, Flyway first-run/idempotency/order behavior, preserved OpenAPI regression coverage, and no country/language-specific leakage. |
| 2026-05-24 18:54 local | po | READY_FOR_PO -> PO_REVIEW -> DONE | Reviewed QA evidence, ran the application against an isolated PostgreSQL container, validated API endpoints and live migration results, and accepted the story as sufficient to unblock downstream persistence work. |

