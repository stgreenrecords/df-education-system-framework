# Solution Design - TASK-013

## Summary

Create a documentation-owned PowerPoint presentation package that explains the Education System Framework in a stakeholder-friendly way while remaining faithful to the current repository and runtime state, and add a concise Russian executive variant centered on business pain/value.

## Context

The user explicitly requested a PowerPoint presentation with detailed slides explaining the project. After the first detailed deck was produced, the user requested a shorter Russian executive presentation that emphasizes business problems and adds a slide about the instability of outsourcing for developers/businesses and the strategic upside of building a globally relevant own product. The repository already contains the source material needed for a factual overview: product vision, architecture direction, runtime status, risk register, local run guidance, deployment guidance, and accepted task history.

## Requirements and acceptance criteria

1. Deliver PowerPoint-compatible `.pptx` decks.
2. Keep the detailed project-overview deck covering project vision, user groups, Dark Factory delivery model, roadmap, architecture, repository/module layout, implemented work, operational experience, governance guardrails, runtime status, risks, and next steps.
3. Add a Russian executive deck focused on business problems, stakeholder value, product strategy, and a brief technical overview.
4. Include the requested developers/business pain slide about outsourcing instability and the value of an own globally relevant product.
5. Keep the Russian executive deck concise and bullet-driven rather than paragraph-heavy.
6. Keep the content fact-based and status-aware as of 2026-05-27.
7. Include regeneration assets so the presentation package can be updated later without manual slide recreation.

## Proposed solution

- Keep the existing presentation package under `docs/presentations/education-system-framework-project-overview/`.
- Generate both `.pptx` files via the Python `python-pptx` script so updates remain reproducible.
- Keep `README.md`, `requirements.txt`, and `deck-outline.md` aligned with both deliverables.
- Retain the existing 15-slide detailed deck for broad project-overview use.
- Add a second concise Russian executive deck with this structure:
  1. Title / positioning
  2. General project description
  3. Why the project is needed now
  4. State/system pain points
  5. Teacher/parent/student pain points
  6. Platform response
  7. General technical overview
  8. Business value
  9. Developers/business pain: outsourcing instability vs own product
  10. Why framework model is strategically stronger
  11. Rollout and scale-up approach
  12. Closing executive message
- Treat this as an SA-owned documentation deliverable and move directly to `READY_FOR_QA` after regeneration because no implementation lane is required.

## Files/components likely affected

- `docs/presentations/education-system-framework-project-overview/README.md`
- `docs/presentations/education-system-framework-project-overview/requirements.txt`
- `docs/presentations/education-system-framework-project-overview/deck-outline.md`
- `docs/presentations/education-system-framework-project-overview/generate_presentation.py`
- `docs/presentations/education-system-framework-project-overview/education-system-framework-project-overview-2026-05-27.pptx`
- `docs/presentations/education-system-framework-project-overview/education-system-framework-executive-ru-2026-05-27.pptx`
- `df/artifacts/TASK-013/task.md`
- `df/artifacts/TASK-013/solution-design.md`
- `df/artifacts/TASK-013/handoffs.md`
- `df/runtime/board.md`
- `df/runtime/activity-log.md`

## Data/API contract changes

None. This task is documentation/presentation only.

## Security/privacy considerations

- Do not include secrets, credentials, or hidden operational details in the deck.
- Do not claim country-specific behavior that violates the framework guardrails.
- Keep personal-data discussion at the policy level only.

## Test strategy

- Run the presentation generator script successfully.
- Verify both generated `.pptx` files exist at the expected paths.
- Verify the slide counts match the planned outlines (`15` detailed, `12` executive RU).
- Check the generator source file for errors after creation.

## Risks and mitigations

- Risk: the runtime board can change after deck generation.
  - Mitigation: document the snapshot date and provide regeneration instructions.
- Risk: presentation may overstate completion.
  - Mitigation: explicitly separate accepted work from QA/PO/blocked items.
- Risk: executive messaging may become too marketing-heavy or drift from repository-grounded facts.
  - Mitigation: keep even the Russian executive deck tied to existing documented scope and current runtime evidence.

## Rollback plan

Delete the new presentation folder, task artifact folder, and runtime references if the task is rejected.

## Open questions

None.

