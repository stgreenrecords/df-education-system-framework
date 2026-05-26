# Solution Design - TASK-007

## Summary

Update the Dark Factory documentation and current design artifacts so root-level design asset files use `design/{page-slug}/` instead of `design/{task-id}/{page-slug}/`.

## Context

The current framework guidance and `TASK-006` design package reference a task-scoped root asset structure. The explicit user request requires removing the `{task-id}` folder layer while keeping the broader designer workflow intact.

## Requirements and acceptance criteria

- Replace the active framework convention `design/{task-id}/{page-slug}/` with `design/{page-slug}/`.
- Preserve task documentation ownership in `df/artifacts/{task-id}/design/`.
- Update the live `TASK-006` design package and handoff evidence to the new path structure.
- Move the current `TASK-006` root design assets to the flattened structure.
- Avoid application code, schema, API, or delivery-lane changes.

## Proposed solution

1. Update the active global and role-specific documentation files that define the designer asset structure.
2. Add explicit guidance that page-slug folders under `design/` must use globally unique, descriptive names.
3. Move the existing `TASK-006` root design assets from `design/TASK-006/{page-slug}/` to `design/{page-slug}/`.
4. Update the `TASK-006` design package and handoff references to match the flattened paths.
5. Record the convention change in runtime documentation, a decision record, and the task artifact.

Because this is a framework/process-only documentation change, no delivery lane applies after SA completion; the task moves directly to `READY_FOR_QA` for independent verification.

## Files/components likely affected

- `AGENTS.md`
- `.github/copilot-instructions.md`
- `df/04-documentation-standards.md`
- `df/roles/designer.md`
- `df/artifacts/TASK-006/design/design-package.md`
- `df/artifacts/TASK-006/design/handoff-to-frontend.md`
- `df/artifacts/TASK-006/handoffs.md`
- `design/**`
- `df/runtime/board.md`
- `df/runtime/design-board.md`
- `df/runtime/frontend-dev-board.md`
- `df/runtime/activity-log.md`
- `df/runtime/decisions.md`
- `df/runtime/risks.md`

## Data/API contract changes

- None.

## Security/privacy considerations

- None beyond preserving the existing rule not to expose secrets or personal data in documentation.

## Test strategy

- Search the active framework documentation for stale `design/{task-id}/{page-slug}/` references.
- Search the current task artifacts for stale `design/TASK-006/` references.
- Verify the flattened root design folders exist and the `design/TASK-006/` folder no longer contains active assets.

## Risks and mitigations

- Root-level page-slug collisions may occur in future tasks.
  - Mitigation: require globally unique, descriptive page slugs and document the exact asset paths in each task’s design package and handoff.

## Rollback plan

- Restore the previous documentation wording.
- Move `TASK-006` assets back under `design/TASK-006/{page-slug}/`.
- Revert the new decision/risk/runtime entries.

## Open questions

- None for SA execution. QA should confirm the updated convention is applied consistently across active framework documents and live `TASK-006` references.

