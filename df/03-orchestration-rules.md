# 03 - Dark Factory Orchestration Rules

## Goal

Keep the SDLC moving after the human starts the factory. Agents should not wait for repeated human commands between roles.

## Main loop

```text
while factory is running:
  load board
  load design and delivery subdashboards
  choose highest-priority actionable task
  choose role from task state
  execute role checklist
  validate outputs
  update documentation
  hand off to next role
  if task is DONE:
    choose next task
  if no task is actionable:
    record NO_TASKS or BLOCKED and stop
```

## Refinement loop

When a task enters `INTAKE`, the following sub-loop applies:

```text
SA reads raw input
SA generates only decision-grade questions + options + recommendations
SA moves task to REFINEMENT_QUESTIONS
PO answers questions from available product authority/context
if PO cannot answer safely:
  PO marks task BLOCKED for human/product input
PO moves task to REFINEMENT_IN_PROGRESS
SA reviews answers
if more questions needed (max 3 rounds):
  SA posts new questions -> loop
else:
  SA writes acceptance criteria
  SA moves task to REFINED
  SA decides: NEEDS_ARCHITECTURE or READY_FOR_DEV
```

### Refinement challenge rules

The refinement loop is mandatory for ambiguous work, but it must not become a bureaucracy loop or a hallucination source.

- Ask questions only when the answer can change scope, acceptance criteria, architecture, testing, risk, priority, or decomposition.
- Do not ask questions that can be answered by reading repository code, existing docs, tests, logs, or linked artifacts.
- Each question must include impact if unanswered and a recommended default when a safe default exists.
- The PO role may answer from documented business context, existing product decisions, or explicit human input only. If none exists, PO must not invent stakeholder intent.
- Critical unanswered questions must move the task to `BLOCKED`; they must not be converted into assumptions merely because the round limit was reached.
- Low-risk assumptions are allowed only when documented in `task.md`, referenced in the handoff, and later verified by QA/PO.
- If refinement discovers multiple independent deliverables, SA must split or propose child tasks instead of creating one oversized task.
- Refinement completion does not replace QA or final PO acceptance.

### Refinement skip conditions

The refinement loop may be skipped when:

- the task already has clear, testable acceptance criteria;
- the task is a well-defined bug with reproduction steps;
- a human explicitly marked the task as pre-refined;
- the task is a simple chore with no ambiguity.

Document the skip reason in the task artifact.

## Priority rules

Sort actionable tasks by:

1. production incident / critical bug;
2. user-requested task from current session;
3. rejected or failed task;
4. high business priority;
5. dependency unblocks other work;
6. oldest task;
7. smallest safe task.

When design, implementation, or data work is involved, sort lane subdashboard items together with the main board. A design task in `READY_FOR_DESIGN` outranks dependent frontend implementation work. A lane task in `RETURNED_TO_DEV` outranks new `READY_FOR_DEV` lane work in the same priority band.

## Design and delivery lane routing rules

Dark Factory has one design lane and four delivery lanes:

- `designer` in `df/runtime/design-board.md`
- `backend-dev` in `df/runtime/backend-dev-board.md`
- `frontend-dev` in `df/runtime/frontend-dev-board.md`
- `devops` in `df/runtime/devops-board.md`
- `data-engineer` in `df/runtime/data-engineer-board.md`

Before a task moves to `READY_FOR_DEV`, SA must assign exactly one delivery lane or split the task into child lane tasks. UI-facing frontend work must have an accepted design package or a prior designer task. Use the generic delivery states (`READY_FOR_DEV`, `DEV_IN_PROGRESS`, `READY_FOR_QA`, `RETURNED_TO_DEV`) with the lane owner recorded in the board and subdashboard.

Routing guidance:

- Route server-side modules, domain logic, persistence, migrations, backend APIs, and backend tests to `backend-dev`.
- Route UI code, client behavior, frontend assets, accessibility, and frontend tests to `frontend-dev`, and include one affected scope: `frontend/website`, `frontend/android`, or `frontend/ios`.
- Route build/deploy automation, containers, CI/CD, infrastructure-as-code, environment configuration, observability wiring, and deployment evidence to `devops`.
- Route country data templates, seed/test datasets, import fixtures, source mapping, and data-quality checks to `data-engineer`.
- Route UI/UX design packages, static HTML markup guidance, wireframes, interaction states, responsive notes, accessibility expectations, and visual assets to `designer` before frontend implementation.
- If a task touches more than one lane, split it into lane-specific child tasks unless the work is inseparable.
- If a lane task needs a shared file owned by another lane or a root-level shared file, document the dependency and hand off to SA for sequencing or task redesign.

## Parallel work rules

Agents may work in parallel only when tasks do not touch the same files, components, infrastructure, or acceptance criteria.

Designer, backend, frontend, DevOps, and data-engineering lane tasks may run at the same time only when SA has documented independent scope and each lane has a separate subdashboard entry and lane-owned artifact folder. Frontend implementation may not run in parallel with a design task that is still defining the same UI acceptance criteria or markup.

