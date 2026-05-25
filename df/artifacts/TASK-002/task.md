# Task - TASK-002

## Summary

Add a strict architecture rule that country templates, including Poland, must never drive country-specific framework code, framework structure, or schema changes. Only configuration data and country-specific values may vary by country.

## Type

Chore

## Priority

P0

## Current state

DONE

## Business goal

Protect the framework from country lock-in. The Education System Framework must remain generic, reusable, and data-driven across countries. Poland is only the first reference dataset, not a structural or code-level special case.

## Acceptance criteria

- [x] Universal agent/framework guidance explicitly forbids country-specific code changes
- [x] Architecture guidance explicitly states that country templates are data-only and must not change framework structure or schemas
- [x] Poland template artifact explicitly states it must not influence framework code, structure, or schemas
- [x] The decision is documented in a task artifact and referenced from runtime decisions
- [x] Runtime board/activity log reflect the task and QA handoff

## Out of scope

- Implementing country template code
- Changing application source code or database schema
- Reworking other country templates

## Assumptions

- The user intent is a repository-wide architecture constraint, not a Poland-only exception
- Documentation updates are sufficient for this task because no application code currently depends on a Poland-specific implementation

## Dependencies

- SPIKE-001 (READY_FOR_QA) — Poland template research artifact needs the new guardrail reflected in its wording
- TASK-001 (DONE) — architecture/backlog documents already define framework direction

## Risks

- If future contributors miss this rule, Poland-specific branching could still be introduced later; mitigated by placing the rule in global guidance and architecture docs

## Links

- Issue: n/a
- PR: n/a
- Design: `df/artifacts/TASK-002/decision-001-no-country-specific-code.md`

## Refinement

Refinement: not required. The user request is explicit, narrow in scope, and directly testable through documentation updates.

## Role history

| Timestamp | Role | State | Summary |
|---|---|---|---|
| 2026-05-23 09:42 | dev | READY_FOR_DEV → DEV_IN_PROGRESS → READY_FOR_QA | Implemented the strict no-country-specific-code rule in universal docs, architecture guidance, runtime decision log, and the Poland template artifact. |
| 2026-05-23 09:45 | qa | READY_FOR_QA → QA_IN_PROGRESS → READY_FOR_PO | QA passed. Verified all files for accurate framing as structural independence and data-only artifacts. |
| 2026-05-23 | po | READY_FOR_PO → PO_REVIEW → DONE | PO accepted. All 5 acceptance criteria verified. Rule is embedded in AGENTS.md, architecture-direction.md, decisions.md, and Poland template artifact. No UI changes; no screenshots required. |
