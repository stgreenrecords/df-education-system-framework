# Data Engineer Runtime Subdashboard

This is the live queue for `data-engineer` data population tasks. The main board remains the overall task source of truth.

| Priority | Task ID | Parent task | Title | State | Owner role | Affected scope | Blocked? | Last updated | Next action |
|---|---|---|---|---|---|---|---|---|---|
| P0 | TASK-011 | - | Prepare country-agnostic institution dataset for homepage selector | READY_FOR_QA | data-engineer | data/list-of-schools-poland and df/artifacts/TASK-011/data | No | 2026-05-26 local | `qa` validates dataset normalization, source map, and country-agnostic contract compliance |

## Lane notes

- New data population or fixture tasks must be added here before `data-engineer` starts work.
- Data-engineering artifacts belong under `df/artifacts/{task-id}/data/`.
- City, district, school, and subject names must be true and traceable to public sources.
- Teacher names, student names, and individual grade records must be fake/synthetic.
- Country template work remains data-only and must not change framework code, schema, structure, or API contracts.
