# Handoff - TASK-006

## designer -> frontend-dev

- Timestamp: 2026-05-25 18:51 local
- Task: TASK-006
- From state: DESIGN_IN_PROGRESS
- To state: READY_FOR_DEV
- Lane: frontend-dev
- Summary: Completed the initial low-fidelity website design package for the home page, student dashboard, and teacher dashboard, including the required homepage banner CTAs `Institution selection` and `Account login`.

## Evidence

- `df/artifacts/TASK-006/task.md`
- `df/artifacts/TASK-006/design/design-package.md`
- `design/home-page/low-fi-wireframe.html`
- `design/student-dashboard/low-fi-wireframe.html`
- `design/teacher-dashboard/low-fi-wireframe.html`
- `df/artifacts/TASK-006/design/handoff-to-frontend.md`
- `frontend/README.md`
- `frontend/website/README.md`
- `frontend/website/app/page.tsx`

## Tests/checks

| Check | Command/source | Result | Notes |
|---|---|---|---|
| Runtime/design board review | `df/runtime/board.md`; `df/runtime/design-board.md` | PASS | No active design task existed before this explicit request; the task was created and documented for the designer lane |
| Frontend scope review | `frontend/README.md`; `frontend/website/README.md`; `frontend/website/app/page.tsx` | PASS | Confirmed the current scope is `frontend/website` and that user-visible UI requires a design package first |
| Design completeness review | `df/artifacts/TASK-006/design/design-package.md`; page-specific wireframes under `design/{home-page,student-dashboard,teacher-dashboard}/` | PASS | Package includes screen structure, CTA placement, states, responsive behavior, accessibility, copy, and frontend handoff notes with per-page asset separation in the root design folder |

## Known risks

- Final brand direction and product copy may later refine the low-fidelity structure.
- Dashboard data priorities may shift slightly once later product and API stories are implemented.

## Next role instructions

- `frontend-dev` should implement the low-fidelity website pages in `frontend/website` using the design package without inventing a new page hierarchy.
- Preserve the home page hero CTAs exactly as `Institution selection` and `Account login` unless PO/human later changes the copy.
- Implement loading, empty, and error states with the initial page skeletons.
- Keep this implementation limited to the three screens covered by the package.

## Blockers

- None.

## frontend-dev -> factory (blocked)

- Timestamp: 2026-05-26 local
- Task: TASK-006
- From state: DEV_IN_PROGRESS
- To state: BLOCKED
- Lane: frontend-dev (`frontend/website`)
- Summary: Implemented the four-page low-fidelity website scope plus frontend-owned auth proxy wiring, but local validation is blocked because this workstation does not expose `node`, `npm`, `npx`, or `corepack`.

## Evidence

- `df/artifacts/TASK-006/frontend/website/dev-notes.md`
- `frontend/website/app/page.tsx`
- `frontend/website/app/login/page.tsx`
- `frontend/website/app/student/page.tsx`
- `frontend/website/app/teacher/page.tsx`
- `frontend/website/components/login-form.tsx`
- `frontend/website/components/dashboard-page.tsx`
- `frontend/website/app/api/auth/login/route.ts`
- `frontend/website/app/api/auth/me/route.ts`
- `frontend/website/app/api/auth/logout/route.ts`
- `frontend/website/lib/auth.ts`
- `frontend/website/lib/backend.ts`
- `frontend/website/app/globals.css`
- `frontend/website/README.md`
- `df/runtime/risks.md`

## Tests/checks

| Check | Command/source | Result | Notes |
|---|---|---|---|
| Design/contract alignment review | `df/artifacts/TASK-006/design/design-package.md`; `df/artifacts/TASK-006/design/handoff-to-frontend.md`; backend auth/current-user controllers | PASS | Confirmed the frontend implementation follows the revised four-page package and the accepted backend login/current-user endpoints |
| Static diagnostics review | `get_errors` on the changed `frontend/website` pages, route handlers, layout, CSS, and env typing files | PASS with warnings | No concrete errors were reported on the changed page/route/layout/CSS files; JetBrains still showed non-blocking local warnings on exported component/helper files |
| Frontend terminal validation | `cd frontend/website && npm install && npm run lint && npm run typecheck && npm run build` | BLOCKED | `zsh: command not found: npm` |
| Environment availability check | `command -v node`; `command -v npm`; `command -v npx`; `command -v corepack`; `which -a node`; `which -a npm` | BLOCKED | None of the required Node/npm entrypoints are available in the current shell environment |

