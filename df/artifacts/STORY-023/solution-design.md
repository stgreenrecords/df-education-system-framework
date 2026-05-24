# Solution Design - STORY-023

## Summary

Create a DevOps-owned cloud-portable deployment baseline for `backend/platform-core` that keeps the application image and source code provider-neutral while introducing Kubernetes-compatible deployment assets and OpenTofu-compatible infrastructure-as-code structure for AWS, Azure, Google Cloud, and self-hosted/on-prem targets.

## Context

`STORY-020` established the governing sovereign deployment operating model: each country owns its environments, data, backups, secrets, observability, and deployment execution. `STORY-022` then established the first Podman-compatible OCI baseline and documented the initial runtime contract around environment-driven configuration and the `/platform/status` readiness endpoint.

The remaining Phase 1 gap is the deployment baseline that turns the OCI image into a reusable orchestration and infrastructure contract. This story must preserve the country-sovereign boundary, keep application code cloud-neutral, and create a repeatable layout that later provider-specific implementation can extend without forking the framework.

## Requirements and acceptance criteria

- Keep application code unchanged across AWS, Azure, Google Cloud, and self-hosted/on-prem deployment targets
- Separate provider-neutral Kubernetes application deployment from provider-specific infrastructure concerns
- Provide or plan provider-specific infrastructure-as-code modules for AWS, Azure, Google Cloud, and self-hosted/on-prem targets
- Use an OpenTofu-compatible IaC approach that can also be consumed by Terraform-minded operators if needed
- Make registries, secret stores, networking, databases, and observability configurable per provider without changing application source code

## Proposed solution

Implement a DevOps-only deployment baseline in two layers: a provider-neutral application deployment layer and provider-specific infrastructure overlays/modules.

1. Provider-neutral Kubernetes application layer
   - Create Kubernetes-compatible base manifests/templates for the existing `platform-core` application under a dedicated DevOps path such as `devops/kubernetes/platform-core/base/`.
   - The base should express only application concerns shared across all providers:
     - image reference/tag placeholders
     - container port and service exposure
     - readiness/liveness probes using `GET /platform/status`
     - environment variable and secret/config references
     - resource requests/limits as overridable defaults
     - labels/annotations required for generic operations
   - The base must not hardcode one provider's ingress, load balancer, secret store, registry, IAM, storage class, or managed database assumptions.

2. Provider-specific Kubernetes overlays/examples
   - Add overlays or equivalent provider-specific customization entry points, for example under `devops/kubernetes/platform-core/overlays/aws/`, `azure/`, `gcp/`, and `self-hosted/`.
   - Keep overlays focused on deployment-environment concerns such as ingress class, service annotations, storage classes, DNS/certificate integrations, and observability wiring.
   - Self-hosted/on-prem should be treated as a first-class target rather than a leftover fallback.
   - If a provider path cannot be fully implemented in one pass, create documented baseline placeholders with explicit variables, assumptions, and TODO boundaries rather than mixing provider logic into the base.

3. OpenTofu-compatible IaC layer
   - Introduce a provider-neutral module contract for deploying the application baseline, plus provider-specific entry modules under a structure such as:
     - `devops/iac/modules/platform-core-kubernetes-baseline/`
     - `devops/iac/providers/aws/`
     - `devops/iac/providers/azure/`
     - `devops/iac/providers/gcp/`
     - `devops/iac/providers/self-hosted/`
   - Use HCL syntax that stays compatible with OpenTofu and remains consumable by Terraform where a country operator standard requires it.
   - The provider modules should own infrastructure wiring only: cluster/namespace references, registry coordinates, secrets integration, networking, managed/external database endpoints, and observability hooks.
   - Avoid embedding environment secrets, live credentials, or country-specific values.

4. Configuration boundary and sovereignty model
   - The application continues to receive runtime settings through environment variables and Kubernetes secret/config references only.
   - Registries, secret stores, networking, databases, and observability endpoints must be configurable per provider and per country deployment.
   - The same application image should remain reusable across countries and providers; only deployment configuration, overlays, and IaC variables change.
   - Country ownership from `STORY-020` remains mandatory: the vendor ships portable artifacts and deployment guidance, while the country operates the infrastructure.

5. Documentation and operator guidance
   - Add DevOps README/design notes that explain the layer split, directory layout, required variables, provider responsibilities, and validation commands.
   - Document what is implemented now vs. what remains a provider-specific extension point so QA and PO can judge the baseline honestly.

## Alternatives considered

