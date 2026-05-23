# Solution Design - TASK-004

## Summary

Dark Factory will replace the single implementation role with three lane-specific implementation roles: `backend-dev`, `frontend-dev`, and `devops`. Work enters development only after SA routes it to exactly one implementation lane or splits it into independent child tasks across lanes.

## Context

The current workflow has one `dev` role responsible for backend, frontend, and DevOps implementation. That creates a bottleneck and causes shared documentation conflicts when work could otherwise run in parallel.

The repository already has physical project boundaries for `backend/`, `frontend/`, and `devops/`. The process model must match those boundaries.

## Requirements and acceptance criteria

- Define three implementation roles with separate role files.
- Add separate runtime subdashboards for backend, frontend, and DevOps work.
- Route every new development task to one lane before implementation starts.
- Allow parallel lane execution when scopes, files, environments, and acceptance criteria are independent.
- Prevent parallel developers from editing the same lane-owned notes, evidence, or handoff files.
- Preserve SA, QA, PO gates and the single-role-per-session rule.

## Proposed solution

Use lane-routed development tasks:

- `backend-dev`: owns server-side code, domain/application modules, persistence, migrations, backend tests, and backend API contracts.
- `frontend-dev`: owns user interface code, client-side behavior, frontend assets, frontend tests, accessibility, and UI evidence.
- `devops`: owns build/deploy automation, containers, CI/CD, infrastructure-as-code, environment configuration, observability wiring, and deployment evidence.

Frontend lane addendum:

- `frontend/website`: independent website project using Next.js + React.
- `frontend/android`: independent Android mobile application project.
- `frontend/ios`: independent iOS mobile application project.

Frontend implementation priority is website first. Android and iOS mobile applications are last-priority work unless PO/SA explicitly promotes them. Frontend features that touch multiple platforms must be split into platform child tasks when the work can proceed independently. Use suffixes such as `{parent-id}-WEB`, `{parent-id}-AND`, and `{parent-id}-IOS`; default `{parent-id}-AND` and `{parent-id}-IOS` to the last priority band.

The canonical state names stay unchanged. The responsible implementation role is determined by `Owner role` on the main board and by membership in exactly one subdashboard:

- `df/runtime/backend-dev-board.md`
- `df/runtime/frontend-dev-board.md`
- `df/runtime/devops-board.md`

SA must route a development-ready task to exactly one lane. If a user story requires more than one implementation lane, SA must split it into child tasks such as `{parent-id}-BE`, `{parent-id}-FE`, and `{parent-id}-OPS`, each with its own acceptance criteria, owner role, and lane artifact folder.

Lane-owned artifact folders prevent documentation collisions:

```text
df/artifacts/{task-id}/backend/
df/artifacts/{task-id}/frontend/
df/artifacts/{task-id}/frontend/website/
df/artifacts/{task-id}/frontend/android/
df/artifacts/{task-id}/frontend/ios/
df/artifacts/{task-id}/devops/
```

Each lane writes only inside its lane folder for implementation notes, evidence, and handoffs. Shared files such as `task.md`, `solution-design.md`, runtime decisions, and runtime risks are changed by SA or by another role only when that role owns the task and the change is part of its checklist.

## Files/components likely affected

- `AGENTS.md`
- `df/00-start-here.md`
- `df/01-operating-model.md`
- `df/02-state-machine.md`
- `df/03-orchestration-rules.md`
- `df/04-documentation-standards.md`
- `df/roles/sa.md`
- `df/roles/dev.md`
- `df/roles/backend-dev.md`
- `df/roles/frontend-dev.md`
- `df/roles/devops.md`
- `df/templates/dev-subdashboard.md`
- `df/templates/dev-notes.md`
- `df/templates/handoff.md`
- `df/runtime/backend-dev-board.md`
- `df/runtime/frontend-dev-board.md`
- `df/runtime/devops-board.md`
- `df/backlog/architecture-direction.md`
- `df/backlog/user-stories.md`

## Data/API contract changes

None. This is a process and documentation change.

## Security/privacy considerations

No new secrets or production access are introduced. DevOps evidence must continue to redact secrets from logs, environment output, CI output, screenshots, and Markdown.

## Test strategy

- Inspect canonical docs for remaining guidance that implies backend, frontend, and DevOps must share one `dev` lane.
- Verify every new implementation role has a role file and clear ownership boundaries.
- Verify subdashboards exist and are referenced by operating, orchestration, and documentation standards.
- Verify artifact ownership rules are documented.
- Verify frontend work is routed by project scope: `frontend/website`, `frontend/android`, or `frontend/ios`.
- Verify the website technology choice is documented as Next.js + React.
- Verify Android and iOS work is documented as last-priority unless explicitly promoted.
- No application tests are required because no application code changes are part of this SA task.

## Risks and mitigations

- Risk: Existing runtime tasks may still reference the old `dev` owner.
  - Mitigation: New tasks must use the lane roles; existing active tasks can be completed or explicitly migrated by SA/QA as they resume.
- Risk: Cross-lane parent work can still create merge conflicts.
  - Mitigation: SA must split independent child tasks and document files/components likely affected before routing.
- Risk: DevOps work may touch shared build files also used by backend/frontend.
  - Mitigation: DevOps must coordinate through SA when shared root build, wrapper, or CI files are in scope.
- Risk: Frontend projects may accidentally share mutable source code directly.
  - Mitigation: Share through APIs, generated clients, design tokens, or explicit shared packages only after SA approval.

## Rollback plan

Revert the documentation and runtime changes from this task, remove the lane role files and subdashboards, and restore `dev` as the single implementation owner. No data migration is involved.

## Open questions

None blocking.
