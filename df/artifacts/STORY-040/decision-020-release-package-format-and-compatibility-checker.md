# Decision Record - DECISION-020

- Date: 2026-05-25
- Status: Accepted
- Owner role: SA
- Related task: STORY-040

## Context

The sovereign deployment model requires the vendor to distribute updates as release packages that countries review and deploy themselves, but the framework has not yet defined what those packages contain or how countries determine whether an update is safe for their active framework/configuration/template state. Without this contract, later rollout tooling would risk provider-specific drift, unclear migration safety, or country-specific release variants.

## Decision

Adopt a **generic release package and compatibility-checker concept** with these governing rules:

1. A framework release is delivered as an immutable package with a manifest, release notes, migration content, compatibility metadata, rollback guidance, and integrity-verification data.
2. Every release package must include explicit version, release notes, migration-script references, and compatibility metadata.
3. The compatibility checker should evaluate manifest/rule metadata against the country’s current framework version, configuration markers, and country-template markers, and should emit a structured `PASS`/`WARN`/`FAIL` report.
4. Breaking changes must identify affected configuration/template areas and required migration steps before country approval.
5. The package and checker remain provider-neutral and country-neutral; unsupported cases require architecture review rather than country-specific release variants.
6. Country operators retain control of verification, testing, approval, and deployment within their own infrastructure.

## Consequences

- Future release/update tooling can target one canonical package contract.
- Countries get a clearer approval artifact and compatibility report before upgrade decisions.
- Migration and rollback expectations become explicit earlier in the product lifecycle.
- The release-management open question shifts from “what concept should we use?” to “how should we implement the accepted concept?”

## Alternatives considered

- Vendor-controlled direct upgrades
- Pure schema diffing as the first compatibility strategy
- Country-specific package variants
- Delaying package-contract design until after tooling starts

## Evidence

- `df/backlog/final-initial-prompt.md`
- `df/backlog/roadmap.md`
- `df/backlog/mvp-definition.md`
- `df/backlog/user-stories.md`
- `df/backlog/open-questions.md`
- `df/backlog/architecture-direction.md`
- `df/artifacts/STORY-020/country-sovereign-deployment-architecture.md`
- `df/artifacts/STORY-050/country-template-schema-and-builder-concept.md`

## Follow-up actions

- Route `STORY-040` to QA as a documentation-only architecture deliverable
- Use this package/checker contract for later implementation and CI/release automation work
- Mark the compatibility-checker architecture question as answered in shared backlog documentation

