# Decision Record - DECISION-009

- Date: 2026-05-23
- Status: Accepted
- Owner role: SA
- Related task: TASK-005

## Context

Dark Factory needs explicit ownership for design input before frontend UI implementation and for source-backed country/test data population.

## Decision

Add `designer` and `data-engineer` as first-class roles.

`designer` owns UI/UX design packages before frontend implementation. UI-facing frontend work must not proceed without designer input unless SA documents the task as non-visual.

`data-engineer` owns country data templates, seed/test datasets, fixtures, source maps, and data-quality evidence. City, district, school, and subject names must be true and public-source-backed. Teacher names, student names, and individual grade records must be fake/synthetic.

## Consequences

- Frontend UI work may be blocked until design artifacts exist.
- Data work has a dedicated lane and runtime board.
- QA must check design evidence for UI work and source/synthetic separation for data work.
- The no-country-specific-code invariant remains unchanged.

## Alternatives considered

- Let frontend developers design UI as they implement: rejected because the user explicitly requires designer input before frontend implementation.
- Assign data population to backend-dev: rejected because dataset sourcing and privacy evidence are separate responsibilities.
- Use real teacher/student public-directory names for realism: rejected because test data must not copy real personal records.

## Evidence

- `AGENTS.md`
- `df/01-operating-model.md`
- `df/02-state-machine.md`
- `df/03-orchestration-rules.md`
- `df/roles/designer.md`
- `df/roles/data-engineer.md`
- `df/roles/frontend-dev.md`
- `df/runtime/design-board.md`
- `df/runtime/data-engineer-board.md`

## Follow-up actions

QA verifies cross-document consistency and that no application code/schema/API changes were introduced.
