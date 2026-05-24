# Task - TASK-004

## Summary

Split the single Dark Factory developer role into backend developer, frontend developer, and DevOps implementation lanes with independent subdashboards and artifact ownership.

## Type

Chore

## Priority

P0

## Current state

DONE

## Business goal

Enable backend, frontend, and DevOps implementation work to proceed in parallel without forcing unrelated work through one shared developer queue or shared mutable documentation files.

## Acceptance criteria

- [x] Dark Factory role documentation defines separate backend developer, frontend developer, and DevOps roles.
- [x] Development work is routed to one of three implementation subdashboards when it becomes ready for development.
- [x] The framework supports independent backend, frontend, and DevOps child tasks that can run in parallel when file/component scope does not overlap.
- [x] Documentation ownership rules prevent parallel implementation roles from editing the same lane-specific notes, handoffs, or evidence files.
- [x] State-machine and orchestration guidance explain how `READY_FOR_DEV`, `DEV_IN_PROGRESS`, `READY_FOR_QA`, and rework apply to lane-owned implementation tasks.
- [x] Runtime files are updated with the task state, decision, handoff, and current subdashboard structure.
- [x] Frontend lane architecture defines independent website, Android, and iOS project scopes.
- [x] Website frontend architecture uses Next.js + React.
- [x] Android and iOS mobile frontend work is explicitly last priority unless promoted.

## Out of scope

- Reworking existing application code.
- Completing QA or PO acceptance for this framework change in the same session.
- Changing the single-role-per-session rule.

## Assumptions

- Existing implementation states can remain stable if ownership is routed through `Owner role` and the lane subdashboards.
- QA and PO remain shared gates after each lane task, unless a parent task intentionally coordinates multiple child lane tasks.
- Existing historical artifacts may keep old `dev` references; active instructions and new tasks must use the lane-specific roles.
- Frontend project implementation remains future `frontend-dev` work; this SA addendum records architecture and routing only.
- Mobile application implementation is intentionally deferred behind website frontend work.

## Dependencies

- None.

## Risks

- Existing active tasks may still reference `dev` until they are migrated or completed.
- Parallel task safety depends on SA decomposing scopes clearly and each lane honoring artifact ownership.

## Links

- Issue: n/a
- PR: n/a
- Design: df/artifacts/TASK-004/solution-design.md

## Role history

| Timestamp | Role | State | Summary |
|---|---|---|---|
| 2026-05-23 11:25 local | sa | OPEN -> NEEDS_ARCHITECTURE | Explicit user request created a framework architecture task for splitting developer responsibilities. |
| 2026-05-23 11:25 local | sa | NEEDS_ARCHITECTURE -> ARCHITECTURE_IN_PROGRESS | Started architecture update because the change affects workflow, state ownership, runtime boards, and role files. |
| 2026-05-23 11:25 local | sa | ARCHITECTURE_IN_PROGRESS -> READY_FOR_QA | Designed and documented the lane-based implementation model and prepared QA handoff for framework documentation verification. |
| 2026-05-23 11:38 local | sa | READY_FOR_QA | Added SA architecture addendum: frontend lane has independent `website`, `android`, and `ios` project scopes; website uses Next.js + React. |
| 2026-05-23 11:46 local | sa | READY_FOR_QA | Added priority addendum: website frontend first; Android and iOS mobile applications are last-priority work unless promoted. |
| 2026-05-24 local | qa | READY_FOR_QA → READY_FOR_PO | QA independently verified all 9 acceptance criteria. All checks PASS. |
| 2026-05-24 local | po | READY_FOR_PO → DONE | PO accepted. All 9 acceptance criteria confirmed. Documentation-only change; no screenshots required. |
