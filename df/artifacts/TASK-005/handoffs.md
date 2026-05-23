# Handoffs - TASK-005

## SA -> QA

- Timestamp: 2026-05-23 12:17 local
- Task: TASK-005
- From state: ARCHITECTURE_IN_PROGRESS
- To state: READY_FOR_QA
- Lane: n/a
- Summary: Added first-class `designer` and `data-engineer` roles, frontend design-gate rules, data-source/synthetic-data rules, runtime boards, role docs, and documentation standards.

## Evidence

- `AGENTS.md`
- `df/00-start-here.md`
- `df/01-operating-model.md`
- `df/02-state-machine.md`
- `df/03-orchestration-rules.md`
- `df/04-documentation-standards.md`
- `df/roles/designer.md`
- `df/roles/data-engineer.md`
- `df/roles/frontend-dev.md`
- `df/roles/sa.md`
- `df/roles/qa.md`
- `df/roles/po.md`
- `df/runtime/design-board.md`
- `df/runtime/data-engineer-board.md`
- `df/artifacts/TASK-005/task.md`
- `df/artifacts/TASK-005/solution-design.md`
- `df/artifacts/TASK-005/decision-009-designer-data-engineer-roles.md`

## Tests/checks

| Check | Command/source | Result | Notes |
|---|---|---|---|
| Stale three-lane/generic-dev language search | `rg -n 'three implementation|three required|six required|backend-dev, frontend-dev, or devops|implementation subdashboards|implementation lane|implementation lanes|implementation-lane|implementation-owned|return it to \`dev\`' AGENTS.md CLAUDE.md JETBRAINS_AI.md .github/copilot-instructions.md README.md df/00-start-here.md df/01-operating-model.md df/02-state-machine.md df/03-orchestration-rules.md df/04-documentation-standards.md df/roles df/templates df/runtime` | PASS | No active-framework matches remained; historical activity-log text was not rewritten. |
| New role wiring search | `rg -n 'designer|data-engineer|design-board|data-engineer-board|READY_FOR_DESIGN|DESIGN_IN_PROGRESS' ...` | PASS | New roles, boards, and states are referenced across entrypoints, workflow docs, role files, templates, runtime, and TASK-005 artifacts. |
| Data rule search | `rg -n 'City, district, school, and subject names|city, district, school, and subject names|Teacher names, student names|teacher names, student names|fake/synthetic|synthetic' ...` | PASS | Public-source-backed true names and synthetic person/grade rules are present in global, role, QA, runtime, and task artifacts. |
| Required file existence | `Test-Path df/roles/designer.md; Test-Path df/roles/data-engineer.md; Test-Path df/runtime/design-board.md; Test-Path df/runtime/data-engineer-board.md; Test-Path df/artifacts/TASK-005/task.md` | PASS | All returned `True`. |

## Known risks

- Documentation-only gates require QA/PO enforcement during future tasks.
- Public-source data can drift; data tasks must record retrieval dates.

## Next role instructions

- QA should verify the new roles are consistently referenced across entrypoints, role files, runtime boards, state machine, documentation standards, templates, and adapter docs.
- QA should confirm frontend-dev is blocked without designer input for UI-facing work.
- QA should confirm data-engineering rules preserve the data-only country-template invariant and require public-source-backed true names plus synthetic personal/grade records.
- QA should confirm no application code, schemas, or API contracts changed.

## Blockers

- None.
