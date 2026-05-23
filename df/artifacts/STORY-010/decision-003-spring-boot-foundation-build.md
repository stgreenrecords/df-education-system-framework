# Decision Record - DECISION-003

- Date: 2026-05-23
- Status: Accepted
- Owner role: SA
- Related task: STORY-010

## Context

The repository needs an application foundation before database-backed or domain implementation stories can proceed. Existing architecture direction selects Java Spring Boot, PostgreSQL, REST/OpenAPI, and modular monolith first, but leaves the build tool open.

## Decision

Use Java 21, Spring Boot, a single-repository Maven multi-module build, and a modular monolith layout for the initial backend scaffold.

Required initial modules are:

- `platform-core`
- `identity-access`
- `organization`
- `school-pack`
- `attendance`
- `gradebook`
- `meal-catering`
- `common`

## Consequences

- Developers get one root build command for the backend foundation.
- Modules can be tested independently while remaining deployable as one application for MVP.
- Future stories can add PostgreSQL, OpenAPI, security, and domain implementation without changing the root project shape.
- Java 21 becomes the baseline runtime requirement.

## Alternatives Considered

- Gradle Kotlin DSL multi-module build: accepted industry option, but superseded by explicit human preference for Maven on 2026-05-23.
- Single-module Spring Boot app: faster initial setup, but does not satisfy the module acceptance criterion strongly enough.
- Multi-repository modules: unnecessary operational overhead for MVP.

## Evidence

- `df/backlog/architecture-direction.md`
- `df/backlog/user-stories.md` (`STORY-010`)
- `df/backlog/open-questions.md` (`DEV-Q01`, `DEV-Q02`, `DEV-Q09`)
- `df/artifacts/STORY-010/solution-design.md`
- Human preference stated in the IDE session on 2026-05-23: use Maven instead of Gradle.

## Follow-up Actions

- Dev implements the scaffold and records build/test evidence.
- `STORY-011` adds PostgreSQL, connection pooling, and migration framework after `STORY-010` passes QA/PO.
