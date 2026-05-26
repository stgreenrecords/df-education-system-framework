# QA Report - TASK-009

## Scope

Verify the DevOps-owned single-file local startup flow delivered in `compose.local.yaml`, including service coverage, backend readiness, documentation accuracy, cleanup behavior, and the user-visible expectation around frontend startup.

## Environment

- Date: 2026-05-26
- OS: macOS
- Shell: zsh
- Docker Compose: `v5.1.4`
- Validation runtime: local Docker Desktop / Docker Compose

## Test cases

1. Confirm the task artifact and DevOps handoff still match the delivered implementation.
2. Confirm `compose.local.yaml` is the single launcher file and identify exactly which services it declares.
3. Re-run the compose startup path with isolated ports/container names and verify PostgreSQL plus the Spring backend start successfully.
4. Verify backend readiness through `GET /platform/status` and `GET /api-docs`.
5. Verify cleanup removes the temporary containers, network, and compose volume.
6. Confirm `README.md` and `docs/run-application.md` accurately describe the compose scope, including the fact that website startup remains separate/manual.
7. Validate whether the missing frontend process is a defect or expected behavior against the task acceptance criteria.

## Test execution

| Category | Command / method | Result | Notes |
|---|---|---|---|
| Task + handoff review | Direct review of `df/artifacts/TASK-009/task.md`, `df/artifacts/TASK-009/handoffs.md`, and `df/artifacts/TASK-009/devops/handoff-to-qa.md` | PASS | The active implementation is the compose-based launcher, not the discarded Java draft |
| Compose service inspection | `docker compose -f compose.local.yaml config --services` | PASS | Declared services are `postgres` and `backend` only |
| Compose startup | `DF_DB_PORT=55439 DF_APP_PORT=18091 DF_POSTGRES_CONTAINER_NAME=df-local-postgres-task009-qa DF_BACKEND_CONTAINER_NAME=df-local-backend-task009-qa docker compose -f compose.local.yaml up -d` | PASS | Temporary QA stack started successfully |
| Compose runtime inspection | `docker compose -f compose.local.yaml ps` with the same overrides | PASS | PostgreSQL became healthy and the backend container started with the expected port binding |
| Backend readiness | `curl http://127.0.0.1:18091/platform/status` after a short retry loop while the JVM finished starting | PASS | First probe hit a startup-time connection reset; retry returned `200` with `{"service":"education-system-framework","status":"UP"}` |
| OpenAPI readiness | `curl -o /dev/null -w '%{http_code}' http://127.0.0.1:18091/api-docs` | PASS | Returned `200` |
| Backend log inspection | `docker compose -f compose.local.yaml logs --tail=120 backend` with the same overrides | PASS | Confirmed reactor packaging, Flyway validation, Tomcat startup, and successful Spring boot completion |
| Frontend expectation check | `curl -o /dev/null -w '%{http_code}' http://127.0.0.1:3000` | PASS (expected absence) | Returned connection failure / `000` because the compose file does not declare a website service; this matches the documented scope rather than a runtime defect |
| Documentation review | Direct review of `README.md` and `docs/run-application.md` | PASS | Both docs state that `docker compose -f compose.local.yaml up` starts PostgreSQL + Spring backend, while the website has a separate `frontend/website` quick-start path |
| Cleanup | `DF_DB_PORT=55439 DF_APP_PORT=18091 DF_POSTGRES_CONTAINER_NAME=df-local-postgres-task009-qa DF_BACKEND_CONTAINER_NAME=df-local-backend-task009-qa docker compose -f compose.local.yaml down -v` | PASS | Removed the temporary containers, network, and compose volume |

## Acceptance criteria coverage

| Acceptance criterion | Status | Evidence |
|---|---|---|
| The repository contains one launcher file that can be invoked on Windows, macOS, and Linux without requiring separate OS-specific script files. | PASS | Root `compose.local.yaml`; docs and handoff consistently point to `docker compose -f compose.local.yaml up` |
| The launcher starts the local backend prerequisites and backend application using the repository's real runtime paths and module entrypoints. | PASS | Compose startup succeeded; backend service packages/runs the real `backend/platform-core` module path and served live endpoints |
| The launcher documents or prints the resulting local URLs needed to verify startup success. | PASS | `README.md` and `docs/run-application.md` document the backend URLs and website URLs separately |
| The launcher avoids hardcoding real secrets and uses safe local defaults and/or environment-variable overrides. | PASS | `compose.local.yaml` uses local demo defaults with `DF_*` overrides |
| If website startup is included, the launcher handles missing `node`/`npm` gracefully instead of failing silently. | PASS (not applicable by design) | The launcher does not include website startup. Docs explicitly keep website startup separate/manual, so the conditional website-start path is not exercised here |
| The repository documentation explains how to run the new launcher. | PASS | `README.md` and `docs/run-application.md` both document the compose path |

## QA conclusion

The user observation is correct: `docker compose -f compose.local.yaml up` does **not** start the website frontend. QA confirmed that this is the intended behavior of `TASK-009`, not a defect in the delivered compose file. The accepted scope for the launcher is PostgreSQL + Spring backend only, and the documentation explicitly keeps website startup as a separate manual path under `frontend/website`.

This means:

- `TASK-009` is acceptable as delivered for its current scope.
- The missing frontend process should not fail QA for `TASK-009`.
- If a future requirement wants one command to start the website too, that should be handled as new scope or DevOps rework, likely coordinated with the still-blocked website toolchain/runtime work in `TASK-006`.

## QA Result: PASS

- Task: `TASK-009`
- Acceptance criteria covered: Yes — criteria 1-4 and 6 passed directly; criterion 5 is conditionally not applicable because website startup is intentionally excluded and documented as such
- Unit tests: Not applicable — DevOps/runtime-orchestration change, no new unit-test surface
- Integration tests: Compose-based local integration path passed with PostgreSQL + backend startup, live `/platform/status`, live `/api-docs`, backend log validation, and cleanup
- Manual checks: Confirmed the compose file declares only `postgres` and `backend`; confirmed no frontend process is started; confirmed the docs describe that limitation accurately
- Regression checks: Verified backend startup still succeeds from the real repository path, readiness endpoints stay reachable, and cleanup still removes the temporary stack resources
- Risks: Possible user-expectation mismatch remains because the command name can be read as “start everything,” but the docs already clarify that website startup remains separate; `TASK-006` remains blocked on Node.js/npm availability
- Handoff: `READY_FOR_PO`

