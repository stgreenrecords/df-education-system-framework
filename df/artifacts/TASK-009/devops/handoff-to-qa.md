# DevOps Handoff to QA - TASK-009

## Summary

`devops` completed rework for `TASK-009` by extending the single terminal-first cross-platform startup file:

- `compose.local.yaml`

The launcher is still invoked through the same command on Windows, macOS, and Linux:

```text
docker compose -f compose.local.yaml up
```

It now starts the full currently implemented local stack:

- PostgreSQL
- the Spring backend from `backend/platform-core`
- the website frontend from `frontend/website`

## Scope delivered

- Single-file terminal startup path for PostgreSQL + Spring backend + website frontend
- Cross-platform `docker compose` entrypoint
- Containerized Node.js runtime for the website so host `node`/`npm` are not required
- Safe local default environment variables with override support
- Cache volumes for Maven, npm, and frontend `node_modules`
- Updated runtime documentation in `README.md` and `docs/run-application.md`
- No backend or frontend source-code changes

## Evidence

- `compose.local.yaml`
- `README.md`
- `docs/run-application.md`
- `df/artifacts/TASK-009/devops/dev-notes.md`
- `df/artifacts/TASK-009/task.md`
- `df/artifacts/TASK-009/handoffs.md`
- `df/runtime/board.md`
- `df/runtime/devops-board.md`

## Validation performed

| Check | Command | Result | Notes |
|---|---|---|---|
| Compose availability | `docker compose version` | PASS | Docker Compose `v5.1.4` available locally |
| Wrapper-in-container check | `docker run --rm -v "$PWD:/workspace" -w /workspace eclipse-temurin:25-jdk sh -lc 'sh ./mvnw -version'` | PASS | Proved the backend build/run path remains viable inside the compose runtime |
| Node-image check | `docker run --rm node:22-bookworm sh -lc 'node --version && npm --version'` | PASS | Confirmed the frontend service image includes a supported Node.js/npm runtime |
| Compose service listing | `docker compose -f compose.local.yaml config --services` | PASS | Services now render as `postgres`, `backend`, and `frontend` |
| Compose stack startup | `DF_DB_PORT=55441 DF_APP_PORT=18092 DF_WEB_PORT=3002 DF_POSTGRES_CONTAINER_NAME=df-local-postgres-task009-dev DF_BACKEND_CONTAINER_NAME=df-local-backend-task009-dev DF_FRONTEND_CONTAINER_NAME=df-local-frontend-task009-dev docker compose -f compose.local.yaml up -d` | PASS | Database, backend, and frontend containers all started successfully |
| Backend readiness | Python polling of `http://127.0.0.1:18092/platform/status` and `http://127.0.0.1:18092/api-docs` | PASS | Both endpoints returned `200` |
| Frontend readiness | Python polling of `http://127.0.0.1:3002/` and `http://127.0.0.1:3002/login` | PASS | Both website routes returned `200` |
| Frontend auth proxy | `curl -X POST http://127.0.0.1:3002/api/auth/login ...` | PASS | Returned `200`, confirming the website container can reach the backend via `EDUCATION_API_BASE_URL=http://backend:8080` |
| Container log inspection | `docker compose -f compose.local.yaml logs --tail=80 backend frontend` | PASS | Confirmed successful backend build/startup and Next.js startup |
| Cleanup | `docker compose -f compose.local.yaml down -v` | PASS | Containers, network, and compose volumes removed |

## Important implementation notes

- An earlier Java launcher draft was already discarded after explicit user feedback that the startup flow must be terminal-first. QA should validate only the compose-based implementation.
- A previous compose version started only PostgreSQL plus the Spring backend. This rework adds the `frontend` service so the one-command launcher now matches the updated product expectation.
- The website service runs inside a Node.js container, so host `node`/`npm` absence should no longer block launcher validation.

## QA focus

1. Confirm `compose.local.yaml` is still the single required launcher file.
2. Confirm the recommended command remains terminal-first and cross-platform:
   - `docker compose -f compose.local.yaml up`
3. Re-run the compose startup path and verify:
   - PostgreSQL starts
   - Spring backend becomes healthy
   - `/platform/status` works
   - `/api-docs` works
   - website `/` and `/login` work
4. Confirm the website auth proxy can still reach the backend:
   - `POST /api/auth/login`
5. Confirm cleanup works with:
   - `docker compose -f compose.local.yaml down -v`
6. Confirm `README.md` and `docs/run-application.md` now describe frontend startup as part of the compose launcher.

## Expected next state

- If all checks pass: `READY_FOR_PO`
- If defects are found: return to `devops` with exact reproduction steps and evidence
