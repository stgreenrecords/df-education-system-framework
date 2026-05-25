# Task - STORY-050

## Summary

Define a generic, versioned, evidence-backed country template schema and builder concept that can represent country education rules as data without introducing country-specific framework code.

## Type

Story

## Priority

P1

## Current state

DONE

## Business goal

Establish the generic country-template contract before country-specific implementation work starts so later templates such as Poland can be built, reviewed, versioned, and approved as data packages instead of driving country-specific framework forks.

## Acceptance criteria

- [ ] Given a country template, when created, then it includes: education stages, institution types, grade scales, required subjects, academic calendar, semester structure, attendance rules, teacher roles, legal constraints, evidence links, version, approval status
- [ ] Given a template, when versioned, then previous versions are preserved
- [ ] Given a template, when not approved, then it is marked as draft

## Out of scope

- Implementing runtime persistence, APIs, UI, or import/export tooling for country templates
- Populating the Poland template itself; that remains in `STORY-060`
- Introducing country-specific code paths, schemas, or API variants

## Assumptions

- This story is a documentation/architecture deliverable and does not require a delivery-lane implementation session
- Country templates will remain data-only artifacts governed by the no-country-specific-code rule from `DECISION-001`
- The accepted configuration inheritance foundation from `STORY-030` is the correct generic base for template values and lower-scope overrides

## Dependencies

- `STORY-030` for the generic configuration/inheritance model
- `SPIKE-001` as a source-backed example of the kind of evidence and country data the future template structure must be able to carry

## Risks

- If the schema concept is too rigid, later country templates may require redesign rather than extension
- If the schema concept is too vague, `STORY-060` may not have enough structure to implement and verify source-backed country data consistently

## Links

- Issue: n/a
- PR: n/a
- Design: `df/artifacts/STORY-050/solution-design.md`

## Role history

| Timestamp | Role | State | Summary |
|---|---|---|---|
| 2026-05-25 12:21 local | sa | OPEN -> NEEDS_ARCHITECTURE | Selected `STORY-050` as the next highest-priority actionable backlog task because the remaining Phase 1 work now shifts from core platform foundations to the country-template contract that unblocks the critical Poland template story. |
| 2026-05-25 12:21 local | sa | NEEDS_ARCHITECTURE -> ARCHITECTURE_IN_PROGRESS | Started architecture because the task defines a cross-cutting data contract, builder workflow, evidence model, versioning rules, and approval lifecycle for future country-template work. |
| 2026-05-25 12:21 local | sa | ARCHITECTURE_IN_PROGRESS -> READY_FOR_QA | Completed the documentation-only country-template schema and builder concept, recorded `DECISION-019`, updated shared architecture guidance, and prepared QA handoff. |
| 2026-05-25 12:31 local | qa | READY_FOR_QA -> QA_IN_PROGRESS -> READY_FOR_PO | Independently reviewed the story artifact package, confirmed all three acceptance criteria, verified immutable versioning and default-`draft` lifecycle behavior, confirmed documentation-only routing with no lane leakage, and passed the story to PO with no defects. |
| 2026-05-25 12:34 local | po | READY_FOR_PO -> PO_REVIEW -> DONE | Reviewed the QA-approved documentation-only architecture package, confirmed the schema concept is sufficient product direction to unblock future country-template work, documented why screenshots are not applicable, and accepted the story. |

