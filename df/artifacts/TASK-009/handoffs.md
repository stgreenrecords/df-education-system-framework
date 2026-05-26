# Handoff - TASK-009

## SA -> devops

- Timestamp: 2026-05-26 local
- Task: TASK-009
- From state: ARCHITECTURE_IN_PROGRESS
- To state: READY_FOR_DEV
- Lane: devops
- Subdashboard: `df/runtime/devops-board.md`
- Summary: SA routed the user's request for one Windows/macOS/Linux startup file to `devops` as local runtime automation. The recommended approach is one Java single-file source launcher that orchestrates local PostgreSQL, backend startup from `backend/platform-core`, readiness checks, and optional website startup when the frontend toolchain is available.

## Evidence

- `df/artifacts/TASK-009/task.md`
- `df/artifacts/TASK-009/solution-design.md`
- `docs/run-application.md`
- `README.md`
- `backend/platform-core/pom.xml`
- `backend/platform-core/src/main/resources/application.properties`
- `frontend/website/package.json`
- `devops/container/platform-core/README.md`
- `df/runtime/board.md`
- `df/runtime/devops-board.md`

## Tests/checks

| Check | Command/source | Result | Notes |
|---|---|---|---|
| Existing runtime-path review | `docs/run-application.md`; `README.md`; `backend/platform-core/pom.xml`; `backend/platform-core/src/main/resources/application.properties`; `frontend/website/package.json`; `devops/container/platform-core/README.md` | PASS | Confirmed the repository currently has manual/documented local-run paths but no single cross-platform launcher file |
| Lane routing review | `df/runtime/devops-board.md`; `df/roles/devops.md` | PASS | The requested work fits DevOps scope: startup/build/runtime automation and operational documentation |
| Cross-platform runtime assumption review | repository requirements + `java.version=25` | PASS | Java is already a hard prerequisite for the backend, making a Java single-file launcher the safest cross-platform single-file assumption currently available |

## Constraints

- Do not edit backend or frontend source files unless a real cross-lane blocker forces rerouting.
- Keep secrets local/demo only and allow environment-variable overrides.
- Avoid multi-file OS-specific wrappers; the task goal is one launcher file.
- Website startup must degrade gracefully when `node`/`npm` are unavailable.

## Recommended approach

1. Add one root launcher file, preferably `run-local.java`.
2. Detect Windows vs Unix-like command execution at runtime.
3. Start local PostgreSQL through Docker with configurable defaults.
4. Start backend from `backend/platform-core` using the verified module-level Maven command.
5. Wait for `/platform/status` and print local URLs.
6. Optionally start `frontend/website` only if `node` and `npm` are available.
7. Update root/docs instructions to use the new launcher.

## Risks

- Windows command branching may differ from Unix command branching.
- Fixed local ports may already be occupied.
- Website startup may not be possible in the current environment because `node`/`npm` are absent.

## Next role instructions

- `devops` should implement the single-file launcher and validate it locally on the current macOS environment.
- `devops` should document exact commands, runtime behavior, and limitations in `df/artifacts/TASK-009/devops/dev-notes.md`.
- If implementation succeeds, hand off to QA with reproducible verification steps.
- If the single-file constraint proves impossible with the current repository/runtime assumptions, return the task to SA with concrete evidence instead of substituting multi-file wrappers.

## devops -> qa

- Timestamp: 2026-05-26 local
- Task: TASK-009
- From state: DEV_IN_PROGRESS
- To state: READY_FOR_QA
- Lane: devops
- Summary: `devops` completed `TASK-009` by replacing the discarded Java launcher draft with one terminal-first cross-platform compose file, `compose.local.yaml`, that starts PostgreSQL plus the Spring backend from the real repository module path. The implementation was validated locally with compose startup, live backend health/OpenAPI/login checks, and compose cleanup.

## Evidence

- `compose.local.yaml`
- `README.md`
- `docs/run-application.md`
- `df/artifacts/TASK-009/devops/dev-notes.md`
- `df/artifacts/TASK-009/devops/handoff-to-qa.md`
- `df/artifacts/TASK-009/task.md`
- `df/runtime/board.md`
- `df/runtime/devops-board.md`

## Tests/checks

