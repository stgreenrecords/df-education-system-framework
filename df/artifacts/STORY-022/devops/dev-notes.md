# DevOps Notes - STORY-022

## Task

Implement the first Podman-compatible OCI container baseline for `backend/platform-core`.

## Implementation summary

Implemented a DevOps-owned OCI/container-runtime baseline for the existing Spring Boot `platform-core` application.

### What changed

| File | Change | Notes |
|---|---|---|
| `backend/platform-core/pom.xml` | Updated | Added Spring Boot `repackage` execution with classifier `exec` so the build now produces `platform-core-0.1.0-SNAPSHOT-exec.jar` for container packaging while preserving the normal jar for tests |
| `devops/container/platform-core/Containerfile` | Created | Added OCI-compatible runtime image definition using Java 25 JRE, exposed port `8080`, and a healthcheck against `/platform/status` |
| `devops/container/platform-core/build-image.ps1` | Created | Added Podman-first image build helper that packages `platform-core` and builds the OCI image |
| `devops/container/platform-core/run-local-stack.ps1` | Created | Added local PostgreSQL + application container startup helper with environment-backed configuration |
| `devops/container/platform-core/stop-local-stack.ps1` | Created | Added local container/network cleanup helper |
| `devops/container/platform-core/README.md` | Created | Documented Podman-first usage, Docker fallback, readiness contract, and runtime variables |

## Important implementation details

### 1. Executable jar packaging without breaking tests

The initial Spring Boot plugin addition replaced the main module jar with a repackaged boot jar, which broke the `failsafe` integration-test classpath.

Resolution:
- kept the regular jar as the main artifact
- attached the runnable Spring Boot jar with classifier `exec`
- updated the container assets to copy `backend/platform-core/target/platform-core-0.1.0-SNAPSHOT-exec.jar`

### 2. Podman-first, OCI-compatible assets

The container assets are written to stay Podman-compatible and OCI-first:
- `Containerfile` instead of Dockerfile-specific assumptions
- no daemon-specific build features
- runtime configuration only through environment variables
- no cloud-provider or country-specific values embedded in the image

### 3. Local environment fallback

`podman` was not available in the local environment, but Docker Desktop was available. Per the task assumptions and SA guidance:
- assets remained Podman-first and OCI-compatible
- validation used Docker only as a local environment fallback
- the helper scripts accept `-ContainerRuntime podman|docker`

### 4. Readiness/health contract

The image exposes:
- container port `8080`
- HTTP readiness/health endpoint `/platform/status`
- image/container healthcheck using `wget --quiet --tries=1 --spider http://127.0.0.1:8080/platform/status`

### 5. PowerShell helper hardening

The first local-stack run failed because Windows PowerShell treated expected “resource not found yet” checks from the external container runtime as terminating errors.

Resolution:
- changed quiet resource-probe and cleanup logic in `run-local-stack.ps1` and `stop-local-stack.ps1` to use `Start-Process` with redirected stdout/stderr
- kept normal successful runtime commands as direct invocations

## Validation commands and results

### Backend reactor verify

```text
Command: .\mvnw.cmd -f backend/pom.xml -pl platform-core -am clean verify
Result: BUILD SUCCESS
Tests run: 15, Failures: 0, Errors: 0, Skipped: 0
Finished at: 2026-05-24T19:37:39+02:00
```

### Full parent verify

```text
Command: .\mvnw.cmd clean verify
Result: BUILD SUCCESS
Tests run: 15, Failures: 0, Errors: 0, Skipped: 0
Finished at: 2026-05-24T19:37:54+02:00
```

### OCI image build

```text
Command: .\devops\container\platform-core\build-image.ps1 -ContainerRuntime docker -ImageName df-platform-core:story022
Result: PASS
Notes: Docker used as local fallback because Podman was unavailable; image built successfully from `eclipse-temurin:25-jre-alpine`
Finished at: 2026-05-24T19:38:06+02:00
```

