# Dark Factory Framework

Dark Factory (DF) is an MCP-agnostic operating framework for autonomous AI-agent software delivery. It defines a repeatable SDLC where agents collaborate as a small delivery team and keep working until the task queue is empty, blocked by missing information/permissions, or explicitly stopped by a human.

The framework is intentionally documentation-first so it can be used by Claude Code, GitHub Copilot, JetBrains AI Assistant, custom MCP clients, CLI agents, or any future AI tool. Agents should treat the files in `df/` as the source of truth.

## Core idea

A Dark Factory is a software delivery system that can run with minimal human intervention:

1. A human gives a single start command.
2. The factory selects the next open task.
3. The assigned design or delivery lane completes the task and records evidence.
4. `qa` performs unit, integration, regression, and quality checks.
5. `po` reviews the completed behavior through end-to-end validation, captures screenshots when UI is involved, and decides whether the result is accepted.
6. If accepted, `po` hands off to the next responsible role or lane.
7. If rejected, `po` sends the work back to the responsible lane with defects and evidence.
8. Every step is documented.

## Start here

Agents and humans should read these files in order:

1. [AGENTS.md](AGENTS.md) - universal entrypoint for any AI agent.
2. [df/00-start-here.md](df/00-start-here.md) - factory boot instructions.
3. [df/01-operating-model.md](df/01-operating-model.md) - roles, responsibilities, and collaboration rules.
4. [df/02-state-machine.md](df/02-state-machine.md) - task states and transitions.
5. [df/03-orchestration-rules.md](df/03-orchestration-rules.md) - how agents continue without human prompting.
6. [df/04-documentation-standards.md](df/04-documentation-standards.md) - required logs and evidence.
7. Role instructions such as [df/roles/designer.md](df/roles/designer.md), [df/roles/backend-dev.md](df/roles/backend-dev.md), [df/roles/frontend-dev.md](df/roles/frontend-dev.md), [df/roles/devops.md](df/roles/devops.md), [df/roles/data-engineer.md](df/roles/data-engineer.md), [df/roles/qa.md](df/roles/qa.md), [df/roles/sa.md](df/roles/sa.md), and [df/roles/po.md](df/roles/po.md).
8. Runtime tracking files such as [df/runtime/board.md](df/runtime/board.md) and [df/runtime/activity-log.md](df/runtime/activity-log.md).

## Human start command

A human can start the factory with any equivalent command, for example:

```text
Dark Factory: start work.
```

After receiving this command, the active agent must follow [df/00-start-here.md](df/00-start-here.md) and continue the SDLC loop until no actionable task remains or a blocking condition is reached.

## Minimal Repository Structure

```text
.
|-- AGENTS.md
|-- CLAUDE.md
|-- JETBRAINS_AI.md
|-- .github/
|   `-- copilot-instructions.md
`-- df/
    |-- 00-start-here.md
    |-- 01-operating-model.md
    |-- 02-state-machine.md
    |-- 03-orchestration-rules.md
    |-- 04-documentation-standards.md
    |-- roles/
    |   |-- designer.md
    |   |-- backend-dev.md
    |   |-- frontend-dev.md
    |   |-- devops.md
    |   |-- data-engineer.md
    |   |-- qa.md
    |   |-- sa.md
    |   `-- po.md
    |-- templates/
    `-- runtime/
```

## Project Build Layout

The application scaffold is one Maven parent with three independent project areas:

- `backend/`: Spring Boot backend parent containing `common`, `identity-access`, `organization`, `school-pack`, `attendance`, `gradebook`, `meal-catering`, and `platform-core`.
- `frontend/`: independent frontend project area. Target structure is three independent frontend projects: `frontend/website` (Next.js + React), `frontend/android`, and `frontend/ios`.
- `devops/`: independent DevOps Maven project scaffold.

Frontend priority: website first; Android and iOS mobile applications are last-priority work unless PO/SA explicitly promotes them.

UI-facing frontend implementation requires a designer package first. If the task changes UI, markup, layout, screens, or visual states and no design package exists, `frontend-dev` marks the task blocked and requests `designer` input.

Country-specific data population belongs to `data-engineer`. City, district, school, and subject names must be true and traceable to public sources; teacher names, student names, and individual grade records must be fake/synthetic.

Build paths:

```powershell
.\mvnw.cmd -f backend/pom.xml clean verify
.\mvnw.cmd -f frontend/pom.xml clean verify
.\mvnw.cmd -f devops/pom.xml clean verify
.\mvnw.cmd clean verify
```

Future frontend validation must preserve independent project paths for website-only, Android-only, iOS-only, and all-frontend checks.

## Run the application locally

Use the dedicated run guide for exact prerequisites, environment variables, backend startup, health checks, optional bootstrap-admin login, and website startup:

- [`docs/run-application.md`](./docs/run-application.md)

Quick summary:

- Recommended cross-platform launcher: `java RunLocal.java`
- Optional website startup: `java RunLocal.java --with-website`
- Backend application entrypoint: `backend/platform-core`
- Manual backend fallback command: `sh ./mvnw -f backend/platform-core/pom.xml spring-boot:run`
- Backend health endpoint: `GET /platform/status`
- OpenAPI endpoint: `GET /api-docs`
- Website project: `frontend/website` (requires Node.js 20+ and npm)
- Website proxy default backend URL: `http://127.0.0.1:8080`

## Non-Negotiable Factory Principles

- One source of truth: task state and evidence must be documented.
- No silent work: every role records what it did, what it found, and what should happen next.
- No unverified completion: work is not done until `qa` passes and `po` accepts.
- No endless guessing: unclear requirements are documented as questions or blockers.
- No tool lock-in: agents may use any MCP/server/tooling available, but must follow the same workflow.
- No destructive action without evidence: agents must protect existing user work and document risky operations.
