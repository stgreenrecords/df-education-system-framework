# Handoff - STORY-022

## SA -> devops

- Timestamp: 2026-05-24 19:29 local
- Task: STORY-022
- From state: ARCHITECTURE_IN_PROGRESS
- To state: READY_FOR_DEV
- Lane: `devops`
- Subdashboard: `df/runtime/devops-board.md`
- Summary: Promoted the next Phase 1 foundation story from backlog and routed it to `devops`. The design keeps the work limited to a Podman-compatible OCI image and a local application + PostgreSQL container baseline for `backend/platform-core`, with cloud/provider specifics deferred to `STORY-023`.

## Evidence

- `df/artifacts/STORY-022/task.md`
- `df/artifacts/STORY-022/solution-design.md`
- `df/artifacts/TASK-003/decision-004-containerization-phase-1.md`
- `df/artifacts/TASK-003/containerization-stage-evaluation.md`
- `df/backlog/user-stories.md`
- `df/backlog/architecture-direction.md`
- `df/runtime/board.md`
- `df/runtime/devops-board.md`
- `pom.xml`
- `devops/pom.xml`

## Tests/checks

| Check | Command/source | Result | Notes |
|---|---|---|---|
| Backlog priority review | `df/runtime/board.md`; `df/backlog/user-stories.md`; `df/backlog/roadmap.md` | PASS | `STORY-022` is the strongest documented next foundation task after `STORY-220` acceptance because containerization is explicitly prioritized before deeper feature work |
| Containerization direction review | `df/artifacts/TASK-003/decision-004-containerization-phase-1.md`; `df/artifacts/TASK-003/containerization-stage-evaluation.md`; `df/backlog/architecture-direction.md` | PASS | Existing decisions already establish Podman-compatible OCI images as the Phase 1 direction |
| Repository fit review | `pom.xml`; `devops/pom.xml`; `README.md` | PASS | Repository already contains an independent `devops/` project area and a runnable Spring Boot application path suitable for an initial OCI baseline |

## Known risks

- `RISK-015`: containerization must stay early to avoid deployment rework.
- `RISK-016`: local Maven/trust-store issues can still affect build validation on some machines.
- `RISK-017`: DevOps project area is still a scaffold and will need concrete container assets in this story.
- `RISK-019`: any shared build-file edits must remain minimal and documented.

## Next role instructions

- Read `df/artifacts/STORY-022/task.md`, `df/artifacts/STORY-022/solution-design.md`, `df/runtime/board.md`, and `df/runtime/devops-board.md` before editing.
- Keep the work in DevOps-owned files plus minimal build-file changes needed to produce a runnable container image input artifact.
- Implement Podman-first OCI image assets and a local PostgreSQL + application container baseline without embedding secrets or Docker-daemon-specific assumptions.
- Record exact validation commands/results in `df/artifacts/STORY-022/devops/dev-notes.md` and hand off to QA only after container build/runtime evidence is captured or environment limits are explicitly documented.

## Blockers

- None.

## qa -> po

- Timestamp: 2026-05-24 19:48 local
- Task: STORY-022
- From state: QA_IN_PROGRESS
- To state: READY_FOR_PO
- Lane: `devops`
- Summary: QA independently verified the OCI baseline and passed the story for product-owner review. The build still produces both the regular and executable classified jars, the OCI image builds cleanly, the local PostgreSQL-backed application stack serves `/platform/status`, and the container reports healthy readiness behavior.

## Evidence

- `df/artifacts/STORY-022/qa-report.md`
- `df/artifacts/STORY-022/task.md`
- `df/artifacts/STORY-022/devops/dev-notes.md`
- `df/artifacts/STORY-022/devops/handoff-to-qa.md`
- `backend/platform-core/pom.xml`
- `devops/container/platform-core/Containerfile`
- `devops/container/platform-core/build-image.ps1`
- `devops/container/platform-core/run-local-stack.ps1`
- `devops/container/platform-core/stop-local-stack.ps1`
- `devops/container/platform-core/README.md`

## Tests/checks

