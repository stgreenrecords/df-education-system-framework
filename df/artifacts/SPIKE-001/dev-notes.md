# Dev Notes - SPIKE-001

## Implementation summary

Completed the research spike by producing `df/artifacts/SPIKE-001/poland-template-v1.md`, a source-backed Poland country-template draft for the Education System Framework. The artifact documents:

- public/open sources used;
- education levels from early childhood to higher education;
- institution types;
- grade scales and external exam result models;
- school-year and semester assumptions;
- proposed common subjects;
- validation unknowns and implementation cautions.

No application code was changed. This dev session produced documentation/research artifacts only.

## Files changed

- `df/artifacts/SPIKE-001/poland-template-v1.md`: main research deliverable
- `df/artifacts/SPIKE-001/task.md`: updated current state and role history
- `df/artifacts/SPIKE-001/handoffs.md`: dev-to-QA handoff
- `df/runtime/board.md`: moved task to `READY_FOR_QA`
- `df/runtime/activity-log.md`: recorded dev start and completion evidence
- `df/runtime/risks.md`: added runtime risks discovered during the spike

## Commands run

```text
git --no-pager status --short
Invoke-WebRequest -Uri "https://www.gov.pl/web/edukacja" -UseBasicParsing -TimeoutSec 20
Invoke-WebRequest -Uri "https://eurydice.eacea.ec.europa.eu/national-education-systems/poland/overview" -UseBasicParsing -TimeoutSec 25
Invoke-WebRequest -Uri "https://eurydice.eacea.ec.europa.eu/eurypedia/poland/organisation-education-system-and-its-structure" -UseBasicParsing -TimeoutSec 25
Invoke-WebRequest -Uri "https://eurydice.eacea.ec.europa.eu/eurypedia/poland/assessment-single-structure-education" -UseBasicParsing -TimeoutSec 20
Invoke-WebRequest -Uri "https://eurydice.eacea.ec.europa.eu/eurypedia/poland/assessment-general-upper-secondary-education" -UseBasicParsing -TimeoutSec 20
Invoke-WebRequest -Uri "https://eurydice.eacea.ec.europa.eu/eurypedia/poland/assessment-vocational-upper-secondary-education" -UseBasicParsing -TimeoutSec 20
Invoke-WebRequest -Uri "https://eurydice.eacea.ec.europa.eu/eurypedia/poland/assessment-post-secondary-non-tertiary-education" -UseBasicParsing -TimeoutSec 20
Invoke-WebRequest -Uri "https://eurydice.eacea.ec.europa.eu/national-education-systems/poland/higher-education" -UseBasicParsing -TimeoutSec 25
Invoke-WebRequest -Uri "https://eurydice.eacea.ec.europa.eu/eurypedia/poland/organisation-academic-year" -UseBasicParsing -TimeoutSec 25
Invoke-WebRequest -Uri "https://eurydice.eacea.ec.europa.eu/eurypedia/poland/teaching-and-learning-single-structure-education" -UseBasicParsing -TimeoutSec 25
Invoke-WebRequest -Uri "https://eurydice.eacea.ec.europa.eu/eurypedia/poland/teaching-and-learning-general-upper-secondary-education" -UseBasicParsing -TimeoutSec 25
Invoke-WebRequest -Uri "https://welcome.uw.edu.pl/academics/academic-calendar/" -UseBasicParsing -TimeoutSec 20
Invoke-WebRequest -Uri "https://cke.gov.pl/egzamin-maturalny/" -UseBasicParsing -TimeoutSec 20
Invoke-WebRequest -Uri "https://cke.gov.pl/egzamin-osmoklasisty/" -UseBasicParsing -TimeoutSec 20
```

Result: PASS

## Unit tests

Not applicable. This task produced research/documentation artifacts, not executable code.

## Integration tests

Not applicable. No application behavior changed.

## Manual checks

- Verified network access to public Polish education sources.
- Verified each acceptance criterion is explicitly covered in `poland-template-v1.md`.
- Verified source traceability by including a source register with URLs.
- Verified uncertainties are called out as unknowns rather than presented as fact.

## Risks and limitations

- Higher-education grading could not be safely normalized to one national default from accessible sources.
- Exact school-year dates and regional winter breaks need yearly validation before implementation.
- Preschool subject taxonomy is proposed at learning-area level and needs product/QA validation.

## Rollback notes

Low risk. Changes are documentation-only and reversible by removing the SPIKE-001 artifact updates and restoring runtime/task Markdown state.

## Ready for QA?

Yes

## Dev handoff

QA should verify that every claim in `poland-template-v1.md` is either source-backed or clearly marked as an assumption/unknown, and confirm that all 7 acceptance criteria in `df/artifacts/SPIKE-001/task.md` are satisfied.

