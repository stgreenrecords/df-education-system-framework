# Decision Record - DECISION-006

## Title

Frontend lane uses independent website, Android, and iOS projects

## Status

Accepted

## Date

2026-05-23

## Context

The frontend implementation lane was created as part of `TASK-004`, but the project structure still treated `frontend/` as a single broad area. The user explicitly requested three independent frontend subprojects and selected Next.js + React for the website frontend.

## Decision

The `frontend-dev` lane contains three independent project scopes:

- `frontend/website` - website application using Next.js + React.
- `frontend/android` - Android mobile application project.
- `frontend/ios` - iOS mobile application project.

Frontend work must be routed to exactly one of these scopes unless SA splits a feature into platform-specific child tasks. Each project must be independently buildable, testable, and deployable.

Priority amendment:

- `frontend/website` is the first frontend implementation target.
- `frontend/android` and `frontend/ios` are last-priority frontend work unless PO/SA explicitly promotes them.

## Consequences

- Website work can proceed independently from Android and iOS work.
- Android and iOS application foundations can evolve independently.
- Cross-platform features should be split into child tasks such as `{parent-id}-WEB`, `{parent-id}-AND`, and `{parent-id}-IOS`.
- Mobile child tasks default to the last priority band.
- Shared functionality must be explicit through backend APIs, OpenAPI contracts, generated clients, design tokens, or separately approved shared packages.
- Hidden direct source coupling between website, Android, and iOS projects is not allowed.

## Alternatives considered

- Keep one frontend project: rejected because it blocks independent website/mobile work.
- Use one cross-platform mobile/web codebase: rejected for now because the user explicitly requested independent website, Android, and iOS projects.
- Create separate Dark Factory roles for website, Android, and iOS: deferred; current need is independent project structure within the `frontend-dev` lane.

## Related artifacts

- `df/artifacts/TASK-004/task.md`
- `df/artifacts/TASK-004/solution-design.md`
- `df/backlog/architecture-direction.md`
- `df/backlog/user-stories.md` (`STORY-014`)