Parallel work is forbidden when:

- tasks modify the same code area;
- architecture or required design is not stable;
- one task blocks another;
- the repository has unresolved merge conflicts;
- shared test environments or seed datasets cannot isolate changes.
- lane tasks need to edit the same runtime artifact, notes file, handoff file, root build file, CI file, deployment environment, dataset, design package, or acceptance criteria.

## Lane documentation rules

Delivery lanes must not share mutable implementation/data/design files. Each lane writes only inside its lane artifact folder:

- `df/artifacts/{task-id}/design/`
- `df/artifacts/{task-id}/backend/`
- `df/artifacts/{task-id}/frontend/`
- `df/artifacts/{task-id}/devops/`
- `df/artifacts/{task-id}/data/`

Shared files are updated only by the role that currently owns the task state. A lane that discovers cross-lane impact records it in its lane notes and stops for SA coordination if continuing would create file, scope, or evidence conflicts.

## Frontend project routing

Frontend work has three independent project scopes under the `frontend-dev` lane:

- Website: `frontend/website`, using Next.js + React.
- Android: `frontend/android`, last-priority frontend work by default.
- iOS: `frontend/ios`, last-priority frontend work by default.

SA must split multi-platform frontend features into independent child tasks when the platforms can be implemented separately. Sequence `frontend/website` before mobile child tasks unless PO/SA explicitly promotes mobile. Frontend parallel work is forbidden when website, Android, and iOS tasks need the same mutable artifact, generated client, design-token package, release configuration, or acceptance criterion at the same time.

## Frontend design gate

For UI-facing frontend work, `designer` must produce or update a design package before `frontend-dev` implements UI code. The design package belongs under `df/artifacts/{task-id}/design/` and should include enough concrete input for implementation, such as HTML/static markup, wireframes, component states, responsive behavior, accessibility expectations, assets, copy, and design-token guidance.

If `frontend-dev` starts a UI task without a design package, it must:

1. document the missing design input in its frontend lane notes;
2. mark the work `BLOCKED` with blocker owner `designer` or `product`, depending on whether design execution or product decision is missing;
3. update runtime files with a handoff requesting designer action;
4. avoid implementing the visible UI until design evidence exists.

Non-visual frontend tasks may skip designer input only when SA explicitly documents that the task changes no user-visible UI.

## Data engineering rules

Data engineering tasks are data-only and must preserve the no-country-specific-code invariant. `data-engineer` may create or update country templates, seed data, test fixtures, import mappings, and data-quality evidence, but must not alter framework code, schemas, API contracts, or runtime behavior for one country.

For country-specific data:

- city, district, school, and subject names must be true and traceable to public sources;
- source evidence must include URL/source name, retrieval date, license/usage note when available, and transformation notes;
- teacher names, student names, and individual grade records must be synthetic;
- synthetic personal data must be clearly labeled and generated without copying real people from public directories or production records;
- QA must verify source traceability and synthetic/real-data separation.

## Handoff rules

Every handoff must include:

- task id;
- current state;
- previous role result;
- files changed or artifacts created;
- tests run and results;
- known risks;
- next role checklist, including lane owner when the next role is an implementation role;
- explicit acceptance or rejection criteria.

Use `df/templates/handoff.md`.

## Rework rules

If QA or PO rejects work:

1. Create/update a defect report in the task artifact folder.
2. Move task to `RETURNED_TO_DEV`.
3. The lane owner must fix root cause, not only the visible symptom.
4. The lane owner must add or update tests proving the defect is fixed when feasible.
5. The task must go through QA and PO again.

## Evidence hierarchy

Prefer stronger evidence:

1. automated tests and CI logs;
2. local command output;
3. browser/API/UI screenshots;
4. structured manual test notes;
5. reasoned code inspection.

Do not claim a test passed unless it was executed or an authoritative CI result was inspected.

## Tool failure handling

If an MCP tool, IDE action, terminal command, browser, test runner, or external service fails:

1. Retry once if failure looks transient.
2. Capture the failure message.
3. Try an alternative verification path.
4. If still blocked, document `BLOCKED` with exact reason.

## Budget and rate-limit handling

If an AI model, API, CI runner, or external service reaches a limit:

- record the limit;
- reduce scope to the current task only;
- switch to available tooling if safe;
- stop only when no safe progress remains.

## Security and privacy gate

Before executing commands, changing infrastructure, reading secrets, or using production data, agents must verify that the action is allowed by the current environment. Secrets must never be pasted into Markdown.

## Stop conditions

The factory session must stop when:

- the current role's work is complete and handoff is documented (a new session is required for the next role);
- all tasks are `DONE`;
- all remaining tasks are `BLOCKED`;
- no task exists;
- human explicitly says stop;
- continuing would risk data loss, security exposure, policy violation, or production outage.

**One session = one role. When the role finishes, the session ends. No exceptions.**

