# Decision Record - DECISION-019

- Date: 2026-05-25
- Status: Accepted
- Owner role: SA
- Related task: STORY-050

## Context

The framework already enforces that country templates are data-only, but it does not yet define the actual contract for what a country template contains, how it is versioned, or how evidence/source traceability and approval state are represented. Without that contract, the first real country-template implementation risks becoming country-shaped, unstructured, or hard to verify.

## Decision

Adopt a **generic country-template schema and builder concept** with these governing rules:

1. A country template is a versioned, immutable, evidence-backed data package rather than code.
2. Each template version must contain a manifest with version, status, schema version, and source-map linkage.
3. Required content sections are: education stages, institution types, grade scales, required subjects, academic calendar, semester/term structure, attendance rules, teacher roles, legal constraints, and evidence links.
4. A new or modified template version defaults to `draft` until it is explicitly promoted through review.
5. Previous versions are preserved; changes create a new version rather than overwriting history.
6. Optional extensions such as curriculum references, official exams, language configuration, and report/document formats remain data-only and may extend the package without creating country-specific framework code.
7. If a country requirement cannot be represented through this generic schema, the factory must raise architecture review instead of implementing a country-specific framework branch.

## Consequences

- `STORY-060` and future country-template work can target one canonical artifact shape.
- QA gets a clearer basis for validating country-template completeness, traceability, and approval status.
- Later data-engineering or tooling work can implement import/build validation without changing the core framework contract.
- Unsupported country-specific requirements must be solved through schema evolution or architecture review, not ad-hoc country code.

## Alternatives considered

- Implement the first country template without a canonical schema
- Use one unstructured data blob per country
- Allow country templates to include custom code hooks or schema variants
- Delay versioning/approval concerns until after the first country is implemented

## Evidence

- `df/backlog/final-initial-prompt.md`
- `df/backlog/roadmap.md`
- `df/backlog/mvp-definition.md`
- `df/backlog/domain-model.md`
- `df/backlog/user-stories.md`
- `df/backlog/architecture-direction.md`
- `df/artifacts/SPIKE-001/poland-template-v1.md`
- `df/artifacts/STORY-030/decision-015-generic-configuration-scope-path-and-field-behavior.md`

## Follow-up actions

- Route `STORY-050` to QA as a documentation-only architecture deliverable
- Use this schema concept as the design baseline for `STORY-060`
- Ensure later data-engineering work records public-source traceability and synthetic-person-data separation

