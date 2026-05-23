# Dark Factory Activity Log

Append-only chronological log of factory actions.

No activity has been recorded yet.

Use `df/templates/activity-log-entry.md` for new entries.

## 2026-05-22 local - sa - Framework enhancement

- State: DONE
- Action: Added refinement/intake phase with questions loop to the Dark Factory framework.
- Evidence: Updated `df/02-state-machine.md` (new INTAKE/REFINEMENT states), `df/01-operating-model.md` (updated flow), `df/03-orchestration-rules.md` (refinement loop), `df/roles/sa.md` (refinement checklist), `df/roles/po.md` (answering questions), `df/00-start-here.md` (task selection), `AGENTS.md` (role table), `df/04-documentation-standards.md` (artifact list), `df/templates/refinement-questions.md` (new template).
- Result: PASS
- Next: Framework ready for use. Await next delivery task.
- Risks/blockers: None.

## 2026-05-23 01:12 local - sa - TASK-001

- State: INTAKE → REFINED
- Action: Received initial prompt for Education System Framework. Prompt already contains clear acceptance criteria (13 required outputs). Refinement skipped — requirements are explicit and testable.
- Evidence: `df/backlog/final-initial-prompt.md` (existing), `df/artifacts/TASK-001/task.md`
- Result: PASS — acceptance criteria identified
- Next: Produce Phase 0 deliverables (acting as SA + Dev for documentation task)

## 2026-05-23 01:15 local - sa/dev - TASK-001

- State: DEV_IN_PROGRESS → READY_FOR_QA
- Action: Produced all 13 required Phase 0 deliverables
- Evidence:
  - Product vision: `df/backlog/product-vision.md`
  - Scope boundaries: `df/backlog/product-vision.md` (scope section)
  - Domain model: `df/backlog/domain-model.md`
  - Architecture direction: `df/backlog/architecture-direction.md`
  - MVP definition: `df/backlog/mvp-definition.md`
  - Product roadmap: `df/backlog/roadmap.md` (existing, verified complete)
  - Initial backlog: `df/backlog/initial-backlog.md` (existing) + `df/backlog/user-stories.md` (expanded)
  - Epics: `df/backlog/epics.md` (existing, 20 epics)
  - User stories: `df/backlog/user-stories.md` (27 detailed stories with AC)
  - Acceptance criteria: included in each user story
  - Risks and assumptions: `df/backlog/risks-and-assumptions.md`
  - Open questions: `df/backlog/open-questions.md` (40 questions across PO/SA/Dev/QA)
  - Jira-ready structure: `df/backlog/jira-structure.md`
- Result: PASS — all deliverables produced
- Next: QA review

## 2026-05-23 01:18 local - qa - TASK-001

- State: READY_FOR_QA → QA passed
- Action: Verified all 13 required outputs against acceptance criteria
- Evidence: `df/artifacts/TASK-001/qa-report.md`
- Result: PASS — all deliverables complete, structured, and tracker-ready
- Next: PO review

## 2026-05-23 01:20 local - po - TASK-001

- State: PO_REVIEW → DONE
- Action: Accepted all deliverables. Product vision, domain model, architecture direction, roadmap, backlog, epics, stories, AC, risks, questions, and Jira structure all align with initial prompt intent.
- Evidence: `df/artifacts/TASK-001/po-review.md`
- Result: PASS — ACCEPTED
- Next: Pick up SPIKE-001 (Poland education research) or await human direction

## 2026-05-23 01:26 local - sa - SPIKE-001

- State: OPEN → READY_FOR_DEV
- Action: Triaged SPIKE-001. Acceptance criteria already clear and testable from initial backlog (7 explicit deliverables). Refinement skipped. No architecture needed (research spike, no code/infra changes). Created task artifact with full AC, assumptions, risks, and out-of-scope.
- Evidence: `df/artifacts/SPIKE-001/task.md`
- Result: PASS — task ready for dev execution
- Next: Dev role executes research spike, produces Poland Template v1 document
- Risks/blockers: Public sources may be outdated; unknowns listed for human validation.

## 2026-05-23 08:50 local - State change

- Task: SPIKE-001
- From: READY_FOR_DEV
- To: DEV_IN_PROGRESS
- Role: dev
- Reason: Started implementation of the Poland research spike; verified repository status and public-source network access before collecting evidence.
- Evidence: `git --no-pager status --short`; `Invoke-WebRequest https://www.gov.pl/web/edukacja`; `df/artifacts/SPIKE-001/handoff-sa-to-dev.md`
- Next: Gather public-source evidence and draft `df/artifacts/SPIKE-001/poland-template-v1.md`

## 2026-05-23 08:50 local - dev - SPIKE-001

