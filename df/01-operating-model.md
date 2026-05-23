# 01 - Dark Factory Operating Model

## Roles

Dark Factory uses eight required roles.

| Role | Short name | Purpose |
|---|---|---|
| Solution Architect | `sa` | Converts requirements into a safe, coherent technical plan and guards architecture quality. |
| Designer | `designer` | Produces UI/UX design packages, HTML/static markup guidance, interaction states, responsive behavior, and accessibility notes before frontend implementation. |
| Backend Developer | `backend-dev` | Implements backend services, domain/application modules, persistence, migrations, backend APIs, and backend tests. |
| Frontend Developer | `frontend-dev` | Implements website, Android, and iOS frontend projects, client behavior, frontend assets, accessibility, and frontend tests. |
| DevOps Engineer | `devops` | Implements build/deploy automation, containers, CI/CD, infrastructure-as-code, environment configuration, and deployment evidence. |
| Data Engineer | `data-engineer` | Produces country-specific data templates, seed/test datasets, fixtures, source traceability, and data-quality evidence. |
| Quality Engineer | `qa` | Verifies the implementation through automated and manual-quality checks. |
| Product Owner | `po` | Validates business outcome through E2E review, screenshots, and acceptance/rejection. |

## Role ownership

A role owns the task only while the task is in that role's state. Ownership must be documented in `df/runtime/board.md` and the task artifact.

Design ownership is role-specific. A task in `READY_FOR_DESIGN` or `DESIGN_IN_PROGRESS` must be owned by `designer` and appear in `df/runtime/design-board.md`.

Delivery ownership is lane-specific. A task in `READY_FOR_DEV`, `DEV_IN_PROGRESS`, or `RETURNED_TO_DEV` must be owned by exactly one of:

- `backend-dev`
- `frontend-dev`
- `devops`
- `data-engineer`

The same lane task must also appear on exactly one implementation subdashboard:

- `df/runtime/backend-dev-board.md`
- `df/runtime/frontend-dev-board.md`
- `df/runtime/devops-board.md`
- `df/runtime/data-engineer-board.md`

The generic `dev` owner is retired for new work. Historical artifacts may mention `dev`, but active tasks must use a lane-specific delivery role.

## Single-role-per-session rule (mandatory)

**An agent MUST NOT switch to a different role within the same session. This rule is absolute and has no exceptions.**

One session = one role execution. When an agent completes its work in the current role, it must:

1. Document the final state of the task in runtime files.
2. Record a handoff note specifying the next role and next action.
3. Stop and ask the human to create a new session for the next role.

The agent must NOT:

- Execute another role's checklist in the same session.
- "Continue as" a different role after finishing the current role.
- Combine SA + Dev, Dev + QA, QA + PO, or any other role combination in one session.
- Justify role-switching by claiming efficiency, simplicity, or continuity.

If work is complete for the current role and no more actions remain for that role, the session ends. A new session must be started for the next role to act.

This ensures traceability, prevents self-approval, and maintains separation of concerns across the SDLC.

## Standard flow

```text
OPEN
  -> INTAKE
  -> REFINEMENT_IN_PROGRESS
  -> REFINEMENT_QUESTIONS (loop until all questions answered)
  -> REFINED
  -> NEEDS_ARCHITECTURE (if needed)
  -> READY_FOR_DESIGN (for UI-facing frontend work that needs design)
  -> DESIGN_IN_PROGRESS
  -> READY_FOR_DEV (routed to backend-dev, frontend-dev, devops, or data-engineer)
  -> DEV_IN_PROGRESS (same lane owner)
  -> READY_FOR_QA
  -> QA_IN_PROGRESS
  -> READY_FOR_PO
  -> PO_REVIEW
  -> DONE
  -> next task
```

If the task already has clear acceptance criteria and no refinement is needed, it may skip directly from `OPEN` to `NEEDS_ARCHITECTURE` or `READY_FOR_DEV`.

If architecture is unnecessary for a small, low-risk task, SA may document `Architecture: not required` with a reason and move directly from `REFINED` to `READY_FOR_DEV` after assigning the delivery lane.

If the requested deliverable is only a framework/process/architecture documentation change and no application delivery lane is required, SA may move from `ARCHITECTURE_IN_PROGRESS` to `READY_FOR_QA` after updating the relevant documentation and runtime artifacts. The handoff must explicitly state why design, backend, frontend, DevOps, and data-engineering lanes are not applicable.

## Definition of Refined

A task may move to `REFINED` only when:

- raw input has been converted into a clear task summary and business goal;
- acceptance criteria are specific, testable, and scoped;
- refinement questions are answered with documented answer authority, or explicitly marked not applicable;
- critical unanswered product, legal, security, compliance, budget, or scope decisions are not present;
- low-risk assumptions, if any, are documented in `task.md` and referenced in the next handoff;
- independent deliverables have been split into child tasks or documented as intentionally bundled;
- expected validation approach is known well enough for Dev and QA to plan tests.

A task must not be marked `REFINED` when the only basis for a critical product decision is an AI-generated guess.

## Rework flow

```text
QA_FAILED -> RETURNED_TO_DEV -> DEV_IN_PROGRESS -> READY_FOR_QA
PO_REJECTED -> RETURNED_TO_DEV -> DEV_IN_PROGRESS -> READY_FOR_QA
```

All rework must include defect evidence, reproduction steps, expected result, actual result, and severity.

Rework returns to the same delivery lane that produced the rejected work unless QA/PO evidence proves the root cause belongs to another lane. If ownership changes, SA must document the reroute and update both affected subdashboards.

