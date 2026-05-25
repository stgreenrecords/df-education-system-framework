# Handoffs - TASK-002

## Dev -> QA

- Timestamp: 2026-05-23 09:42
- Task: TASK-002
- State: READY_FOR_QA
- Summary: Added a strict architectural rule that no country template, including Poland, may cause country-specific code, framework structure, schema, or API changes; only configuration data and values may vary by country.
- Files changed:
  - `AGENTS.md`
  - `df/backlog/architecture-direction.md`
  - `df/runtime/decisions.md`
  - `df/artifacts/SPIKE-001/poland-template-v1.md`
  - `df/runtime/board.md`
  - `df/runtime/activity-log.md`
  - `df/artifacts/TASK-002/task.md`
  - `df/artifacts/TASK-002/decision-001-no-country-specific-code.md`
  - `df/artifacts/TASK-002/dev-notes.md`
- Tests run:
  - Manual doc consistency review
  - File validation on edited Markdown files
- Known risks:
  - Historical archive notes were left untouched because they are background material, not active framework guidance
- QA focus areas:
  - Confirm the rule is strict and unambiguous in global guidance
  - Confirm architecture guidance clearly forbids country-specific structure/schema/code changes
  - Confirm the Poland artifact is framed as data-only and not a structural driver
  - Confirm decision/runtime/task artifacts are internally consistent

