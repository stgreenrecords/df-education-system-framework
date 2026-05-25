# QA Report - STORY-022

## Scope

Verify the DevOps-owned Podman-compatible OCI baseline for `backend/platform-core`, including executable-jar packaging, OCI image build behavior, local PostgreSQL-backed runtime startup, readiness/health behavior, and scope/security constraints.

## Environment

- Date: 2026-05-24
- OS: Windows
- Shell: Windows PowerShell 5.1
- Java: 25.0.2
- Maven Wrapper: 3.9.15
- OCI runtime availability:
  - `podman`: not installed in the local environment
  - `docker`: available at `C:\Program Files\Docker\Docker\resources\bin\docker.exe`
- Validation runtime used: Docker Desktop 29.2.1 as the documented OCI-compatible fallback

## Test cases

1. Re-run targeted backend verification and confirm the packaging change still preserves the normal jar while producing the executable classified jar.
2. Re-run the full parent build to catch regressions outside `platform-core`.
3. Build the OCI image from the repository using the shipped DevOps helper and confirm the resulting image exposes the expected readiness/health contract.
4. Run the local PostgreSQL + application stack with externalized configuration and confirm `/platform/status` returns success while the app container becomes healthy.
5. Inspect the changed files for embedded secrets, country-specific values, cloud/provider-specific assumptions, and cross-lane scope violations.
6. Clean up the temporary runtime resources and confirm they are removed.

## Test execution

| Category | Command / method | Result | Notes |
|---|---|---|---|
| Static/editor diagnostics | IDE diagnostics on `backend/platform-core/pom.xml`, `devops/container/platform-core/Containerfile`, `build-image.ps1`, `run-local-stack.ps1`, `stop-local-stack.ps1`, `README.md` | PASS | No file-level errors reported |
| Runtime availability check | `Get-Command podman`; `Get-Command docker` | PASS (environment-limited) | `podman` missing locally; `docker` available and used as fallback per task assumptions |
| Backend reactor verify | `./mvnw.cmd -f backend/pom.xml -pl platform-core -am clean verify` | PASS | Build succeeded; 15 integration tests passed; both `backend/platform-core/target/platform-core-0.1.0-SNAPSHOT.jar` and `backend/platform-core/target/platform-core-0.1.0-SNAPSHOT-exec.jar` were present |
| Full parent verify | `./mvnw.cmd clean verify` | PASS | 12-project parent build succeeded; 15 integration tests passed |
| OCI image build | `./devops/container/platform-core/build-image.ps1 -ContainerRuntime docker -ImageName df-platform-core:qa022` | PASS | Image built successfully from `eclipse-temurin:25-jre-alpine` using the repository root build context |
| Image contract inspection | `docker image inspect df-platform-core:qa022 --format '{{json .Config.Healthcheck.Test}}|{{json .Config.ExposedPorts}}'` | PASS | Healthcheck targets `http://127.0.0.1:8080/platform/status`; exposed ports include `8080/tcp` |
| Local runtime smoke test | `./devops/container/platform-core/run-local-stack.ps1 -ContainerRuntime docker -ImageName df-platform-core:qa022 -HostPort 18084` | PASS | Started `df-platform-core-postgres` and `df-platform-core-app` on an isolated local network with environment-backed datasource settings |
| Manual readiness check | `curl.exe -s http://127.0.0.1:18084/platform/status` | PASS | Returned `{"service":"education-system-framework","status":"UP"}` |
| Manual container-health check | `docker ps`; `docker inspect --format "{{json .State.Health}}" df-platform-core-app` | PASS | App container reported `Up ... (healthy)`; health state was `{"Status":"healthy","FailingStreak":0,...}` |
| Scope/security inspection | Direct review of changed files + targeted searches for cloud/country/secret patterns | PASS | No secrets embedded; no country-specific values; no cloud-provider-specific or Docker-daemon-specific artifact behavior. `README.md` only contains a warning not to commit secrets |
| Cleanup verification | `./devops/container/platform-core/stop-local-stack.ps1 -ContainerRuntime docker`; follow-up `docker ps` and `docker network ls` | PASS | Temporary app/postgres containers and `df-platform-core-local` network were removed |

## Acceptance criteria coverage

| Acceptance criterion | Status | Evidence |
|---|---|---|
| Given the Maven application build, when the container image is built, then an OCI-compatible application image is produced | PASS | `build-image.ps1` succeeded and produced `df-platform-core:qa022` from `devops/container/platform-core/Containerfile` |
| Given a developer or country operator uses Podman, when they run the application image with externalized configuration, then the application starts successfully | PASS with environment note | Assets are Podman-first (`Containerfile`, helper scripts default to `podman`) and runtime settings are externalized; local execution was validated with Docker only because Podman was unavailable |
| Given PostgreSQL is required, when running the local container baseline, then the application connects to a containerized PostgreSQL instance using environment-provided configuration | PASS | `run-local-stack.ps1` injected `EDU_DB_URL`, `EDU_DB_USERNAME`, and `EDU_DB_PASSWORD`; live stack returned HTTP 200 from `/platform/status` while PostgreSQL was running |
| Given the container definition is reviewed, then no secrets, country-specific code, or cloud-specific code are embedded in the image | PASS | `Containerfile`, scripts, and docs contain no embedded secrets or provider/country-specific implementation values |
| Given the image is inspected, then it exposes health/readiness behavior suitable for later orchestration | PASS | `EXPOSE 8080`, `/platform/status`, and the configured image healthcheck were all observed during QA |

## Regression notes

- The shared build-file change in `backend/platform-core/pom.xml` did not break the normal jar artifact or the existing integration tests.
- The full parent build remained green after the packaging addition.
- Non-blocking warnings remained during Maven validation (Springdoc endpoint warning, Mockito inline agent warning, Java native-access warning), but none prevented successful build or runtime verification.

## Risks / limitations

- `RISK-027`: Podman could not be exercised locally because it is not installed in this environment. Docker fallback evidence is acceptable under the documented task assumption but Podman execution remains unproven on this machine.
- `RISK-015`: this story is the local OCI baseline only; orchestration/IaC work remains for `STORY-023`.

## QA Result: PASS

- Task: STORY-022
- Acceptance criteria covered: Yes
- Unit tests: Covered via `./mvnw.cmd -f backend/pom.xml -pl platform-core -am clean verify` and `./mvnw.cmd clean verify`; both passed with 15 integration tests total and no failures
- Integration tests: Existing `platform-core` integration suite passed; live local OCI smoke test against containerized PostgreSQL passed
- Manual checks: Verified runtime availability, image healthcheck/exposed port config, `/platform/status` response, healthy container state, and cleanup behavior
- Regression checks: Full parent Maven verify passed; regular jar plus classified executable jar both remained present
- Risks: Docker-only runtime validation due missing Podman (`RISK-027`); broader orchestration remains out of scope (`RISK-015`)
- Handoff: READY_FOR_PO

