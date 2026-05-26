# Task - TASK-009

## Summary

Create a single cross-platform launcher file that can start the local application stack on Windows, macOS, and Linux.

## Type

Task

## Priority

P0

## Current state

READY_FOR_QA

## Business goal

Reduce local startup friction by giving humans and agents one launcher entrypoint instead of separate platform-specific commands for database, backend, and optional website startup.

## Acceptance criteria

- [x] The repository contains one launcher file that can be invoked on Windows, macOS, and Linux without requiring separate OS-specific script files.
- [x] The launcher starts the local backend prerequisites and backend application using the repository's real runtime paths and module entrypoints.
- [x] The launcher documents or prints the resulting local URLs needed to verify startup success.
- [x] The launcher avoids hardcoding real secrets and uses safe local defaults and/or environment-variable overrides.
- [x] If website startup is included, the launcher handles missing `node`/`npm` gracefully instead of failing silently.
- [x] The repository documentation explains how to run the new launcher.

## Out of scope

- Production deployment automation.
- Replacing the existing container baseline under `devops/container/platform-core/`.
- Changing backend or frontend application behavior.
- Mandatory website startup when the frontend toolchain is unavailable.
- QA or PO acceptance in this session.

## Assumptions

- The user's request for a "single file for windows mac and linux" is best satisfied by one cross-platform launcher file rather than multiple `.ps1`, `.sh`, and `.cmd` wrappers.
- The smallest safe scope is a local developer launcher for the currently implemented application stack, not a production orchestrator.
- Because the backend already requires Java 25+, a Java single-file launcher (`java <file>.java`) is the safest cross-platform runtime assumption available in this repository.
- The compose-based launcher may satisfy the website-startup requirement by running the frontend inside a Node.js container, avoiding host `node`/`npm` dependence.

## Dependencies

- `TASK-008` for the newly documented local runtime behavior and constraints
- `backend/platform-core/pom.xml`
- `backend/platform-core/src/main/resources/application.properties`
- `frontend/website/package.json`
- `devops/container/platform-core/README.md`

## Risks

- A launcher that assumes Unix shell semantics will fail on Windows.
- A launcher that assumes Node.js is always present will not work on environments like the current workspace.
- A launcher that embeds brittle demo secrets or fixed occupied ports may fail in common local environments.
- A launcher that edits backend/frontend code to compensate for runtime orchestration gaps would violate lane ownership.

## Links

- Issue: n/a
- PR: n/a
- Design: `df/artifacts/TASK-009/solution-design.md`

## Implementation lane

- Lane: `devops`
- Subdashboard: `df/runtime/devops-board.md`
- Artifact folder for implementation notes: `df/artifacts/TASK-009/devops/`

## Role history

| Timestamp | Role | State | Summary |
|---|---|---|---|
| 2026-05-26 local | sa | OPEN -> NEEDS_ARCHITECTURE | Promoted the explicit user request for a single cross-platform startup file into a new runtime task because it requires repository-owned local-run automation rather than more documentation alone. |
| 2026-05-26 local | sa | NEEDS_ARCHITECTURE -> ARCHITECTURE_IN_PROGRESS | Started architecture because the task affects local runtime orchestration across OSes, container/process startup sequencing, environment-variable handling, and user-facing startup verification behavior. |
| 2026-05-26 local | sa | ARCHITECTURE_IN_PROGRESS -> READY_FOR_DEV | Selected a DevOps-owned single-file launcher approach, documented the cross-platform execution model and constraints, and routed implementation to `devops`. |
| 2026-05-26 local | devops | READY_FOR_DEV -> DEV_IN_PROGRESS | Started implementation, reviewed the live runtime/startup assets, and corrected the approach after explicit user feedback: the task will deliver a terminal-first single-file startup flow for Spring + PostgreSQL instead of a Java main-class launcher. |
| 2026-05-26 local | devops | DEV_IN_PROGRESS -> READY_FOR_QA | Replaced the discarded Java launcher draft with one terminal-first cross-platform `docker compose` file (`compose.local.yaml`), validated Spring + PostgreSQL startup plus cleanup locally, updated the run documentation, and prepared the task for independent QA review. |
| 2026-05-26 local | qa | READY_FOR_QA -> READY_FOR_PO | Independently reran the compose startup path, confirmed PostgreSQL + Spring backend readiness and cleanup, verified the docs, and confirmed that website startup is intentionally separate/manual rather than a defect in `TASK-009`. |
| 2026-05-26 local | po | READY_FOR_PO -> RETURNED_TO_DEV | Rejected the delivered scope after explicit human product feedback confirmed that the one-command startup experience is expected to start the website frontend too; returned the task to `devops` for rework. |
| 2026-05-26 local | devops | RETURNED_TO_DEV -> DEV_IN_PROGRESS | Started rework after PO rejection, reviewed the compose/docs/frontend runtime inputs, and selected a containerized `frontend/website` service so the same compose command can start database, backend, and website together without host Node.js/npm. |
| 2026-05-26 local | devops | DEV_IN_PROGRESS -> READY_FOR_QA | Extended `compose.local.yaml` with a Node.js-powered `frontend` service, added cache volumes and frontend-related overrides, updated the run documentation, and revalidated backend plus website startup together through live HTTP checks and the website auth proxy. |