- State: DEV_IN_PROGRESS → READY_FOR_QA
- Action: Produced a source-backed Poland Template v1 research deliverable covering education levels, institution types, school/post-secondary grade scales, higher-education structure, school-year and semester assumptions, proposed common subjects, and explicit unknowns for validation.
- Evidence: `df/artifacts/SPIKE-001/poland-template-v1.md`; `df/artifacts/SPIKE-001/dev-notes.md`; `df/artifacts/SPIKE-001/handoffs.md`
- Result: PASS — research artifact ready for QA review
- Next: QA should verify source traceability, factual consistency, and that all 7 acceptance criteria are covered
- Risks/blockers: Higher-education grading is institution-specific rather than a single national scale; exact yearly school dates and voivodeship winter-break mapping still require annual validation.

## 2026-05-23 08:50 local - State change

- Task: SPIKE-001
- From: DEV_IN_PROGRESS
- To: READY_FOR_QA
- Role: dev
- Reason: Development/research implementation and dev validation are complete; artifact package and QA handoff were prepared.
- Evidence: `df/artifacts/SPIKE-001/poland-template-v1.md`; `df/artifacts/SPIKE-001/dev-notes.md`; `df/artifacts/SPIKE-001/handoffs.md`; `df/artifacts/SPIKE-001/task.md`
- Next: QA reviews evidence, source traceability, assumptions, and acceptance-criteria coverage

## 2026-05-23 local - State change

- Task: SPIKE-001
- From: READY_FOR_QA
- To: QA_IN_PROGRESS
- Role: qa
- Reason: Starting QA verification of Poland Template v1 research deliverable.
- Evidence: `df/artifacts/SPIKE-001/handoffs.md`; `df/artifacts/SPIKE-001/poland-template-v1.md`
- Next: Verify all 7 acceptance criteria, source traceability, assumption/fact separation, and framework guardrail compliance.

## 2026-05-23 local - qa - SPIKE-001

- State: QA_IN_PROGRESS → READY_FOR_PO
- Action: Reviewed `poland-template-v1.md` against all 7 acceptance criteria. Verified source traceability (14 sources, SRC-01 to SRC-14). Confirmed assumptions/facts separation throughout the document. Confirmed framework guardrail compliance (data-only, no code/schema/structure changes). Checked 7 test cases; all passed. Documented 5 residual risks for PO awareness.
- Evidence: `df/artifacts/SPIKE-001/qa-report.md`
- Result: PASS — no defects, all acceptance criteria met
- Next: PO reviews Poland Template v1 research content for business intent alignment and accepts or provides feedback.
- Risks/blockers: HE grading scale unconfirmed nationally; school-year dates must remain versioned; voivodeship winter breaks need region-aware configuration; preschool taxonomy needs PO decision; vocational qualification catalog needs follow-up spike.

## 2026-05-23 09:42 local - State change

- Task: TASK-002
- From: READY_FOR_DEV
- To: DEV_IN_PROGRESS
- Role: dev
- Reason: Explicit user request required a strict framework rule forbidding country-specific code/structure/schema changes; the request was clear enough to skip refinement.
- Evidence: `df/artifacts/TASK-002/task.md`; `git --no-pager status --short`
- Next: Update global guidance, architecture guidance, runtime decision tracking, and the Poland template artifact

## 2026-05-23 09:42 local - dev - TASK-002

- State: DEV_IN_PROGRESS → READY_FOR_QA
- Action: Added a strict architecture guardrail that country templates are data-only and may not drive country-specific framework code, structure, schema, or API changes. Updated the Poland template artifact to reinforce the rule and recorded a formal decision.
- Evidence: `AGENTS.md`; `df/backlog/architecture-direction.md`; `df/runtime/decisions.md`; `df/artifacts/SPIKE-001/poland-template-v1.md`; `df/artifacts/TASK-002/decision-001-no-country-specific-code.md`; `df/artifacts/TASK-002/dev-notes.md`; `df/artifacts/TASK-002/handoffs.md`
- Result: PASS — rule documented and ready for QA verification
- Next: QA should confirm the rule is unambiguous, global, and consistent across artifacts
- Risks/blockers: Historical archive notes may still mention Poland as a reference country, but active framework guidance now makes the data-only rule explicit.

## 2026-05-23 09:42 local - State change

- Task: TASK-002
- From: DEV_IN_PROGRESS
- To: READY_FOR_QA
- Role: dev
- Reason: Documentation updates and validation are complete; QA handoff has been prepared.
- Evidence: `df/artifacts/TASK-002/task.md`; `df/artifacts/TASK-002/decision-001-no-country-specific-code.md`; `df/artifacts/TASK-002/dev-notes.md`; `df/artifacts/TASK-002/handoffs.md`
- Next: QA reviews the strict rule wording and cross-document consistency

## 2026-05-23 09:43 local - State change