| Check | Command/source | Result | Notes |
|---|---|---|---|
| File diagnostics | IDE diagnostics on changed files | PASS | No file-level errors reported |
| Backend reactor verify | `./mvnw.cmd -f backend/pom.xml -pl platform-core -am clean verify` | PASS | 15 integration tests passed; both `platform-core-0.1.0-SNAPSHOT.jar` and `platform-core-0.1.0-SNAPSHOT-exec.jar` were present |
| Full parent verify | `./mvnw.cmd clean verify` | PASS | 12-project parent build succeeded after the packaging change |
| OCI runtime availability review | `Get-Command podman`; `Get-Command docker` | PASS (environment-limited) | `podman` missing locally; Docker available as the task-approved OCI fallback |
| OCI image build | `./devops/container/platform-core/build-image.ps1 -ContainerRuntime docker -ImageName df-platform-core:qa022` | PASS | Image built successfully from `eclipse-temurin:25-jre-alpine` |
| Local stack smoke test | `./devops/container/platform-core/run-local-stack.ps1 -ContainerRuntime docker -ImageName df-platform-core:qa022 -HostPort 18084`; `curl.exe -s http://127.0.0.1:18084/platform/status`; `docker ps`; `docker inspect --format "{{json .State.Health}}" df-platform-core-app`; `docker image inspect ...` | PASS | `/platform/status` returned the expected JSON, the app container reported `healthy`, and the image exposes `8080/tcp` with a healthcheck targeting `/platform/status` |
| Scope/security inspection | Direct review plus targeted searches | PASS | No secrets, country-specific values, or cloud-provider-specific behavior embedded in the image assets |
| Cleanup verification | `./devops/container/platform-core/stop-local-stack.ps1 -ContainerRuntime docker`; follow-up `docker ps`; `docker network ls` | PASS | Temporary local stack resources were removed |

## Known risks

- `RISK-027`: Podman is still unverified on this specific QA machine because the runtime is not installed locally.
- `RISK-015`: the story remains a local OCI baseline only; broader orchestration/IaC work stays in `STORY-023`.

## Next role instructions

- PO should review `df/artifacts/STORY-022/qa-report.md` first.
- Reconfirm that Docker fallback evidence is acceptable under the documented task assumption because Podman is unavailable locally.
- Validate the user-facing product outcome at the story level: portable OCI baseline, externalized DB configuration, PostgreSQL-backed local runtime, and readiness/health contract suitable for later orchestration.

## Blockers

- None.

## po -> factory/sa

- Timestamp: 2026-05-24 19:52 local
- Task: STORY-022
- From state: READY_FOR_PO
- To state: DONE
- Lane: `devops`
- Summary: PO accepted the first Podman-compatible OCI baseline after reviewing QA evidence and independently validating the local PostgreSQL-backed container workflow. The accepted output now provides a reusable OCI image contract, externalized runtime configuration, and `/platform/status` readiness behavior for later deployment work.

## Evidence

- `df/artifacts/STORY-022/po-review.md`
- `df/artifacts/STORY-022/qa-report.md`
- `df/artifacts/STORY-022/devops/dev-notes.md`
- `df/artifacts/STORY-022/task.md`
- `backend/platform-core/pom.xml`
- `devops/container/platform-core/Containerfile`
- `devops/container/platform-core/build-image.ps1`
- `devops/container/platform-core/run-local-stack.ps1`
- `devops/container/platform-core/stop-local-stack.ps1`
- `devops/container/platform-core/README.md`

## Tests/checks

| Check | Command/source | Result | Notes |
|---|---|---|---|
| QA pass review | `df/artifacts/STORY-022/qa-report.md` | PASS | QA covered all acceptance criteria and documented the Podman availability limitation clearly |
| OCI runtime availability review | `Get-Command podman`; `Get-Command docker` | PASS (environment-limited) | `podman` is missing on this PO machine; Docker is available as the documented OCI fallback |
| Live local stack validation | `./devops/container/platform-core/run-local-stack.ps1 -ContainerRuntime docker -ImageName df-platform-core:qa022 -HostPort 18086`; `Invoke-WebRequest http://127.0.0.1:18086/platform/status`; `docker ps`; `docker inspect --format "{{json .State.Health}}" df-platform-core-app` | PASS | Application container became `healthy`, status endpoint returned `UP`, and the local stack behaved as expected |
| Externalized configuration confirmation | Redacted `docker exec df-platform-core-app printenv` review | PASS | `EDU_DB_URL`, `EDU_DB_USERNAME`, and `EDU_DB_PASSWORD` were present at runtime without exposing secret values in documentation |
| Cleanup verification | `./devops/container/platform-core/stop-local-stack.ps1 -ContainerRuntime docker`; follow-up `docker ps`; `docker network ls` | PASS | Temporary stack resources were removed after PO validation |

