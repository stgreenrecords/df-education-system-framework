# Website Frontend Foundation

This directory contains the independent website frontend project for the Education System Framework.

## Scope

- Project scope: `frontend/website`
- Framework: Next.js + React
- Current purpose: low-fidelity website implementation for the home page, login page, student dashboard, teacher dashboard, and initial login flow
- Explicitly out of scope: Android initialization, iOS initialization, shared generated clients, shared design-token packages, polished brand styling, or additional role dashboards beyond student/teacher

This project now implements the accepted `TASK-006` designer package with a structurally focused, low-fidelity website UI. Styling remains intentionally neutral so the routes, layout, accessibility structure, and initial auth flow can be tested without waiting for brand-polish work.

## Independence rules

- Run website commands from `frontend/website` only.
- The website project must build without requiring any `frontend/android` or `frontend/ios` files.
- Android and iOS remain last-priority future frontend work unless SA/PO explicitly promotes them.
- If shared generated API clients or design tokens are introduced later, they must be added through an explicit shared package/process decision rather than direct cross-project imports.

## Local validation

From `frontend/website`:

```zsh
npm install
npm run lint
npm run typecheck
npm run build
```

## Local login-flow testing

The website uses frontend-owned Next.js route handlers to proxy the accepted backend identity endpoints so the browser can test the login flow through one website origin:

- `POST /api/auth/login` -> backend `POST /api/v1/identity/auth/login`
- `GET /api/auth/me` -> backend `GET /api/v1/identity/me`
- `POST /api/auth/logout` clears the website session cookie

By default, the proxy assumes the backend is available at `http://127.0.0.1:8080`.
Override that assumption when needed:

```zsh
export EDUCATION_API_BASE_URL="http://127.0.0.1:18080"
npm run dev
```

Then test the four pages and login flow:

```zsh
npm run dev
```

- Home page: `http://localhost:3000/`
- Login page: `http://localhost:3000/login`
- Student dashboard: `http://localhost:3000/student`
- Teacher dashboard: `http://localhost:3000/teacher`

If the backend is unavailable, the login page and dashboards surface the intended inline error states instead of failing silently.

## Future implementation guardrails

- UI-facing website work requires an accepted designer package under `df/artifacts/{task-id}/design/` before visible UI is implemented.
- Non-visual tasks such as tooling, generated clients, tests, or internal plumbing may proceed without designer input when SA documents that no user-visible UI is included.

