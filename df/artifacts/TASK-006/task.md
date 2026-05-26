# Task - TASK-006

## Summary

Prepare the initial low-fidelity block-scheme screens for the website home page, login page, student dashboard, and teacher dashboard, including a homepage banner with the CTAs `Institution selection` and `Account login`, plus a testable initial login flow across the four pages.

## Type

Task

## Priority

P0

## Current state

BLOCKED

## Business goal

Provide a concrete, implementation-ready website design package for the first public-facing and role-specific screens so future frontend work starts from an agreed structure instead of inventing UI during implementation, while also defining the first testable website login journey.

## Acceptance criteria

- [ ] Given the website home page design, when reviewed, then it includes a homepage banner/hero with the CTAs `Institution selection` and `Account login`
- [ ] Given the initial design package, when reviewed, then low-fidelity block schemes exist for the home page, login page, student dashboard, and teacher dashboard
- [ ] Given the design package, when reviewed, then each screen includes concrete layout, states, responsive behavior, accessibility guidance, and implementation notes for `frontend/website`
- [ ] Given the requested login flow, when reviewed, then the package defines a testable path from the home page to the login page and into the supported role-specific dashboards without inventing extra page scope

## Out of scope

- High-fidelity visual design, brand system, or polished graphic assets
- Implementing the UI in `frontend/website`
- Parent, admin, institution-admin, or mobile application screens
- Final copywriting, analytics events, or backend API contract changes

## Assumptions

- This request is for initial low-fidelity structural design only, not final visual branding.
- Target scope is `frontend/website` only.
- Neutral grayscale wireframe guidance is acceptable until PO/human brand direction exists.
- The initial login page should align to the accepted backend auth shape (`username` + `password`) and may route only to the student and teacher dashboards that are currently in scope.
- Student and teacher dashboards should focus on the first practical blocks most likely needed in Phase 2 operations: summary, schedule/work queue, assignments, and status cards.

## Dependencies

- `STORY-014` for the existing website project foundation
- future frontend implementation work in `frontend/website`

## Risks

- Final product copy, branding, or IA may evolve later and require wireframe adjustments.
- Dashboard data blocks may shift slightly when future backend/domain stories define exact data contracts.

## Links

- Issue: n/a
- PR: n/a
- Design: `df/artifacts/TASK-006/design/design-package.md`

## Implementation lane

- Lane: `frontend-dev`
- Subdashboard: `df/runtime/frontend-dev-board.md`
- Artifact folder for implementation notes: `df/artifacts/TASK-006/frontend/website/`

## Role history

| Timestamp | Role | State | Summary |
|---|---|---|---|
| 2026-05-25 18:51 local | designer | OPEN -> READY_FOR_DESIGN | Created `TASK-006` from the explicit user request for initial website block-scheme screens covering the homepage, student dashboard, and teacher dashboard. |
| 2026-05-25 18:51 local | designer | READY_FOR_DESIGN -> DESIGN_IN_PROGRESS | Started the website design package after confirming the current website scope, the lack of existing design tasks, and the need for low-fidelity implementation guidance. |
| 2026-05-25 18:51 local | designer | DESIGN_IN_PROGRESS -> READY_FOR_DEV | Completed the low-fidelity design package and static wireframe guidance for `frontend/website` and handed the task to `frontend-dev` for implementation. |
| 2026-05-26 10:42 CEST | designer | READY_FOR_DEV -> READY_FOR_DEV | Revised the design package from the user's follow-up request to add the login page, align the initial login flow with the existing backend auth endpoints, and keep `frontend/website` implementation ready for four pages. |
| 2026-05-26 local | frontend-dev | READY_FOR_DEV -> DEV_IN_PROGRESS | Started the four-page `frontend/website` implementation after confirming the revised design package, accepted auth/current-user contract, and current website project structure. |
| 2026-05-26 local | frontend-dev | DEV_IN_PROGRESS -> BLOCKED | Completed the code implementation pass for the four-page website scope, but local frontend validation is blocked because this workstation does not expose `node`, `npm`, `npx`, or `corepack`. |