- Task: TASK-002
- From: READY_FOR_QA
- To: QA_IN_PROGRESS
- Role: qa
- Reason: Started QA verification of the new architecture guardrail.
- Evidence: `df/runtime/board.md`
- Next: Check acceptance criteria and verify changed files.

## 2026-05-23 09:45 local - qa - TASK-002

- State: QA_IN_PROGRESS → READY_FOR_PO
- Action: QA passed TASK-002 verification. Evaluated explicitly required texts prohibiting country-specific framework structure and verified the data-only directive is present and strictly applied in `AGENTS.md`, architecture directions, decisions log, and the Poland template itself.
- Evidence: `df/artifacts/TASK-002/qa-report.md`; `df/runtime/board.md`
- Result: PASS — Acceptance criteria fully met
- Next: PO begins verifying TASK-002 and optionally QA takes SPIKE-001. Awaiting human to create a new session.

## 2026-05-23 09:55 local - State change

- Task: SPIKE-001
- From: READY_FOR_PO
- To: PO_REVIEW
- Role: po
- Reason: Started PO validation of the Poland Template v1 research deliverable against the task acceptance criteria, QA report, epic intent, and initial prompt requirements.
- Evidence: `df/artifacts/SPIKE-001/task.md`; `df/artifacts/SPIKE-001/qa-report.md`; `df/artifacts/SPIKE-001/dev-notes.md`; `df/artifacts/SPIKE-001/poland-template-v1.md`; `df\backlog\epics.md`; `df\backlog\final-initial-prompt.md`
- Next: Complete product review and accept or reject the deliverable with evidence

## 2026-05-23 09:55 local - po - SPIKE-001

- State: PO_REVIEW → DONE
- Action: Completed product review of the Poland Template v1 research artifact. Confirmed the deliverable aligns with the original Poland MVP reference-template intent, stays within the data-only framework guardrail, covers all 7 acceptance criteria, and leaves unresolved country-specific unknowns explicitly listed for later validation rather than hidden in implementation assumptions.
- Evidence: `df/artifacts/SPIKE-001/po-review.md`; `df/artifacts/SPIKE-001/poland-template-v1.md`; `df/artifacts/SPIKE-001/qa-report.md`; `df/artifacts/SPIKE-001/task.md`
- Result: PASS — ACCEPTED
- Next: New session required. Next actionable work is `TASK-002` in `READY_FOR_PO` for the `po` role.

## 2026-05-23 - State change

- Task: TASK-002
- From: READY_FOR_PO
- To: PO_REVIEW
- Role: po
- Reason: Starting PO validation of the no-country-specific-code rule deliverable.
- Evidence: `df/artifacts/TASK-002/task.md`; `df/artifacts/TASK-002/qa-report.md`; `df/artifacts/TASK-002/dev-notes.md`; `df/artifacts/TASK-002/decision-001-no-country-specific-code.md`
- Next: Verify acceptance criteria and accept or reject.

## 2026-05-23 - po - TASK-002

- State: PO_REVIEW → DONE
- Action: Completed product review of the no-country-specific-code architecture guardrail. Verified the rule is embedded at three authority tiers: universal guidance (AGENTS.md), architecture backlog (architecture-direction.md), and the runtime decision log (decisions.md). Poland template artifact also updated (SPIKE-001). Decision record formally documents rationale, alternatives, and consequences.
- Evidence: `df/artifacts/TASK-002/po-review.md`; `df/artifacts/TASK-002/decision-001-no-country-specific-code.md`; `df/artifacts/TASK-002/qa-report.md`
- Result: PASS — ACCEPTED
- Next: No remaining actionable tasks on the board. Factory is idle until new tasks are added.
- Risks accepted: Implementation-time Code Review remains the required complementary control; documentation alone cannot guarantee enforcement.

## 2026-05-23 local - po - EPIC-22 backlog addition

- State: OPEN → DRAFT (backlog)
- Action: Added EPIC-22 (Internationalisation / i18n) to the backlog per explicit PO/user request. Epic captures: database-backed translation storage, no language-specific code invariant, global language catalogue (200 + languages), country-level active-language subset via config, RTL/LTR driven by data, per-user language preference, translation management API, and admin UI. Six user stories (STORY-220 – STORY-225) created with full acceptance criteria.
- Evidence: `df/backlog/epics.md` (EPIC-22 section); `df/backlog/user-stories.md` (STORY-220 through STORY-225)
- Result: PASS — epic and stories added to backlog
- Next: Stories are in DRAFT. SA refinement and scheduling into a sprint required before dev can start.
- Risks/blockers: BCP 47 code assignment for minority/regional languages (e.g. Cornish, Saterland Frisian) may require research; fallback chain depth needs architecture decision; translation cache invalidation strategy to be confirmed during STORY-220 design.

