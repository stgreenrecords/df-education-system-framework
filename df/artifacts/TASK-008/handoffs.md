# Handoff - TASK-008

## SA -> qa

- Timestamp: 2026-05-26 local
- Task: TASK-008
- From state: ARCHITECTURE_IN_PROGRESS
- To state: READY_FOR_QA
- Summary: SA completed the documentation-only local run guide requested by the user, added a new `docs/run-application.md` file, linked it from the root `README.md`, and verified the backend startup/health/login examples against the current repository layout and a real local PostgreSQL container.

## Evidence

- `df/artifacts/TASK-008/task.md`
- `df/artifacts/TASK-008/solution-design.md`
- `README.md`
- `docs/run-application.md`
- `backend/platform-core/pom.xml`
- `backend/platform-core/src/main/resources/application.properties`
- `backend/platform-core/src/main/java/com/darkfactory/education/platform/EducationSystemApplication.java`
- `frontend/website/package.json`
- `frontend/website/README.md`
- `devops/container/platform-core/README.md`
- `df/runtime/board.md`

## Tests/checks

| Check | Command/source | Result | Notes |
|---|---|---|---|
| Runtime prerequisite check | `command -v java`; `command -v node`; `command -v npm`; `command -v docker`; `command -v psql` | PASS | Confirmed `java` and `docker` are present in this environment; `node`, `npm`, and `psql` were not present, so frontend steps were documented from repository metadata rather than executed locally |
| Backend module-entry inspection | `backend/platform-core/pom.xml`; `backend/platform-core/src/main/java/com/darkfactory/education/platform/EducationSystemApplication.java` | PASS | Confirmed the runnable Spring Boot entrypoint belongs to `backend/platform-core` |
| Backend configuration inspection | `backend/platform-core/src/main/resources/application.properties`; `backend/identity-access/src/main/java/com/darkfactory/education/identityaccess/auth/AuthProperties.java` | PASS | Confirmed DB/auth/bootstrap-admin settings and captured the secret-format constraints needed for reliable local examples |
| Local PostgreSQL startup | `docker run -d --name df-docs-postgres ... -p 55433:5432 postgres:17-alpine` | PASS | First attempt on `55432` failed because the port was already allocated; `55433` succeeded |
| Backend startup verification | `sh ./mvnw -f backend/platform-core/pom.xml spring-boot:run` with local env vars | PASS | Verified the module-level command works; also confirmed the parent-level `sh ./mvnw -f backend/pom.xml -pl platform-core -am spring-boot:run` fails with `Unable to find a suitable main class`, so the guide explicitly warns against it |
| Backend health/OpenAPI verification | `curl http://127.0.0.1:8080/platform/status`; `curl http://127.0.0.1:8080/api-docs` | PASS | Confirmed the backend responds successfully after startup |
| Optional bootstrap-admin login verification | `curl -X POST http://127.0.0.1:8080/api/v1/identity/auth/login ...` | PASS | Confirmed the documented bootstrap-admin example returns `200` after using strong raw-string demo secrets |

## Known risks

- Website commands were not executed in this workstation because `node`/`npm` are unavailable here; QA should validate that the written frontend section aligns with `frontend/website/package.json` and `frontend/website/README.md`.
- The guide intentionally documents local demo secrets only; production secret-management guidance remains out of scope.

## Next role instructions

- Confirm the task remains documentation-only and did not change application code, schema, or contracts.
- Review `docs/run-application.md` for accuracy against the referenced backend/frontend metadata.
- Re-run or inspect the documented backend startup path, health checks, and troubleshooting notes.
- Confirm the root `README.md` now links cleanly to the run guide.
- If the guide is accurate and sufficient, move `TASK-008` to `READY_FOR_PO`; otherwise return it with defects.

