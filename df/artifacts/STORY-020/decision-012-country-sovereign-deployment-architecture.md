# Decision Record - DECISION-012

- Date: 2026-05-24
- Status: Accepted
- Owner role: SA
- Related task: STORY-020

## Context

The framework must support sovereign country/ministry deployments across AWS, Azure, Google Cloud, private cloud, and on-premises environments. Phase 1 now has an accepted PostgreSQL baseline and an accepted OCI container baseline, but later deployment work would be unsafe if the repository did not first define who owns environments, data, backups, access, and release execution.

`STORY-023` depends on this decision because Kubernetes and IaC assets must preserve country ownership and provider-neutral application packaging.

## Decision

Adopt a country-sovereign deployment architecture.

The governing rules are:

- each country/ministry operates its own isolated deployment estate
- each country owns its own `dev`, `qa`, `stage`, and `prod` environments
- each country owns its own data, backups, secrets, operator access, and observability stack
- the framework vendor delivers portable release artifacts, migration guidance, compatibility guidance, and documentation rather than a mandatory central runtime
- no cross-country production data plane is allowed
- application code and OCI artifacts remain provider-neutral
- provider-specific differences belong in deployment overlays and IaC modules, not in application source code

## Consequences

- `STORY-023` must build Kubernetes/IaC assets around provider-neutral application packaging plus provider-specific deployment modules/overlays.
- Later tenant, security, audit, and release-management stories must preserve country-owned operations and data isolation.
- A centralized multi-country production SaaS operating model is not the default architecture.
- Countries may choose different infrastructure providers without requiring framework code forks.

## Alternatives considered

- Centralized vendor-operated multi-country runtime: rejected because it conflicts with sovereignty, data-residency, and country-operated deployment goals.
- Standardize on one cloud provider first: rejected because it undermines portability and sovereign country choice.
- Skip the sovereign operating model and go straight to Kubernetes/IaC implementation: rejected because later deployment assets could encode the wrong ownership boundary.

## Evidence

- `df/artifacts/STORY-020/country-sovereign-deployment-architecture.md`
- `df/artifacts/TASK-003/decision-004-containerization-phase-1.md`
- `df/artifacts/TASK-003/containerization-stage-evaluation.md`
- `df/backlog/architecture-direction.md`
- `df/backlog/roadmap.md`
- `df/backlog/user-stories.md`

## Follow-up actions

- QA reviews `STORY-020` as a documentation-only architecture deliverable.
- PO validates that the sovereign operating model is acceptable for product direction.
- After acceptance, SA/devops can continue with `STORY-023` using this decision as the governing deployment boundary.

