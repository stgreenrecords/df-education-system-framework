# Solution Design - TASK-005

## Summary

Dark Factory adds two first-class roles: `designer` for UI/UX design packages before frontend implementation, and `data-engineer` for source-backed country data and synthetic test records.

## Context

The current factory has separate backend, frontend, and DevOps implementation lanes. Frontend work still risks starting without explicit design input, and country dataset creation needs dedicated evidence rules so public facts and synthetic personal records are not mixed.

## Requirements and acceptance criteria

- Frontend UI implementation must require designer input such as HTML/static markup, states, responsive behavior, and accessibility notes.
- Missing design input must block `frontend-dev`.
- Data-engineering work must populate country/test data through data artifacts only.
- City, district, school, and subject names must be true and public-source-backed.
- Teacher names, student names, and individual grade records must be fake/synthetic.

## Proposed solution

Add `designer` as a design-gate role with states `READY_FOR_DESIGN` and `DESIGN_IN_PROGRESS`, a runtime board, and artifacts under `df/artifacts/{task-id}/design/`.

Add `data-engineer` as a delivery lane using `READY_FOR_DEV`, `DEV_IN_PROGRESS`, `RETURNED_TO_DEV`, and `READY_FOR_QA`, with a runtime board and artifacts under `df/artifacts/{task-id}/data/`.

Update `frontend-dev` so UI-facing changes cannot be implemented unless a design package exists. If missing, frontend work is blocked and handed off for design.

## Alternatives considered

- Keep designer as an informal input: rejected because the frontend blocker must be enforceable by state and documentation.
- Fold data work into backend-dev: rejected because country data, source traceability, and synthetic-data privacy checks are distinct from backend implementation.
- Add country-specific code paths for data loading: rejected by the existing no-country-specific-code invariant.

## Files/components likely affected

- `AGENTS.md`
- `df/00-start-here.md`
- `df/01-operating-model.md`
- `df/02-state-machine.md`
- `df/03-orchestration-rules.md`
- `df/04-documentation-standards.md`
- `df/roles/designer.md`
- `df/roles/data-engineer.md`
- `df/roles/frontend-dev.md`
- `df/roles/sa.md`
- `df/roles/qa.md`
- `df/roles/po.md`
- `df/runtime/design-board.md`
- `df/runtime/data-engineer-board.md`
- templates and adapter docs

## Data model changes

None. This task changes workflow documentation only.

## API/contract changes

None.

## UI/UX impact

Future UI-facing frontend work must have designer-authored implementation input before `frontend-dev` changes UI code.

## Security and privacy considerations

The data-engineering rules explicitly forbid real teacher/student personal data and production records in seed/test datasets. Public-source-backed values must be source-mapped so QA can distinguish true public facts from synthetic records.

## Performance/scalability considerations

None for this documentation-only change.

## Test strategy

Run repository text checks for:

- new role files and runtime boards;
- stale three-lane-only routing statements;
- frontend design gate language;
- data-engineering public-source and synthetic-data rules.

QA should independently inspect changed docs for consistency and verify no application code/schema/API files were changed.

## Deployment/migration plan

No deployment. Existing active tasks continue in their current states. New frontend UI and data population tasks must follow the new gates.

## Rollback plan

Revert TASK-005 documentation/runtime changes and remove the new role files/subdashboards. No database migration or application rollback is involved.

## Risks and mitigations

- Risk: frontend work still bypasses design in practice. Mitigation: frontend-dev, QA, and PO instructions now make design evidence a required gate.
- Risk: data engineers accidentally use real personal records. Mitigation: role instructions require synthetic people/grade records and QA source separation checks.

## Open questions

None.

## SA decision

Approved for QA: Yes. This is a framework/process documentation change; backend, frontend, DevOps, data implementation, and product UI design execution are not part of this task.
