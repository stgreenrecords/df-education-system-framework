# Decision Record - DECISION-005

## Title

Split implementation ownership into backend, frontend, and DevOps lanes

## Status

Accepted

## Date

2026-05-23

## Context

Dark Factory previously used a single `dev` role for backend, frontend, and DevOps implementation. That blocked parallel execution and caused documentation ownership conflicts for work that can be delivered independently.

## Decision

Dark Factory will use three implementation roles:

- `backend-dev`
- `frontend-dev`
- `devops`

Every task entering `READY_FOR_DEV` must be routed to exactly one implementation lane, or split by SA into lane-specific child tasks before implementation begins. Each lane has its own runtime subdashboard and lane-owned artifact folder.

## Consequences

- Backend, frontend, and DevOps agents can work in parallel when their child tasks do not share files, components, infrastructure, environments, or acceptance criteria.
- The single-role-per-session rule remains unchanged.
- QA and PO remain shared gates unless future architecture creates lane-specific QA/PO roles.
- Shared documents are owned by the active role and must not be edited opportunistically by parallel implementation roles.

## Alternatives considered

- Keep one `dev` role and add tags: rejected because it does not remove the bottleneck or shared artifact conflicts.
- Create separate state names for every lane: rejected for now because ownership plus subdashboards gives the needed routing without tripling the state machine.
- Let implementers self-select lanes: rejected because SA must control decomposition and dependency safety before parallel work starts.

## Related artifacts

- `df/artifacts/TASK-004/task.md`
- `df/artifacts/TASK-004/solution-design.md`
