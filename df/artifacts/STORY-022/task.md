# Task - STORY-022

## Summary

Implement the first Podman-compatible OCI container baseline for the Spring Boot application and its local PostgreSQL runtime contract.

## Type

Story

## Priority

P0

## Current state

DONE

## Business goal

Establish a portable, sovereign-friendly container packaging baseline early in Phase 1 so application delivery, local validation, and later Kubernetes/IaC work can build on one OCI-compatible runtime contract instead of workstation-specific assumptions.

## Acceptance criteria

- [ ] Given the Maven application build, when the container image is built, then an OCI-compatible application image is produced
- [ ] Given a developer or country operator uses Podman, when they run the application image with externalized configuration, then the application starts successfully
- [ ] Given PostgreSQL is required, when running the local container baseline, then the application connects to a containerized PostgreSQL instance using environment-provided configuration
- [ ] Given the container definition is reviewed, then no secrets, country-specific code, or cloud-specific code are embedded in the image
- [ ] Given the image is inspected, then it exposes health/readiness behavior suitable for later orchestration

## Out of scope

- Kubernetes manifests, Helm/Kustomize overlays, or infrastructure-as-code modules for cloud providers; these belong to `STORY-023`
- CI/CD pipeline automation beyond the minimal local/container validation needed for this story
- Production secrets-vault integration or cloud registry provisioning
- Multi-service production deployment topology beyond the local application + PostgreSQL baseline

## Assumptions

- The first OCI baseline may focus on the existing `backend/platform-core` Spring Boot application rather than every backend module independently.
- Existing environment-backed datasource configuration from `STORY-011` is sufficient for the first container runtime contract.
- Podman-first commands and documentation are required, but OCI-compatible assets may still be validated with another available OCI runtime when Podman is unavailable in the local environment, as long as no Docker-daemon-specific behavior is introduced into the artifacts.
- Existing `/platform/status` behavior may be used as the initial health/readiness signal until broader actuator or orchestration-specific health work is introduced.

## Dependencies

- STORY-010 - Initialize Spring Boot project with modular structure.
- STORY-011 - Implement PostgreSQL database configuration and migration framework.
- DECISION-004 - Containerization is a Phase 1 foundation concern using Podman-compatible OCI images.
- Existing architecture direction: OCI images, Podman-compatible workflows, cloud-neutral application code.

## Risks

- Some developer/QA environments may not have Podman installed even though the artifact contract must remain Podman-compatible.
- Container packaging may require build-file changes so `platform-core` can produce a runnable image input artifact.
- Health/readiness behavior is currently minimal and may need tightening in `STORY-023` or later security/operations stories.

## Links

- Issue: n/a
- PR: n/a
- Design: `df/artifacts/STORY-022/solution-design.md`

## Implementation lane

- Lane: `devops`
- Subdashboard: `df/runtime/devops-board.md`
- Artifact folder for implementation notes: `df/artifacts/STORY-022/devops/`

## Role history

| Timestamp | Role | State | Summary |
|---|---|---|---|
| 2026-05-24 19:29 local | sa | OPEN -> NEEDS_ARCHITECTURE | Promoted the next Phase 1 foundation story from the documented backlog after `STORY-220` acceptance left no active runtime task; selected `STORY-022` because `DECISION-004` and the runtime board explicitly prioritize the Podman-compatible OCI baseline before deeper feature work. |
| 2026-05-24 19:29 local | sa | NEEDS_ARCHITECTURE -> ARCHITECTURE_IN_PROGRESS -> READY_FOR_DEV | Produced the DevOps-oriented solution design, routed the work to `devops`, and documented a minimal OCI/container-runtime baseline that stays cloud-neutral and Podman-compatible without requiring frontend or backend source ownership changes. |
| 2026-05-24 19:35 local | devops | READY_FOR_DEV -> DEV_IN_PROGRESS | DevOps implementation started after reviewing the SA guidance, runtime boards, current build state, repository status, and local OCI runtime availability. |
| 2026-05-24 19:40 local | devops | DEV_IN_PROGRESS -> READY_FOR_QA | Implemented the OCI image baseline, helper scripts, executable-jar packaging, and local container smoke validation; reran backend/full Maven verification, built the image successfully, validated `/platform/status` plus container health, and documented Docker fallback evidence because Podman was unavailable locally. |
| 2026-05-24 19:48 local | qa | READY_FOR_QA -> QA_IN_PROGRESS -> READY_FOR_PO | Independently reran backend and full-parent Maven verification, confirmed both the normal and classified executable jars are produced, rebuilt the OCI image, revalidated the PostgreSQL-backed local container smoke path plus `/platform/status` and healthy container state, inspected the changed assets for secrets/cloud/country-specific scope violations, and passed the story for PO review while documenting the local Podman availability limitation. |
| 2026-05-24 19:52 local | po | READY_FOR_PO -> PO_REVIEW -> DONE | Reviewed the QA evidence and performed an independent product-style validation of the shipped OCI baseline by starting the local PostgreSQL-backed container stack, confirming healthy readiness on `/platform/status`, verifying that database configuration stays externalized at runtime, accepting the documented Docker fallback under the task assumptions because Podman is unavailable locally, and approving the story as the accepted Phase 1 OCI baseline. |

