# Decision Record - DECISION-014

- Date: 2026-05-24
- Status: Accepted
- Owner role: SA
- Related task: STORY-021

## Context

The sovereign deployment model from `STORY-020` defines each country/ministry deployment as isolated and country-operated rather than a centrally hosted shared runtime. `STORY-021` now needs the first persisted tenant/deployment configuration model. The architecture must avoid drifting into centralized multi-country SaaS routing while still giving later modules a reusable tenant context.

## Decision

Adopt a **single active deployment tenant** model for the Phase 1 tenant/deployment foundation.

The governing rules are:

1. each sovereign country/ministry deployment persists one active tenant record representing that deployment;
2. tenant metadata contains deployment identity/configuration fields such as country code, name, timezone, and locale only;
3. API and backend service scoping resolve through a server-controlled deployment tenant context, not a request-selected cross-country tenant switch;
4. later modules should consume the shared tenant context abstraction instead of inventing parallel country-scoping approaches;
5. provider-specific deployment settings and country template content remain externalized configuration/data, not tenant-specific code forks.

## Consequences

- `backend-dev` owns implementation of the first persisted tenant table, bootstrap flow, and tenant context abstraction.
- Later configuration, organization, security, audit, and release-management stories should build on this same deployment-tenant context.
- The broader schema-isolation strategy remains deferred; this decision does not require schema-per-tenant today.
- Central multi-country routing remains explicitly out of scope for the current deployment model.

## Alternatives considered

- **Central multi-country runtime routing now**: rejected because it conflicts with the sovereign deployment boundary and adds unnecessary Phase 1 complexity.
- **Configuration-only tenant identity with no persisted record**: rejected because later stories need a verifiable stored deployment identity and reusable backend context.
- **Full hierarchy modeling in the tenant story**: rejected because country → region → city → institution inheritance belongs to later stories such as `STORY-030`.

## Evidence

- `df/artifacts/STORY-021/solution-design.md`
- `df/artifacts/STORY-020/country-sovereign-deployment-architecture.md`
- `df/artifacts/STORY-020/decision-012-country-sovereign-deployment-architecture.md`
- `df/backlog/architecture-direction.md`
- `df/backlog/final-initial-prompt.md`
- `df/backlog/user-stories.md`

## Follow-up actions

- `backend-dev` implements the tenant bootstrap, persistence, context abstraction, and minimal backend contract using this sovereign deployment-tenant model.
- QA verifies that tenant scoping remains deployment-local and does not introduce request-selectable cross-country runtime behavior.
- Later Phase 1 stories reuse the same deployment tenant context rather than inventing a new top-level scoping mechanism.

