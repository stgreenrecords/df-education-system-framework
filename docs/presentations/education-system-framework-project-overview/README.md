# Education System Framework presentation package

This folder contains generated PowerPoint presentations that explain the project from two angles:

- `education-system-framework-project-overview-2026-05-27.pptx` — detailed English project-overview deck
- `education-system-framework-executive-ru-2026-05-27.pptx` — concise Russian executive deck with short bullet points, business problems, stakeholder value, a high-level technical overview, and the strategic case for a globally scalable own product

## Files

- `education-system-framework-project-overview-2026-05-27.pptx` - generated detailed deck
- `education-system-framework-executive-ru-2026-05-27.pptx` - generated Russian executive deck
- `generate_presentation.py` - generator script for both decks
- `deck-outline.md` - human-readable slide-by-slide outline for both decks
- `requirements.txt` - Python dependency for regeneration

## Regenerate the decks

From the repository root in PowerShell:

```powershell
python -m pip install -r .\docs\presentations\education-system-framework-project-overview\requirements.txt
python .\docs\presentations\education-system-framework-project-overview\generate_presentation.py
```

## Source basis

The deck content is grounded in the repository state as of `2026-05-27`, especially:

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

## Presentation intent

- Use the detailed English deck for stakeholder onboarding, project walkthroughs, internal planning discussions, or demo framing.
- Use the Russian executive deck for colleagues, leadership, or business-facing conversations where the emphasis should be on market pain, stakeholder value, a compact technical overview, and product strategy.

Both decks are intentionally factual and status-aware: they explain what is already implemented, what remains in progress, and what is still blocked or awaiting QA/PO review.

