# Task - STORY-012

## Summary

Configure automatic OpenAPI 3.x specification generation from Spring Boot REST controllers.

## Business goal

Expose machine-readable and browsable API documentation so website, mobile, AI, desktop, and third-party clients can consume the same backend contracts.

## Source

- Backlog: `df/backlog/user-stories.md`
- Epic: `EPIC-01`
- Dependency: `STORY-010` accepted on 2026-05-23.

## Refinement

Refinement skipped. The backlog story already defines explicit, testable acceptance criteria, and the remaining decisions are technical architecture decisions.

## Acceptance criteria

1. Given any REST endpoint, when the OpenAPI spec is generated, then the endpoint appears with request and response schemas.
2. Given the running application, when accessing `/api-docs`, then a valid OpenAPI JSON document is returned.
3. Given Swagger UI is enabled, when accessing `/swagger-ui`, then API documentation is browsable.
4. Given the backend build is run, when integration tests execute, then they verify the OpenAPI JSON endpoint and Swagger UI route are available.
5. Given the implementation is inspected, then no country-specific or language-specific behavior, schema, or API contract is introduced.

## Scope

In scope:

- Backend Spring MVC/OpenAPI generation in `backend/platform-core`.
- Maven dependency and configuration needed for OpenAPI generation and Swagger UI.
- A minimal generic REST endpoint if needed to prove documentation generation while no domain endpoints exist yet.
- Backend integration tests for OpenAPI and Swagger UI availability.

Out of scope:

- Frontend API client generation.
- Database, migrations, authentication, authorization, or tenant-specific API documentation.
- Country template or language-specific API behavior.

## Assumptions

- Springdoc OpenAPI is the preferred Spring Boot integration because it generates OpenAPI from Spring controllers at runtime and supports Spring Boot 4.
- `/api-docs` is a project-specific alias for springdoc's default `/v3/api-docs` path and should be configured rather than implemented through a custom controller.
- `/swagger-ui` should route to the Swagger UI browser experience; redirecting to the concrete UI page is acceptable if Springdoc uses an internal page path.

## Dependencies

- `STORY-010` complete.

## Validation approach

- Run `.\mvnw.cmd -f backend/pom.xml clean verify`.
- Run `.\mvnw.cmd clean verify` when feasible to ensure the full parent build still passes.
- Integration-test the running Spring context with HTTP requests against `/api-docs` and `/swagger-ui`.
- Inspect generated OpenAPI JSON for `openapi` metadata and at least one generic endpoint path/schema.

## Runtime history

| Timestamp | Role | State change | Notes |
|---|---|---|---|
| 2026-05-23 12:05 local | sa | OPEN -> NEEDS_ARCHITECTURE -> ARCHITECTURE_IN_PROGRESS -> READY_FOR_DEV | Promoted from backlog, skipped refinement, completed architecture, and routed to `backend-dev`. |
| 2026-05-24 local | backend-dev | READY_FOR_DEV -> DEV_IN_PROGRESS | Started implementation. Discovered Spring Boot 4.1.0-SNAPSHOT + Java 25 baseline from spring-demo reference. Updated root pom.xml and wrapper, added springdoc 3.0.3 (Spring Boot 4.x compatible), web starter, status endpoint, integration tests. |
| 2026-05-24 local | backend-dev | DEV_IN_PROGRESS -> READY_FOR_QA | All 5 integration tests pass. Backend build and full parent build succeed. Handoff written to `df/artifacts/STORY-012/backend/handoff-to-qa.md`. |
