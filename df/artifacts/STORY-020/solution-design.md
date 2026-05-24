# Solution Design - STORY-020

## Summary

Document a country-sovereign deployment architecture in which each country/ministry fully operates its own isolated environments, data, access, backups, and deployment lifecycle while consuming the same cloud-neutral application release artifacts.

## Context

Phase 1 already established the backend foundation, PostgreSQL baseline, and the first OCI container baseline. The next architectural dependency is the operating model for sovereign country deployments. `STORY-023` depends on this because Kubernetes manifests and IaC structure must be built around a clear separation between provider-neutral application packaging and provider-specific country infrastructure.

The existing architecture direction already states sovereignty awareness, OCI portability, Kubernetes-compatible deployment, and provider-specific IaC modules. What is still missing is a task-owned deployment architecture document that explicitly defines per-country environment topology, the vendor-to-country release flow, and the no-cross-country-data boundary.

## Requirements and acceptance criteria

- Describe country-owned infrastructure, data, backups, and access responsibilities
- Show dev/QA/stage/prod environments per country deployment
- Define the release flow as vendor -> package -> country receives -> country tests -> country deploys
- State that no cross-country data flow exists in the deployment architecture

## Proposed solution

Deliver this as an SA-owned architecture/documentation package rather than implementation work:

1. Create a sovereign deployment architecture artifact under `df/artifacts/STORY-020/` that describes:
   - one isolated deployment estate per country/ministry
   - country-owned dev/QA/stage/prod environments
   - country-owned PostgreSQL data, backups, observability, secrets, and operator access
   - vendor responsibilities limited to release packages, migration guidance, compatibility guidance, and advisory support
   - explicit no-cross-country runtime data plane

2. Update `df/backlog/architecture-direction.md` to summarize the sovereign operating model and release flow so the architecture backlog stays aligned with the accepted story output.

3. Record a formal decision that:
   - the application code and OCI image stay provider-neutral
   - provider-specific differences live in deployment overlays and IaC modules, not application code
   - every country controls its own environments and data plane
   - later deployment automation (`STORY-023`) must preserve these boundaries

4. Keep the scope documentation-only in this session:
   - no delivery lane routing
   - no Kubernetes manifests yet
   - no IaC module implementation yet
   - QA is the next independent gate

## Alternatives considered

- Centralized multi-country SaaS deployment operated by the framework vendor: rejected because it conflicts with sovereignty, data-residency, and country-operated deployment requirements
- One mandatory cloud provider baseline first: rejected because the framework must remain portable across AWS, Azure, Google Cloud, private cloud, and on-premises targets
- Jump directly into Kubernetes/IaC implementation without first documenting the sovereign operating model: rejected because deployment assets could otherwise encode the wrong ownership and isolation assumptions

## Files/components likely affected

- `df/artifacts/STORY-020/task.md`
- `df/artifacts/STORY-020/solution-design.md`
- `df/artifacts/STORY-020/country-sovereign-deployment-architecture.md`
- `df/artifacts/STORY-020/decision-012-country-sovereign-deployment-architecture.md`
- `df/artifacts/STORY-020/handoffs.md`
- `df/backlog/architecture-direction.md`
- `df/backlog/user-stories.md`
- `df/runtime/board.md`
- `df/runtime/activity-log.md`
- `df/runtime/decisions.md`

## Data model changes

- None. This story defines deployment architecture and operating boundaries only.

## API/contract changes

- No application API changes
- Operational contract clarification only: countries receive portable release artifacts and deploy them into country-owned environments without changing application source code

## UI/UX impact

- None

## Security and privacy considerations

- Country data, backups, secrets, and operator access must remain country-owned
- No cross-country shared production data plane is allowed
- Vendor artifacts must remain portable and must not require vendor access into country data or infrastructure for normal operation
- Provider-specific secret stores, IAM, networking, and registries may vary, but those differences must remain outside application code

## Performance/scalability considerations

- The architecture must support both smaller sovereign deployments and scalable Kubernetes-based deployments later
- Environment separation (dev/QA/stage/prod) must exist per country so performance and release validation happen without cross-country coupling
- Later provider overlays may differ in scaling/autoscaling mechanics, but the release artifact and application behavior should remain consistent

## Test strategy

QA should verify by documentation inspection that:

- the sovereign deployment artifact covers all four acceptance criteria
- the updated architecture direction is consistent with the story output
- the decision record matches the documented architecture
- no delivery lane was incorrectly routed for this documentation-only task
- the design remains country-neutral in code terms and cloud-neutral in application behavior

No application build or runtime tests are required in this SA session because no runnable code changes are part of the task.

## Deployment/migration plan

- No deployment rollout is performed in this story
- The output becomes the reference architecture for `STORY-023` and later deployment/tenant/security work

## Rollback plan

- Revert the documentation artifacts and decision record for this story
- Remove the sovereign deployment addendum from `df/backlog/architecture-direction.md`
- Return deployment-baseline planning to backlog refinement if QA or PO rejects the design

## Risks and mitigations

- Risk: The document could accidentally prescribe one provider too early
  - Mitigation: keep the application/release layer provider-neutral and isolate provider differences to later IaC overlays/modules
- Risk: The sovereign ownership boundary may be too vague for later implementation
  - Mitigation: explicitly define country-owned environments, data, backups, secrets, access, and release responsibility in the story artifact
- Risk: Future deployment work could bypass the documented release flow
  - Mitigation: record a formal decision and require QA/PO review before `STORY-023` implementation starts

## Open questions

- None blocking this documentation-only story. Provider-specific implementation detail remains intentionally deferred to `STORY-023`.

## SA decision

Approved for development: No — this is an SA-owned documentation deliverable; the next gate is QA review.

