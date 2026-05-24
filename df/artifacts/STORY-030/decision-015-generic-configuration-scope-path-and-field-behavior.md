# Decision Record - DECISION-015

- Date: 2026-05-24
- Status: Accepted
- Owner role: SA
- Related task: STORY-030

## Context

`STORY-030` needs the first working configuration inheritance engine after the deployment-tenant foundation from `STORY-021`. The architecture must support country-rooted inheritance, lower-scope overrides, lock enforcement, and extensible-field merging without waiting for the unfinished `organization` module or introducing country-specific framework code.

## Decision

Adopt a **generic scope-path configuration engine** in `backend/platform-core`.

The governing rules are:

1. the country/root scope is anchored to the active deployment tenant from `STORY-021`;
2. lower scopes (`REGION`, `CITY`, `INSTITUTION`, `UNIT`) are represented by generic ordered scope-path identifiers in this story rather than hard dependencies on organization-module persistence;
3. configuration behavior is driven by persisted field-definition metadata such as value type and merge strategy, not feature-specific or country-specific branching;
4. scoped configuration values may carry a lock flag so ancestor scopes can reject deeper overrides for that field;
5. the first implementation supports only the smallest generic merge set needed now: `REPLACE` and `EXTEND_SET`.

## Consequences

- `backend-dev` can implement the first inheritance engine immediately in `platform-core` without waiting for separate organization CRUD stories.
- Later organization work must integrate by supplying authoritative scope identifiers/paths, not by replacing the inheritance semantics.
- Compatibility reporting, inheritance-break workflows, richer merge types, and full audit breadth remain deferred to later stories.
- The design stays framework-generic and respects the no-country-specific-code invariant.

## Alternatives considered

- **Wait for the organization hierarchy first**: rejected because it blocks a critical Phase 1 foundation story and delays multiple downstream stories.
- **Hardwire hierarchy logic into each consuming module**: rejected because it would create duplicated, inconsistent, feature-specific inheritance behavior.
- **Implement all future merge and compatibility features now**: rejected because it adds unnecessary Phase 1 complexity and delivery risk.

## Evidence

- `df/artifacts/STORY-030/solution-design.md`
- `df/artifacts/STORY-021/decision-014-sovereign-deployment-tenant-model.md`
- `df/artifacts/STORY-021/task.md`
- `df/backlog/architecture-direction.md`
- `df/backlog/roadmap.md`
- `df/backlog/user-stories.md`
- `df/backlog/risks-and-assumptions.md`

## Follow-up actions

- `backend-dev` implements the configuration field-definition, scoped-value persistence, resolution service, validation logic, and minimal proof API/coverage in `backend/platform-core`.
- QA must verify inheritance, override precedence, lock rejection, and extensible-field merge behavior.
- Later stories integrate the organization hierarchy and compatibility reporting on top of this shared engine rather than creating parallel configuration mechanisms.

