# Decision Record - DECISION-001

- Date: 2026-05-23
- Status: Accepted
- Owner role: Dev
- Related task: TASK-002

## Context

Poland is the first reference country template for the Education System Framework. Without an explicit guardrail, later implementation work could incorrectly treat the Poland template as a reason to alter framework code paths, module structure, or schemas.

## Decision

Country templates are strictly data-only inputs to the framework.

No country template, including Poland, may introduce or justify:

- country-specific framework code branches;
- country-specific module structure changes;
- country-specific database schema forks;
- country-specific API contract forks;
- country-specific framework behavior implemented in code instead of configuration data.

Country variation is allowed only through configuration data, template values, versioned configuration content, and institution/country-operated data managed within the generic framework model.

## Consequences

- Poland remains a reference configuration, not a structural exception.
- Future implementation work must model national differences via data/configuration.
- Any request that appears to need country-specific framework code should first trigger schema/configuration design review rather than direct code branching.

## Alternatives considered

- Allowing Poland-specific code temporarily for MVP speed — rejected because it would bias the framework toward one country and undermine reuse.
- Allowing country-specific schema forks per deployment — rejected because it would damage compatibility, migration, and maintainability.

## Evidence

- `AGENTS.md`
- `df/backlog/architecture-direction.md`
- `df/artifacts/SPIKE-001/poland-template-v1.md`
- `df/runtime/decisions.md`

## Follow-up actions

- QA should verify this rule is unambiguous in the updated docs.
- Future architecture and development tasks should treat country differences as configuration-data design work only.

