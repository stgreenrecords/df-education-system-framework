# Task - TASK-003

## Summary

Evaluate when containerization should enter the roadmap and whether Podman, OCI images, Kubernetes portability, and infrastructure as code should be included early.

## Type

Task

## Priority

P0

## Current state

READY_FOR_DEV

## Business goal

Avoid late deployment rework by deciding the containerization and cloud-portability strategy before feature modules depend on local-only runtime assumptions.

## Acceptance criteria

- [x] Recommend the SDLC stage where containerization should be introduced.
- [x] Evaluate the rework risk of delaying containerization.
- [x] Evaluate open-source Podman usage.
- [x] Evaluate whether the same application code can run on AWS, Azure, and Google Cloud.
- [x] Include infrastructure-as-code support in the deployment direction.
- [x] Update backlog/runtime artifacts with the recommendation.

## Out of scope

- Implementing container images or infrastructure modules.
- Choosing one production cloud provider.
- Creating production Kubernetes clusters.
- Deploying to any external infrastructure.

## Assumptions

- The application remains a Java 21 Spring Boot modular monolith for MVP.
- Maven is the selected build tool for `STORY-010`.
- Country/ministry deployments must remain sovereign and provider-selectable.

## Dependencies

- `STORY-010` for the application build.
- `STORY-011` for database configuration.
- `STORY-020` for sovereign deployment architecture.

## Risks

- Deferring containerization past core feature work would likely require rework in runtime config, secrets, health checks, filesystem handling, startup order, and deployment documentation.

## Links

- Issue: n/a
- PR: n/a
- Design: `df/artifacts/TASK-003/containerization-stage-evaluation.md`

## Role history

| Timestamp | Role | State | Summary |
|---|---|---|---|
| 2026-05-23 10:43 local | sa | ARCHITECTURE_REVIEW | Evaluated containerization timing and cloud portability; updated backlog and architecture guidance. |
