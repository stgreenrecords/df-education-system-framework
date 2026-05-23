# 04 - Dark Factory Documentation Standards

## Required runtime files

| File | Purpose |
|---|---|
| `df/runtime/board.md` | Current task queue and states. |
| `df/runtime/design-board.md` | Designer queue and state mirror for UI/UX design packages. |
| `df/runtime/backend-dev-board.md` | Backend delivery lane queue and state mirror. |
| `df/runtime/frontend-dev-board.md` | Frontend delivery lane queue and state mirror. |
| `df/runtime/devops-board.md` | DevOps delivery lane queue and state mirror. |
| `df/runtime/data-engineer-board.md` | Data-engineering lane queue and state mirror. |
| `df/runtime/activity-log.md` | Chronological record of factory activity. |
| `df/runtime/decisions.md` | Architecture/product/quality decisions. |
| `df/runtime/risks.md` | Open risks, blockers, and mitigations. |

## Required task artifact folder

Each task must have a folder:

```text
df/artifacts/{task-id}/
```

Recommended files:

```text
df/artifacts/{task-id}/
|-- task.md
|-- refinement-questions.md
|-- solution-design.md
|-- design/
|   |-- design-package.md
|   `-- handoff-to-frontend.md
|-- backend/
|   |-- dev-notes.md
|   `-- handoff-to-qa.md
|-- frontend/
|   |-- website/
|   |   |-- dev-notes.md
|   |   `-- handoff-to-qa.md
|   |-- android/
|   |   |-- dev-notes.md
|   |   `-- handoff-to-qa.md
|   `-- ios/
|       |-- dev-notes.md
|       `-- handoff-to-qa.md
|-- devops/
|   |-- dev-notes.md
|   `-- handoff-to-qa.md
|-- data/
|   |-- data-notes.md
|   |-- source-map.md
|   `-- handoff-to-qa.md
|-- qa-report.md
|-- po-review.md
|-- defects.md
|-- handoffs.md
`-- screenshots/
```

Create only the lane folders that apply to the task. A backend-only task should not create or edit `frontend/`, `devops/`, `design/`, or `data/` artifact folders.

If a task has no UI, the `screenshots/` folder may be omitted and PO must state why screenshots are not applicable.

## Implementation subdashboard format

Use `df/templates/dev-subdashboard.md` for delivery lane boards:

- `df/runtime/backend-dev-board.md`
- `df/runtime/frontend-dev-board.md`
- `df/runtime/devops-board.md`
- `df/runtime/data-engineer-board.md`

Each lane subdashboard is a queue mirror for delivery-owned tasks. The main board remains the source of overall task state; the subdashboard is the source for lane-local priority, affected scope, and lane handoff status.

Use the same table shape for `df/runtime/design-board.md`; its entries must use owner `designer` and point to `df/artifacts/{task-id}/design/`.

## Documentation ownership

Parallel implementation roles must avoid shared mutable documentation:

- `designer` writes only `df/artifacts/{task-id}/design/` for design packages, markup guidance, assets notes, and handoffs.
- `backend-dev` writes only `df/artifacts/{task-id}/backend/` for implementation notes and lane evidence.
- `frontend-dev` writes only the assigned project folder under `df/artifacts/{task-id}/frontend/website/`, `df/artifacts/{task-id}/frontend/android/`, or `df/artifacts/{task-id}/frontend/ios/` for implementation notes and lane evidence.
- `devops` writes only `df/artifacts/{task-id}/devops/` for implementation notes and lane evidence.
- `data-engineer` writes only `df/artifacts/{task-id}/data/` for data notes, source maps, fixture evidence, and data-quality checks.
- QA writes QA reports and defects after the lane handoff.
- PO writes PO review artifacts after QA passes.
- SA writes task refinement, solution design, lane routing, and architecture decisions.

Designer and delivery roles may append to `df/runtime/activity-log.md` and update their own lane subdashboard while they own the task. They must not edit another lane's subdashboard or lane artifact folder. If a lane discovers a cross-lane documentation or scope conflict, it must document the issue in its own lane notes and hand off to SA.

## Timestamp format

Use ISO-like local timestamp:

```text
YYYY-MM-DD HH:mm {timezone if known}
```

If timezone is unknown, use local system time and write `local`.

## Evidence links

Evidence can point to:

- files in this repository;
- terminal command output copied into an artifact;
- CI build URLs;
- PR URLs;
- issue tracker URLs;
- screenshot file paths;
- logs with secrets redacted.

## Markdown quality rules

- Be factual and concise.
- Separate expected vs actual behavior.
- Record assumptions explicitly.
- Record commands exactly as run.
- Redact secrets and personal data.
- Do not overwrite historical logs; append new entries.
- When correcting a previous entry, add a correction note instead of deleting history.

## Minimum activity log entry

```markdown
## {timestamp} - {role} - {task-id}

- State: {state}
- Action: {what was done}
- Evidence: {files/commands/screenshots}
- Result: {pass/fail/blocked/partial}
- Next: {next role/action}
```

## Screenshot rules

PO must capture screenshots for UI-facing changes when possible.

Screenshot evidence should include:

- final happy-path result;
- changed UI area;
- error state if relevant;
- browser/device/viewport when known;
- file path under `df/artifacts/{task-id}/screenshots/`.

If screenshot capture is impossible, PO must document:

- why it is impossible;
- what alternative evidence was used;
- whether acceptance is conditional.

## Test evidence rules

QA and implementation roles must record:

- test command;
- environment;
- result;
- failures;
- reruns;
- skipped tests and reason.

## Decision record rules

Use `df/templates/decision-record.md` for decisions that affect architecture, scope, behavior, security, data, test strategy, or deployment.
