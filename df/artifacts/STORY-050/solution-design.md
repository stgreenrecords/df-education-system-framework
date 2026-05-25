# Solution Design - STORY-050

## Summary

Define a generic country-template schema and builder concept as a documentation-owned architecture package so future country templates can be created as immutable, evidence-backed data bundles rather than country-specific framework code.

## Context

The platform now has accepted Phase 1 foundations for deployment tenancy, configuration inheritance, audit, authentication, and RBAC. The next missing contract is the shape of a country template itself. `STORY-060` depends on this because Poland cannot be implemented safely until the framework defines what a country template contains, how it is versioned, how source evidence is attached, and how approval state is tracked.

The existing architecture direction already states that country templates are data-only and must never change framework code, database schemas, or API contracts. What is still missing is a concrete schema concept and builder workflow that translate that rule into a reusable artifact shape.

## Requirements and acceptance criteria

- Include education stages, institution types, grade scales, required subjects, academic calendar, semester structure, attendance rules, teacher roles, legal constraints, evidence links, version, and approval status
- Preserve previous template versions rather than overwriting them
- Represent not-yet-approved templates as `draft`

## Proposed solution

Deliver this as an SA-owned documentation package with four parts:

1. **Canonical country-template package structure**
   - Define the template as a versioned data bundle with a top-level manifest and modular sections.
   - Use stable machine-readable identifiers for template sections so multiple countries can share one generic framework contract.
   - Keep the representation format implementation-neutral for now, but document a future-friendly package layout such as manifest + catalogs + rules + evidence/source map.

2. **Template schema concept**
   - Define a required metadata/manifest section containing:
     - `templateId`
     - `countryCode`
     - `version`
     - `status` (`draft`, `verified`, `approved`, optionally `deprecated` later)
     - `effectiveFrom` / `effectiveTo` when applicable
     - builder/schema version
     - source package reference
   - Define required content sections covering:
     - education stages and progression rules
     - institution types and allowed stage mappings
     - grade-scale and assessment models
     - subject catalogs and required-subject rules
     - academic-calendar and semester/term structure
     - attendance rules
     - teacher-role catalog
     - legal/privacy constraints
     - evidence/source references
   - Document optional extension sections that remain data-only, such as official exams, curriculum references, language configuration, and report/document formats, because these were present in the original product prompt even if not all are required by this story’s acceptance criteria.

3. **Builder concept**
   - Define the builder as a generic assembly/validation workflow rather than country-specific code.
   - Inputs: researched source set, normalized data tables, evidence/source map, and template metadata.
   - Outputs: immutable versioned template package plus a validation report.
   - Validation responsibilities:
     - required sections exist
     - all evidence-backed values reference a source entry
     - status lifecycle rules are enforced (`draft` by default until explicitly promoted)
     - version uniqueness is preserved
     - no template content attempts to alter framework code/schema/API contracts

4. **Lifecycle and governance**
   - Every saved version is immutable; updates create a new version instead of editing history.
   - Approval workflow starts at `draft`, can move to `verified`, and then `approved` when product/governance review exists.
   - Template evidence must distinguish real public-source-backed place/school/subject data from synthetic people/grade data in later data-engineering work.
   - Country-specific needs that cannot fit this schema trigger architecture review instead of special-case framework code.

## Alternatives considered

- Implement Poland directly first and infer the generic schema later: rejected because it risks Poland-shaped framework coupling and weak reuse.
- Put all country data into one large unstructured JSON blob: rejected because QA, versioning, validation, and evidence traceability would be weak.
- Treat country templates as code modules or schema forks: rejected because it violates the data-only country-template invariant.

## Files/components likely affected

- `df/artifacts/STORY-050/task.md`
- `df/artifacts/STORY-050/solution-design.md`
- `df/artifacts/STORY-050/country-template-schema-and-builder-concept.md`
- `df/artifacts/STORY-050/decision-019-country-template-schema-and-builder.md`
- `df/artifacts/STORY-050/handoffs.md`
- `df/backlog/architecture-direction.md`
- `df/runtime/board.md`
- `df/runtime/activity-log.md`
- `df/runtime/decisions.md`

## Data/API contract changes

- No runtime API or database changes in this story
- Defines a future data-package contract only
- Establishes required metadata fields and modular content sections for later template persistence or import tooling

## Security/privacy considerations

- Country templates must stay data-only and must not carry executable behavior or country-specific framework branches
- Evidence/source traceability is required for real country facts
- Later template instances must separate real public-source-backed place/school/subject names from synthetic personal data
- Legal/privacy constraints belong in template data so the framework remains generic while country-specific obligations stay configurable

## Test strategy

QA should verify by documentation inspection that:

- the schema concept covers every required acceptance-criteria dimension
- the versioning model preserves prior versions by immutability rather than overwrite
- the approval lifecycle defaults non-approved templates to `draft`
- the builder concept remains generic and does not introduce country-specific code assumptions
- the shared architecture direction and decision record are consistent with the story artifact
- the story correctly remained SA-owned/documentation-only with no delivery-lane routing

No application build or runtime tests are required in this SA session because the deliverable is documentation-only.

## Risks and mitigations

- Risk: the schema concept may omit dimensions needed by later country templates
  - Mitigation: define required core sections plus explicitly extensible optional sections
- Risk: later teams may treat evidence/source references as optional
  - Mitigation: make source traceability part of the builder validation concept and governance rules
- Risk: template lifecycle could become mutable and erase prior versions
  - Mitigation: define immutable version storage and explicit status transitions

## Rollback plan

- Revert the `STORY-050` artifact folder additions
- Remove the shared architecture-direction addendum for the template schema concept
- Remove `DECISION-019` from `df/runtime/decisions.md`
- Return the story to `sa` if QA or PO finds the schema concept incomplete or inconsistent

## Open questions

- The eventual persistence/import encoding format can remain open (`YAML`, `JSON`, database-backed authoring, or generated package) as long as it preserves the documented manifest/catalog/rules/evidence contract.
- Exact QA/PO promotion criteria from `verified` to `approved` can be tightened later when the first real country-template workflow is implemented.

## SA decision

Approved for development: No delivery lane required in this session. This is an SA-owned documentation deliverable and the next gate is QA.

