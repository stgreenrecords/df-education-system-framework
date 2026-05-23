# 02 - Dark Factory State Machine

This file defines task states and allowed transitions. Agents must update `df/runtime/board.md` when changing state.

## States

| State | Owner | Meaning |
|---|---|---|
| `OPEN` | factory | Task exists but has not been prepared. |
| `INTAKE` | `sa` | Task is being triaged and refined from raw input. |
| `REFINEMENT_IN_PROGRESS` | `sa` | SA is generating acceptance criteria and asking clarifying questions. |
| `REFINEMENT_QUESTIONS` | `po` / human | Questions have been posted; PO or human product authority must answer before work continues. |
| `REFINED` | factory | Questions answered or low-risk assumptions documented, acceptance criteria written, task is ready for architecture or lane routing. |
| `NEEDS_ARCHITECTURE` | `sa` | Task needs solution design before development. |
| `ARCHITECTURE_IN_PROGRESS` | `sa` | SA is designing or reviewing the approach. |
| `READY_FOR_DESIGN` | `designer` | UI/UX design input is required before frontend implementation. |
| `DESIGN_IN_PROGRESS` | `designer` | Designer is producing or revising the design package. |
| `READY_FOR_DEV` | delivery lane | Task can be implemented or populated by exactly one lane owner: `backend-dev`, `frontend-dev`, `devops`, or `data-engineer`. |
| `DEV_IN_PROGRESS` | delivery lane | The lane owner is implementing or populating the task. |
| `READY_FOR_QA` | `qa` | Dev work is complete and ready for QA. |
| `QA_IN_PROGRESS` | `qa` | QA is verifying the task. |
| `QA_FAILED` | `qa` | QA found issues. |
| `READY_FOR_PO` | `po` | QA passed and PO review is needed. |
| `PO_REVIEW` | `po` | PO is performing E2E/product validation. |
| `PO_REJECTED` | `po` | PO rejected the result. |
| `RETURNED_TO_DEV` | delivery lane | Rework is required by the original lane owner unless SA reroutes it. |
| `BLOCKED` | human/factory | Work cannot continue without external input. |
| `DONE` | factory | Task accepted and complete. |
| `NO_TASKS` | factory | No actionable work exists. |

## Allowed transitions

| From | To | Required evidence |
|---|---|---|
| `OPEN` | `INTAKE` | Raw task exists; refinement needed. |
| `OPEN` | `READY_FOR_DEV` | Acceptance criteria already clear and no architecture needed. |
| `OPEN` | `NEEDS_ARCHITECTURE` | Acceptance criteria clear but architecture needed. |
| `INTAKE` | `REFINEMENT_IN_PROGRESS` | SA start note. |
| `REFINEMENT_IN_PROGRESS` | `REFINEMENT_QUESTIONS` | Questions document created with impact, decision owner, recommendation, and safe-default status. |
| `REFINEMENT_IN_PROGRESS` | `REFINED` | No critical questions remain; acceptance criteria and assumptions written. |
| `REFINEMENT_QUESTIONS` | `REFINEMENT_IN_PROGRESS` | Answers provided with answer authority; SA resumes refinement. |
| `REFINED` | `NEEDS_ARCHITECTURE` | Architecture needed reason. |
| `REFINED` | `READY_FOR_DESIGN` | UI-facing frontend work is ready for designer input before implementation. |
| `REFINED` | `READY_FOR_DEV` | No architecture needed reason. |
| `NEEDS_ARCHITECTURE` | `ARCHITECTURE_IN_PROGRESS` | SA start note. |
| `ARCHITECTURE_IN_PROGRESS` | `READY_FOR_DEV` | Solution design artifact plus assigned delivery lane and subdashboard entry. |
| `ARCHITECTURE_IN_PROGRESS` | `READY_FOR_DESIGN` | UI-facing frontend task lacks accepted design package; design scope and subdashboard entry created. |
| `ARCHITECTURE_IN_PROGRESS` | `READY_FOR_QA` | Documentation/process-only change completed by SA; no delivery lane required; QA handoff explains why. |
| `READY_FOR_DESIGN` | `DESIGN_IN_PROGRESS` | Designer start note and design artifact path. |
| `DESIGN_IN_PROGRESS` | `READY_FOR_DEV` | Design package complete, with affected frontend scope and frontend subdashboard entry or SA handoff. |
| `READY_FOR_DEV` | `DEV_IN_PROGRESS` | Lane start note in the lane-owned artifact folder. |
| `RETURNED_TO_DEV` | `DEV_IN_PROGRESS` | Rework plan. |
| `DEV_IN_PROGRESS` | `READY_FOR_QA` | Implementation summary and lane-specific test evidence. |
| `READY_FOR_QA` | `QA_IN_PROGRESS` | QA start note and test plan. |
| `QA_IN_PROGRESS` | `QA_FAILED` | Defect report. |
| `QA_FAILED` | `RETURNED_TO_DEV` | QA handoff with reproduction steps. |
| `QA_IN_PROGRESS` | `READY_FOR_PO` | QA report with pass result. |
| `READY_FOR_PO` | `PO_REVIEW` | PO start note. |
| `PO_REVIEW` | `PO_REJECTED` | PO rejection report with screenshots/evidence. |
| `PO_REJECTED` | `RETURNED_TO_DEV` | Rework request. |
| `PO_REVIEW` | `DONE` | PO acceptance report and final evidence. |
| Any active state | `BLOCKED` | Blocker reason and owner. |
| `BLOCKED` | previous actionable state | Blocker resolution note. |
| `DONE` | next task state | Factory heartbeat and next task selection. |

## State update format

When changing state, append this block to `df/runtime/activity-log.md`:

```markdown
## {timestamp} - State change

- Task: {task-id}
- From: {old-state}
- To: {new-state}
- Role: {role}
- Reason: {why}
- Evidence: {links/files}
- Next: {next role/action}
```

For delivery states, `{role}` must be `designer`, `backend-dev`, `frontend-dev`, `devops`, or `data-engineer` as appropriate; do not use the retired generic `dev` owner for new work.

## Implementation lane routing

`READY_FOR_DEV`, `DEV_IN_PROGRESS`, and `RETURNED_TO_DEV` use the same state names across all delivery lanes. The responsible role is resolved from:

1. `Owner role` in `df/runtime/board.md`;
2. exactly one matching subdashboard entry in `df/runtime/backend-dev-board.md`, `df/runtime/frontend-dev-board.md`, `df/runtime/devops-board.md`, or `df/runtime/data-engineer-board.md`;
3. lane-owned artifacts under `df/artifacts/{task-id}/backend/`, `df/artifacts/{task-id}/frontend/{website|android|ios}/`, `df/artifacts/{task-id}/devops/`, or `df/artifacts/{task-id}/data/`.

`READY_FOR_DESIGN` and `DESIGN_IN_PROGRESS` are resolved from `df/runtime/design-board.md` and design-owned artifacts under `df/artifacts/{task-id}/design/`.

If a task requires multiple lanes, SA must split it into child lane tasks before any delivery role starts. A parent task may remain in the main board as a coordination item while children move independently through design, implementation/data population, QA, and PO.

## Blocker handling

When blocked:

1. Keep all work artifacts.
2. Document exact missing input.
3. Assign blocker owner: `human`, `external-system`, `credential-owner`, `security`, or `product`.
4. State what can continue independently.
5. Do not spin in a loop.

