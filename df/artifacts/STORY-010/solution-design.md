# Solution Design - STORY-010

## Summary

Create a Java 21 Spring Boot modular monolith foundation using a Maven multi-module build. The first implementation should provide enough working application structure for developers to compile the project and run a sample integration test without introducing database infrastructure yet.

## Context

`STORY-220` is blocked because the repository currently contains planning/runtime documents but no Spring Boot source tree, build file, migration framework, PostgreSQL configuration, or test harness. `STORY-010` is the root dependency for the platform foundation and must establish the application substrate before database-backed stories can proceed.

## Requirements and Acceptance Criteria

- A new developer can clone the repo and run the build successfully.
- Modules exist for `platform-core`, `identity-access`, `organization`, `school-pack`, `attendance`, `gradebook`, `meal-catering`, and `common`.
- Running tests executes and passes at least one sample integration test.

## Proposed Solution

Use a single-repository Maven parent with independent project areas for backend, frontend, and DevOps.

Rework amendment from 2026-05-23 human/QA feedback:

- Root build: parent `pom.xml`, Maven wrapper files `mvnw`, `mvnw.cmd`, and `.mvn/wrapper/*`.
- Backend project: `backend/pom.xml`, an independent Maven parent containing the Spring Boot modular monolith modules.
- Frontend project area: `frontend/pom.xml`, an independent Maven project scaffold. Follow-up frontend foundation work must split this area into independent `frontend/website`, `frontend/android`, and `frontend/ios` projects; `frontend/website` uses Next.js + React.
- DevOps project: `devops/pom.xml`, an independent Maven project scaffold.
- Build paths: backend-only, frontend-only, DevOps-only, and all-project verification must be documented and validated.

Backend module structure:

- Boot application module: `platform-core`, containing the main Spring Boot application entry point.
- Library modules: `common`, `identity-access`, `organization`, `school-pack`, `attendance`, `gradebook`, and `meal-catering`.
- Module dependencies: domain modules may depend on `common`; `platform-core` wires the application and may depend on the feature modules needed to start the application context.
- Package namespace: use a generic namespace such as `com.darkfactory.education`.
- Initial code: minimal package structure, module marker/configuration classes only where needed, and one health-style sample component or controller if needed for the integration test.
- Tests: JUnit 5 with Spring Boot test support. Include one sample integration test that starts the Spring context and does not require PostgreSQL.

Keep all country and language variation out of code. Country templates remain data-only and must not influence module creation or package names.

## Alternatives Considered

- Gradle Kotlin DSL multi-module build: viable, but superseded by explicit human preference for Maven.
- Single Maven module with packages only: simpler today, but weaker as a foundation for independently testable modules.
- Creating all architecture-direction modules immediately: deferred to avoid broad scaffolding beyond the acceptance criteria.

## Files/Components Likely Affected

- `pom.xml`
- `mvnw`
- `mvnw.cmd`
- `.mvn/wrapper/*`
- `backend/pom.xml`
- `backend/{module}/pom.xml` for each required backend module
- `backend/{module}/src/main/java/...`
- `backend/{module}/src/test/java/...`
- `frontend/pom.xml`
- future `frontend/website/`
- future `frontend/android/`
- future `frontend/ios/`
- `devops/pom.xml`
- Optional docs updates if Dev records build instructions

## Data Model Changes

None. Database setup belongs to `STORY-011`.

## API/Contract Changes

No public business API is required for this story. If Dev adds a sample endpoint, it must be clearly marked as scaffolding and not treated as a stable product API.

## UI/UX Impact

None.

## Security and Privacy Considerations

- Do not add secrets, credentials, or environment-specific values.
- Do not add country-specific or language-specific code branches.
- Dependency versions should be current and compatible with Java 21/Spring Boot.

## Performance/Scalability Considerations

The structure should support independent module tests and future extraction paths, but no runtime performance claims are required.

## Test Strategy

Dev should run and document:

- `./mvnw clean verify` or `.\mvnw.cmd clean verify`
- `.\mvnw.cmd -f backend/pom.xml clean verify`
- `.\mvnw.cmd -f frontend/pom.xml clean verify`
- `.\mvnw.cmd -f devops/pom.xml clean verify`
- A sample Spring Boot integration test that verifies the application context starts.
- Any module-level tests created as part of scaffolding.

## Deployment/Migration Plan

No deployment or database migration is introduced. The output is source/build scaffolding only.

## Rollback Plan

Revert the generated project scaffold files if the build foundation is rejected before dependent work starts.

## Risks and Mitigations

- JDK 21 may be missing locally. Mitigate by documenting Java 21 as required in Dev notes or README.
- Module boundaries may evolve. Mitigate by keeping scaffolding minimal and using generic module names aligned with the accepted backlog.
- Maven wrapper generation may require network access. Mitigate by documenting the exact command/tooling used and any failure reason.

## Open Questions

- None blocking for development.

## SA Decision

Approved for development: Yes.
