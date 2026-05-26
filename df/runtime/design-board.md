# Designer Runtime Subdashboard

This is the live queue for `designer` tasks. The main board remains the overall task source of truth.

| Priority | Task ID | Parent task | Title | State | Owner role | Affected scope | Blocked? | Last updated | Next action |
|---|---|---|---|---|---|---|---|---|---|
| - | - | - | No active design tasks | NO_TASKS | designer | - | No | 2026-05-26 10:42 CEST | Await next design task; `TASK-006` was revised to include the login page and re-handed off for frontend implementation |

## Lane notes

- 2026-05-26 10:42 CEST: Revised `TASK-006` from the user's follow-up request. The design package now adds the `frontend/website` login page, documents a testable home -> login -> dashboard flow, and aligns the login-page handoff with the accepted backend identity endpoints while keeping the design queue empty after the handoff.
- 2026-05-25 18:51 local: Completed `TASK-006` from an explicit user design request. The design package now covers the `frontend/website` home page, student dashboard, and teacher dashboard as low-fidelity block schemes, including the required hero/banner CTAs `Institution selection` and `Account login`, and the task has been handed off for frontend implementation.
- New UI/UX design tasks must be added here before `designer` starts work.
- Design task documentation belongs under `df/artifacts/{task-id}/design/`; root design asset files belong under `design/{page-slug}/` with globally unique descriptive slugs.
- UI-facing frontend tasks must have a design package before `frontend-dev` implements visible UI.
- Do not track backend, frontend implementation, DevOps, or data-engineering tasks on this subdashboard.
