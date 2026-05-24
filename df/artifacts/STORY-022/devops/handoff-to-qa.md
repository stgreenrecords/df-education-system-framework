# DevOps Handoff to QA - STORY-022

## Task

STORY-022 — Implement Podman-compatible OCI container baseline for `backend/platform-core`

## From state

`DEV_IN_PROGRESS`

## To state

`READY_FOR_QA`

## Lane

`devops`

## Summary

DevOps implementation is complete. The repository now produces a classified executable Spring Boot jar for `platform-core`, includes Podman-compatible OCI image assets under `devops/container/platform-core/`, and provides local helper scripts for building the image and running the application against a containerized PostgreSQL dependency with environment-backed configuration.

Local validation used Docker as the OCI fallback because Podman was not installed in the current environment. The assets remain Podman-first and avoid Docker-daemon-specific assumptions.

## Files changed

| File | Change |
|---|---|
| `backend/platform-core/pom.xml` | Added classified Spring Boot `repackage` execution for executable jar packaging |
| `devops/container/platform-core/Containerfile` | Added OCI image definition |
| `devops/container/platform-core/build-image.ps1` | Added image build helper |
| `devops/container/platform-core/run-local-stack.ps1` | Added local PostgreSQL + app runtime helper |
| `devops/container/platform-core/stop-local-stack.ps1` | Added cleanup helper |
| `devops/container/platform-core/README.md` | Added usage and runtime contract documentation |
| `df/artifacts/STORY-022/devops/dev-notes.md` | Added implementation evidence |

## Test evidence

### Backend verify

```text
Command: .\mvnw.cmd -f backend/pom.xml -pl platform-core -am clean verify
Result: BUILD SUCCESS
Tests run: 15, Failures: 0, Errors: 0, Skipped: 0
Timestamp: 2026-05-24T19:37:39+02:00
```

### Full parent verify

```text
Command: .\mvnw.cmd clean verify
Result: BUILD SUCCESS
Tests run: 15, Failures: 0, Errors: 0, Skipped: 0
Timestamp: 2026-05-24T19:37:54+02:00
```

### OCI image build + runtime smoke test

```text
Command: .\devops\container\platform-core\build-image.ps1 -ContainerRuntime docker -ImageName df-platform-core:story022
Result: PASS

Command: .\devops\container\platform-core\run-local-stack.ps1 -ContainerRuntime docker -ImageName df-platform-core:story022 -HostPort 18084
Result: PASS

Command: Invoke-WebRequest -UseBasicParsing http://127.0.0.1:18084/platform/status
Result: PASS (200 {"service":"education-system-framework","status":"UP"})
```

## Acceptance criteria evidence

| AC | Evidence |
|---|---|
| 1. OCI-compatible image is produced | Successful image build `df-platform-core:story022`; `Containerfile` |
| 2. Podman users can run with externalized configuration | Podman-first scripts/docs under `devops/container/platform-core/`; no daemon-specific features; Docker used only as local fallback evidence |
| 3. App connects to containerized PostgreSQL via environment config | `run-local-stack.ps1`; live application container used `EDU_DB_*` values and served `/platform/status` while PostgreSQL container was running |
| 4. No secrets/country/cloud specifics embedded | Direct review of `Containerfile`, scripts, and docs; runtime-only configuration injection |
| 5. Image exposes health/readiness behavior | `EXPOSE 8080`; `/platform/status`; image/container healthcheck observed as `healthy` |

## Known risks for QA

- `RISK-027`: Podman was unavailable locally, so the smoke test used Docker as OCI-compatible fallback evidence.
- `RISK-015`: this is a local OCI baseline only; orchestration/IaC work remains for `STORY-023`.
- Shared build-file change in `backend/platform-core/pom.xml` should be regression-checked carefully because it affects packaging behavior.

## QA focus areas

1. Re-run backend and full parent Maven verification
2. Confirm `backend/platform-core/target/platform-core-0.1.0-SNAPSHOT-exec.jar` is produced without breaking tests
3. Inspect `devops/container/platform-core/Containerfile` for OCI compatibility, externalized configuration, and `/platform/status` health behavior
4. Build the image and run the local stack with Podman if available, otherwise document Docker fallback as environment-limited evidence
5. Confirm the running container reports `healthy` and `/platform/status` returns 200
6. Confirm no secrets, cloud-specific values, or country-specific values are embedded in the image assets

## Blockers

None.

