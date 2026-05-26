# Task - TASK-007

## Summary

Remove the `{task-id}` folder layer from root-level design asset paths and update Dark Factory documentation to use `design/{page-slug}/` instead.

## Type

Chore

## Priority

P0

## Current state

DONE

## Business goal

Simplify the shared design-asset folder convention while keeping designer task documentation under `df/artifacts/{task-id}/design/` and preserving clear handoff references for frontend implementation.

## Acceptance criteria

- [x] Active framework guidance updates the root design asset convention from `design/{task-id}/{page-slug}/` to `design/{page-slug}/`.
- [x] Designer-specific instructions explain that page-slug folders at the root design directory must use globally unique, descriptive names.
- [x] Existing `TASK-006` design documentation and handoff references now point to the flattened root design asset paths.
- [x] Existing `TASK-006` root design assets are moved from `design/TASK-006/{page-slug}/` to `design/{page-slug}/`.
- [x] No application code, schema, or API contract changes are introduced.

## Out of scope

- Changing the requirement that design task documentation stays under `df/artifacts/{task-id}/design/`.
- Reworking visual design content or wireframe markup.
- Completing QA or PO acceptance in this session.

## Assumptions

- The user request applies to the root `design/` asset convention only.
- Task-scoped provenance remains available through `df/artifacts/{task-id}/design/` and task handoff artifacts.
- A descriptive page slug is sufficient to avoid ambiguity at the root design directory when documented in the design package.

## Dependencies

- Existing designer workflow rules added in `TASK-005`.
- Existing `TASK-006` design artifacts under `design/`.

## Risks

- Removing the task namespace at the root design directory can create page-slug collisions if future tasks reuse generic names without coordination.

## Links

- Issue: n/a
- PR: n/a
- Design: `df/artifacts/TASK-007/solution-design.md`

## Role history

| Timestamp | Role | State | Summary |
|---|---|---|---|
| 2026-05-25 19:45 local | sa | OPEN -> NEEDS_ARCHITECTURE -> ARCHITECTURE_IN_PROGRESS -> READY_FOR_QA | Processed the explicit documentation request, flattened the root design asset convention to `design/{page-slug}/`, migrated `TASK-006` asset paths, and prepared the documentation-only change for QA. |
| 2026-05-26 local | qa | READY_FOR_QA -> READY_FOR_PO | Independently verified the flattened root design-asset convention, confirmed the migrated `TASK-006` references and assets, found no stale active references to the old task-layer path, and approved the documentation-only change for PO review. |
| 2026-05-26 local | po | READY_FOR_PO -> DONE | Independently validated the flattened `design/{page-slug}/` convention, confirmed the live `TASK-006` references and assets align with the simplified path structure, documented the non-UI evidence path, and accepted the task. |

