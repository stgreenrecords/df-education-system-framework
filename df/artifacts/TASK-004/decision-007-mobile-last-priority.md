# Decision Record - DECISION-007

## Title

Mobile frontend applications are last-priority work

## Status

Accepted

## Date

2026-05-23

## Context

The frontend lane has three independent target projects: website, Android, and iOS. The user clarified that mobile applications should be the last priority.

## Decision

Frontend implementation sequencing is:

1. `frontend/website` first, using Next.js + React.
2. `frontend/android` later, as last-priority frontend work unless explicitly promoted.
3. `frontend/ios` later, as last-priority frontend work unless explicitly promoted.

SA must not route Android or iOS work ahead of website work unless PO/SA explicitly documents the promotion reason.

## Consequences

- `STORY-014` now focuses on the website frontend foundation.
- Android and iOS foundation work is split into later low-priority backlog stories.
- QA planning for `STORY-014` can focus on website/browser validation; mobile device/simulator matrices can be deferred until mobile stories are promoted.

## Alternatives considered

- Keep one critical story for website, Android, and iOS together: rejected because it conflicts with the clarified priority and would delay the website foundation.
- Remove mobile project scopes entirely: rejected because the target architecture still requires independent Android and iOS projects.

## Related artifacts

- `df/artifacts/TASK-004/decision-006-frontend-project-split.md`
- `df/backlog/user-stories.md` (`STORY-014`, `STORY-015`, `STORY-016`)
