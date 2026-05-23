# Task - TASK-005

## Summary

Add `designer` and `data-engineer` roles to Dark Factory, including a frontend design gate and data-source/privacy rules.

## Type

Chore

## Priority

P0

## Current state

READY_FOR_QA

## Business goal

Ensure frontend work is implemented from explicit design input, and ensure country-specific test/database data is populated by a dedicated data role with public-source traceability and synthetic personal records.

## Acceptance criteria

- [x] Dark Factory defines a `designer` role with responsibility for UI/UX design packages and frontend handoff.
- [x] Frontend developer instructions require a design package before UI-facing frontend implementation.
- [x] Frontend developer instructions require missing design input to be treated as a blocker, not improvised implementation.
- [x] Dark Factory defines a `data-engineer` role with responsibility for country data templates, seed/test datasets, source maps, and data-quality evidence.
- [x] Data-engineering rules require true city, district, school, and subject names from public sources.
- [x] Data-engineering rules require fake/synthetic teacher names, student names, and individual grade records.
- [x] Runtime boards, role-selection guidance, documentation standards, and handoff guidance reference the new roles.
- [x] No application code, schema, or API contract is changed by this framework update.

## Out of scope

- Creating an actual UI design package for a product feature.
- Creating actual country seed datasets.
- Implementing frontend, backend, DevOps, or database changes.

## Assumptions

- `designer` is a pre-frontend role, not an application-code implementation role.
- `data-engineer` is a data delivery lane and may use `READY_FOR_DEV`/`DEV_IN_PROGRESS` states because it delivers versioned data artifacts rather than code.
- "Grades should be fake" means individual student grade records are synthetic; official grade scales or subject names remain public-source-backed data when required.

## Dependencies

- Existing Dark Factory role/state model from `TASK-004`.

## Risks

- Documentation-only gates still require QA/PO discipline to enforce.
- Public sources may change; data-engineering tasks must record retrieval dates and transformation notes.

## Links

- Issue: n/a
- PR: n/a
- Design: `df/artifacts/TASK-005/solution-design.md`

## Role history

| Timestamp | Role | State | Summary |
|---|---|---|---|
| 2026-05-23 12:17 local | sa | OPEN -> ARCHITECTURE_IN_PROGRESS -> READY_FOR_QA | Added designer and data-engineer role model, design gate, data-source rules, runtime boards, and QA handoff. |
