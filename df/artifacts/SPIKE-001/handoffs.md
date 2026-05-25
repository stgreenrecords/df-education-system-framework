# Handoffs - SPIKE-001

## Dev -> QA

- Timestamp: 2026-05-23 08:50
- Task: SPIKE-001
- State: READY_FOR_QA
- Summary: Completed the Poland research spike and produced `poland-template-v1.md` with source register, education levels, institution types, grade scales, semester/school-year assumptions, proposed common subjects, and explicit validation unknowns.
- Files changed:
  - `df/artifacts/SPIKE-001/poland-template-v1.md`
  - `df/artifacts/SPIKE-001/dev-notes.md`
  - `df/artifacts/SPIKE-001/task.md`
  - `df/runtime/board.md`
  - `df/runtime/activity-log.md`
  - `df/runtime/risks.md`
- Tests run:
  - Source-access/manual validation via `Invoke-WebRequest` against Eurydice, CKE, gov.pl, and University of Warsaw pages
  - Acceptance-criteria coverage check against `df/artifacts/SPIKE-001/task.md`
- Known risks:
  - Higher-education grading is likely institution-specific and should not yet be hard-coded as one national default
  - School-year dates and winter breaks must be versioned annually and may vary by voivodeship
- QA focus areas:
  - Verify each of the 7 acceptance criteria is fully covered
  - Spot-check source-backed claims against the URLs in the source register
  - Confirm assumptions are clearly separated from confirmed facts
  - Challenge whether preschool and higher-education sections are sufficiently explicit for downstream implementation

## QA -> PO

- Timestamp: 2026-05-23 local
- Task: SPIKE-001
- State: READY_FOR_PO
- Summary: QA passed. All 7 acceptance criteria verified. Framework guardrail compliance confirmed (data-only, no code/schema changes). Source traceability confirmed across 14 sources. Assumption vs fact separation confirmed throughout the document. No defects raised.
- Files reviewed:
  - `df/artifacts/SPIKE-001/poland-template-v1.md`
  - `df/artifacts/SPIKE-001/dev-notes.md`
  - `df/artifacts/SPIKE-001/task.md`
- Evidence produced:
  - `df/artifacts/SPIKE-001/qa-report.md`
- Risks for PO:
  - Higher-education grading scale unconfirmed nationally — should remain institution-configurable in MVP
  - School-year dates must be annual/versioned; do not hard-code
  - Voivodeship winter-break variation needs region-aware configuration if scheduling is in scope
  - Preschool subject taxonomy requires a product decision (learning areas vs subjects)
  - Vocational qualification catalog requires a follow-up spike
- PO next action: Review `poland-template-v1.md` for business intent alignment. Confirm or challenge research scope, proposed unknowns, and any recommendations. Accept or provide feedback.

## PO -> Next session

- Timestamp: 2026-05-23 09:55 local
- Task: SPIKE-001
- State: DONE
- Summary: PO accepted the Poland Template v1 research deliverable. Product review confirmed alignment with the original Poland MVP reference-template intent, explicit handling of residual unknowns, and compliance with the data-only country-template guardrail.
- Files reviewed/created:
  - `df/artifacts/SPIKE-001/poland-template-v1.md`
  - `df/artifacts/SPIKE-001/qa-report.md`
  - `df/artifacts/SPIKE-001/dev-notes.md`
  - `df/artifacts/SPIKE-001/po-review.md`
  - `df/artifacts/SPIKE-001/task.md`
  - `df/runtime/board.md`
  - `df/runtime/activity-log.md`
- Tests/checks:
  - Product review against `df/artifacts/SPIKE-001/task.md` acceptance criteria
  - Cross-check against `df/backlog/epics.md` and `df/backlog/final-initial-prompt.md` Poland MVP intent
  - Review of residual risks and unknowns from `df/artifacts/SPIKE-001/qa-report.md`
- Known risks accepted:
  - Higher-education grading remains institution-configurable until confirmed
  - School-year dates and winter-break details require annual/versioned validation
  - Preschool taxonomy and vocational-catalog detail require follow-up product/design work
- Next role instructions:
  - Stop this session per the single-role rule.
  - In a new session, `po` should review `TASK-002` in `READY_FOR_PO`.
- Blockers:
  - None for SPIKE-001 completion

