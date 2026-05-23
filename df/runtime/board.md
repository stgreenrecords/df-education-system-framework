# Dark Factory Runtime Board

This is the live task queue. Agents must update it when task state changes.

| Priority | Task ID | Title | Type | State | Owner role | Blocked? | Last updated | Next action |
|---|---|---|---|---|---|---|---|---|
| P0 | TASK-001 | Transform initial prompt into roadmap and backlog | Story | DONE | factory | No | 2026-05-23 01:20 local | — |
| P0 | TASK-002 | Add strict no-country-specific-code rule | Chore | DONE | factory | No | 2026-05-23 | — |
| P0 | SPIKE-001 | Research Polish education system for country template | Spike | DONE | factory | No | 2026-05-23 09:55 local | Next session: PO reviews TASK-002 |

## Queue notes

- TASK-001: Complete. All 13 deliverables produced, QA passed, PO accepted.
- TASK-002: Complete. Dev added the strict data-only guardrail across AGENTS.md, architecture-direction.md, decisions.md, and the Poland template. QA passed. PO accepted 2026-05-23.
- SPIKE-001: PO accepted the Poland Template v1 research deliverable. All 7 acceptance criteria passed, no UI/screenshots applied, and residual risks were explicitly accepted as follow-up validation items.
- EPIC-22 / i18n backlog entries added to `df/backlog/epics.md` and `df/backlog/user-stories.md` (STORY-220 through STORY-225). Stories are in DRAFT state, not yet scheduled for a sprint.
