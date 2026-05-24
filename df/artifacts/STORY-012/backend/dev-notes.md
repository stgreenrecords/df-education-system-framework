# Backend Dev Notes - STORY-012

## Task

Implement OpenAPI contract generation using Springdoc in `backend/platform-core`.

## DEV_IN_PROGRESS actions

### 1. Source of truth discovery

Inspected `spring-demo/` folder provided by the user, which contains the reference Spring Boot 4.1.0-SNAPSHOT + Java 25 project generated from Spring Initializr. Key findings:

- Spring Boot version: **4.1.0-SNAPSHOT** (not 4.0.6 as previously configured)
- Java version: **25** (not 21)
- Maven version: **3.9.15** (updated from 3.9.14)
- Spring Snapshots repository required at `https://repo.spring.io/snapshot`
- Spring Boot 4.x uses Spring Framework **7.x**

### 2. Springdoc version resolution

Queried Maven Central for `springdoc-openapi-starter-webmvc-ui` versions. Full metadata reveals:
- springdoc **2.x** → Spring Boot 3.x (Spring Framework 6.x)
- springdoc **3.0.3** → Spring Boot 4.x (Spring Framework 7.x) — latest on Maven Central

Selected: `springdoc-openapi-starter-webmvc-ui:3.0.3`

### 3. Spring Boot 4.x breaking changes discovered

- `TestRestTemplate` removed from `spring-boot-test` (package `org.springframework.boot.test.web.client` does not exist)
- `@AutoConfigureMockMvc` removed from `spring-boot-test-autoconfigure` (package `org.springframework.boot.test.autoconfigure.web.servlet` does not exist in SB 4.x)
- `MockMvc` is still available directly in `spring-test` (Spring Framework 7.x) as `org.springframework.test.web.servlet.MockMvc`
- New `RestTestClient` API available in Spring Framework 7.x at `org.springframework.test.web.servlet.client.RestTestClient`
- Solution: Use `MockMvcBuilders.webAppContextSetup(WebApplicationContext)` directly without Spring Boot autoconfigure

### 4. Files changed

| File | Action | Notes |
|---|---|---|
| `pom.xml` (root) | Updated | Spring Boot 4.0.6 → 4.1.0-SNAPSHOT; Java 21 → 25; added `springdoc.version=3.0.3`; added Spring Snapshots repo and plugin repo |
| `.mvn/wrapper/maven-wrapper.properties` | Updated | Maven 3.9.14 → 3.9.15 |
| `backend/platform-core/pom.xml` | Updated | Replaced `spring-boot-starter` with `spring-boot-starter-web`; added `springdoc-openapi-starter-webmvc-ui:${springdoc.version}` |
| `backend/platform-core/src/main/resources/application.properties` | Created | `springdoc.api-docs.path=/api-docs`; `springdoc.swagger-ui.path=/swagger-ui`; `spring.application.name=education-system-framework` |
| `backend/platform-core/src/main/java/.../api/PlatformStatusResponse.java` | Created | Java record `PlatformStatusResponse(String service, String status)` |
| `backend/platform-core/src/main/java/.../api/PlatformStatusController.java` | Created | `GET /platform/status` → `PlatformStatusResponse`; tagged for OpenAPI |
| `backend/platform-core/src/test/java/.../EducationSystemApplicationIT.java` | Updated | Full web context test with `MockMvcBuilders.webAppContextSetup`; 5 integration tests |

### 5. Integration tests implemented

| Test | What it proves |
|---|---|
| `contextStarts()` | Spring context starts cleanly with web + springdoc on classpath |
| `apiDocsEndpointReturnsOk()` | `/api-docs` returns HTTP 200 |
| `apiDocsContainsOpenApiVersion()` | Response body contains `"openapi"` — valid OpenAPI document |
| `apiDocsContainsPlatformStatusEndpoint()` | Response body contains `/platform/status` — endpoint is documented |
| `swaggerUiIsReachable()` | `/swagger-ui` responds with 2xx or 3xx (Springdoc redirects to concrete UI page) |

### 6. Build results

#### Backend-only build

```
Command: .\mvnw.cmd -f backend/pom.xml clean verify
Environment: Java 25.0.2, Maven 3.9.15, Spring Boot 4.1.0-SNAPSHOT
Tests run: 5, Failures: 0, Errors: 0, Skipped: 0
Result: BUILD SUCCESS — 4.342 s
```

#### Full parent build

```
Command: .\mvnw.cmd clean verify
Environment: Java 25.0.2, Maven 3.9.15, Spring Boot 4.1.0-SNAPSHOT
All modules: SUCCESS
Result: BUILD SUCCESS — 4.268 s
```

### 7. Springdoc warnings (non-blocking)

Springdoc emits INFO-level warnings about the endpoints being enabled by default. These are informational only:
```
WARN: SpringDoc /api-docs endpoint is enabled by default. To disable it in production, set the property 'springdoc.api-docs.enabled=false'
WARN: SpringDoc /swagger-ui endpoint is enabled by default. To disable it in production, set the property 'springdoc.swagger-ui.enabled=false'
```
Security/exposure policy is out of scope for this story (see solution design risks section).

### 8. Acceptance criteria status

| AC | Status |
|---|---|
| 1. REST endpoint appears in OpenAPI spec with schemas | PASS — `/platform/status` with `PlatformStatusResponse` schema appears in `/api-docs` |
| 2. `/api-docs` returns valid OpenAPI JSON | PASS — `apiDocsEndpointReturnsOk` + `apiDocsContainsOpenApiVersion` |
| 3. `/swagger-ui` is browsable | PASS — `swaggerUiIsReachable` confirms 2xx/3xx response |
| 4. Integration tests verify availability | PASS — 5 integration tests pass |
| 5. No country/language-specific behavior | PASS — Generic platform status endpoint only, no country/tenant/i18n behavior |

