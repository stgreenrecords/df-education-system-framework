# Containerization Stage Evaluation - TASK-003

## Recommendation

Include containerization in Phase 1, immediately after the application and database foundation are available:

1. `STORY-010`: keep the Maven/Spring Boot scaffold container-ready, but do not require full production container deployment yet.
2. `STORY-011`: add PostgreSQL/Flyway database foundation.
3. New `STORY-022`: create the first Podman-compatible OCI image and local container runtime baseline.
4. New `STORY-023`: define cloud-portable Kubernetes and infrastructure-as-code baseline for AWS, Azure, Google Cloud, and self-hosted/on-prem targets.

This should happen before domain feature modules such as users, school packs, i18n, attendance, gradebook, and catering become deep dependencies.

## Why Not Wait

Waiting until later creates avoidable rework because feature code and operations would likely grow around local-only assumptions:

- hard-coded hostnames, ports, profiles, or filesystem paths;
- ad hoc secret loading rather than environment/secret-store injection;
- missing startup/readiness/liveness behavior;
- missing externalized configuration for database, cache, object storage, and identity provider endpoints;
- tests that depend on a developer workstation instead of reproducible container services;
- deployment docs that must be rewritten for each country or cloud.

The lowest-cost path is not to build full production infrastructure immediately, but to set container contracts early.

## Podman Evaluation

Podman is a good fit for the project direction because it is open source, supports OCI-compatible containers/images, and can run rootless in developer or sovereign Linux environments. It can be used as the preferred local and self-hosted build/run tool without making the application dependent on Podman-specific runtime APIs.

Architecture rule:

- Build OCI images.
- Test locally with Podman.
- Avoid Docker-daemon-specific assumptions.
- Keep image/runtime definitions portable to Kubernetes.

Podman is not the production orchestration strategy by itself. It is best used for local development, CI validation where available, and smaller sovereign/self-hosted deployments. Production scale should target Kubernetes-compatible manifests.

## Cloud Portability Evaluation

The same application code can run on AWS, Azure, Google Cloud, private cloud, or on-premises when the release package is defined around:

- OCI images;
- externalized configuration;
- stateless application containers;
- generic PostgreSQL connection contracts;
- Kubernetes manifests/Helm/Kustomize overlays;
- provider-specific infrastructure modules kept outside application code.

The same infrastructure code cannot be literally identical across every cloud because networking, managed databases, registries, identity, secret stores, and load balancers are provider-specific. The correct target is same application image and mostly shared Kubernetes deployment layer, with separate IaC provider modules.

## Infrastructure as Code Direction

Use IaC in Phase 1 as a deployment baseline, not as a production commitment to one cloud.

Recommended structure:

- `deploy/container/`: OCI image build/runtime documentation.
- `deploy/kubernetes/base/`: provider-neutral Kubernetes manifests or Kustomize base.
- `deploy/kubernetes/overlays/{local,aws,azure,gcp,on-prem}/`: environment/provider overlays.
- `deploy/iac/modules/`: reusable infrastructure abstractions.
- `deploy/iac/envs/{aws,azure,gcp,on-prem}/`: provider-specific compositions.

Prefer OpenTofu-compatible modules where possible because it is open source and Terraform-compatible, while keeping room for Terraform if a country/ministry standard requires it.

## Rework-Minimizing Requirements for Early Stories

`STORY-010` should remain Maven-focused but container-ready:

- executable Spring Boot artifact;
- no local-only paths;
- profiles/config via environment variables;
- basic actuator health/readiness endpoints when Spring Actuator is introduced;
- no secrets in source.

`STORY-011` should be container-aware:

- database host/port/name/user/password externalized;
- migrations run predictably in container startup or controlled release flow;
- local dev can run PostgreSQL as a container.

## Decision

Containerization is a Phase 1 foundation concern. Full production hardening can evolve later, but the first OCI image and cloud-portable deployment/IaC skeleton should be completed before major domain feature development.

## Evidence

- Existing architecture direction already lists OCI-style containerization and sovereign deployment.
- Podman official documentation identifies Podman as a container management tool suitable for building/running containers and related Kubernetes-style workflows.
- AWS EKS, Azure AKS, and Google GKE all provide managed Kubernetes paths for running containerized workloads.
- OpenTofu is an open-source IaC option under Linux Foundation stewardship; Terraform provider ecosystems also support the major clouds.
