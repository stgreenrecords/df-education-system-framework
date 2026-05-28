# Task - TASK-013

## Summary

Prepare PowerPoint presentation assets that explain the Education System Framework project, including a detailed project-overview deck and a short Russian executive deck focused on business problems and value.

## Type

Task

## Priority

P0

## Current state

READY_FOR_QA

## Business goal

Provide stakeholder-ready presentation assets that can be used for onboarding, planning, demos, executive explanation, and Russian-language business discussions without overstating the current implementation status.

## Acceptance criteria

- [x] A PowerPoint-compatible `.pptx` deck exists in `docs/presentations/education-system-framework-project-overview/`.
- [x] The deck explains the project vision, users, Dark Factory workflow, architecture, repository structure, implemented capabilities, runtime status, risks, and next steps.
- [x] The presentation content is grounded in current repository documentation/runtime evidence as of 2026-05-27 and clearly distinguishes accepted work from work still in QA/PO/blocked states.
- [x] Regeneration assets exist so the deck can be updated later (`generate_presentation.py`, `requirements.txt`, `README.md`, and `deck-outline.md`).
- [x] QA handoff documentation points to the generated deliverables and the regeneration/verification commands.
- [x] A Russian-language executive `.pptx` deck exists in the same folder and stays focused on business problems, stakeholder value, strategic product positioning, and a concise bullet-driven format.
- [x] The Russian executive deck includes an explicit slide covering the developers/business pain of outsourcing instability and the opportunity of building an in-demand global own product.
- [x] The Russian executive deck includes a general technical overview slide describing the platform stack and architecture at a high level.

## Out of scope

- Brand-polished graphic design beyond a clean presentation baseline
- PDF export or presenter rehearsal notes
- Any change to application code, runtime behavior, or existing accepted task scope

## Assumptions

- A documentation-owned SA deliverable is appropriate because this request is a project-explanation artifact rather than application implementation.
- PowerPoint `.pptx` files are the primary requested outputs.
- The most accurate project snapshot is the repository/runtime state on 2026-05-27 local.
- The requested executive variation should remain concise, bullet-driven, and business-problem-first, while still covering a short technical overview.

## Dependencies

- `README.md`
- `df/backlog/product-vision.md`
- `df/backlog/architecture-direction.md`
- `df/backlog/mvp-definition.md`
- `df/backlog/roadmap.md`
- `docs/run-application.md`
- `docs/deploy-aws.md`
- `df/runtime/board.md`
- `df/runtime/decisions.md`
- `df/runtime/risks.md`

## Risks

- Presentation quality depends on repository-grounded facts only; it intentionally avoids inventing future completion claims.
- The runtime snapshot can age, so future updates should rerun the generator after reviewing the current board.

## Links

- Issue: n/a
- PR: n/a
- Design: `docs/presentations/education-system-framework-project-overview/education-system-framework-project-overview-2026-05-27.pptx`
- Design: `docs/presentations/education-system-framework-project-overview/education-system-framework-executive-ru-2026-05-27.pptx`

## Role history

| Timestamp | Role | State | Summary |
|---|---|---|---|
| 2026-05-27 10:18 local | sa | READY_FOR_QA | Created the project-overview presentation package and prepared QA handoff for the documentation-only deliverable. |
| 2026-05-27 local | sa | READY_FOR_QA | Revised the package to add a Russian executive deck, updated regeneration assets, and added the requested developers/outsourcing-instability slide. |

