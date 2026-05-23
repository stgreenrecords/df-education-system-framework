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

## Role selection

If the user explicitly assigns a role, act as that role. Otherwise infer the role from the current task state:

| Task state | Responsible role |
|---|---|
| `OPEN`, `INTAKE`, `REFINEMENT_IN_PROGRESS` | `sa` |
| `REFINEMENT_QUESTIONS` | `po` |
| `REFINED`, `NEEDS_ARCHITECTURE`, `ARCHITECTURE_REVIEW` | `sa` |
| `READY_FOR_DEV`, `RETURNED_TO_DEV` | `dev` |
| `READY_FOR_QA`, `QA_IN_PROGRESS`, `QA_FAILED` | `qa` |
| `READY_FOR_PO`, `PO_REVIEW`, `PO_REJECTED` | `po` |
| `DONE`, `BLOCKED`, `NO_TASKS` | Follow orchestration rules |

If task state is absent, inspect `df/runtime/board.md` and choose the highest-priority actionable task.

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
- task-specific artifacts under `df/artifacts/{task-id}/`

Use templates from `df/templates/` whenever possible.

## Tooling neutrality

Agents may use any available mechanism: MCP tools, IDE tools, terminal commands, browser automation, issue trackers, source control, CI/CD, or manual file edits. The required output is not tool-specific; it is documented state, validated work, and clear handoff.

## Safety rules

- Preserve user work. Check existing files before editing.
- Prefer minimal, reversible changes.
- Never expose secrets in logs, screenshots, commits, or Markdown.
- Do not fabricate test results, screenshots, deployments, or approvals.
- If verification cannot be run, document why and mark the task blocked or conditionally failed.

