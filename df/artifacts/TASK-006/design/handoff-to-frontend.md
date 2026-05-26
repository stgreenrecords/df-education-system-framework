# Design Handoff to Frontend - TASK-006

## Summary

The initial low-fidelity website design package is complete for four screens:

- home page
- login page
- student dashboard
- teacher dashboard

The package is intentionally structural and implementation-oriented. It gives `frontend-dev` a clear first-pass page architecture without requiring final brand polish.

## Target frontend scope

- `frontend/website`

## Key requirements to preserve

- The home page hero/banner must include the two CTAs exactly as requested:
  - `Institution selection`
  - `Account login`
- The home page `Account login` path should take the user into the dedicated login page rather than directly to a dashboard.
- The login page should use the existing backend credential shape: `username` + `password`.
- The initial website login flow should be testable end to end across the four pages: home -> login -> supported dashboard.
- Student and teacher dashboards should be implemented as role-specific landing pages with summary cards, task-oriented content blocks, and quick actions.
- Loading, empty, and error states are part of the design scope and should not be deferred.

## Files to use

- `df/artifacts/TASK-006/design/design-package.md`
- `design/home-page/low-fi-wireframe.html`
- `design/login-page/low-fi-wireframe.html`
- `design/student-dashboard/low-fi-wireframe.html`
- `design/teacher-dashboard/low-fi-wireframe.html`

## Implementation guidance

- Start with layout shells and reusable page-section/card primitives.
- Keep the first implementation visually neutral if no further brand package exists.
- Use semantic HTML landmarks and heading structure from the design package.
- Make the home page hero, login form shell, and dashboard summary cards responsive from the start.
- Prefer the accepted backend auth endpoints for the first login-flow implementation:
  - `POST /api/v1/identity/auth/login`
  - `GET /api/v1/identity/me`
- Route authenticated users only to the two dashboards that are in scope for this task:
  - supported student user -> student dashboard
  - supported teacher user -> teacher dashboard
- If authentication succeeds for another role type, keep the user on the login page and show the scoped unsupported-role message instead of inventing extra screens.
- Do not invent additional public pages or role dashboards in the same task.

## Frontend assumptions to respect

- This package targets `frontend/website` only.
- The wireframes are low-fidelity and may evolve once product copy or branding is finalized.
- Dashboard content may remain mocked or placeholder-driven during the first UI implementation.
- The login form should align to the already accepted backend auth contract instead of using a made-up credential shape.

## Ready-for-dev statement

The design package is sufficiently concrete for `frontend-dev` to implement a first website UI skeleton without guessing page hierarchy, CTA placement, login-form structure, dashboard blocks, role-routing expectations, or baseline states.