## Design and delivery lane routing

Before a task can move to `READY_FOR_DEV`, SA must decide one of:

- Backend-only task: add it to `df/runtime/backend-dev-board.md` with owner `backend-dev`.
- Frontend-only task: add it to `df/runtime/frontend-dev-board.md` with owner `frontend-dev`.
- DevOps-only task: add it to `df/runtime/devops-board.md` with owner `devops`.
- Data-only task: add it to `df/runtime/data-engineer-board.md` with owner `data-engineer`.
- UI-facing frontend task without an accepted design package: add or split a design task to `df/runtime/design-board.md` with owner `designer` before frontend implementation starts.
- Cross-lane task: split it into child lane tasks before work starts, unless the work is inseparable and must be serialized.

Child task IDs should preserve the parent relationship, for example:

- `{parent-id}-BE`
- `{parent-id}-FE`
- `{parent-id}-OPS`
- `{parent-id}-DATA`
- `{parent-id}-DESIGN`

Each child task must have its own acceptance criteria, owner role, affected-file/data/design scope, and lane-owned artifact folder. Parallel execution is allowed only for child tasks that do not modify the same files, components, infrastructure, environments, datasets, design package, or acceptance criteria.

## Lane artifact ownership

Lane roles must write notes and evidence only in their lane-owned artifact folder:

```text
df/artifacts/{task-id}/design/
df/artifacts/{task-id}/backend/
df/artifacts/{task-id}/frontend/
df/artifacts/{task-id}/devops/
df/artifacts/{task-id}/data/
```

Shared task files such as `task.md`, `solution-design.md`, runtime decisions, and runtime risks are owned by the active role responsible for the current state. Parallel delivery roles must not edit shared artifacts to avoid documentation conflicts. If a lane discovers a shared architecture or scope issue, it must document the issue in its lane notes and hand off to SA instead of editing another lane's files.

## Frontend project separation

The `frontend-dev` lane contains three independent frontend projects:

- `frontend/website` - website application using Next.js + React.
- `frontend/android` - Android mobile application project, last-priority frontend work by default.
- `frontend/ios` - iOS mobile application project, last-priority frontend work by default.

SA must route frontend work to one of these project scopes. Website work should be sequenced before mobile work unless PO/SA explicitly promotes a mobile task. If a feature needs website, Android, and iOS work, split it into child tasks such as `{parent-id}-WEB`, `{parent-id}-AND`, and `{parent-id}-IOS`; default mobile child tasks to the last priority band. The three frontend projects must be independently buildable, testable, and deployable. Shared behavior should flow through backend APIs, generated API clients, design tokens, or published packages only after an explicit SA decision; do not create hidden project-to-project coupling.

## Frontend design gate

Frontend UI implementation requires designer input before `frontend-dev` changes UI code. An accepted design package may include HTML/static markup, wireframes, component state notes, responsive layouts, accessibility expectations, assets, copy, and design-token guidance.

If no design package exists for a UI-facing frontend task, `frontend-dev` must not improvise the UI. It must document the missing design input in its frontend lane notes, mark the task `BLOCKED`, and request `designer` action through the runtime board or a `{parent-id}-DESIGN` child task. Non-visual frontend work such as generated API client updates, build fixes, tests, and internal state plumbing may proceed without designer input when SA explicitly documents that no user-visible UI change is included.

## Data engineering lane

The `data-engineer` lane owns data-only country template population, seed/test datasets, import fixtures, and data-quality evidence. It must preserve the strict no-country-specific-code invariant: country data may change data/configuration only, never framework code, framework structure, schemas, or API contracts.

For country-specific datasets:

- city, district, school, and subject names must be true and traceable to public sources;
- each public-source-backed value set must identify source URLs, retrieval date, and any transformation performed;
- teacher names, student names, and individual grade records must be fake/synthetic;
- no real teacher/student personal data, production records, private datasets, or scraped personal profiles may be used;
- QA must be able to reproduce source checks and data-quality checks from the recorded evidence.

## Communication protocol

Agents communicate through Markdown, not private memory.

Required communication points:

- role start note;
- role completion note;
- handoff note;
- blocker note;
- decision record for meaningful architecture/product decisions;
- verification evidence.

## Definition of Ready

A task is ready for development when:

- task id and summary exist;
- refinement is complete (acceptance criteria exist or assumptions are documented);
- all refinement questions have been answered with authority, documented as safe low-risk assumptions, or moved to blockers;
- no critical unanswered refinement decision remains;
- dependencies and blockers are known;
- architecture guidance exists when needed;
- expected validation path is defined.
- design package exists for UI-facing frontend work, or a design task is routed before frontend implementation.
- delivery lane and subdashboard are assigned for development/data-ready work.

## Definition of Done

A task is done only when:

- implementation is complete;
- relevant unit tests pass or a documented reason explains why none apply;
- relevant integration tests pass or a documented reason explains why none apply;
- QA has approved the task;
- PO has completed E2E validation;
- screenshots or equivalent visual evidence are attached for UI changes;
- PO has accepted the result;
- all runtime files are updated.

## Human-in-the-loop rules

Ask for human input only when required:

- credentials/secrets are missing;
- paid service or infrastructure change is needed;
- destructive data operation is required;
- legal/security/privacy risk exists;
- requirement ambiguity cannot be safely resolved;
- external system access is unavailable.

Do not ask humans to perform routine SDLC work that agents can do with available tools.

