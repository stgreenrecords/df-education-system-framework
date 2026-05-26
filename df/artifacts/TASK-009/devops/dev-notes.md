# DevOps Notes - TASK-009

## Session

- Timestamp: 2026-05-26 local
- Role: `devops`
- Task: `TASK-009`
- State: `RETURNED_TO_DEV -> DEV_IN_PROGRESS -> READY_FOR_QA`

## Inputs reviewed

- `df/artifacts/TASK-009/task.md`
- `df/artifacts/TASK-009/solution-design.md`
- `df/artifacts/TASK-009/handoffs.md`
- `df/runtime/board.md`
- `df/runtime/devops-board.md`
- `docs/run-application.md`
- `README.md`
- `devops/container/platform-core/Containerfile`
- `backend/platform-core/src/main/resources/application.properties`
- `frontend/website/package.json`
- `frontend/website/package-lock.json`
- `frontend/website/README.md`
- `frontend/website/lib/backend.ts`

## Scope confirmation

- The user explicitly rejected the earlier host-side Java launcher direction and requested a terminal-first startup flow.
- PO later rejected the backend-only compose scope after explicit human feedback confirmed that the one-command launcher is expected to start the website frontend too.
- The reworked scope is therefore one single terminal-driven startup file for PostgreSQL, the Spring backend, and `frontend/website` together.
- The current host shell still lacks `node`/`npm`, so the frontend must run inside a containerized Node.js runtime rather than a host-side process.
- No backend or frontend source files were edited.

## Implementation completed

- Updated the single root-level launcher file `compose.local.yaml`.
- The compose file now starts:
  - PostgreSQL 17 (`postgres:17-alpine`)
  - the Spring backend from the real repository sources using `backend/platform-core`
  - the website frontend from `frontend/website` using a Node.js container
- The backend service still builds the backend reactor inside the container and then runs the generated executable jar:
  - `sh ./mvnw -f backend/pom.xml -pl platform-core -am package -DskipTests`
  - `java -jar backend/platform-core/target/platform-core-0.1.0-SNAPSHOT-exec.jar --server.port=8080`
- Added a new frontend service that:
  - uses `node:22-bookworm`
  - installs dependencies with `npm install --no-fund --no-audit`
  - starts Next.js with `npm run dev -- --hostname 0.0.0.0 --port 3000`
  - uses `EDUCATION_API_BASE_URL=http://backend:8080` so the website auth proxy can reach the backend over the compose network
- Added configurable environment-variable overrides for frontend port/container/image settings.
- Added named cache volumes for Maven, npm, and frontend `node_modules` so repeated compose startups can reuse dependency caches unless the user explicitly runs `docker compose ... down -v`.
- Updated `README.md` and `docs/run-application.md` so the recommended startup command remains terminal-first:
  - `docker compose -f compose.local.yaml up`
  and now accurately includes the website frontend.

## Files changed

- `compose.local.yaml`
- `README.md`
- `docs/run-application.md`
- `df/artifacts/TASK-009/task.md`
- `df/artifacts/TASK-009/handoffs.md`
- `df/runtime/board.md`
- `df/runtime/devops-board.md`
- `df/runtime/activity-log.md`

## Validation evidence

| Check | Command | Result | Notes |
|---|---|---|---|
| Compose availability | `docker compose version` | PASS | Docker Compose `v5.1.4` is available in the current macOS environment |
| Maven wrapper in Java container | `docker run --rm -v "$PWD:/workspace" -w /workspace eclipse-temurin:25-jdk sh -lc 'sh ./mvnw -version'` | PASS | Confirmed the repo wrapper runs in a plain Java 25 container |
| Node.js container check | `docker run --rm node:22-bookworm sh -lc 'node --version && npm --version'` | PASS | Confirmed the frontend service image includes a supported Node.js/npm runtime |
| Compose service render | `docker compose -f compose.local.yaml config --services` | PASS | Rendered `postgres`, `backend`, and `frontend` |
| Compose stack startup | `cd "/Users/Viachaslau_Karnaushanka/Downloads/DF Education System Framework" && DF_DB_PORT=55441 DF_APP_PORT=18092 DF_WEB_PORT=3002 DF_POSTGRES_CONTAINER_NAME=df-local-postgres-task009-dev DF_BACKEND_CONTAINER_NAME=df-local-backend-task009-dev DF_FRONTEND_CONTAINER_NAME=df-local-frontend-task009-dev docker compose -f compose.local.yaml up -d` | PASS | PostgreSQL, backend, and frontend containers all started successfully |
| Backend health verification | Python polling of `http://127.0.0.1:18092/platform/status` | PASS | Returned `200` with the expected `UP` payload |
| OpenAPI verification | Python polling of `http://127.0.0.1:18092/api-docs` | PASS | Returned `200` and OpenAPI JSON |
| Frontend route verification | Python polling of `http://127.0.0.1:3002/` and `http://127.0.0.1:3002/login` | PASS | Both routes returned `200` after Next.js startup |
| Frontend auth-proxy verification | `curl -X POST http://127.0.0.1:3002/api/auth/login ...` | PASS | Returned `200`, proving the website container can reach the backend through `EDUCATION_API_BASE_URL=http://backend:8080` |
| Container log inspection | `docker compose -f compose.local.yaml logs --tail=80 backend frontend` | PASS | Confirmed backend build/startup and Next.js startup/auth-proxy activity |
| Compose cleanup | `docker compose -f compose.local.yaml down -v` | PASS | Containers, network, and compose volumes were removed successfully |

## Previously fixed implementation defect

### Defect 1: backend service command failed in a clean container

- Initial attempt: backend service ran `spring-boot:run` directly from `backend/platform-core`
- Actual result: Maven failed to resolve sibling reactor modules (`common`, `identity-access`, `organization`, etc.) in the clean container cache
- Root cause: `platform-core` alone does not have the local reactor artifacts preinstalled in a fresh container environment
- Fix: changed the backend service command to package the backend reactor first from `backend/pom.xml` and then run the generated executable jar
- Verification: the corrected compose file brought the backend up successfully and passed the live health/OpenAPI/login checks

## Risks / QA focus

- Confirm the task still uses one terminal-first compose flow and that `RunLocal.java` is no longer part of the recommended startup path.
- Confirm `compose.local.yaml` is the only required launcher file and that the documented command is the same on Windows, macOS, and Linux.
- Re-run the compose startup path and verify PostgreSQL, Spring backend, and `frontend/website` behavior plus cleanup.
- Confirm the website routes respond on the published frontend port.
- Confirm the website auth proxy can still reach the backend from inside the compose network.
- Confirm the docs now describe frontend startup as included in the compose launcher rather than a separate manual-only path.

## Ready for QA

- DevOps rework is complete.
- Local runtime validation succeeded for database + backend + website.
- Documentation was updated to the corrected full-stack compose path.
