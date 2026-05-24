# Frontend Developer Runtime Subdashboard

This is the live queue for `frontend-dev` implementation tasks. The main board remains the overall task source of truth.

| Priority | Task ID | Parent task | Title | State | Owner role | Affected scope | Blocked? | Last updated | Next action |
|---|---|---|---|---|---|---|---|---|---|
| P0 | STORY-014 | - | Initialize website frontend application project | DONE | factory | `frontend/website`, minimal frontend-root documentation/build wiring, website-only validation paths | No | 2026-05-24 22:26 local | Accepted by PO. New session: `sa` should inspect the runtime board and select the next highest-priority actionable task. |

## Lane notes

- New frontend implementation tasks must be added here before `frontend-dev` starts work.
- Each task must identify exactly one project scope in `Affected scope`: `frontend/website`, `frontend/android`, or `frontend/ios`.
- The website project uses Next.js + React.
- Mobile application scopes (`frontend/android` and `frontend/ios`) are last-priority frontend work unless PO/SA explicitly promotes them.
- UI-facing frontend work requires a designer package under `df/artifacts/{task-id}/design/` before `frontend-dev` implements visible UI; otherwise the frontend task is blocked pending `designer`.
- Frontend implementation notes belong under `df/artifacts/{task-id}/frontend/{website|android|ios}/`.
- Do not track design, backend, DevOps, or data-engineering tasks on this subdashboard.
