# Task - STORY-040

## Summary

Define the generic release package format and compatibility-checker concept so countries can receive, review, validate, and deploy framework updates without vendor-operated production access.

## Type

Story

## Priority

P1

## Current state

READY_FOR_QA

## Business goal

Complete the remaining Phase 1 release/update-manager concept by documenting how versioned framework releases are packaged, validated, and approved for sovereign country deployments, so future rollout work can remain portable, auditable, and country-controlled.

## Acceptance criteria

- [ ] Given a release package, when inspected, then it contains version, release notes, migration scripts, compatibility metadata
- [ ] Given a country's current config, when the compatibility checker runs against a new release, then it reports conflicts and required actions
- [ ] Given a release with breaking changes, when the compatibility report is generated, then it identifies affected configurations and suggests migration steps

## Out of scope

- Implementing the release package builder, checker CLI/service, or CI/CD automation in this story
- Executing a live release rollout or deployment
- Writing country-specific migration logic, package variants, or compatibility rules

## Assumptions

- This story is a documentation/architecture deliverable and does not require a delivery-lane implementation session
- The accepted sovereign deployment model from `STORY-020` remains the governing release flow boundary: vendor publishes packages, countries review and deploy them
- The accepted country-template and configuration foundations from `STORY-050` and `STORY-030` provide the baseline inputs for future compatibility analysis

## Dependencies

- `STORY-030` for the generic configuration/inheritance model
- `STORY-020` for sovereign release/distribution boundaries
- `STORY-050` for the country-template manifest/versioning concept used by compatibility metadata

## Risks

- If the package format is underspecified, future tooling may drift into provider-specific or country-specific release variants
- If compatibility metadata is too weak, countries may not detect breaking changes before deployment

## Links

- Issue: n/a
- PR: n/a
- Design: `df/artifacts/STORY-040/solution-design.md`

## Role history

| Timestamp | Role | State | Summary |
|---|---|---|---|
| 2026-05-25 12:39 local | sa | OPEN -> NEEDS_ARCHITECTURE | Selected `STORY-040` as the next highest-priority actionable task because Phase 1 still requires the release/update-manager concept and release-package contract, and this story safely unblocks later rollout/tooling work more directly than the remaining implementation-heavy follow-ups. |
| 2026-05-25 12:39 local | sa | NEEDS_ARCHITECTURE -> ARCHITECTURE_IN_PROGRESS | Started architecture because the story defines the release artifact contract, compatibility-reporting model, migration/rollback expectations, and sovereign deployment boundaries for future release tooling. |
| 2026-05-25 12:39 local | sa | ARCHITECTURE_IN_PROGRESS -> READY_FOR_QA | Completed the documentation-only release package and compatibility-checker design, recorded `DECISION-020`, updated shared architecture guidance, and prepared QA handoff. |

