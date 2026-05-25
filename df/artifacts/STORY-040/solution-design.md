# Solution Design - STORY-040

## Summary

Define a provider-neutral, country-consumable release package format and a generic compatibility-checker concept so framework updates can be distributed as versioned artifacts with explicit migration and compatibility guidance instead of direct vendor-operated deployment.

## Context

The sovereign deployment architecture from `STORY-020` established the operating boundary `vendor -> package -> country receives -> country tests -> country deploys`, but the repository still lacks the concrete release package contract that countries receive and the compatibility-checking concept they use before approving an update. `STORY-040` completes that missing Phase 1 design layer.

The recently accepted country-template schema concept from `STORY-050` and the generic configuration inheritance foundation from `STORY-030` now provide the right baseline inputs for compatibility metadata and release-impact analysis. What is still missing is a documented package layout, required metadata, and checker behavior for upgrade decisions.

## Requirements and acceptance criteria

- A release package must contain version, release notes, migration scripts, and compatibility metadata
- A compatibility check against a country’s current config must report conflicts and required actions
- Breaking-change reports must identify affected configurations and suggest migration steps

## Proposed solution

Deliver this as an SA-owned architecture/documentation package with four parts:

1. **Canonical release package structure**
   - Define the release package as an immutable versioned bundle with a manifest and well-known sections.
   - Keep the artifact provider-neutral and country-neutral.
   - Document required sections for release notes, migrations, compatibility metadata, and rollback guidance.

2. **Release manifest and compatibility metadata**
   - Define a manifest containing at least:
     - framework version
     - release date
     - package schema version
     - supported upgrade path(s)
     - included migration set identifiers
     - compatibility metadata reference
     - release channel/status (`draft`, `candidate`, `approved-for-distribution` or equivalent publishing state)
   - Define compatibility metadata to describe:
     - minimum/maximum supported current framework versions
     - required configuration schema versions
     - required country-template schema versions
     - breaking changes and affected domains
     - required manual actions
     - automated migration availability
     - rollback limitations

3. **Compatibility checker concept**
   - Define the checker as a generic rule-driven validation flow rather than country-specific comparison logic.
   - Inputs:
     - target release manifest + compatibility metadata
     - current deployed framework version
     - current configuration schema/version markers
     - current country-template version/schema markers
     - optionally available environment capability markers later (database engine version, feature flags, etc.)
   - Output:
     - a structured compatibility report with `PASS`, `WARN`, and `FAIL` outcomes
     - explicit conflicts
     - required actions
     - affected configuration/template areas
     - suggested migration steps
   - Prefer manifest/rule evaluation over raw schema diffing as the primary Phase 1 model because it is more explainable, portable, and compatible with sovereign review workflows.

4. **Governance and rollout flow**
   - Countries receive the package, run the checker locally, review the compatibility report, test in their own environments, and then decide whether to deploy.
   - The package must never require vendor control of country production infrastructure.
   - Rollback guidance and known irreversible migrations must be visible before approval.

## Alternatives considered

- Direct vendor-controlled upgrades without country-side package review: rejected because it violates the sovereign operating model.
- Pure schema diffing as the first compatibility approach: rejected because it is too narrow for country-template, configuration, and operational precondition checks.
- Country-specific release-package variants: rejected because they would violate framework portability and increase maintenance risk.
- Implement the checker first without defining a package contract: rejected because tooling would lack a stable input model.

## Files/components likely affected

- `df/artifacts/STORY-040/task.md`
- `df/artifacts/STORY-040/solution-design.md`
- `df/artifacts/STORY-040/release-package-format-and-compatibility-checker.md`
- `df/artifacts/STORY-040/decision-020-release-package-format-and-compatibility-checker.md`
- `df/artifacts/STORY-040/handoffs.md`
- `df/backlog/architecture-direction.md`
- `df/backlog/open-questions.md`
- `df/runtime/board.md`
- `df/runtime/activity-log.md`
- `df/runtime/decisions.md`

## Data/API contract changes

- No runtime API or database changes in this story
- Defines a future release-artifact contract only
- Establishes generic metadata/reporting fields for later package-builder and compatibility-checker tooling

## Security/privacy considerations

- Release packages must not embed secrets or country production data
- Compatibility reports must describe country-side actions without requiring vendor access to country infrastructure
- Migration metadata must call out irreversible or sensitive operations explicitly
- Release artifacts must remain country-neutral and must not encode country-specific code or package variants

## Test strategy

QA should verify by documentation inspection that:

- the release package format explicitly includes version, release notes, migration scripts, and compatibility metadata
- the checker concept produces conflicts and required actions for incompatible country-state inputs
- breaking-change reporting identifies affected configurations and migration guidance
- the design remains consistent with the sovereign deployment model from `STORY-020`
- the solution stays documentation-only and is not routed to a delivery lane
- the open question on compatibility-checker direction is resolved consistently across shared docs

No application build or runtime tests are required in this SA session because the deliverable is documentation-only.

## Risks and mitigations

- Risk: later tooling could treat compatibility metadata as optional
  - Mitigation: make it a required release-package section and a prerequisite for country approval workflows
- Risk: the checker could become too abstract to implement consistently
  - Mitigation: define concrete inputs, outputs, and severity outcomes (`PASS`, `WARN`, `FAIL`) now
- Risk: release packages could drift into provider-specific or country-specific variants
  - Mitigation: keep the package format provider-neutral, country-neutral, and manifest-driven

## Rollback plan

- Revert the `STORY-040` artifact folder additions
- Remove the release-package architecture section from `df/backlog/architecture-direction.md`
- Remove `DECISION-020` from `df/runtime/decisions.md`
- Return the story to `sa` if QA or PO finds the package/checker concept incomplete or inconsistent

## Open questions

- The eventual implementation form of the checker can remain open (CLI, service endpoint, CI job, or package-verification tool) as long as it consumes the documented manifest/compatibility contract.
- Packaging/signing/distribution mechanics can be tightened later once the CI/release lane is implemented.

## SA decision

Approved for development: No delivery lane required in this session. This is an SA-owned documentation deliverable and the next gate is QA.

