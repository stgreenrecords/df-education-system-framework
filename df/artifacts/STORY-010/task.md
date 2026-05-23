# Task - STORY-010

## Summary

Initialize the Spring Boot project with a modular monolith structure and baseline build/test setup.

## Type

Story

## Priority

P0

## Current state

DONE

## Business goal

Create the application foundation required before database, i18n, security, and domain modules can be implemented.

## Acceptance criteria

- [x] Given a new developer, when they clone the repo and run the build, then the project compiles successfully.
- [x] Given the project structure, when inspected, then modules exist for `platform-core`, `identity-access`, `organization`, `school-pack`, `attendance`, `gradebook`, `meal-catering`, and `common`.
- [x] Given the build system, when running tests, then a sample integration test passes.

## Refinement

Not required. The backlog story already has explicit, testable acceptance criteria and no product ambiguity that blocks development.

## Architecture

Required and completed because this task defines the backend project structure, build system, module boundaries, and baseline test strategy.

## Out of scope

- PostgreSQL connection, migrations, and schema setup. Covered by `STORY-011`.
- Authentication, authorization, and user management implementation.
- Domain feature implementation inside the modules beyond minimal scaffolding.
- CI/CD pipeline and release packaging.
- Frontend application setup. Covered by `STORY-014` for independent `frontend/website`, `frontend/android`, and `frontend/ios` projects.

## Assumptions

- Use Java 21 because the architecture direction calls for latest LTS Java and the open questions recommend Java 21.
- Use Maven for the multi-module build per explicit human preference.
- Use a single repository modular monolith for MVP, matching the architecture direction and `DEV-Q09` recommendation.
- Include a sample integration test that does not require PostgreSQL; database-backed integration testing starts in `STORY-011`.

## Dependencies

- None.

## Risks

- Local machines or CI images must have JDK 21 available.
- Early module boundaries may need adjustment as domain stories mature.

## Links

- Issue: n/a
- PR: n/a
- Design: `df/artifacts/STORY-010/solution-design.md`

## Role history

| Timestamp | Role | State | Summary |
|---|---|---|---|
| 2026-05-23 10:31 local | sa | OPEN -> READY_FOR_DEV | Promoted foundation story from backlog, skipped refinement, completed architecture, and prepared Dev handoff. |
| 2026-05-23 10:36 local | sa | READY_FOR_DEV | Updated architecture guidance from Gradle to Maven per explicit human preference. |
| 2026-05-23 10:47 local | dev | READY_FOR_DEV -> DEV_IN_PROGRESS | Started Maven multi-module Spring Boot scaffold implementation. |
| 2026-05-23 10:52 local | dev | DEV_IN_PROGRESS -> READY_FOR_QA | Implemented scaffold and passed wrapper build/integration test validation. |
| 2026-05-23 11:02 local | qa | READY_FOR_QA -> RETURNED_TO_DEV | Human rework requires clear backend/frontend/devops Maven project separation under one parent. |
| 2026-05-23 11:10 local | dev | RETURNED_TO_DEV -> DEV_IN_PROGRESS | Started rework for parent plus independent backend, frontend, and DevOps Maven project separation. |
| 2026-05-23 11:13 local | dev | DEV_IN_PROGRESS -> READY_FOR_QA | Reworked scaffold into root parent plus independent backend, frontend, and DevOps Maven projects and passed targeted/full validation. |
| 2026-05-23 11:51 local | qa | READY_FOR_QA -> READY_FOR_PO | Verified reworked parent/backend/frontend/devops Maven structure, targeted/full builds, and integration test evidence. |
| 2026-05-23 11:55 local | po | READY_FOR_PO -> PO_REVIEW | Started product review of the reworked Maven scaffold and QA evidence. |
| 2026-05-23 11:56 local | po | PO_REVIEW -> DONE | Accepted the Maven scaffold after product review and full parent verification. |
