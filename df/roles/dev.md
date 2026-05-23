# Role: Developer (`dev`) - Retired Alias

## Status

The generic `dev` role is retired for new Dark Factory work.

Use one of the lane-specific design/delivery roles instead:

- `designer` in `df/roles/designer.md`
- `backend-dev` in `df/roles/backend-dev.md`
- `frontend-dev` in `df/roles/frontend-dev.md`
- `devops` in `df/roles/devops.md`
- `data-engineer` in `df/roles/data-engineer.md`

## Compatibility rule

Historical tasks and artifacts may still mention `dev`. Do not update those records unless the current task explicitly migrates them.

For new or resumed design, implementation, or data work:

1. Inspect `df/runtime/board.md` and the design/delivery subdashboards.
2. Determine the lane owner from `Owner role`.
3. Read the matching lane role file.
4. Execute only that lane role in the current session.

If a task is still assigned to `dev`, stop and hand off to SA to route it to `designer`, `backend-dev`, `frontend-dev`, `devops`, or `data-engineer`.
