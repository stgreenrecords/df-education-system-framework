# Frontend Developer Runtime Subdashboard

This is the live queue for `frontend-dev` implementation tasks. The main board remains the overall task source of truth.

| Priority | Task ID | Parent task | Title | State | Owner role | Affected scope | Blocked? | Last updated | Next action |
|---|---|---|---|---|---|---|---|---|---|
| - | - | - | No active frontend delivery lane tasks | NO_TASKS | frontend-dev | - | No | 2026-05-23 11:25 local | Await SA-routed frontend task |

## Lane notes

- New frontend implementation tasks must be added here before `frontend-dev` starts work.
- Each task must identify exactly one project scope in `Affected scope`: `frontend/website`, `frontend/android`, or `frontend/ios`.
- The website project uses Next.js + React.
- Mobile application scopes (`frontend/android` and `frontend/ios`) are last-priority frontend work unless PO/SA explicitly promotes them.
- UI-facing frontend work requires a designer package under `df/artifacts/{task-id}/design/` before `frontend-dev` implements visible UI; otherwise the frontend task is blocked pending `designer`.
- Frontend implementation notes belong under `df/artifacts/{task-id}/frontend/{website|android|ios}/`.
- Do not track design, backend, DevOps, or data-engineering tasks on this subdashboard.
