# Task - TASK-008

## Summary

Create repository documentation that explains how to run the application locally, including the backend startup path, required environment variables, health checks, and the website frontend prerequisites.

## Type

Chore

## Priority

P0

## Current state

READY_FOR_QA

## Business goal

Make it straightforward for humans and agents to start the currently implemented application components locally without guessing the correct module entrypoint, required environment variables, or validation endpoints.

## Acceptance criteria

- [x] The repository contains a dedicated local run guide that explains how to start the backend application from the correct executable module.
- [x] The run guide documents required backend environment variables, including database connection and authentication/MFA secrets, with safe local examples.
- [x] The run guide includes health-check or verification steps for the backend and an optional example for bootstrap-admin login.
- [x] The run guide explains how to start the website frontend and clearly states that Node.js 20+ and npm are required.
- [x] The root `README.md` links to the dedicated run guide.

## Out of scope

- Adding new runtime automation or helper scripts.
- Changing application behavior, schema, APIs, or security defaults.
- Completing QA or PO acceptance in this session.

## Assumptions

- The user requested documentation only, not a new runtime feature or deployment workflow.
- A documentation-first answer is sufficient as long as the commands reflect the actual repository layout and current runnable entrypoints.
- The existing container helper documentation under `devops/container/platform-core/README.md` should be referenced rather than replaced.

## Dependencies

- `README.md`
- `backend/platform-core/pom.xml`
- `backend/platform-core/src/main/resources/application.properties`
- `frontend/website/package.json`
- `frontend/website/README.md`
- `devops/container/platform-core/README.md`

## Risks

- If the documented backend startup command targets the backend parent POM instead of `backend/platform-core`, the run guide will fail at runtime.
- Weak or misleading sample secret values can cause runtime authentication failures during login verification.
- Website instructions must not imply that the frontend can run without Node.js and npm.

## Links

- Issue: n/a
- PR: n/a
- Design: `df/artifacts/TASK-008/solution-design.md`

## Role history

| Timestamp | Role | State | Summary |
|---|---|---|---|
| 2026-05-26 local | sa | OPEN -> NEEDS_ARCHITECTURE -> ARCHITECTURE_IN_PROGRESS -> READY_FOR_QA | Processed the explicit documentation request, inspected the runnable backend/frontend/container entrypoints, verified the module-level backend startup path plus local health/login checks, added a dedicated run guide under `docs/`, updated the root `README.md`, and prepared the documentation-only change for QA. |

