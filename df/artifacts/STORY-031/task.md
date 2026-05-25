# Task - STORY-031

## Summary

Extend the generic configuration engine with validation, inheritance-break request recording, and compatibility reporting so countries and lower scopes can detect blocked overrides and downstream impact safely.

## Type

Story

## Priority

P1

## Current state

READY_FOR_DEV

## Business goal

Build the next safe layer on top of the accepted configuration foundation so configuration changes can be validated before write, exceptional inheritance-break requests can be recorded with justification and audit traceability, and country-level changes can expose downstream impact before rollout or approval.

## Acceptance criteria

- [ ] Given a locked field override attempt, when submitted, then a validation error is returned
- [ ] Given an inheritance break request, when submitted with justification, then it is recorded with audit trail
- [ ] Given a country config update, when institutions have overrides, then a compatibility report lists affected institutions

## Out of scope

- Full approval workflow for inheritance-break requests
- Organization-module-backed institution metadata beyond generic scope identifiers
- UI, release-package implementation, or country-specific compatibility rules

## Assumptions

- This story remains backend-only and should stay generic in `backend/platform-core`
- The accepted `STORY-030` scope-path model remains the foundation; later organization stories can enrich identifiers without replacing configuration semantics
- The existing audit foundation from `STORY-013` should capture inheritance-break request recording rather than introducing feature-specific audit persistence

## Dependencies

- `STORY-030` for the configuration inheritance foundation
- `STORY-013` for shared audit recording
- `STORY-021` for active deployment-tenant/root-scope resolution

## Risks

- If compatibility reporting depends too heavily on unfinished organization persistence, it may block a safe backend-only implementation
- If inheritance-break recording is modeled as an automatic override instead of a traceable request, it could weaken the current lock/guardrail behavior

## Links

- Issue: n/a
- PR: n/a
- Design: `df/artifacts/STORY-031/solution-design.md`

## Role history

| Timestamp | Role | State | Summary |
|---|---|---|---|
| 2026-05-25 12:56 local | sa | OPEN -> NEEDS_ARCHITECTURE | Selected `STORY-031` as the next highest-priority actionable task because it is the strongest remaining Phase 1 follow-up on the accepted configuration foundation and directly advances compatibility-reporting and inheritance-break workflows needed by later release and country-template work. |
| 2026-05-25 12:56 local | sa | NEEDS_ARCHITECTURE -> ARCHITECTURE_IN_PROGRESS | Started architecture because the story adds validation/reporting APIs, audit-traced inheritance-break persistence, and cross-scope impact reporting on top of the shared configuration engine. |
| 2026-05-25 12:56 local | sa | ARCHITECTURE_IN_PROGRESS -> READY_FOR_DEV | Completed the backend-only architecture, recorded `DECISION-021`, created the task package, and routed implementation to `backend-dev`. |

