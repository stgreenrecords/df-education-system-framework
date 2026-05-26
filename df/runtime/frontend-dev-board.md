# Frontend Developer Runtime Subdashboard

This is the live queue for `frontend-dev` implementation tasks. The main board remains the overall task source of truth.

| Priority | Task ID | Parent task | Title | State | Owner role | Affected scope | Blocked? | Last updated | Next action |
|---|---|---|---|---|---|---|---|---|---|
| P0 | TASK-006 | - | Prepare initial website block-scheme screens | BLOCKED | frontend-dev | `frontend/website`, home page, login page, student dashboard, teacher dashboard, initial login flow, low-fidelity website layout implementation based on designer package | Yes | 2026-05-26 local | Restore Node.js/npm availability, then rerun `frontend/website` validation (`npm install`, `npm run lint`, `npm run typecheck`, `npm run build`, manual login-flow testing) before handing the task to QA. |
| P0 | STORY-014 | - | Initialize website frontend application project | DONE | factory | `frontend/website`, minimal frontend-root documentation/build wiring, website-only validation paths | No | 2026-05-24 22:26 local | Accepted by PO. New session: `sa` should inspect the runtime board and select the next highest-priority actionable task. |

## Lane notes

- 2026-05-26 10:42 CEST: The designer revised `TASK-006` to add the `frontend/website` login page and a testable home -> login -> dashboard flow aligned with the accepted backend identity endpoints. Frontend implementation should now cover four pages without inventing extra dashboards or a different credential shape.
- 2026-05-26 local: `frontend-dev` completed the code implementation pass for the four-page website scope, including frontend-owned auth proxy handlers and role-gated dashboard shells, but the task is blocked on this workstation because `node`, `npm`, `npx`, and `corepack` are unavailable. See `df/artifacts/TASK-006/frontend/website/dev-notes.md` and `BLOCKER-033`.
- 2026-05-26 local: `frontend-dev` started implementation of `TASK-006` after confirming the revised design package, current `frontend/website` project structure, and backend auth/current-user contract. The frontend will keep the user-visible scope to the four designed pages and use frontend-owned integration wiring for the initial login flow.
- 2026-05-25 18:51 local: The designer completed `TASK-006` and handed off an implementation-ready low-fidelity package for the `frontend/website` home page, student dashboard, and teacher dashboard. Frontend implementation should use the structural package rather than inventing a new hierarchy.
- New frontend implementation tasks must be added here before `frontend-dev` starts work.
- Each task must identify exactly one project scope in `Affected scope`: `frontend/website`, `frontend/android`, or `frontend/ios`.
- The website project uses Next.js + React.
- Mobile application scopes (`frontend/android` and `frontend/ios`) are last-priority frontend work unless PO/SA explicitly promotes them.
- UI-facing frontend work requires a designer package under `df/artifacts/{task-id}/design/` before `frontend-dev` implements visible UI; otherwise the frontend task is blocked pending `designer`.
- Static design assets referenced by a design package live under root `design/{page-slug}/` folders with descriptive unique slugs.
- Frontend implementation notes belong under `df/artifacts/{task-id}/frontend/{website|android|ios}/`.
- Do not track design, backend, DevOps, or data-engineering tasks on this subdashboard.
