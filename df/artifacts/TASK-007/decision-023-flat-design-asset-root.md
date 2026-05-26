# Decision Record - DECISION-023

- Date: 2026-05-25
- Status: Accepted
- Owner role: SA
- Related task: TASK-007

## Context

Dark Factory currently stores root-level design assets under `design/{task-id}/{page-slug}/`. The explicit user request requires removing the `{task-id}` folder layer while keeping task-owned design documentation in `df/artifacts/{task-id}/design/`.

## Decision

Use `design/{page-slug}/` as the root-level design asset structure.

Task documentation, design packages, and handoff notes remain under `df/artifacts/{task-id}/design/`. Root-level page folders must use globally unique, descriptive slugs so the flattened structure stays understandable across tasks.

## Consequences

- Active framework guidance must reference `design/{page-slug}/` instead of `design/{task-id}/{page-slug}/`.
- Existing live design assets and references, starting with `TASK-006`, must move to the flattened structure.
- QA must verify both the wording change and the migrated asset paths.
- Provenance now relies on task artifacts plus explicit asset references rather than a task-namespaced root folder.

## Alternatives considered

- Keep `design/{task-id}/{page-slug}/`: rejected because it conflicts with the explicit user request.
- Store root design assets back inside `df/artifacts/{task-id}/design/`: rejected because the framework still requires shared design assets under the root `design/` directory.
- Flatten to `design/{page-slug}/` without a uniqueness rule: rejected because it would make collisions more likely and less governable.

## Evidence

- `AGENTS.md`
- `.github/copilot-instructions.md`
- `df/04-documentation-standards.md`
- `df/roles/designer.md`
- `df/artifacts/TASK-006/design/design-package.md`
- `df/artifacts/TASK-006/design/handoff-to-frontend.md`
- `df/artifacts/TASK-006/handoffs.md`
- `design/home-page/low-fi-wireframe.html`
- `design/student-dashboard/low-fi-wireframe.html`
- `design/teacher-dashboard/low-fi-wireframe.html`

## Follow-up actions

QA verifies there are no stale active-document references to the old task-layer path convention and confirms the flattened design folders now contain the live `TASK-006` assets.

