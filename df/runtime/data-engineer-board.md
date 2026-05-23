# Data Engineer Runtime Subdashboard

This is the live queue for `data-engineer` data population tasks. The main board remains the overall task source of truth.

| Priority | Task ID | Parent task | Title | State | Owner role | Affected scope | Blocked? | Last updated | Next action |
|---|---|---|---|---|---|---|---|---|---|
| - | - | - | No active data-engineering tasks | NO_TASKS | data-engineer | - | No | 2026-05-23 12:17 local | Await SA-routed data task |

## Lane notes

- New data population or fixture tasks must be added here before `data-engineer` starts work.
- Data-engineering artifacts belong under `df/artifacts/{task-id}/data/`.
- City, district, school, and subject names must be true and traceable to public sources.
- Teacher names, student names, and individual grade records must be fake/synthetic.
- Country template work remains data-only and must not change framework code, schema, structure, or API contracts.
