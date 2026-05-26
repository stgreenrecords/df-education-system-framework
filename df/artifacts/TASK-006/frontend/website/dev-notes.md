# Frontend Dev Notes - TASK-006 (`frontend/website`)

## Summary

Implemented the first low-fidelity website pass for the four designed pages:

- home page: `frontend/website/app/page.tsx`
- login page: `frontend/website/app/login/page.tsx`
- student dashboard: `frontend/website/app/student/page.tsx`
- teacher dashboard: `frontend/website/app/teacher/page.tsx`

The implementation follows the accepted designer package and keeps styling intentionally neutral. The initial login flow is wired through frontend-owned Next.js route handlers so the browser can exercise the accepted backend identity endpoints through one website origin:

- website `POST /api/auth/login` -> backend `POST /api/v1/identity/auth/login`
- website `GET /api/auth/me` -> backend `GET /api/v1/identity/me`
- website `POST /api/auth/logout` clears the session cookie

## Files changed

- `frontend/website/app/page.tsx`
- `frontend/website/app/layout.tsx`
- `frontend/website/app/globals.css`
- `frontend/website/app/login/page.tsx`
- `frontend/website/app/student/page.tsx`
- `frontend/website/app/teacher/page.tsx`
- `frontend/website/app/api/auth/login/route.ts`
- `frontend/website/app/api/auth/me/route.ts`
- `frontend/website/app/api/auth/logout/route.ts`
- `frontend/website/components/login-form.tsx`
- `frontend/website/components/dashboard-page.tsx`
- `frontend/website/lib/auth.ts`
- `frontend/website/lib/backend.ts`
- `frontend/website/env.d.ts`
- `frontend/website/README.md`

## Implemented behavior

### Home page

- Added the required hero/banner with the exact CTAs:
  - `Institution selection`
  - `Account login`
- Added the institution selection preview block, value blocks, audience pathways, support/deployment notes, and footer placeholders from the design package.
- Kept the structure semantic with `header`, `nav`, `main`, and `section` landmarks.

### Login page

- Added the dedicated login page shell with intro/trust panel and sign-in form.
- Implemented `Username` and `Password` fields with native form submission.
- Implemented inline states for:
  - invalid credentials
  - unsupported authenticated role
  - service/network failure
  - logged-out confirmation
- Preserved typed username/password across retry-worthy error states until the user changes them.

### Student and teacher dashboards

- Added dedicated landing pages for the student and teacher routes.
- Implemented summary-card rows, content blocks, quick actions, and sign-out behavior.
- Added loading, unauthorized, wrong-dashboard, unsupported-role, and error states in the shared dashboard shell.
- Left dashboard content placeholder-driven and structural as allowed by the design handoff.

### Auth flow

- Added frontend-owned proxy route handlers to avoid browser cross-origin dependency on direct backend calls from the website origin.
- On login success, the website stores the backend bearer token in an HTTP-only cookie.
- Dashboard routes resolve current-user context through `/api/auth/me` and gate rendering by supported role:
  - `student` -> `/student`
  - `teacher` -> `/teacher`
  - any other authenticated role -> login-page unsupported-role message

## Assumptions

- Local testing assumes the backend is reachable at `http://127.0.0.1:8080` unless `EDUCATION_API_BASE_URL` overrides it.
- Only student and teacher post-login dashboards are in scope for this task.
- The initial website pass prioritizes structural testability over brand styling or high-fidelity visuals.

## Validation attempted

### Static/editor diagnostics

- `get_errors` reported no errors for the added page, route-handler, layout, CSS, and environment files.
- JetBrains still reported non-blocking local warnings on exported component/helper files and JSX prop resolution in the new dashboard pages, but not concrete compile failures in the changed page/route files.

### Terminal commands attempted

```text
cd "/Users/Viachaslau_Karnaushanka/Downloads/DF Education System Framework/frontend/website" && npm install && npm run lint && npm run typecheck && npm run build
```

Result:

```text
zsh: command not found: npm
```

Environment availability check:

```text
command -v node
command -v npm
command -v npx
command -v corepack
which -a node
which -a npm
```

Result:

```text
node not found
npm not found
```

## Blocker

`BLOCKER-033`: This workstation does not currently expose `node`, `npm`, `npx`, or `corepack`, so frontend runtime validation could not be completed.

What is blocked:

- dependency installation
- lint execution
- type-check execution through `tsc`
- Next.js production build
- local dev-server startup for manual browser testing of the four pages and login flow

What can continue independently:

- code review and static artifact review
- runtime resumption on a workstation/shell with Node.js 20+ and npm available

## Recommended resume commands

From `frontend/website` after Node.js/npm are available:

```zsh
npm install
npm run lint
npm run typecheck
npm run build
npm run dev
```

Then manually verify:

- `/`
- `/login`
- `/student`
- `/teacher`
- home -> login -> supported dashboard flow against the running backend

