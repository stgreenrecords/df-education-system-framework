# Solution Design - STORY-012

## Summary

Add Springdoc OpenAPI to the backend platform core so Spring MVC controllers automatically produce an OpenAPI 3.x JSON document and Swagger UI.

## Context

The accepted `STORY-010` scaffold provides a Maven backend reactor with `backend/platform-core` as the Spring Boot application. It currently uses `spring-boot-starter`, not the web starter, and has no REST controller. The backlog and architecture direction require REST plus OpenAPI contracts for API-first client consumption.

Springdoc's official documentation says the library automates OpenAPI documentation generation for Spring Boot applications, supports OpenAPI 3 and Spring Boot 4, and provides Swagger UI through `springdoc-openapi-starter-webmvc-ui`. Its default JSON endpoint is `/v3/api-docs`, and the path can be customized with `springdoc.api-docs.path`.

## Requirements and acceptance criteria

See `df/artifacts/STORY-012/task.md`.

## Proposed solution

1. Add Spring MVC runtime support to `backend/platform-core` with `spring-boot-starter-web`.
2. Add `org.springdoc:springdoc-openapi-starter-webmvc-ui` using a version compatible with the current Spring Boot 4 line.
3. Configure:
   - `springdoc.api-docs.path=/api-docs`
   - `springdoc.swagger-ui.path=/swagger-ui`
4. Add a minimal generic platform status endpoint under `com.darkfactory.education.platform.api`, for example `GET /platform/status`, returning a small DTO such as status and service name.
5. Prefer Java records or simple DTOs so generated schemas are visible in the OpenAPI document.
6. Add integration tests using Spring Boot's web test support to verify:
   - `/api-docs` returns HTTP 200 and a JSON document with an `openapi` property.
   - `/api-docs` includes the generic endpoint path and response schema.
   - `/swagger-ui` or its redirect target is reachable.
7. Keep all behavior generic and framework-level. Do not add country, language, tenant, auth, database, or product-domain behavior.

## Files/components likely affected

- `backend/platform-core/pom.xml`
- `backend/platform-core/src/main/resources/application.yml` or `.properties`
- `backend/platform-core/src/main/java/com/darkfactory/education/platform/api/*`
- `backend/platform-core/src/test/java/com/darkfactory/education/platform/*`
- Optional README note for backend validation commands if Dev finds it helpful.

## Data/API contract changes

- Adds a generic backend documentation endpoint at `/api-docs`.
- Adds Swagger UI at `/swagger-ui`.
- May add one generic sample/status REST endpoint only to prove OpenAPI contract generation.
- No persistence schema changes.

## Security/privacy considerations

- No secrets or personal data are introduced.
- The sample endpoint must not expose environment variables, host details, build metadata, database details, user data, country configuration, or language configuration.
- Authentication is not in scope yet; later security stories must decide whether OpenAPI and Swagger UI remain public, restricted, or profile-gated.

## Test strategy

- Backend integration tests with a random web port or mock web environment.
- Assert status, JSON content type where applicable, OpenAPI version metadata, documented path presence, and Swagger UI reachability.
- Run backend Maven verification.
- Run full parent Maven verification if environment trust-store/network conditions allow.

## Risks and mitigations

| Risk | Mitigation |
|---|---|
| Springdoc version drift with Spring Boot 4 | Use the Springdoc 3.x Spring Boot 4 documentation as the implementation reference and verify through integration tests. |
| Swagger UI endpoint redirects differ from expected path | Configure `springdoc.swagger-ui.path=/swagger-ui` and test either direct 200 or redirect to the concrete UI page. |
| Empty API spec if no controllers exist | Add one generic status endpoint with a DTO solely as a contract-generation proof point. |
| Future security policy may restrict docs endpoints | Document this as a later security decision; do not preempt authentication work in this story. |

## Rollback plan

Revert the Springdoc/web dependencies, application OpenAPI properties, generic status endpoint, and related integration tests. No data rollback is required.

## Open questions

None blocking.

## Implementation lane

- Lane: `backend-dev`
- Subdashboard: `df/runtime/backend-dev-board.md`
- Artifact folder for implementation notes: `df/artifacts/STORY-012/backend/`