## Known risks

- `RISK-027`: Podman remains unavailable on this workstation, so PO acceptance relies on the task-approved Docker OCI fallback.
- `RISK-015`: orchestration, Kubernetes, registry, and IaC scope remain future work in `STORY-023`.

## Next role instructions

- Factory/SA should select the next highest-priority actionable task.
- `STORY-023` remains the documented Phase 1 follow-up candidate because `STORY-022` is now accepted.

## Blockers

- None.

## devops -> qa

- Timestamp: 2026-05-24 19:40 local
- Task: STORY-022
- From state: DEV_IN_PROGRESS
- To state: READY_FOR_QA
- Lane: `devops`
- Summary: DevOps completed the first OCI baseline for `backend/platform-core`: the build now produces a classified executable Spring Boot jar, the `devops/container/platform-core/` folder contains Podman-compatible image/runtime helpers, and local smoke validation proved the image against a containerized PostgreSQL dependency with `/platform/status` health behavior.

## Evidence

- `df/artifacts/STORY-022/devops/dev-notes.md`
- `df/artifacts/STORY-022/devops/handoff-to-qa.md`
- `df/artifacts/STORY-022/task.md`
- `backend/platform-core/pom.xml`
- `devops/container/platform-core/Containerfile`
- `devops/container/platform-core/build-image.ps1`
- `devops/container/platform-core/run-local-stack.ps1`
- `devops/container/platform-core/stop-local-stack.ps1`
- `devops/container/platform-core/README.md`

## Tests/checks

| Check | Command/source | Result | Notes |
|---|---|---|---|
| Backend reactor verify | `./mvnw.cmd -f backend/pom.xml -pl platform-core -am clean verify` | PASS | 15 integration tests passed after the packaging change; executable jar attached with classifier `exec` |
| Full parent verify | `./mvnw.cmd clean verify` | PASS | 12-project parent build succeeded after the shared build-file change |
| OCI image build | `./devops/container/platform-core/build-image.ps1 -ContainerRuntime docker -ImageName df-platform-core:story022` | PASS | Docker used as local OCI fallback because Podman was unavailable |
| Local container smoke test | `./devops/container/platform-core/run-local-stack.ps1 -ContainerRuntime docker -ImageName df-platform-core:story022 -HostPort 18084`; `Invoke-WebRequest http://127.0.0.1:18084/platform/status`; `docker image inspect ...`; `docker ps ...` | PASS | App served `/platform/status` with HTTP 200 and container health reported `healthy` |
| Cleanup verification | `./devops/container/platform-core/stop-local-stack.ps1 -ContainerRuntime docker`; `docker ps --filter "name=df-platform-core" ...` | PASS | Temporary containers and network were removed |

## Known risks

- `RISK-015`: this is still the local OCI baseline only; broader orchestration/IaC work remains for `STORY-023`.
- `RISK-027`: Podman was unavailable locally, so runtime evidence used Docker as the documented OCI fallback.
- Non-blocking Springdoc, Mockito, and Java native-access warnings remained during Maven validation.

## Next role instructions

- QA should re-run backend and full parent builds.
- QA should confirm `backend/platform-core/target/platform-core-0.1.0-SNAPSHOT-exec.jar` is produced while the regular jar remains test-compatible.
- QA should inspect the container assets for OCI compatibility, externalized configuration, and absence of cloud/country-specific values.
- QA should build/run the image with Podman if available; otherwise document Docker fallback evidence as an environment limitation.

## Blockers

- None.