- Helm-only baseline: rejected as the only abstraction because the backlog explicitly calls for Kubernetes-compatible manifests/templates plus IaC structure; Helm can be introduced later if needed but should not replace the visible provider-neutral/provider-specific split.
- Provider-specific manifests only: rejected because it would duplicate shared application concerns and invite drift or hidden code-coupling to one provider.
- Defer IaC until after business features: rejected by `DECISION-004`, `STORY-020`, and the Phase 1 roadmap because late deployment design would create rework in runtime configuration, secret handling, and provider portability.
- One cloud provider first, others later: rejected because the accepted sovereign model requires country choice and cloud-neutral application packaging from the baseline onward.

## Files/components likely affected

- `df/artifacts/STORY-023/task.md`
- `df/artifacts/STORY-023/solution-design.md`
- `df/artifacts/STORY-023/cloud-portable-deployment-baseline.md`
- `df/artifacts/STORY-023/decision-013-provider-neutral-kubernetes-iac-layering.md`
- `df/artifacts/STORY-023/handoffs.md`
- `df/backlog/architecture-direction.md`
- `df/backlog/user-stories.md`
- `df/runtime/board.md`
- `df/runtime/devops-board.md`
- `df/runtime/activity-log.md`
- `df/runtime/decisions.md`
- `df/runtime/risks.md`
- expected DevOps implementation targets under `devops/kubernetes/**`, `devops/iac/**`, and supporting documentation files

## Data model changes

- None expected. This story defines deployment assets and infrastructure-as-code structure only.

## API/contract changes

- No business API changes
- Operational contract continues to use the existing application image/runtime variables and `GET /platform/status` readiness path
- New contract boundary: provider-neutral Kubernetes base plus provider-specific overlays/modules

## UI/UX impact

- None

## Security and privacy considerations

- Keep secrets out of source-controlled manifests and IaC variables files; use secret references and documented placeholder names only
- Preserve country-sovereign operation boundaries from `STORY-020`
- Prevent provider-specific IAM, secret-store, registry, or networking logic from leaking into application code
- Keep database endpoints, registry URLs, observability sinks, and certificate/network details configurable per deployment target

## Performance/scalability considerations

- The base deployment should support future horizontal scaling and provider-specific autoscaling without changing application code
- Resource defaults should be overridable so smaller sovereign installations and larger managed clusters can both use the same baseline
- Probe behavior should reuse the existing application health contract so orchestration behavior stays consistent across providers

## Test strategy

DevOps should run the strongest available verification path in the local environment and document any tool limitations explicitly:

- render the Kubernetes base plus each provider overlay using `kubectl kustomize`, `kustomize build`, or equivalent
- validate that provider-neutral base assets do not contain provider-specific annotations/configuration except through overlays
- format/validate OpenTofu-compatible HCL (`tofu fmt` / `terraform fmt`, `tofu validate` where practical)
- if a local Kubernetes runtime is available, perform at least one smoke deployment path using the existing OCI image and externalized configuration
- rerun relevant Maven/OCI build steps only if deployment assets require an updated image or shared build documentation changes
- document any missing local tools (`kubectl`, `kustomize`, `tofu`, `terraform`, local cluster runtime) as evidence rather than silently skipping verification

## Deployment/migration plan

- Add the deployment baseline under the DevOps project area in a reversible directory structure
- Reuse the accepted OCI image/runtime contract from `STORY-022`
- Keep provider-specific values externalized so no application or schema migration is required
- Document how countries/operators substitute their own registry, secret store, network, database, and observability values per provider

## Rollback plan

- Revert the Kubernetes and IaC baseline assets plus related documentation added for `STORY-023`
- Remove the related shared architecture-direction addendum and decision record if the approach is rejected
- Fall back to the accepted OCI-only baseline from `STORY-022` while redesigning the orchestration/IaC contract

## Risks and mitigations

- Risk: Provider-specific details may creep into the provider-neutral layer
  - Mitigation: require a strict base-vs-overlay/module separation and QA inspection against that boundary
- Risk: Local tooling may not support full Kubernetes/OpenTofu validation
  - Mitigation: define a prioritized fallback validation path and require explicit evidence of what was and was not validated
- Risk: Future lanes may touch shared deployment/build docs or assets
  - Mitigation: keep implementation DevOps-only and document expected file ownership up front
- Risk: Self-hosted/on-prem may be under-specified relative to managed clouds
  - Mitigation: treat self-hosted/on-prem as an explicit provider path with documented assumptions, not an implicit leftover category

## Open questions

- None blocking the DevOps baseline. If the chosen Kubernetes templating/rendering structure or HCL layout proves incompatible with the current toolchain, DevOps should document the exact constraint and return to SA only if the layer split itself must change.

## Implementation lane

- Lane: `devops`
- Subdashboard: `df/runtime/devops-board.md`
- Artifact folder for implementation notes: `df/artifacts/STORY-023/devops/`

## SA decision

Approved for development: Yes

