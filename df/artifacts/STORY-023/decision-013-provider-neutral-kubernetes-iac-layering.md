# Decision Record - DECISION-013

- Date: 2026-05-24
- Status: Accepted
- Owner role: SA
- Related task: STORY-023

## Context

The sovereign deployment model from `STORY-020` is accepted, and `STORY-022` already provides a reusable OCI application image plus the initial runtime/health contract. The next Phase 1 step must introduce Kubernetes and infrastructure-as-code assets without letting one cloud provider, one secret store, or one registry model leak into the framework application layer.

## Decision

Adopt a three-layer deployment baseline for `STORY-023`:

1. a provider-neutral Kubernetes application base for shared workload behavior;
2. provider-specific Kubernetes overlays/examples for AWS, Azure, Google Cloud, and self-hosted/on-prem deployment concerns;
3. OpenTofu-compatible provider modules that keep infrastructure wiring separate from application source code.

The governing rules are:

- the same application image and application code must remain reusable across providers
- provider-specific differences belong only in overlays, IaC modules, variables, and operator-owned infrastructure configuration
- self-hosted/on-prem is a first-class provider target alongside AWS, Azure, and Google Cloud
- OpenTofu compatibility is the baseline open-source path, but the HCL layout should remain consumable by Terraform where country operators require it
- secrets, live credentials, and country-specific values must not be committed into manifests or IaC defaults

## Consequences

- `devops` owns implementation of the Kubernetes/IaC baseline as a single delivery-lane task.
- QA will need to verify both the architectural separation and the strongest available render/validation evidence.
- Later deployment/security/release stories can extend provider paths without changing application code.
- If a future provider requires a new overlay/module, it should be added as deployment configuration, not as a framework code fork.

## Alternatives considered

- One provider first, others later: rejected because it would weaken sovereign operator choice and encourage provider leakage into the shared layer.
- Kubernetes manifests only, no IaC baseline: rejected because the backlog and Phase 1 direction explicitly require infrastructure-as-code planning as part of the portability contract.
- IaC only, no repository-visible Kubernetes base: rejected because QA and downstream operators need a clear application deployment layer independent of provider infrastructure wiring.

## Evidence

- `df/artifacts/STORY-023/solution-design.md`
- `df/artifacts/STORY-023/cloud-portable-deployment-baseline.md`
- `df/artifacts/STORY-020/decision-012-country-sovereign-deployment-architecture.md`
- `df/artifacts/STORY-022/solution-design.md`
- `df/artifacts/TASK-003/decision-004-containerization-phase-1.md`
- `df/backlog/architecture-direction.md`
- `df/backlog/roadmap.md`
- `df/backlog/user-stories.md`

## Follow-up actions

- `devops` implements the baseline Kubernetes and IaC asset structure using this layering rule.
- QA verifies that the provider-neutral base remains free of provider-specific assumptions.
- PO later validates that the resulting deployment baseline satisfies the cloud-portable, sovereign deployment intent.

