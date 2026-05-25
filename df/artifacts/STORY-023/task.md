# Task - STORY-023

## Summary

Define and route a cloud-portable Kubernetes and infrastructure-as-code deployment baseline so the same OCI application image can be deployed across AWS, Azure, Google Cloud, and self-hosted/on-premises environments without changing application source code.

## Type

Story

## Priority

P0

## Current state

DONE

## Business goal

Establish the Phase 1 deployment baseline before deeper feature work so sovereign country operators can use the same provider-neutral application artifact while choosing their own registry, secret store, networking, database, observability, and infrastructure provider through deployment configuration and provider-specific IaC only.

## Acceptance criteria

- [ ] Given the deployment baseline, when reviewed, then application code remains unchanged across AWS, Azure, Google Cloud, and self-hosted/on-prem targets
- [ ] Given Kubernetes manifests or templates, when reviewed, then they separate provider-neutral application deployment from provider-specific infrastructure concerns
- [ ] Given infrastructure as code, when reviewed, then provider-specific modules exist or are planned for AWS, Azure, Google Cloud, and self-hosted/on-prem infrastructure
- [ ] Given the IaC strategy, when reviewed, then it supports an open-source OpenTofu-compatible path and can accommodate Terraform if required by a country operator
- [ ] Given a country/ministry deployment model, when reviewed, then container registries, secret stores, networking, databases, and observability are configurable per provider without changing application source code

## Out of scope

- Deploying into a real AWS, Azure, Google Cloud, or on-prem environment with live credentials
- Introducing provider-specific behavior into Java application code, database schema, or API contracts
- Replacing the accepted sovereign deployment operating model from `STORY-020`
- Building a full release-management workflow beyond the baseline deployment assets needed for this story

## Assumptions

- `STORY-020` and `STORY-022` are accepted and provide the sovereign operating model plus the reusable OCI image/runtime contract for `backend/platform-core`
- The existing `/platform/status` endpoint and environment-backed database configuration remain the initial readiness/runtime contract for the deployment baseline
- Provider-specific IaC can be delivered as baseline modules plus documented placeholders/examples without needing live cloud credentials in this session
- The baseline should remain DevOps-only; no backend, frontend, design, or data-engineering lane split is required for the current story scope

## Dependencies

- `STORY-020`
- `STORY-022`

## Risks

- Provider-specific deployment details may leak into the provider-neutral layer if the Kubernetes base and IaC contracts are not clearly separated
- Local validation may be constrained by missing Kubernetes/OpenTofu tooling or cloud credentials, so DevOps must document the strongest available verification path
- Shared deployment/build documentation could conflict with later stories unless the asset layout and ownership boundaries stay explicit

## Links

- Issue: n/a
- PR: n/a
- Design: `df/artifacts/STORY-023/solution-design.md`

## Role history

| Timestamp | Role | State | Summary |
|---|---|---|---|
| 2026-05-24 20:08 local | sa | OPEN -> NEEDS_ARCHITECTURE | Selected `STORY-023` as the next highest-priority actionable backlog task after `STORY-020` acceptance because both dependencies are now accepted and the roadmap still lists the Kubernetes/IaC deployment baseline as a Phase 1 foundation item. |
| 2026-05-24 20:08 local | sa | NEEDS_ARCHITECTURE -> ARCHITECTURE_IN_PROGRESS | Started architecture because the story affects deployment boundaries, Kubernetes structure, IaC module strategy, provider-neutral release packaging, and downstream DevOps validation. |
| 2026-05-24 20:08 local | sa | ARCHITECTURE_IN_PROGRESS -> READY_FOR_DEV | Completed the architecture package, recorded the deployment-layering decision, updated shared architecture direction and backlog status, and routed the story to the `devops` lane for implementation. |
| 2026-05-24 20:19 local | devops | READY_FOR_DEV -> DEV_IN_PROGRESS | DevOps implementation started after reviewing the task, architecture package, existing OCI baseline from `STORY-022`, repository status, and the locally available Kubernetes/IaC tooling. |
| 2026-05-24 20:19 local | devops | DEV_IN_PROGRESS -> READY_FOR_QA | Implemented the provider-neutral Kubernetes base, provider-specific overlays for AWS/Azure/GCP/self-hosted, the OpenTofu/Terraform-compatible IaC module and provider wrappers, validation helpers, and deployment READMEs; rendered all manifests successfully, validated all provider modules successfully, documented the Windows PowerShell validation fixes, and handed the story to QA. |
| 2026-05-24 20:23 local | qa | READY_FOR_QA -> QA_IN_PROGRESS -> READY_FOR_PO | Independently reviewed the deployment assets, confirmed the task remains correctly owned by the `devops` lane, reran manifest rendering and provider-module validation, directly scanned the provider-neutral base for cloud-specific markers, verified the module/overlay separation, and passed the story to PO with environment-tool limitations documented. |
| 2026-05-24 20:26 local | po | READY_FOR_PO -> PO_REVIEW -> DONE | Reviewed the QA-approved deployment baseline, confirmed the provider-neutral Kubernetes base plus provider-specific overlays/modules satisfy the sovereign cloud-portability intent, accepted the non-UI evidence path with no screenshots required, and approved the story as the accepted Phase 1 Kubernetes/IaC baseline. |

