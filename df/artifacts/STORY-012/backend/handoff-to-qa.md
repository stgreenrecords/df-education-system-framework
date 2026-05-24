# Backend Handoff to QA - STORY-012

## Task

STORY-012 — Implement OpenAPI contract generation in `backend/platform-core`

## From state

`DEV_IN_PROGRESS`

## To state

`READY_FOR_QA`

## Lane

`backend-dev`

## Summary

Implementation is complete. Springdoc OpenAPI 3.0.3 (Spring Boot 4.x compatible) is integrated into `backend/platform-core`. `/api-docs` returns a valid OpenAPI 3.x JSON document. `/swagger-ui` is reachable. A generic `GET /platform/status` endpoint demonstrates contract generation with request/response schema. Five integration tests pass. Both the backend-only and full parent Maven builds succeed on Spring Boot 4.1.0-SNAPSHOT / Java 25.

Root `pom.xml` and Maven wrapper were updated to match the project's Spring Boot 4.1.0-SNAPSHOT + Java 25 + Maven 3.9.15 baseline (derived from `spring-demo/pom.xml`).

## Files changed

| File | Change |
|---|---|
| `pom.xml` | Spring Boot 4.0.6 → 4.1.0-SNAPSHOT; Java 21 → 25; `springdoc.version=3.0.3`; Spring Snapshots repo added |
| `.mvn/wrapper/maven-wrapper.properties` | Maven 3.9.14 → 3.9.15 |
| `backend/platform-core/pom.xml` | `spring-boot-starter` → `spring-boot-starter-web`; springdoc 3.0.3 added |
| `backend/platform-core/src/main/resources/application.properties` | Created; `/api-docs` and `/swagger-ui` paths configured |
| `backend/platform-core/src/main/java/.../api/PlatformStatusResponse.java` | Created; Java record |
| `backend/platform-core/src/main/java/.../api/PlatformStatusController.java` | Created; `GET /platform/status` |
| `backend/platform-core/src/test/java/.../EducationSystemApplicationIT.java` | Updated; 5 integration tests |

## Test evidence

### Backend build

```
Command: .\mvnw.cmd -f backend/pom.xml clean verify
Tests run: 5, Failures: 0, Errors: 0, Skipped: 0
Result: BUILD SUCCESS
Timestamp: 2026-05-24T18:09:02+02:00
```

### Full parent build

```
Command: .\mvnw.cmd clean verify
All modules: SUCCESS
Result: BUILD SUCCESS
Timestamp: 2026-05-24T18:09:14+02:00
```

## Acceptance criteria evidence

| AC | Evidence |
|---|---|
| 1. Endpoint in OpenAPI spec | `apiDocsContainsPlatformStatusEndpoint` PASS — `/platform/status` in JSON |
| 2. `/api-docs` returns valid OpenAPI | `apiDocsEndpointReturnsOk` + `apiDocsContainsOpenApiVersion` PASS |
| 3. `/swagger-ui` browsable | `swaggerUiIsReachable` PASS — 2xx/3xx confirmed |
| 4. Integration tests pass | All 5 tests PASS |
| 5. No country/language-specific behavior | Inspection PASS — generic endpoint only |

## Known risks for QA

- Mockito self-attach warning is non-blocking JVM noise (all tests pass despite it)
- `/swagger-ui` redirects to internal Springdoc page rather than responding 200 directly — this is the expected Springdoc behavior; the test accepts 2xx or 3xx
- Springdoc INFO warnings about endpoints being enabled in production are informational only; security exposure policy is a future story

## QA focus areas

1. Verify all 5 acceptance criteria against the build output
2. Inspect generated OpenAPI JSON for `openapi` metadata and `/platform/status` path + `PlatformStatusResponse` schema
3. Verify no country/tenant/i18n/auth/database behavior was introduced
4. Confirm root `pom.xml` changes (Spring Boot + Java version + repo) do not break any other module
5. Confirm `.mvn/wrapper/maven-wrapper.properties` is consistent with spring-demo reference

## Blockers

None.

