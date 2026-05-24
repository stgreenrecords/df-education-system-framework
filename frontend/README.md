# Frontend Projects

The frontend lane is split into independent project roots:

- `frontend/website` - active website project using Next.js + React
- `frontend/android` - future last-priority Android project
- `frontend/ios` - future last-priority iOS project

## Current status

`STORY-014` initializes only `frontend/website`. The website project is built and validated from its own directory and does not require Android or iOS files.

## Website-only validation

Run the website checks from `frontend/website`:

```powershell
npm install
npm run lint
npm run typecheck
npm run build
```

## Coupling guardrail

If shared generated API clients or design tokens are introduced later, they must come through an explicit shared package or routing decision. Do not create direct hidden imports between website, Android, and iOS projects.