| Check | Command/source | Result | Notes |
|---|---|---|---|
| Compose availability | `docker compose version` | PASS | Confirmed Docker Compose is available locally |
| Wrapper-in-container viability | `docker run --rm -v "$PWD:/workspace" -w /workspace eclipse-temurin:25-jdk sh -lc 'sh ./mvnw -version'` | PASS | Confirmed the repo wrapper works inside the containerized runtime |
| Compose startup | `DF_DB_PORT=55437 DF_APP_PORT=18090 DF_POSTGRES_CONTAINER_NAME=df-local-postgres-task009 DF_BACKEND_CONTAINER_NAME=df-local-backend-task009 docker compose -f compose.local.yaml up -d` | PASS | Stack started successfully after the backend service command was corrected to package the reactor before running the exec jar |
| Backend health | `curl http://127.0.0.1:18090/platform/status` | PASS | Returned `200` and the expected health payload |
| OpenAPI exposure | `curl http://127.0.0.1:18090/api-docs` | PASS | Returned `200` and OpenAPI JSON |
| Login behavior | `curl -X POST http://127.0.0.1:18090/api/v1/identity/auth/login ...` | PASS | Returned `200`; bootstrap-admin correctly entered MFA enrollment flow in the validated runtime |
| Cleanup | `DF_DB_PORT=55437 DF_APP_PORT=18090 DF_POSTGRES_CONTAINER_NAME=df-local-postgres-task009 DF_BACKEND_CONTAINER_NAME=df-local-backend-task009 docker compose -f compose.local.yaml down -v` | PASS | Containers, network, and compose volume removed cleanly |

## Known risks

- The first compose startup in a clean environment may take longer because the backend reactor is packaged inside the backend container before the app process starts.
- The single-file startup path intentionally focuses on Spring + PostgreSQL only; website startup remains separate and manual.

## Next role instructions

- `qa` should confirm `compose.local.yaml` is now the single required startup file and that the docs recommend the compose path rather than the discarded Java launcher.
- `qa` should rerun the compose startup path, verify `/platform/status` and `/api-docs`, confirm the login path returns a successful auth/MFA response, and validate cleanup with `docker compose -f compose.local.yaml down -v`.
- If QA finds any regression in startup, port mapping, cleanup, or documentation accuracy, return the task to `devops` with exact reproduction steps and evidence.

## qa -> po

- Timestamp: 2026-05-26 local
- Task: TASK-009
- From state: READY_FOR_QA
- To state: READY_FOR_PO
- Lane: qa
- Summary: `qa` independently re-ran the compose-based startup flow and passed the task. Validation confirmed that `compose.local.yaml` is the single launcher file, that it starts PostgreSQL plus the Spring backend successfully, that `/platform/status` and `/api-docs` work, and that cleanup removes the temporary stack. QA also confirmed that the compose file does not start the website frontend; this is intentional and matches the documented TASK-009 scope rather than a defect.

## Evidence

- `df/artifacts/TASK-009/qa-report.md`
- `df/artifacts/TASK-009/task.md`
- `df/artifacts/TASK-009/devops/handoff-to-qa.md`
- `compose.local.yaml`
- `README.md`
- `docs/run-application.md`
- `df/runtime/board.md`
- `df/runtime/devops-board.md`

## Tests/checks

| Check | Command/source | Result | Notes |
|---|---|---|---|
| Compose service listing | `docker compose -f compose.local.yaml config --services` | PASS | Confirmed the launcher declares `postgres` and `backend` only |
| Compose startup | `DF_DB_PORT=55439 DF_APP_PORT=18091 DF_POSTGRES_CONTAINER_NAME=df-local-postgres-task009-qa DF_BACKEND_CONTAINER_NAME=df-local-backend-task009-qa docker compose -f compose.local.yaml up -d` | PASS | Temporary QA stack started successfully |
| Backend readiness | `curl http://127.0.0.1:18091/platform/status` with a short retry loop | PASS | Returned `200` and the expected `UP` payload after JVM startup completed |
| OpenAPI readiness | `curl -o /dev/null -w '%{http_code}' http://127.0.0.1:18091/api-docs` | PASS | Returned `200` |
| Backend log inspection | `docker compose -f compose.local.yaml logs --tail=120 backend` | PASS | Confirmed successful reactor packaging, Flyway validation, and Spring startup |
| Frontend expectation probe | `curl -o /dev/null -w '%{http_code}' http://127.0.0.1:3000` | PASS (expected absence) | Verified no website process is started by the compose file; docs describe website startup as a separate manual flow |
| Cleanup | `DF_DB_PORT=55439 DF_APP_PORT=18091 DF_POSTGRES_CONTAINER_NAME=df-local-postgres-task009-qa DF_BACKEND_CONTAINER_NAME=df-local-backend-task009-qa docker compose -f compose.local.yaml down -v` | PASS | Removed the temporary containers, network, and compose volume |

## Known risks

- The launcher name may create a user expectation that the website is included, but the docs explicitly scope it to PostgreSQL + Spring backend only.
- `TASK-006` remains blocked on Node.js/npm availability, so frontend runtime validation is still a separate concern from this DevOps launcher task.

## Next role instructions

