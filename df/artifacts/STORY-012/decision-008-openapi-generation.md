# Decision Record - DECISION-008

- Date: 2026-05-23
- Status: Accepted
- Owner role: SA
- Related task: STORY-012

## Context

The platform is API-first and needs OpenAPI 3.x contracts generated from Spring Boot controllers. The accepted backend scaffold uses Spring Boot 4 and Maven.

## Decision

Use Springdoc OpenAPI's Spring MVC Swagger UI starter in `backend/platform-core`, configure the JSON spec path as `/api-docs`, configure Swagger UI at `/swagger-ui`, and validate the generated contract through backend integration tests.

## Consequences

- Backend controllers become the source of generated OpenAPI documentation.
- The website and future mobile/API-client stories can depend on a live OpenAPI contract source after QA/PO acceptance.
- Implementation remains backend-only and does not introduce frontend generation, database changes, or country/language-specific behavior.

## Alternatives considered

- Hand-written OpenAPI YAML: rejected because it can drift from controllers and does not satisfy automatic generation from Spring controllers.
- Springdoc API-only starter without Swagger UI: rejected because the story explicitly requires browsable Swagger UI.
- OpenAPI generator plugins as the first step: deferred because runtime endpoint generation and UI availability are the immediate acceptance criteria.

## Evidence

- Backlog story: `df/backlog/user-stories.md`
- Current backend scaffold: `backend/platform-core/pom.xml`
- Springdoc official documentation: https://springdoc.org/v4/

## Follow-up actions

- `backend-dev` implements and validates the OpenAPI generation.
- Future security architecture should decide whether documentation endpoints are public, authenticated, or profile-gated.
