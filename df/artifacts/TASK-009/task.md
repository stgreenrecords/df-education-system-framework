# Task - TASK-009

## Summary

Create a single cross-platform launcher file that can start the local application stack on Windows, macOS, and Linux.

## Type

Task

## Priority

P0

## Current state

READY_FOR_DEV

## Business goal

Reduce local startup friction by giving humans and agents one launcher entrypoint instead of separate platform-specific commands for database, backend, and optional website startup.

## Acceptance criteria

- [ ] The repository contains one launcher file that can be invoked on Windows, macOS, and Linux without requiring separate OS-specific script files.
- [ ] The launcher starts the local backend prerequisites and backend application using the repository's real runtime paths and module entrypoints.
- [ ] The launcher documents or prints the resulting local URLs needed to verify startup success.
- [ ] The launcher avoids hardcoding real secrets and uses safe local defaults and/or environment-variable overrides.
- [ ] If website startup is included, the launcher handles missing `node`/`npm` gracefully instead of failing silently.
- [ ] The repository documentation explains how to run the new launcher.

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
- Website startup should be optional or gracefully skipped when `node`/`npm` are unavailable, because the current workspace has documented frontend-toolchain gaps.

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

