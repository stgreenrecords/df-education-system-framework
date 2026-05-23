# {Lane Name} Implementation Subdashboard

This is the lane-specific queue for `{lane-role}` work. The main board remains the overall task source of truth; this subdashboard records lane-local priority, ownership, scope, and handoff status.

| Priority | Task ID | Parent task | Title | State | Owner role | Affected scope | Blocked? | Last updated | Next action |
|---|---|---|---|---|---|---|---|---|---|
| P1 | TASK-001-BE | TASK-001 | Example backend task | READY_FOR_DEV | backend-dev | backend/example | No | YYYY-MM-DD HH:mm | Start backend implementation |

## Lane rules

- Only tasks owned by `{lane-role}` belong here.
- Each task here must also appear on `df/runtime/board.md` or be linked from a parent task there.
- Update this file only while acting as `{lane-role}`, SA during routing, QA during failure routing, or PO during rejection routing.
- Do not edit another lane's subdashboard.
- Lane implementation notes belong under `df/artifacts/{task-id}/{lane-folder}/`.
- For `frontend-dev`, `Affected scope` must be one of `frontend/website`, `frontend/android`, or `frontend/ios`, and UI-facing tasks must reference a designer package before implementation.
- For `data-engineer`, `Affected scope` must identify the target country/template/dataset.
- For `designer`, use owner `designer`, states `READY_FOR_DESIGN` or `DESIGN_IN_PROGRESS`, and artifact folder `df/artifacts/{task-id}/design/`.
