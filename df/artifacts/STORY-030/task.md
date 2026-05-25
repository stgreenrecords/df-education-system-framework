# Task - STORY-030

## Summary

Implement the first backend configuration inheritance engine so deployment-tenant and lower organizational scopes can resolve generic settings through ordered inheritance, overrides, locking, and extensible-field merging.

## Type

Story

## Priority

P0

## Current state

DONE

## Business goal

Establish a reusable configuration foundation that later organization, security, grading, schedule, and country-template stories can consume without hardcoding country-specific or institution-specific behavior.

## Acceptance criteria

- [ ] Given a country-level setting, when queried at institution level, then the country value is returned if not overridden
- [ ] Given an institution-level override, when queried, then the institution value takes precedence
- [ ] Given a locked field at country level, when a lower level tries to override, then the override is rejected
- [ ] Given an extensible field, when a lower level adds options, then both inherited and local options are available
- [ ] Given a configuration change at region level, when queried at institution level within that region, then the new value is inherited

## Out of scope

- Full organization/institution CRUD and authoritative hierarchy management in `backend/organization`
- UI/admin screens for editing or browsing inherited configuration
- Compatibility reporting and inheritance-break workflows; those belong to `STORY-031`
- Full platform-wide audit subsystem; this story should stay compatible with `STORY-013` rather than blocking on it
- Country template data population or country-specific configuration packages

## Assumptions

- Refinement is not required because the backlog story already has clear, testable acceptance criteria and the priority/dependency context is documented
- The minimal safe implementation is backend-only and can start in `backend/platform-core` without waiting for a separate frontend, DevOps, design, or data-engineering child task
- The active deployment tenant from `STORY-021` is the country-level root scope for Phase 1 configuration resolution
- Until the `organization` module owns authoritative region/city/institution/unit entities, the configuration engine may use generic scope-path identifiers supplied by callers/tests rather than hardwiring organization-module dependencies
- Field behavior such as replace vs extend and lockability should be modeled generically, not through country-specific or feature-specific code branches

## Dependencies

- `STORY-021`
- `STORY-011`
- `STORY-010`

## Risks

- Configuration inheritance can become too complex for MVP if the first implementation tries to solve every future scope and compatibility edge case at once
- If scope hierarchy is coupled too early to unfinished organization-module persistence, later platform modules may need avoidable rework
- Merge/lock semantics must stay deterministic and generic so later modules can reuse the engine safely

## Links

- Issue: n/a
- PR: n/a
- Design: `df/artifacts/STORY-030/solution-design.md`

## Implementation lane

- Lane: `backend-dev`
- Subdashboard: `df/runtime/backend-dev-board.md`
- Artifact folder for implementation notes: `df/artifacts/STORY-030/backend/`

## Role history

| Timestamp | Role | State | Summary |
|---|---|---|---|
| 2026-05-24 20:55 local | sa | OPEN -> NEEDS_ARCHITECTURE | Selected `STORY-030` as the next highest-priority actionable backlog story after `STORY-021` acceptance because it is a Critical Phase 1 foundation item, its dependency on `STORY-021` is now satisfied, and it unblocks more downstream backlog work than the remaining unscheduled Critical stories. |
| 2026-05-24 20:55 local | sa | NEEDS_ARCHITECTURE -> ARCHITECTURE_IN_PROGRESS | Started architecture because the story affects persistence, generic scope modeling, inheritance resolution behavior, validation rules, backend API/service contracts, and downstream module boundaries. |
| 2026-05-24 20:55 local | sa | ARCHITECTURE_IN_PROGRESS -> READY_FOR_DEV | Completed the backend-oriented solution design, recorded the generic scope-path + field-behavior decision, updated shared runtime architecture/decision tracking, and routed the story to `backend-dev` as a single-lane implementation task. |
| 2026-05-24 21:00 local | backend-dev | READY_FOR_DEV -> DEV_IN_PROGRESS | Backend implementation started after reviewing the task, solution design, SA handoff, runtime boards, repository status, and the existing `platform-core` tenant/translation foundation. |
| 2026-05-24 21:10 local | backend-dev | DEV_IN_PROGRESS -> READY_FOR_QA | Implemented the configuration inheritance foundation in `backend/platform-core`, including Flyway migration `V7`, generic field-definition and scoped-value persistence, scope-path resolution, lock validation, `REPLACE`/`EXTEND_SET` merge behavior, minimal backend endpoints, and unit/integration coverage; reran backend-focused and full-parent Maven verification successfully and handed the story to QA. |
| 2026-05-24 21:12 local | qa | READY_FOR_QA -> QA_IN_PROGRESS | QA started independent verification by reviewing the task/design/backend handoff artifacts, inspecting the configuration migration/controller/service/scope-path implementation directly, and rerunning focused contract tests plus backend-focused and full-parent Maven verification with Testcontainers-backed PostgreSQL coverage. |
| 2026-05-24 21:16 local | qa | QA_IN_PROGRESS -> READY_FOR_PO | QA passed the story after confirming Flyway `V7` creates the configuration inheritance tables, the migration chain remains ordered `1..7`, all five inheritance/override/lock/merge/region propagation acceptance criteria pass through the backend API coverage, `/api/v1/platform/configuration/resolve` is exposed in `/api-docs`, and the configuration package remains framework-generic without organization-module or country-specific hardcoding. |
| 2026-05-24 21:19 local | po | READY_FOR_PO -> DONE | PO accepted the QA-approved backend inheritance foundation after rerunning the focused configuration inheritance/OpenAPI product contract tests, confirming all five acceptance criteria, and validating that the delivered slice stays within the intended generic Phase 1 product boundary. |

