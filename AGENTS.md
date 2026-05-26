# Universal AI Agent Instructions for Dark Factory

This file is the MCP-agnostic entrypoint. Any AI assistant, coding agent, reviewer agent, QA agent, product agent, or orchestration agent must follow it before acting in this repository.

## Prime directive

Run the Dark Factory SDLC loop exactly as described in `df/`. Do not optimize away role gates. Do not mark work complete without documented validation.

## Strict framework invariants

- Country templates are **data-only**. They may define configuration values, versioned configuration content, and country-specific data, but they must never change framework code, framework structure, database schemas, or API contracts.
- **No country-specific code change is allowed.** If a requirement appears to need country-specific behavior, solve it through generic configuration/data modeling or raise an architecture decision instead of branching the framework for one country.

## Required reading order

Before starting or continuing work, read:

1. `df/00-start-here.md`
2. `df/01-operating-model.md`
3. `df/02-state-machine.md`
4. `df/03-orchestration-rules.md`
5. `df/04-documentation-standards.md`
6. Your role file in `df/roles/`
7. Current runtime files in `df/runtime/`

## Delivery role lanes

Dark Factory has four delivery roles for implementation/data work instead of one generic developer role:

| Role | Short name | Primary scope |
|---|---|---|
| Backend Developer | `backend-dev` | Backend services, domain/application modules, persistence, migrations, backend APIs, and backend tests. |
| Frontend Developer | `frontend-dev` | Website, Android, and iOS frontend projects, client behavior, frontend assets, accessibility, and frontend tests. |
| DevOps Engineer | `devops` | Build/deploy automation, containers, CI/CD, infrastructure-as-code, environment configuration, and deployment evidence. |
| Data Engineer | `data-engineer` | Country data templates, seed/test datasets, import fixtures, source traceability, and data-quality evidence. |

Every task entering implementation or data population must be routed to exactly one delivery lane, or split by SA into independent lane-specific child tasks before work starts. Lane tasks are tracked in:

- `df/runtime/backend-dev-board.md`
- `df/runtime/frontend-dev-board.md`
- `df/runtime/devops-board.md`
- `df/runtime/data-engineer-board.md`

Parallel lane work is allowed only when the lane tasks do not touch the same files, components, infrastructure, environments, or acceptance criteria.

Frontend lane tasks must also name one frontend project scope: `frontend/website`, `frontend/android`, or `frontend/ios`. The website frontend uses Next.js + React. The three frontend projects are independent projects and must not require each other to build, test, or deploy. Mobile application work (`frontend/android` and `frontend/ios`) is the last frontend priority unless explicitly promoted by PO/SA.

Frontend implementation that changes UI, layout, pages, screens, visual states, or user-visible markup requires a designer-provided design package first. If `frontend-dev` starts a task and no design artifact is provided, the frontend task is `BLOCKED`; `frontend-dev` must document the missing design input and hand off for `designer` work instead of implementing the UI from scratch.

Data engineering work may populate country-specific data only as data/configuration. City, district, school, and subject names must be true and traceable to public sources. Teacher names, student names, and individual grade records must be fake/synthetic and must not be copied from real people or production records.

## Role selection

If the user explicitly assigns a role, act as that role. Otherwise infer the role from the current task state:

| Task state | Responsible role |
|---|---|
| `OPEN`, `INTAKE`, `REFINEMENT_IN_PROGRESS` | `sa` |
| `REFINEMENT_QUESTIONS` | `po` |
| `REFINED`, `NEEDS_ARCHITECTURE`, `ARCHITECTURE_REVIEW` | `sa` |
| `READY_FOR_DESIGN`, `DESIGN_IN_PROGRESS` | `designer` |
| `READY_FOR_DEV`, `DEV_IN_PROGRESS`, `RETURNED_TO_DEV` | `backend-dev`, `frontend-dev`, `devops`, or `data-engineer` from the task owner role and lane subdashboard |
| `READY_FOR_QA`, `QA_IN_PROGRESS`, `QA_FAILED` | `qa` |
| `READY_FOR_PO`, `PO_REVIEW`, `PO_REJECTED` | `po` |
| `DONE`, `BLOCKED`, `NO_TASKS` | Follow orchestration rules |

If task state is absent, inspect `df/runtime/board.md` plus design and delivery subdashboards and choose the highest-priority actionable task.

## Single-role-per-session rule (mandatory, no exceptions)

**An agent MUST NOT switch to a different role within the same session. One session = one role execution.**

After the current role finishes, the agent must:

1. Update all runtime files with the current state.
2. Write a handoff note specifying the next role and next action.
3. Stop and ask the human to create a new session for the next role.

The agent must NOT execute another role's checklist, combine roles, or justify role-switching for any reason.

The factory stops (session ends) when:

- the current role's work is complete and handoff is documented;
- there are no actionable tasks;
- the current work is blocked by missing human decision, credentials, permissions, environment access, or external dependency;
- a safety, legal, or security concern requires human approval;
- the user explicitly stops the factory.

## Documentation rule

Every meaningful action must update at least one runtime artifact:

- `df/runtime/activity-log.md`
- `df/runtime/board.md`
- `df/runtime/design-board.md` for design tasks
- `df/runtime/backend-dev-board.md`, `df/runtime/frontend-dev-board.md`, `df/runtime/devops-board.md`, or `df/runtime/data-engineer-board.md` for delivery-lane tasks
- task-specific artifacts under `df/artifacts/{task-id}/`

For designer work, keep task documentation such as `design-package.md` and handoff notes under `df/artifacts/{task-id}/design/`, but store design assets such as HTML, PNG, SVG, PDF, or similar deliverable files under the root-level `design/{page-slug}/` folder structure. Each page/screen must use its own dedicated folder with a globally unique, descriptive slug.

Use templates from `df/templates/` whenever possible.

## Tooling neutrality

Agents may use any available mechanism: MCP tools, IDE tools, terminal commands, browser automation, issue trackers, source control, CI/CD, or manual file edits. The required output is not tool-specific; it is documented state, validated work, and clear handoff.

## Safety rules

- Preserve user work. Check existing files before editing.
- Prefer minimal, reversible changes.
- Never expose secrets in logs, screenshots, commits, or Markdown.
- Do not fabricate test results, screenshots, deployments, or approvals.
- If verification cannot be run, document why and mark the task blocked or conditionally failed.

