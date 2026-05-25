# Task - STORY-021

## Summary

Implement the basic sovereign tenant/deployment configuration model so each country-operated deployment has one persisted tenant record with core country metadata and a backend tenant context that later modules can consume without introducing centralized multi-country runtime behavior.

## Type

Story

## Priority

P0

## Current state

DONE

## Business goal

Establish the first persisted country-deployment identity and configuration boundary in the backend so later configuration inheritance, security, organization, and institution features build on one explicit sovereign deployment context rather than ad-hoc country settings.

## Acceptance criteria

- [ ] Given a new deployment, when initialized, then a tenant record is created with country code, name, timezone, locale
- [ ] Given a tenant, when APIs are called, then all operations are scoped to that tenant
- [ ] Given tenant configuration, when loaded, then it provides country-specific settings to all modules

## Out of scope

- Multi-country centralized SaaS hosting or cross-country runtime routing
- Region/city/institution inheritance rules; those belong to `STORY-030`
- UI/admin screens for tenant management
- Identity, RBAC, or MFA behavior; those belong to later security stories
- Provider-specific deployment overlays or country template data packages

## Assumptions

- `STORY-020` is the governing sovereign deployment model, so Phase 1 should treat one country/ministry deployment as one active deployment tenant rather than a request-selectable multi-country SaaS tenant pool
- `STORY-011` already provides the PostgreSQL/Flyway baseline needed for tenant persistence
- The minimal safe implementation is backend-only in `backend/platform-core`; no designer, frontend, DevOps, or data-engineering child task is required for this story
- Tenant bootstrap metadata may come from deployment/runtime configuration and be persisted idempotently during application initialization

## Dependencies

- `STORY-010`
- `STORY-011`
- architectural alignment from `STORY-020`

## Risks

- If the active deployment tenant model is underspecified, later security/configuration stories may drift toward centralized multi-country assumptions
- Existing Phase 1 platform behavior is still sparse, so tenant scoping must be introduced as a reusable abstraction rather than one-off request hacks
- The deferred schema isolation decision in `df/backlog/architecture-direction.md` may affect later expansion of tenant-aware tables beyond this minimal baseline

## Links

- Issue: n/a
- PR: n/a
- Design: `df/artifacts/STORY-021/solution-design.md`

## Implementation lane

- Lane: `backend-dev`
- Subdashboard: `df/runtime/backend-dev-board.md`
- Artifact folder for implementation notes: `df/artifacts/STORY-021/backend/`

## Role history

| Timestamp | Role | State | Summary |
|---|---|---|---|
| 2026-05-24 20:30 local | sa | OPEN -> NEEDS_ARCHITECTURE | Selected `STORY-021` as the next highest-priority actionable backlog story after `STORY-023` acceptance because it is a Critical Phase 1 foundation task, its implementation dependencies are accepted, and it unblocks the downstream configuration/inheritance engine and other country-scoped domain work. |
| 2026-05-24 20:30 local | sa | NEEDS_ARCHITECTURE -> ARCHITECTURE_IN_PROGRESS | Started architecture because the story affects persistence, runtime scoping, backend API behavior, deployment initialization, and the boundary between sovereign deployment context and later organization/configuration modules. |
| 2026-05-24 20:30 local | sa | ARCHITECTURE_IN_PROGRESS -> READY_FOR_DEV | Completed the backend-oriented solution design, recorded the deployment-tenant modeling decision, updated shared architecture direction, and routed the story to `backend-dev` as a single-lane implementation task. |
| 2026-05-24 20:30 local | backend-dev | READY_FOR_DEV -> DEV_IN_PROGRESS | Backend implementation started after reviewing the task/design artifacts, backend subdashboard, repository status, and the existing `platform-core` PostgreSQL/Flyway/backend API foundation. |
| 2026-05-24 20:39 local | backend-dev | DEV_IN_PROGRESS -> READY_FOR_QA | Implemented the persisted deployment-tenant slice in `backend/platform-core`, including Flyway migration `V6`, tenant bootstrap/configuration binding, cached tenant context service, `GET /api/v1/platform/tenant`, and backend unit/integration coverage; reran backend-focused and full-parent Maven verification successfully and handed the story to QA. |
| 2026-05-24 20:42 local | qa | READY_FOR_QA -> QA_IN_PROGRESS | QA started independent verification by reviewing the task/design/backend handoff artifacts, inspecting the tenant migration/bootstrap/controller implementation directly, and rerunning backend-focused plus full-parent Maven verification with Testcontainers-backed PostgreSQL coverage. |
| 2026-05-24 20:46 local | qa | QA_IN_PROGRESS -> READY_FOR_PO | QA passed the story after confirming Flyway `V6` creates the `platform_tenant` table, startup bootstrap remains idempotent with one active tenant row, `GET /api/v1/platform/tenant` returns the active tenant metadata, and the implementation preserves the sovereign single-deployment-tenant model without request-side country switching. |
| 2026-05-24 20:50 local | po | READY_FOR_PO -> PO_REVIEW | PO started product validation by reviewing the task, QA report, handoff evidence, and the governing sovereign deployment architecture from `STORY-020`, then running a focused contract-test pass for tenant bootstrap creation, tenant endpoint output, and OpenAPI exposure. |
| 2026-05-24 20:50 local | po | PO_REVIEW -> DONE | PO accepted the story after confirming the backend-only tenant foundation matches the product intent of one active sovereign deployment tenant per country-operated runtime and that the focused contract validation passed with `BUILD SUCCESS`. |