### Container smoke test

```text
Command: .\devops\container\platform-core\run-local-stack.ps1 -ContainerRuntime docker -ImageName df-platform-core:story022 -HostPort 18084
Result: PASS
Notes: Started `df-platform-core-postgres` and `df-platform-core-app` on isolated local runtime assets; endpoint and image health checks were verified separately
```

### Container readiness and health verification

```text
Command: Invoke-WebRequest -UseBasicParsing http://127.0.0.1:18084/platform/status
Result: PASS
Observed body: {"service":"education-system-framework","status":"UP"}

Command: docker image inspect df-platform-core:story022 --format '{{json .Config.Healthcheck.Test}}|{{json .Config.ExposedPorts}}'
Result: PASS
Observed: ["CMD-SHELL","wget --quiet --tries=1 --spider http://127.0.0.1:8080/platform/status || exit 1"]|{"8080/tcp":{}}

Command: docker ps --format "table {{.Names}}\t{{.Image}}\t{{.Status}}\t{{.Ports}}"
Result: PASS
Observed while running: `df-platform-core-app` was `Up ... (healthy)` on `0.0.0.0:18084->8080/tcp`; `df-platform-core-postgres` was running on the isolated container network
```

### Executable jar probe

```text
Command: java -jar backend/platform-core/target/platform-core-0.1.0-SNAPSHOT-exec.jar --server.port=18085 --spring.main.web-application-type=none
Result: PASS (packaging proof)
Notes: The process launched through Spring Boot's boot loader and reached the expected database-connection attempt; it then failed because no external PostgreSQL runtime was provided for that direct-launch probe.
```

### Cleanup verification

```text
Command: .\devops\container\platform-core\stop-local-stack.ps1 -ContainerRuntime docker
Result: PASS

Command: docker ps --filter "name=df-platform-core" --format "{{.Names}}|{{.Status}}"; docker network ls --filter "name=df-platform-core-local" --format "{{.Name}}"
Result: PASS
Notes: No matching containers or local network remained after cleanup.
```

## Acceptance criteria status

| AC | Status | Evidence |
|---|---|---|
| 1. OCI-compatible application image is produced | PASS | `Containerfile`; `build-image.ps1`; successful image build `df-platform-core:story022` |
| 2. Podman users can run the image with externalized configuration | PASS (design + script path), local runtime validated via Docker fallback | `README.md`; helper scripts accept `podman`; assets avoid daemon-specific assumptions; local smoke test proved the same OCI assets with Docker because Podman was unavailable locally |
| 3. Application connects to containerized PostgreSQL using environment config | PASS | `run-local-stack.ps1`; live local stack with PostgreSQL container and `EDU_DB_*` variables; `GET /platform/status` returned 200 |
| 4. No secrets, country-specific code, or cloud-specific code are embedded in the image | PASS | Direct review of `Containerfile`, scripts, and `README.md`; configuration supplied only at runtime; no cloud-country values embedded |
| 5. Image exposes health/readiness behavior suitable for later orchestration | PASS | `/platform/status` documented and reachable; image exposes `8080/tcp`; container healthcheck configured and observed as `healthy` |

## Environment notes

- OS: Windows
- Java: 25.0.2
- Maven Wrapper: 3.9.15
- OCI runtime availability:
  - `podman`: missing in local environment
  - `docker`: available at `C:\Program Files\Docker\Docker\resources\bin\docker.exe`
- Validation runtime used: Docker Desktop 29.2.1 as OCI fallback

## Risks and limitations

- `RISK-027` remains open: Podman-specific execution was not available locally, so runtime proof used Docker as the documented OCI fallback.
- `RISK-015` remains relevant: this is a local OCI baseline only; broader orchestration and provider overlays remain future work in `STORY-023`.
- Springdoc, Mockito, and Java native-access warnings remained non-blocking during Maven validation.

