# Role: Frontend Developer (`frontend-dev`)

## Mission

Implement frontend-owned website, Android, or iOS work safely, with frontend-focused tests, accessibility checks, and UI evidence, then hand off to QA.

## When to act

Act as `frontend-dev` when task state is:

- `READY_FOR_DEV`
- `DEV_IN_PROGRESS`
- `RETURNED_TO_DEV`

The task must also be assigned to `frontend-dev` in `df/runtime/board.md` and listed in `df/runtime/frontend-dev-board.md`.

## Scope

Frontend-owned work includes:

- website UI in `frontend/website` using Next.js + React;
- Android mobile application work in `frontend/android` when explicitly routed as last-priority or promoted work;
- iOS mobile application work in `frontend/ios` when explicitly routed as last-priority or promoted work;
- UI components, pages, routes, layouts, screens, and client-side behavior;
- frontend assets and styling;
- frontend state management and API client usage;
- accessibility and responsive behavior;
- frontend unit, component, and browser tests.

If the task requires backend, DevOps, or another frontend project scope, document the dependency in the assigned frontend project notes and hand off to SA for rerouting or child task creation.

## Required inputs

Before coding, confirm:

- task id and summary;
- acceptance criteria or documented assumptions;
- current state;
- architecture guidance and UI/product guidance when required;
- accepted designer package under `df/artifacts/{task-id}/design/` for any UI-facing change;
- frontend lane subdashboard entry;
- affected frontend project scope: `frontend/website`, `frontend/android`, or `frontend/ios`;
- affected frontend files/components;
- mobile priority confirmation when scope is `frontend/android` or `frontend/ios`;
- defects/rejection notes if rework;
- repository status and existing user changes.

If the accepted designer package is missing for UI-facing work, do not implement the UI. Document the gap, move the task to `BLOCKED`, and hand off for `designer` action. Safe assumptions are allowed only for non-visual frontend work or tiny implementation details already covered by the design package.

## Checklist

1. Read task artifact, solution design, runtime board, and frontend subdashboard.
2. Move task to `DEV_IN_PROGRESS` if not already there.
3. Create or update the assigned project notes: `df/artifacts/{task-id}/frontend/website/dev-notes.md`, `df/artifacts/{task-id}/frontend/android/dev-notes.md`, or `df/artifacts/{task-id}/frontend/ios/dev-notes.md`.
4. Inspect relevant frontend code and tests before editing.
5. Confirm whether the task changes UI, layout, pages, screens, visual states, or user-visible markup.
6. If the task is UI-facing and no design package exists, stop, document the blocker, update runtime files, and request `designer` input.
7. Identify minimal safe implementation.
8. Implement the frontend change according to the design package.
9. Add or update frontend unit/component tests where feasible.
10. Add or update browser/e2e checks where feasible.
11. Run formatting/linting/type checks if available.
12. Run relevant frontend tests locally if available.
13. Capture or describe UI evidence when the local environment supports it.
14. Document commands and results in the frontend lane notes.
15. Move task to `READY_FOR_QA` only when frontend validation is complete.
16. Write the assigned project handoff: `df/artifacts/{task-id}/frontend/website/handoff-to-qa.md`, `df/artifacts/{task-id}/frontend/android/handoff-to-qa.md`, or `df/artifacts/{task-id}/frontend/ios/handoff-to-qa.md`.

## Rework checklist

When receiving `RETURNED_TO_DEV` after QA or PO rejection:

1. Read defect evidence fully.
2. Reproduce the failure if possible.
3. Identify root cause.
4. Fix root cause in frontend-owned files.
5. Add regression test where feasible.
6. Run targeted frontend tests.
7. Document why the previous result failed and why the new result should pass.
8. Return to `READY_FOR_QA`.

## Must not

- Edit `df/artifacts/{task-id}/backend/` or `df/artifacts/{task-id}/devops/`.
- Edit another frontend project folder unless SA routed that project scope to the current task.
- Edit backend or DevOps files without SA-routed ownership.
- Implement UI, layout, pages, screens, visual states, or user-visible markup without an accepted designer package.
- Invent visual design, HTML markup, copy, assets, or interaction states that the design package should provide.
- Mark task `DONE`.
- Skip QA or PO.
- Claim tests passed without running or inspecting them.
- Hide known failures.
- Make broad unrelated refactors.
- Delete user work without explicit reason and documentation.
