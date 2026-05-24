# Task - STORY-020

## Summary

Document the country-sovereign deployment architecture so each country/ministry operates isolated environments, data, and release flow without requiring country-specific application code.

## Type

Story

## Priority

P0

## Current state

DONE

## Business goal

Define the framework’s sovereign deployment operating model early in Phase 1 so later Kubernetes/IaC work, tenant/deployment modeling, security, and release packaging all build on one country-owned, cloud-neutral architecture instead of drifting into centralized or provider-specific assumptions.

## Acceptance criteria

- [ ] Given the deployment docs, when read, then they describe country-owned infrastructure, data, backups, and access
- [ ] Given the deployment model, when reviewed, then it shows dev/QA/stage/prod environments per country
- [ ] Given the release flow, when described, then it shows vendor -> package -> country receives -> country tests -> country deploys
- [ ] Given isolation requirements, when described, then no cross-country data flow exists

## Out of scope

- Kubernetes manifests, Helm/Kustomize overlays, or OpenTofu/Terraform modules; these belong to `STORY-023`
- Implementing tenant records, deployment configuration persistence, or runtime country provisioning; those remain in later implementation stories such as `STORY-021`
- Selecting one cloud provider or managed service vendor as the required target for all countries

## Assumptions

- This story is documentation/architecture-only and does not require a delivery lane in the current session
- Existing Phase 1 direction already commits the framework to OCI images, Kubernetes-compatible deployment, and provider-specific IaC kept outside application code
- A country/ministry may choose AWS, Azure, Google Cloud, private cloud, or on-premises infrastructure, but the application/release contract must stay generic across those options

## Dependencies

- None

## Risks

- Over-specifying provider-specific infrastructure details too early would conflict with the cloud-portable direction intended for `STORY-023`
- If sovereign ownership boundaries are underspecified, later deployment and tenant stories may drift toward centralized operations assumptions

## Links

- Issue: n/a
- PR: n/a
- Design: `df/artifacts/STORY-020/solution-design.md`

## Role history

| Timestamp | Role | State | Summary |
|---|---|---|---|
| 2026-05-24 19:55 local | sa | OPEN -> NEEDS_ARCHITECTURE | Selected `STORY-020` as the next highest-priority actionable backlog task after `STORY-022` acceptance because `STORY-023` depends on it and the sovereign deployment architecture remains an explicit Phase 1 prerequisite. |
| 2026-05-24 19:55 local | sa | NEEDS_ARCHITECTURE -> ARCHITECTURE_IN_PROGRESS | Started architecture because the story defines deployment boundaries, environment topology, release flow, provider neutrality, and downstream DevOps/IaC constraints. |
| 2026-05-24 19:55 local | sa | ARCHITECTURE_IN_PROGRESS -> READY_FOR_QA | Completed the sovereign deployment architecture as an SA-owned documentation deliverable, recorded the governing deployment decision, updated the shared architecture direction, and prepared QA handoff. |
| 2026-05-24 19:58 local | qa | READY_FOR_QA -> QA_IN_PROGRESS -> READY_FOR_PO | Independently reviewed the documentation-only architecture package, confirmed all four acceptance criteria are covered, verified the shared architecture direction and decision log are consistent with the story output, confirmed no delivery lane was incorrectly routed, and passed the story to PO with no defects. |
| 2026-05-24 20:01 local | po | READY_FOR_PO -> PO_REVIEW -> DONE | Reviewed the QA-approved sovereign deployment architecture, confirmed the country-owned environment/data/access model, accepted the explicit vendor-to-country release flow and no-cross-country-data boundary as the correct Phase 1 product baseline, and approved the story so downstream deployment work can proceed on top of this operating model. |

