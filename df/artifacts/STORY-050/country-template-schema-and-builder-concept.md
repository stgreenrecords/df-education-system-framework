# Country Template Schema and Builder Concept

- Task: `STORY-050`
- Status: Draft architecture baseline pending QA/PO review
- Owner role: `sa`
- Timestamp: 2026-05-25 12:21 local

## 1. Purpose

Country templates must express country-specific education rules as **data**, not framework code. The schema and builder concept below define the minimum reusable contract needed for future country-template implementation work such as `STORY-060`.

## 2. Design principles

1. **Data-only** — templates may contain values, rules, references, and versioned content, but no country-specific code, schema forks, or API variants.
2. **Evidence-backed** — any real country fact included in the template should be traceable to a public source entry.
3. **Versioned and immutable** — new releases create new template versions; prior versions remain preserved.
4. **Approval-aware** — every template version has an explicit lifecycle state. If not approved, it remains `draft`.
5. **Generic and extensible** — the same structure must work for Poland and future countries without redesigning the framework.

## 3. Canonical template package

A future country template package should contain these logical sections:

```text
country-template/
├── manifest
├── education-stages
├── institution-types
├── grade-scales
├── subject-rules
├── academic-calendar
├── semester-structure
├── attendance-rules
├── teacher-roles
├── legal-constraints
├── optional-extensions/
│   ├── curriculum-references
│   ├── official-exams
│   ├── language-configuration
│   └── report-document-formats
└── evidence/
    └── source-map
```

The concrete storage encoding can be decided later, but the logical sections above are required for interoperability and QA traceability.

## 4. Required manifest fields

| Field | Required | Purpose |
|---|---|---|
| `templateId` | Yes | Stable identifier for the country template family |
| `countryCode` | Yes | Country identity key (for example ISO-like code) |
| `version` | Yes | Immutable template version identifier |
| `status` | Yes | Lifecycle state, defaulting to `draft` |
| `title` | Yes | Human-readable template title |
| `summary` | Yes | Short business description |
| `effectiveFrom` | Recommended | First intended applicability date |
| `effectiveTo` | Optional | End-of-validity marker when superseded |
| `schemaVersion` | Yes | Version of the template schema/builder contract |
| `sourceMapRef` | Yes | Pointer to the evidence/source section |
| `notes` | Optional | Author/governance notes |

### Status lifecycle

```text
draft -> verified -> approved
```

- `draft`: default state for any new or changed template version
- `verified`: evidence and structure reviewed, but not yet final business approval
- `approved`: accepted for downstream use

No version may skip traceability checks. A version that is not explicitly approved remains `draft` or `verified`.

## 5. Required content sections

### 5.1 Education stages

Must define:

- stage key
- local label(s)
- age range or entry rule
- progression order
- compulsory/optional status
- linked institution types

### 5.2 Institution types

Must define:

- institution type key
- local label(s)
- applicable education stages
- optional schema-pack compatibility hints
- whether the type is in MVP scope or later scope

### 5.3 Grade scales and assessment models

Must define:

- grade scale key
- value set and labels
- assessment mode (`numeric`, `descriptive`, mixed)
- applicability by institution type and/or education stage
- separate behavior/discipline scales when applicable

### 5.4 Required subjects

Must define:

- subject key
- local label(s)
- required vs optional status
- applicability by stage/institution type
- inheritance/override notes when local additions are allowed

### 5.5 Academic calendar

Must define:

- academic-year start/end assumptions
- breaks/holidays rule categories
- whether dates are fixed, yearly versioned, or region-sensitive
- evidence notes for any nationally defined defaults

### 5.6 Semester/term structure

Must define:

- term model (`semester`, `trimester`, etc.)
- number of terms
- progression or reporting boundaries
- relationship to exams or year-end completion

### 5.7 Attendance rules

Must define:

- attendance granularity (daily, lesson-based, session-based)
- allowed attendance statuses
- excuse/justification model at concept level
- rules that are locked nationally vs locally extensible

### 5.8 Teacher roles

Must define:

- role key
- local label(s)
- scope of assignment
- relation to generic framework RBAC when applicable
- whether the role is mandatory, optional, or institution-specific

### 5.9 Legal constraints

Must define:

- constraint key
- policy summary
- affected domain (privacy, retention, reporting, grading, attendance, etc.)
- whether the constraint is mandatory or advisory
- evidence/source reference

### 5.10 Evidence links

Must define:

- source id
- source title
- URL/reference
- retrieval date
- what facts it supports
- transformation notes when applicable

## 6. Optional extension sections

These remain optional but should follow the same evidence-backed, data-only pattern:

- curriculum references
- official exams and pass criteria
- language configuration
- report/document formats
- approval/governance metadata beyond the core lifecycle

## 7. Builder concept

The builder is a generic workflow, not a country-specific implementation branch.

### Inputs

- researched source register
- normalized country data/content
- manifest metadata
- validation rules for required sections

### Output

- immutable country-template package
- validation summary/report
- explicit status value, defaulting to `draft`

### Builder checks

1. required manifest fields are present
2. required sections exist
3. each evidence-backed fact references a source entry
4. version is new and does not overwrite prior versions
5. status defaults to `draft` if approval is absent
6. template content remains data/configuration only
7. optional extensions do not violate the generic framework contract

## 8. Versioning rules

- Template versions are append-only
- Corrections create a new version rather than editing historical content in place
- Older versions stay available for auditability, rollback analysis, and country release compatibility work
- Future compatibility tooling may compare a country’s active version to a new version using the same canonical section keys

## 9. Relationship to later work

- `STORY-060` should populate the Poland template using this structure
- future data-engineering tasks should attach source maps for real place/school/subject names and use synthetic personal data only
- future release/update-manager work can reference version metadata without coupling the framework to one country

## 10. Guardrails

- No country-specific code change is allowed
- No country-specific schema, module, or API fork is allowed
- Unsupported country requirements must trigger architecture review instead of special-case implementation

