# Decision Record - DECISION-021

- Date: 2026-05-25
- Status: Accepted
- Owner role: SA
- Related task: STORY-031

## Context

`STORY-030` delivered the first generic configuration inheritance engine but explicitly deferred compatibility reporting and inheritance-break workflows. The framework now also has accepted release-package and country-template concepts that will eventually rely on configuration impact analysis and auditable exception handling. The next step must add these capabilities without waiting for the unfinished organization module or weakening the existing lock model.

## Decision

Adopt a **backend-only Phase 1 extension** to the generic configuration engine with these governing rules:

1. Keep configuration validation, inheritance-break request recording, and compatibility reporting inside `backend/platform-core` on top of the existing scope-path model.
2. Introduce an explicit validation/preview contract so callers can detect blocked overrides and other write-precondition failures without mutating state.
3. Model inheritance breaks as auditable **requests** with justification and status, not automatic lock bypasses.
4. Record inheritance-break requests through the shared audit foundation from `STORY-013` rather than building parallel audit storage.
5. Produce compatibility reports from generic field/scope analysis and list affected institution scope identifiers without depending on unfinished organization persistence.
6. Keep all behavior framework-generic and country-neutral; later organization or approval workflows may enrich but must not replace these semantics.

## Consequences

- `backend-dev` can extend the configuration subsystem now without waiting for organization CRUD.
- Release-management and country-template follow-up work gain a concrete compatibility-reporting seam.
- Administrators get traceable inheritance-break submissions without silently weakening lock enforcement.
- Future stories can add approval workflow and richer institution metadata on top of this baseline.

## Alternatives considered

- Delay validation/impact work until the organization module exists
- Auto-apply inheritance breaks immediately once justification is submitted
- Build compatibility reporting as country-specific or release-specific code outside the configuration engine
- Expand directly into full approval workflow in this story

## Evidence

- `df/artifacts/STORY-030/decision-015-generic-configuration-scope-path-and-field-behavior.md`
- `df/artifacts/STORY-013/decision-016-platform-audit-foundation.md`
- `df/artifacts/STORY-040/release-package-format-and-compatibility-checker.md`
- `df/artifacts/STORY-050/country-template-schema-and-builder-concept.md`
- `df/backlog/architecture-direction.md`
- `df/backlog/user-stories.md`

## Follow-up actions

- Route `STORY-031` to `backend-dev`
- Add validation, inheritance-break request, and compatibility-report contracts plus persistence/tests in `backend/platform-core`
- Keep institution-impact reporting generic until authoritative organization metadata exists