## Known risks

- `BLOCKER-033`: local frontend validation cannot run until Node.js/npm are available.
- The default frontend proxy assumption is backend base URL `http://127.0.0.1:8080` unless `EDUCATION_API_BASE_URL` overrides it.

## Next role instructions

- Do not send this task to QA yet.
- Restore Node.js 20+ and npm availability on the workstation or resume `frontend-dev` on another machine/shell where those tools exist.
- From `frontend/website`, run:
  - `npm install`
  - `npm run lint`
  - `npm run typecheck`
  - `npm run build`
  - `npm run dev`
- Then manually verify the four pages and the home -> login -> supported-dashboard flow against the running backend before returning the task to `READY_FOR_QA`.

## Blockers

- `BLOCKER-033`

## designer -> frontend-dev (design revision)

- Timestamp: 2026-05-26 10:42 CEST
- Task: TASK-006
- From state: READY_FOR_DEV
- To state: READY_FOR_DEV
- Lane: frontend-dev
- Summary: Expanded the existing website design package from three pages to four pages by adding a dedicated login-page wireframe and documenting a testable home -> login -> dashboard flow aligned with the accepted backend identity endpoints.

## Evidence

- `df/artifacts/TASK-006/task.md`
- `df/artifacts/TASK-006/design/design-package.md`
- `design/home-page/low-fi-wireframe.html`
- `design/login-page/low-fi-wireframe.html`
- `design/student-dashboard/low-fi-wireframe.html`
- `design/teacher-dashboard/low-fi-wireframe.html`
- `df/artifacts/TASK-006/design/handoff-to-frontend.md`
- `backend/identity-access/src/main/java/com/darkfactory/education/identityaccess/auth/AuthenticationController.java`
- `backend/identity-access/src/main/java/com/darkfactory/education/identityaccess/auth/IdentityUserController.java`

## Tests/checks

| Check | Command/source | Result | Notes |
|---|---|---|---|
| Existing auth contract review | `AuthenticationController`; `LoginRequest`; `LoginResponse`; `IdentityUserController`; `CurrentUserResponse` | PASS | Confirmed the accepted backend login flow uses `POST /api/v1/identity/auth/login` with `username` + `password` and exposes current-user context at `GET /api/v1/identity/me` |
| Frontend scope review | `frontend/website/package.json`; `frontend/website/README.md`; `frontend/website/app/page.tsx` | PASS | Confirmed the current implementation target remains `frontend/website` and that visible UI work should stay within one isolated Next.js project |
| Design package revision review | `df/artifacts/TASK-006/design/design-package.md`; `design/login-page/low-fi-wireframe.html` | PASS | Added the missing login-page structure, login-specific states, accessibility notes, and role-routing handoff guidance without expanding to unsupported extra screens |

## Known risks

- Only student and teacher post-login dashboards are in scope for this first website pass; authenticated users with other roles need a scoped unsupported-role message until later UI tasks exist.
- The backend login response returns a token, so the frontend needs the follow-up current-user request to decide the correct dashboard route.
- Dashboard body content may remain placeholder-driven until later backend/domain UI stories define exact data contracts.

## Next role instructions

- `frontend-dev` should implement the four-page `frontend/website` scope: home page, login page, student dashboard, and teacher dashboard.
- Preserve the home page hero CTAs exactly as `Institution selection` and `Account login`.
- Implement the initial login flow using the accepted backend endpoints where feasible:
  - `POST /api/v1/identity/auth/login`
  - `GET /api/v1/identity/me`
- Route supported student users to the student dashboard and supported teacher users to the teacher dashboard.
- If the authenticated role is outside the current UI scope, show the login-page unsupported-role state instead of inventing more dashboards.
- Keep the first implementation visually neutral and omit CSS-polish work beyond whatever minimal structure the existing project already requires.

## Blockers

- None.

