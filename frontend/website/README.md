# Website Frontend Foundation

This directory contains the independent website frontend project for the Education System Framework.

## Scope

- Project scope: `frontend/website`
- Framework: Next.js + React
- Current purpose: non-visual project foundation only
- Explicitly out of scope: product pages, designed user flows, Android initialization, iOS initialization, shared generated clients, or shared design-token packages

The current scaffold is intentionally minimal so later website work starts from a correctly isolated project boundary without inventing user-facing product UI before a designer package exists.

## Independence rules

- Run website commands from `frontend/website` only.
- The website project must build without requiring any `frontend/android` or `frontend/ios` files.
- Android and iOS remain last-priority future frontend work unless SA/PO explicitly promotes them.
- If shared generated API clients or design tokens are introduced later, they must be added through an explicit shared package/process decision rather than direct cross-project imports.

## Local validation

From `frontend/website`:

```powershell
npm install
npm run lint
npm run typecheck
npm run build
```

## Future implementation guardrails

- UI-facing website work requires an accepted designer package under `df/artifacts/{task-id}/design/` before visible UI is implemented.
- Non-visual tasks such as tooling, generated clients, tests, or internal plumbing may proceed without designer input when SA documents that no user-visible UI is included.

