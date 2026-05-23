# PO Review - TASK-002

- Reviewer role: po
- Date: 2026-05-23
- Task: TASK-002 — Add strict no-country-specific-code rule

---

## PO Result: ACCEPTED

- Task: TASK-002
- Acceptance criteria: PASS
- E2E validation: PASS
- Screenshots/evidence: Not applicable — this task changes repository documentation and decision records only; no UI is affected.
- Product notes: The strict data-only guardrail for country templates is now recorded in three tiers of authority: (1) universal agent guidance (`AGENTS.md`), (2) architecture backlog (`df/backlog/architecture-direction.md`), and (3) the runtime decision log (`df/runtime/decisions.md`). The Poland template artifact (`df/artifacts/SPIKE-001/poland-template-v1.md`) also explicitly confirms its data-only status. The formal decision record (`df/artifacts/TASK-002/decision-001-no-country-specific-code.md`) provides the full rationale, alternatives considered, and consequences. This layered approach means the rule will be encountered by any contributor reading any entry-point into the project.
- Risks accepted: Future contributors who bypass the documentation may still attempt country-specific code; the rule as written is the strongest available preventive control at documentation level. Implementation-time code review is the required complementary control.
- Next: No remaining actionable tasks on the board. Dev or SA should pick up the next item if new tasks are added.

---

## Acceptance criteria verification

| # | Criterion | Status | Evidence |
|---|---|---|---|
| 1 | Universal agent/framework guidance explicitly forbids country-specific code changes | PASS | `AGENTS.md` — "Strict framework invariants" section: *"No country-specific code change is allowed."* |
| 2 | Architecture guidance explicitly states country templates are data-only and must not change framework structure or schemas | PASS | `df/backlog/architecture-direction.md` — data-only invariant added |
| 3 | Poland template artifact explicitly states it must not influence framework code, structure, or schemas | PASS | `df/artifacts/SPIKE-001/poland-template-v1.md` — implementation guardrail section added |
| 4 | Decision documented in a task artifact and referenced from runtime decisions | PASS | `df/artifacts/TASK-002/decision-001-no-country-specific-code.md`; `df/runtime/decisions.md` (DECISION-001) |
| 5 | Runtime board/activity log reflect the task and QA handoff | PASS | `df/runtime/board.md` and `df/runtime/activity-log.md` both updated through QA handoff |

---

## QA pass confirmation

QA report at `df/artifacts/TASK-002/qa-report.md` confirms:
- All 5 acceptance criteria verified through grep analysis on repository files.
- No unit or integration tests required (documentation-only change).
- No regression issues identified.

---

## Product intent alignment

**Business goal met.** The Education System Framework must remain generic, reusable, and data-driven across all countries. Poland is the first reference dataset. The delivered rule ensures that this architectural intent is not eroded by future country-specific implementation requests.

The documentation approach is the correct and minimal intervention for a rule that pre-dates any actual framework code. When implementation begins, the rule must also be enforced through code review, PR templates, and CI checks — but those controls are out of scope for this task.