- `po` should validate the accepted scope of `TASK-009` as a single-file compose launcher for PostgreSQL + Spring backend.
- `po` should confirm the docs accurately explain that website startup remains a separate manual path under `frontend/website`.
- If PO wants one command to start the website too, reject the task as a scope change and return it to the correct lane with that explicit new requirement.

## po -> devops

- Timestamp: 2026-05-26 local
- Task: TASK-009
- From state: READY_FOR_PO
- To state: RETURNED_TO_DEV
- Lane: devops
- Summary: PO rejected the current launcher scope after explicit human feedback confirmed that the one-command startup experience is expected to start the website frontend too. The delivered `compose.local.yaml` starts only PostgreSQL plus the Spring backend.

## Evidence

- `df/artifacts/TASK-009/po-review.md`
- `df/artifacts/TASK-009/qa-report.md`
- `df/artifacts/TASK-009/task.md`
- `compose.local.yaml`
- `docs/run-application.md`
- `README.md`
- `df/runtime/board.md`
- `df/runtime/devops-board.md`

## Rework requested

1. Extend the single-file launcher experience so that `docker compose -f compose.local.yaml up` also starts the website frontend.
2. Keep the command cross-platform and terminal-first.
3. Preserve safe local defaults and environment-variable overrides.
4. If frontend startup cannot be added purely in DevOps-owned files, document the blocker in `df/artifacts/TASK-009/devops/dev-notes.md` and hand off to SA for routing.

## Notes

- This rejection is based on explicit human product feedback in the current session, not on a QA defect.
- The current documentation accurately describes the existing backend-only compose scope, but that scope is not sufficient for product acceptance.

## devops -> qa (rework)

- Timestamp: 2026-05-26 local
- Task: TASK-009
- From state: DEV_IN_PROGRESS
- To state: READY_FOR_QA
- Lane: devops
- Summary: `devops` completed the PO-requested rework by extending `compose.local.yaml` so the same cross-platform command now starts PostgreSQL, the Spring backend, and `frontend/website` together. The updated launcher uses a containerized Node.js runtime for the website, so host `node`/`npm` availability is no longer required.

## Evidence

- `compose.local.yaml`
- `README.md`
- `docs/run-application.md`
- `df/artifacts/TASK-009/devops/dev-notes.md`
- `df/artifacts/TASK-009/devops/handoff-to-qa.md`
- `df/artifacts/TASK-009/task.md`
- `df/runtime/board.md`
- `df/runtime/devops-board.md`

## Tests/checks

| Check | Command/source | Result | Notes |
|---|---|---|---|
| Compose service listing | `docker compose -f compose.local.yaml config --services` | PASS | Confirmed the launcher now declares `postgres`, `backend`, and `frontend` |
| Compose startup | `DF_DB_PORT=55441 DF_APP_PORT=18092 DF_WEB_PORT=3002 DF_POSTGRES_CONTAINER_NAME=df-local-postgres-task009-dev DF_BACKEND_CONTAINER_NAME=df-local-backend-task009-dev DF_FRONTEND_CONTAINER_NAME=df-local-frontend-task009-dev docker compose -f compose.local.yaml up -d` | PASS | Temporary rework-validation stack started successfully |
| Backend readiness | Python polling of `http://127.0.0.1:18092/platform/status` and `http://127.0.0.1:18092/api-docs` | PASS | Returned `200` for both endpoints |
| Frontend readiness | Python polling of `http://127.0.0.1:3002/` and `http://127.0.0.1:3002/login` | PASS | Returned `200` for both website routes |
| Frontend auth proxy | `curl -X POST http://127.0.0.1:3002/api/auth/login ...` | PASS | Returned `200`, proving the website container can reach the backend over the compose network |
| Cleanup | `DF_DB_PORT=55441 DF_APP_PORT=18092 DF_WEB_PORT=3002 DF_POSTGRES_CONTAINER_NAME=df-local-postgres-task009-dev DF_BACKEND_CONTAINER_NAME=df-local-backend-task009-dev DF_FRONTEND_CONTAINER_NAME=df-local-frontend-task009-dev docker compose -f compose.local.yaml down -v` | PASS | Removed the temporary containers, network, and compose volumes |

## Known risks

- The first launcher startup in a clean environment may still take longer because both Maven and npm dependencies are prepared inside containers.
- Manual host-side website startup remains available, but the docs must now make clear that the compose launcher includes the website by default.

## Next role instructions

- `qa` should rerun the compose startup path and confirm that PostgreSQL, Spring backend, and `frontend/website` now start together.
- `qa` should validate backend endpoints, website routes, and the website auth proxy through the published frontend port.
- `qa` should confirm the docs no longer describe the compose launcher as backend-only.

