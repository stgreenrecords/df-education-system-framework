# Solution Design - STORY-022

## Summary

Add the first DevOps-owned OCI container baseline for `backend/platform-core` using Podman-compatible build/run workflows, externalized runtime configuration, and a reproducible local application + PostgreSQL container contract that remains cloud-neutral.

## Context

`STORY-010` created the Maven multi-project scaffold, `STORY-011` established PostgreSQL/Flyway with environment-backed datasource configuration, and `STORY-220` confirmed the running backend now depends on live database-backed behavior. `DECISION-004` and the architecture direction both state that containerization is a Phase 1 foundation concern and that `STORY-022` should land before deeper feature work so deployment assumptions do not drift into workstation-specific patterns.

The repository already contains an independent `devops/` project area, a working Spring Boot application in `backend/platform-core`, and a `/platform/status` endpoint that can serve as the initial readiness signal. The smallest viable next step is to package `platform-core` into a portable OCI image and document/run it with a containerized PostgreSQL dependency using only externalized configuration.

## Requirements and acceptance criteria

- Produce an OCI-compatible application image from the Maven/Spring Boot build.
- Support Podman-first local build/run commands without Docker-daemon-specific assumptions.
- Demonstrate application startup against a containerized PostgreSQL instance using environment variables.
- Keep secrets and country/cloud specifics out of the image definition.
- Expose a health/readiness behavior suitable for later orchestration work.

## Proposed solution

Implement a DevOps-only container baseline centered on the existing `backend/platform-core` application.

Recommended implementation shape:

1. Ensure `platform-core` can produce a runnable packaged artifact suitable for container image assembly.
   - If the current build only emits a plain jar, add the minimal build-plugin configuration needed to create an executable Spring Boot artifact.
   - Keep this as a build-file change only; do not change backend source ownership or application behavior unless SA rerouting becomes necessary.

2. Add OCI image assets under the DevOps project area, for example:
   - `devops/container/platform-core/Containerfile`
   - optional `.containerignore`
   - local run scripts and/or Podman-first documentation under `devops/container/platform-core/`

3. Container image expectations:
   - use an OCI-compatible base image with Java 25 runtime support
   - copy only the packaged application artifact and required launcher assets
   - inject configuration exclusively through environment variables
   - expose the application port explicitly
   - avoid embedding secrets, cloud vendor settings, country values, or local workstation paths

4. Local runtime baseline:
   - provide a reproducible Podman-first workflow that starts PostgreSQL and the application on an isolated container network
   - pass datasource settings through environment variables already supported by `application.properties`
   - include the translation-related settings only as optional examples, not hardcoded behavior
   - keep the workflow portable enough that another OCI runtime can validate the same assets if Podman is absent in the local environment

5. Health/readiness baseline:
   - use the existing `/platform/status` route as the initial readiness contract
   - if practical inside the chosen base image, add a container `HEALTHCHECK` or equivalent documented readiness probe targeting `/platform/status`
   - document the exact HTTP path/port so later `STORY-023` Kubernetes work can reuse it unchanged

6. Documentation/evidence:
   - add DevOps instructions for build, run, stop, and smoke validation
   - include rollback notes that remove only the container assets/build wiring if defects are found
   - record any environment limitation clearly if Podman is unavailable during validation

## Alternatives considered

- Docker-only image build workflow: rejected because the accepted architecture direction explicitly requires Podman-compatible, OCI-first workflows without Docker-daemon-specific assumptions.
- Jump directly to Kubernetes manifests and IaC: rejected because `STORY-023` owns that broader deployment baseline; `STORY-022` should remain the smallest local OCI/runtime contract.
- Containerize every backend module separately now: rejected because only `platform-core` currently provides the running Spring Boot entry point needed for the baseline.
- Delay containerization until more business features exist: rejected by `DECISION-004` because it would create avoidable rework in configuration, health checks, and deployment packaging.

## Files/components likely affected

- `devops/pom.xml`
- `devops/**` new container/runtime documentation or scripts
- root `README.md` and/or DevOps-specific README documentation if needed for container validation paths
- build files needed to produce a runnable `platform-core` artifact, likely under `backend/platform-core/pom.xml` or related Maven configuration

## Data model changes

- None expected.

## API/contract changes

- No new business API endpoints.
- Operational contract: document `/platform/status` as the initial container readiness endpoint and the environment variables required for datasource startup.

## UI/UX impact

- None.

## Security and privacy considerations

- Do not commit secrets, passwords, tokens, or country-specific values.
- Use environment variables or example placeholders only.
- Avoid embedding database credentials in image layers or Markdown evidence.
- Keep the image cloud-neutral and country-neutral; deployment-specific values belong in runtime configuration only.

## Performance/scalability considerations

- The image should be small enough for local and sovereign deployment use, but full optimization is secondary to correctness in this story.
- The runtime contract should allow later scaling/orchestration work to add replicas, liveness/readiness probes, and provider-specific resource tuning without changing application code.

## Test strategy

DevOps should provide and run the strongest validation available in the current environment:

- Maven build that produces the runnable application artifact needed by the container image
- OCI image build using Podman if available; otherwise use an available OCI-compatible runtime only as fallback evidence while keeping the artifacts Podman-neutral
- Local container smoke test with PostgreSQL + application containers on an isolated network
- HTTP readiness check against `/platform/status`
- Verification that environment-driven datasource settings are used and no secrets/country/cloud specifics are hardcoded in the container assets
- Full parent or relevant project build rerun if build-file changes affect shared Maven behavior

## Deployment/migration plan

- Add container assets and any necessary build wiring in a reversible way.
- Keep runtime configuration externalized so no data migration is required.
- Document the exact local container startup/shutdown workflow for future QA and PO sessions.

## Rollback plan

- Revert the DevOps container assets and associated build-file changes.
- Remove any newly added local container scripts/docs if the baseline proves unsuitable.
- Fall back to the existing non-containerized local runtime while redesigning the container contract.

## Risks and mitigations

- Risk: Podman may be unavailable in the local environment. Mitigation: keep artifacts Podman-compatible and use alternative OCI runtime evidence only as an environment fallback.
- Risk: Existing build output may not be directly runnable in a container. Mitigation: allow the smallest necessary build-file adjustment to create an executable application artifact.
- Risk: Later orchestration may require stronger probe behavior than the current status endpoint. Mitigation: document `/platform/status` as the initial contract and evolve probe depth in `STORY-023` or later operations stories.

## Open questions

- None blocking DevOps implementation. If the current Java 25 runtime/base-image combination proves unavailable or unstable in the local environment, DevOps should document the exact limitation and either use the closest OCI-compatible alternative or return to SA for rerouting.

## Implementation lane

- Lane: `devops`
- Subdashboard: `df/runtime/devops-board.md`
- Artifact folder for implementation notes: `df/artifacts/STORY-022/devops/`

## SA decision

Approved for development: Yes

