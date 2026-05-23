# Decision Record - DECISION-004

- Date: 2026-05-23
- Status: Accepted
- Owner role: SA
- Related task: TASK-003

## Context

The platform must support sovereign country/ministry deployments and should be portable across self-hosted infrastructure and major clouds. The backlog already contains foundation tasks for Spring Boot and PostgreSQL, plus a deployment architecture story, but containerization timing was not explicit.

## Decision

Treat containerization as a Phase 1 foundation concern.

The first implementation stage should:

- keep `STORY-010` container-ready without forcing production deployment;
- add PostgreSQL/database foundation in `STORY-011`;
- add a new `STORY-022` for Podman-compatible OCI image and local container runtime baseline;
- add a new `STORY-023` for Kubernetes-compatible deployment manifests and IaC baseline across AWS, Azure, Google Cloud, and self-hosted/on-prem targets.

Use Podman as the preferred open-source local/self-hosted container tool, OCI images as the release artifact, Kubernetes-compatible manifests for scalable deployments, and OpenTofu-compatible IaC modules where possible.

## Consequences

- Reduces late rework in configuration, secrets, health checks, networking, filesystem assumptions, database startup, and deployment documentation.
- Keeps application code cloud-neutral.
- Allows each country/ministry to choose AWS, Azure, Google Cloud, private cloud, or on-prem infrastructure.
- Requires provider-specific IaC modules because cloud networking, IAM, secret stores, registries, and managed databases are not identical.

## Alternatives Considered

- Defer all containerization until after MVP feature work: rejected because it creates high deployment rework risk.
- Choose one cloud provider early: rejected because it conflicts with sovereign country-operated deployment goals.
- Use Podman-only production deployment: rejected as the default scaling path; Kubernetes-compatible deployment remains the scalable target, while Podman remains useful for local/self-hosted operation.

## Evidence

- `df/artifacts/TASK-003/containerization-stage-evaluation.md`
- `df/backlog/architecture-direction.md`
- `df/backlog/user-stories.md`
- Podman documentation: https://podman.io/docs/documentation
- AWS EKS documentation: https://aws.amazon.com/documentation-overview/eks/
- Azure AKS documentation: https://azure.microsoft.com/services/container-service/
- Google GKE documentation: https://docs.cloud.google.com/kubernetes-engine/docs/learn/containers
- OpenTofu documentation: https://opentofu.org/

## Follow-up Actions

- Dev implements `STORY-010` as a Maven scaffold that remains container-ready.
- SA/Dev promote `STORY-011`, then `STORY-022`, then `STORY-023` before major domain feature implementation.
