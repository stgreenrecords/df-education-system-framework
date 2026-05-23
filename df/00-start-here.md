# 00 - Dark Factory Start Here

This is the boot sequence for every AI agent.

## Mission

Operate a self-correcting SDLC loop where AI agents deliver tasks through development, QA, and product-owner acceptance with full traceability.

## Human start command

Any of these commands starts the factory:

```text
Dark Factory: start work.
DF start.
Start the factory.
Pick up the next task.
Continue SDLC.
```

The exact wording does not matter. If the intent is to begin or continue autonomous delivery, start the loop.

## Boot sequence

1. Read this file.
2. Read `df/01-operating-model.md`.
3. Read `df/02-state-machine.md`.
4. Read `df/03-orchestration-rules.md`.
5. Read `df/04-documentation-standards.md`.
6. Read the relevant role file in `df/roles/`.
7. Inspect `df/runtime/board.md`.
8. Inspect design and delivery subdashboards when design, implementation, or data work is involved: `df/runtime/design-board.md`, `df/runtime/backend-dev-board.md`, `df/runtime/frontend-dev-board.md`, `df/runtime/devops-board.md`, and `df/runtime/data-engineer-board.md`.
9. Pick the highest-priority task that is not blocked.
10. Determine the responsible role from the task state and, for delivery states, from the owner role/lane subdashboard.
11. **Execute ONLY that one role.** Do not switch roles within this session.
12. Create or update a task folder under `df/artifacts/{task-id}/`.
13. Execute the responsible role's checklist.
14. Update runtime documentation.
15. Write a handoff note for the next role.
16. **Stop the session.** Ask the human to start a new session for the next role.

## Task selection order

Choose tasks in this order:

1. Tasks explicitly requested by the user in the current message.
2. Tasks marked `RETURNED_TO_DEV`, because rejected work must be fixed first.
3. Tasks marked `QA_FAILED` or `PO_REJECTED`.
4. Tasks marked `REFINEMENT_QUESTIONS` (PO must unblock refinement).
5. Design tasks marked `READY_FOR_DESIGN` and lane tasks marked `RETURNED_TO_DEV` or `READY_FOR_DEV` in design/delivery subdashboards, by priority and owner role.
6. Tasks marked `REFINED` (decide architecture or route to a design/delivery lane).
7. Tasks marked `INTAKE` or `REFINEMENT_IN_PROGRESS`.
8. Tasks marked `OPEN` by priority.
9. Bugs before enhancements when priority is equal.
10. Smaller unblocked task before larger task when all else is equal.

## Design and delivery lane routing

Dark Factory does not use a single generic developer queue. SA must route every design, implementation, or data-ready task to exactly one lane:

- `designer` -> `df/runtime/design-board.md`
- `backend-dev` -> `df/runtime/backend-dev-board.md`
- `frontend-dev` -> `df/runtime/frontend-dev-board.md`
- `devops` -> `df/runtime/devops-board.md`
- `data-engineer` -> `df/runtime/data-engineer-board.md`

If design, backend, frontend, DevOps, and data work can be done independently, SA must split the parent task into lane-specific child tasks and put each child on the matching subdashboard. If the scopes share files, infrastructure, environments, data fixtures, or acceptance criteria, document the dependency and keep the blocked lane from starting until the shared decision is resolved.

Frontend UI implementation must not start without designer input. For UI-facing frontend work, SA should route a design task first or attach an accepted design package before moving frontend implementation to `READY_FOR_DEV`. If `frontend-dev` discovers missing design input later, it must mark the frontend work `BLOCKED` and request `designer` action.

## If there is no board yet

Create `df/runtime/board.md` from `df/templates/board.md`. Add an initial task if the user provided one. If no task exists, record `NO_TASKS` and ask the human to add a task.

## If requirements are unclear

Do not invent critical business requirements. Add questions to the task artifact and mark the task `BLOCKED` if the ambiguity prevents safe work. If a reasonable assumption can be made safely, document it and continue.

## Factory heartbeat

At the end of every agent turn, document:

- current role;
- task id;
- current state;
- actions completed;
- evidence produced;
- next role/action;
- blockers, risks, and assumptions.

