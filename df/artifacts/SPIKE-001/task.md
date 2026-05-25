# Task - SPIKE-001

## Summary

Research Polish education system using public/open sources and prepare Poland Template v1 configuration data.

## Type

Spike

## Priority

P0

## Current state

DONE

## Business goal

Poland is the first reference implementation for the Education System Framework. This spike produces the evidence-based country template data (education levels, institution types, grade scales, calendars, subjects) that will populate the Poland country configuration in the platform. Establishing this research methodology also sets the pattern for future country templates.

## Acceptance criteria

- [x] Sources are listed (public/official references used for all data)
- [x] Education levels are documented (all stages from pre-school to higher education)
- [x] Institution types are documented (school types, kindergartens, universities, vocational)
- [x] Grade scale is documented (all grade scales used across education levels)
- [x] Semester/school-year assumptions are documented (start/end dates, term structure, holidays)
- [x] Common subjects are proposed (core curriculum subjects per education level)
- [x] Unknowns are listed for validation (items requiring human/ministry confirmation)

## Out of scope

- Implementing the country template in code
- Researching other countries
- Defining the configuration schema/API (separate architecture task)
- Detailed regional (voivodeship) differences beyond what is nationally standardized

## Assumptions

- Research uses publicly available information (MEN/MEiN websites, Dz.U. legal acts, ISCED mappings)
- The template captures the national standard; regional/institutional exceptions are noted as unknowns
- Polish-language sources may be used and translated/summarized in English

## Dependencies

- TASK-001 (DONE) — domain model and product vision define what data the template must contain

## Risks

- Public sources may be outdated or incomplete → mitigated by listing unknowns for human validation
- Education reform changes (e.g., 2017 reform) may create ambiguity about current structure → use the post-2017 structure as baseline

## Links

- Issue: n/a
- PR: n/a
- Design: n/a

## Refinement

Refinement: not required. Acceptance criteria are already clear and testable from the initial backlog definition. The spike is a well-defined research task with explicit deliverables.

## Role history

| Timestamp | Role | State | Summary |
|---|---|---|---|
| 2026-05-23 01:26 | sa | OPEN → READY_FOR_DEV | Triaged. AC clear, no refinement needed. No architecture needed (research spike). Moved to READY_FOR_DEV. |
| 2026-05-23 08:50 | dev | READY_FOR_DEV → DEV_IN_PROGRESS → READY_FOR_QA | Completed source-backed research artifact `poland-template-v1.md`, documented assumptions/unknowns, and prepared QA handoff. |
| 2026-05-23 local | qa | READY_FOR_QA → QA_IN_PROGRESS → READY_FOR_PO | QA passed all 7 acceptance criteria, framework guardrail compliance, source traceability, and assumption/fact separation. No defects. See `qa-report.md`. |
| 2026-05-23 09:55 | po | READY_FOR_PO → PO_REVIEW → DONE | Accepted the Poland Template v1 research deliverable. Confirmed business-intent alignment with the Poland MVP reference-template goal, accepted no-UI evidence in place of screenshots, and accepted residual risks as documented follow-up validation items. |

