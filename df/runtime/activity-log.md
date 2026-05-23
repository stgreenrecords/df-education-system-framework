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

## 2026-05-23 10:18 local - State change

- Task: STORY-220
- From: OPEN
- To: NEEDS_ARCHITECTURE
- Role: sa
- Reason: The user started work and the runtime board had no active tasks; STORY-220 is the highest-priority documented i18n backlog item and dependency root for later EPIC-22 stories. Refinement was skipped because the backlog story already has explicit, testable acceptance criteria.
- Evidence: `df/backlog/user-stories.md`; `df/backlog/epics.md`; `df/artifacts/STORY-220/task.md`
- Next: SA documents architecture for translation persistence, fallback, caching, and audit.

## 2026-05-23 10:18 local - State change

- Task: STORY-220
- From: NEEDS_ARCHITECTURE
- To: ARCHITECTURE_IN_PROGRESS
- Role: sa
- Reason: STORY-220 affects database schema, fallback behavior, cache invalidation, audit behavior, and framework invariants, so architecture is required before Dev.
- Evidence: `df/artifacts/STORY-220/task.md`
- Next: Produce `df/artifacts/STORY-220/solution-design.md`.

## 2026-05-23 10:18 local - sa - STORY-220

- State: ARCHITECTURE_IN_PROGRESS -> READY_FOR_DEV
- Action: Created the STORY-220 runtime artifact, solution design, architecture decision record, and SA-to-Dev handoff. Confirmed the design preserves the no-country-specific-code and no-language-specific-code invariants by using data-driven BCP 47 language codes, country configuration for defaults, and generic fallback logic.
- Evidence: `df/artifacts/STORY-220/task.md`; `df/artifacts/STORY-220/solution-design.md`; `df/artifacts/STORY-220/decision-002-i18n-storage-cache-fallback.md`; `df/artifacts/STORY-220/handoffs.md`; `df/runtime/board.md`; `df/runtime/decisions.md`; `df/runtime/risks.md`
- Result: PASS
- Next: New session required. Dev implements STORY-220 and records implementation/test evidence.

## 2026-05-23 10:18 local - State change

- Task: STORY-220
- From: ARCHITECTURE_IN_PROGRESS
- To: READY_FOR_DEV
- Role: sa
- Reason: Solution design and architecture decision are complete, with no blocking questions.
- Evidence: `df/artifacts/STORY-220/solution-design.md`; `df/artifacts/STORY-220/handoffs.md`
- Next: Dev implements translation persistence, fallback, cache invalidation, and audit per the solution design.

## 2026-05-23 10:27 local - State change

- Task: STORY-220
- From: READY_FOR_DEV
- To: DEV_IN_PROGRESS
- Role: dev
- Reason: Started Dev execution for the highest-priority actionable runtime task.
- Evidence: `df/runtime/board.md`; `df/artifacts/STORY-220/task.md`; `df/artifacts/STORY-220/solution-design.md`; `git --no-pager status --short`
- Next: Inspect application source, build files, migrations, and test structure before implementation.

## 2026-05-23 10:27 local - State change

- Task: STORY-220
- From: DEV_IN_PROGRESS
- To: BLOCKED
- Role: dev
- Reason: The repository has no Spring Boot application source tree, build file, database migration framework, PostgreSQL configuration, or test harness. STORY-220 depends on `STORY-010` and `STORY-011`, which are backlog items but not implemented runtime tasks.
- Evidence: `rg --files`; `Get-ChildItem -Recurse -Directory -Force | Where-Object { $_.Name -in @('src','main','test') -or $_.Name -match 'gradle|maven|java|kotlin' }`; `rg -n "STORY-010|STORY-011|Spring Boot|Flyway|PostgreSQL" df README.md`; `df/artifacts/STORY-220/dev-notes.md`
- Next: Human/factory should complete or promote `STORY-010` and `STORY-011`, or provide the existing application codebase, before resuming STORY-220 Dev work.

## 2026-05-23 10:27 local - dev - STORY-220

- State: BLOCKED
- Action: Completed Dev feasibility inspection and documented that implementation cannot safely proceed without the foundational application and database migration substrate.
- Evidence: `df/artifacts/STORY-220/dev-notes.md`; `df/artifacts/STORY-220/handoffs.md`; `df/runtime/board.md`; `df/runtime/risks.md`
- Result: BLOCKED
- Next: New session required after dependency work is available; recommended next role is SA/factory to promote and sequence `STORY-010` and `STORY-011`.
- Risks/blockers: `BLOCKER-014`

## 2026-05-23 10:31 local - State change

- Task: STORY-010
- From: OPEN
- To: NEEDS_ARCHITECTURE
- Role: sa
- Reason: `STORY-220` is blocked by missing application foundation. `STORY-010` is the highest-priority root dependency and has explicit, testable backlog acceptance criteria.
- Evidence: `df/backlog/user-stories.md`; `df/artifacts/STORY-010/task.md`
- Next: SA documents architecture for project structure, build system, module boundaries, and test baseline.

## 2026-05-23 10:31 local - State change

- Task: STORY-010
- From: NEEDS_ARCHITECTURE
- To: ARCHITECTURE_IN_PROGRESS
- Role: sa
- Reason: The task defines system structure, module boundaries, build tooling, and baseline tests, so architecture is required before Dev.
- Evidence: `df/artifacts/STORY-010/task.md`
- Next: Produce `df/artifacts/STORY-010/solution-design.md`.

## 2026-05-23 10:31 local - sa - STORY-010

- State: ARCHITECTURE_IN_PROGRESS -> READY_FOR_DEV
- Action: Promoted the Spring Boot foundation story from backlog to runtime, skipped refinement because acceptance criteria are explicit, selected Java 21 + Spring Boot + Gradle Kotlin DSL multi-module modular monolith, and prepared Dev handoff.
- Evidence: `df/artifacts/STORY-010/task.md`; `df/artifacts/STORY-010/solution-design.md`; `df/artifacts/STORY-010/decision-003-spring-boot-foundation-build.md`; `df/artifacts/STORY-010/handoffs.md`; `df/runtime/board.md`; `df/runtime/decisions.md`
- Result: PASS
- Next: New session required. Dev implements `STORY-010` and records build/test evidence.
- Risks/blockers: JDK 21 availability and Gradle wrapper network access are Dev environment risks; `STORY-011` remains unpromoted until `STORY-010` progresses.

## 2026-05-23 10:31 local - State change

- Task: STORY-010
- From: ARCHITECTURE_IN_PROGRESS
- To: READY_FOR_DEV
- Role: sa
- Reason: Solution design and architecture decision are complete, with no blocking questions.
- Evidence: `df/artifacts/STORY-010/solution-design.md`; `df/artifacts/STORY-010/handoffs.md`
- Next: Dev implements the Spring Boot modular project foundation in a new session.

## 2026-05-23 10:36 local - sa - STORY-010

- State: READY_FOR_DEV
- Action: Updated the approved architecture guidance from Gradle Kotlin DSL to Maven per explicit human preference. Adjusted task assumptions, solution design, decision record, runtime board, runtime decisions, and Dev handoff instructions.
- Evidence: `df/artifacts/STORY-010/task.md`; `df/artifacts/STORY-010/solution-design.md`; `df/artifacts/STORY-010/decision-003-spring-boot-foundation-build.md`; `df/artifacts/STORY-010/handoffs.md`; `df/runtime/board.md`; `df/runtime/decisions.md`
- Result: PASS
- Next: New session required. Dev implements `STORY-010` using Maven multi-module Spring Boot scaffold and records build/test evidence.
- Risks/blockers: JDK 21 availability and Maven wrapper network access are Dev environment risks; `STORY-011` remains unpromoted until `STORY-010` progresses.

## 2026-05-23 10:43 local - sa - TASK-003

- State: ARCHITECTURE_REVIEW
- Action: Evaluated containerization timing, Podman usage, cloud portability across AWS/Azure/Google Cloud, and infrastructure-as-code needs. Recommended treating containerization as a Phase 1 foundation concern, adding a Podman-compatible OCI baseline after `STORY-010`/`STORY-011`, then adding Kubernetes/OpenTofu-compatible deployment baseline before major feature work.
- Evidence: `df/artifacts/TASK-003/task.md`; `df/artifacts/TASK-003/containerization-stage-evaluation.md`; `df/artifacts/TASK-003/decision-004-containerization-phase-1.md`; `df/artifacts/TASK-003/handoffs.md`; `df/backlog/user-stories.md`; `df/backlog/architecture-direction.md`; `df/backlog/roadmap.md`; `df/runtime/decisions.md`; `df/runtime/risks.md`
- Result: PASS
- Next: New session required. Dev continues `STORY-010` with Maven while keeping the scaffold container-ready; SA/factory later promotes `STORY-011`, `STORY-022`, and `STORY-023`.
- Risks/blockers: Provider-specific IaC cannot be fully identical across clouds; keep application code and container image portable while isolating provider differences in IaC modules.

## 2026-05-23 10:47 local - State change

- Task: STORY-010
- From: READY_FOR_DEV
- To: DEV_IN_PROGRESS
- Role: dev
- Reason: Started Dev execution for the Maven multi-module Spring Boot foundation story.
- Evidence: `df/runtime/board.md`; `df/artifacts/STORY-010/task.md`; `df/artifacts/STORY-010/solution-design.md`; `git --no-pager status --short`; `rg --files`
- Next: Implement Maven scaffold, add sample Spring Boot integration test, run build/test validation, and prepare QA handoff.

## 2026-05-23 10:52 local - State change

- Task: STORY-010
- From: DEV_IN_PROGRESS
- To: READY_FOR_QA
- Role: dev
- Reason: Maven multi-module Spring Boot scaffold implementation and Dev validation are complete.
- Evidence: `pom.xml`; `.mvn/wrapper/maven-wrapper.properties`; `mvnw`; `mvnw.cmd`; `common/`; `identity-access/`; `organization/`; `school-pack/`; `attendance/`; `gradebook/`; `meal-catering/`; `platform-core/`; `df/artifacts/STORY-010/dev-notes.md`; `df/artifacts/STORY-010/handoffs.md`
- Next: QA verifies structure, build reproducibility, and integration test evidence in a new session.

## 2026-05-23 10:52 local - dev - STORY-010

- State: READY_FOR_QA
- Action: Implemented Java 21 Spring Boot 4.0.6 Maven reactor scaffold with required modules, wrapper files, module marker classes, platform application entry point, and sample Spring context integration test.
- Evidence: `df/artifacts/STORY-010/dev-notes.md`; `df/artifacts/STORY-010/handoffs.md`; `.\mvnw.cmd clean verify`
- Result: PASS - wrapper build succeeded with all 9 reactor projects successful and integration tests 1 run, 0 failures, 0 errors, 0 skipped. Validation required a temporary trust store because this workstation's Java/Maven trust store could not validate Maven Central certificates.
- Next: New session required. QA reviews `STORY-010` and either passes to PO or reports defects.
- Risks/blockers: `RISK-016`

## 2026-05-23 11:02 local - State change

- Task: STORY-010
- From: READY_FOR_QA
- To: QA_IN_PROGRESS
- Role: qa
- Reason: Started QA review after explicit human rework feedback changed the expected project separation structure.
- Evidence: `df/runtime/board.md`; `df/artifacts/STORY-010/handoffs.md`; human rework request from 2026-05-23
- Next: Document defect and return to Dev.

## 2026-05-23 11:02 local - State change

- Task: STORY-010
- From: QA_IN_PROGRESS
- To: QA_FAILED
- Role: qa
- Reason: Current scaffold lacks required independent backend, frontend, and DevOps Maven project boundaries under one parent.
- Evidence: `df/artifacts/STORY-010/qa-report.md`; `df/artifacts/STORY-010/defects.md`; `pom.xml`
- Next: Return to Dev for structural rework.

## 2026-05-23 11:02 local - State change

- Task: STORY-010
- From: QA_FAILED
- To: RETURNED_TO_DEV
- Role: qa
- Reason: Rework is required to support backend-only, frontend-only, DevOps-only, and all-together build/deploy paths.
- Evidence: `df/artifacts/STORY-010/defects.md`; `df/artifacts/STORY-010/handoffs.md`; `df/runtime/board.md`
- Next: New session required. Dev reworks Maven project separation and records validation evidence.

## 2026-05-23 11:02 local - qa - STORY-010

- State: RETURNED_TO_DEV
- Action: Rejected the current `STORY-010` scaffold based on human rework request. Created QA report and defect requiring a parent Maven project that unites three independent project areas: backend, frontend, and DevOps.
- Evidence: `df/artifacts/STORY-010/qa-report.md`; `df/artifacts/STORY-010/defects.md`; `df/artifacts/STORY-010/handoffs.md`
- Result: FAIL - returned to Dev
- Next: New session required. Dev implements the requested project separation.
- Risks/blockers: None.

## 2026-05-23 11:10 local - State change

- Task: STORY-010
- From: RETURNED_TO_DEV
- To: DEV_IN_PROGRESS
- Role: dev
- Reason: Started Dev rework for QA defect requiring a parent Maven project with independent backend, frontend, and DevOps Maven project boundaries and separate/all build paths.
- Evidence: `df/runtime/board.md`; `df/artifacts/STORY-010/task.md`; `df/artifacts/STORY-010/defects.md`; `git --no-pager status --short`
- Next: Rework Maven structure, preserve backend modules, add frontend/devops Maven scaffolds, and validate targeted/full builds.

## 2026-05-23 11:13 local - State change

- Task: STORY-010
- From: DEV_IN_PROGRESS
- To: READY_FOR_QA
- Role: dev
- Reason: Dev rework completed; scaffold now has a root parent plus independent backend, frontend, and DevOps Maven project areas with targeted and full build paths validated.
- Evidence: `pom.xml`; `backend/pom.xml`; `frontend/pom.xml`; `devops/pom.xml`; `README.md`; `df/artifacts/STORY-010/dev-notes.md`; `df/artifacts/STORY-010/defects.md`; `df/artifacts/STORY-010/handoffs.md`
- Next: QA verifies structure and validation evidence in a new session.

## 2026-05-23 11:13 local - dev - STORY-010

- State: READY_FOR_QA
- Action: Reworked the Maven scaffold into one root parent with three independent project areas: backend, frontend, and DevOps. Preserved the backend Spring Boot modules and sample integration test under `backend/`.
- Evidence: `.\mvnw.cmd -f backend/pom.xml clean verify`; `.\mvnw.cmd -f frontend/pom.xml clean verify`; `.\mvnw.cmd -f devops/pom.xml clean verify`; `.\mvnw.cmd clean verify`; `df/artifacts/STORY-010/dev-notes.md`
- Result: PASS - targeted backend/frontend/devops builds and full parent build passed with the documented temporary Maven trust-store workaround. Backend and full builds ran 1 integration test with 0 failures, 0 errors, 0 skipped.
- Next: New session required. QA reviews `STORY-010` rework and either passes to PO or reports remaining defects.
- Risks/blockers: Local Java/Maven trust-store issue remains environmental; frontend and DevOps are intentionally minimal scaffolds until their implementation stories.

## 2026-05-23 11:25 local - State change

- Task: TASK-004
- From: OPEN
- To: NEEDS_ARCHITECTURE
- Role: sa
- Reason: Explicit user request requires a Dark Factory framework process change that affects role model, orchestration, runtime boards, and documentation ownership.
- Evidence: `df/artifacts/TASK-004/task.md`
- Next: SA documents the lane-based implementation model and required framework changes.

## 2026-05-23 11:25 local - State change

- Task: TASK-004
- From: NEEDS_ARCHITECTURE
- To: ARCHITECTURE_IN_PROGRESS
- Role: sa
- Reason: The task changes SDLC architecture and must define safe routing for backend, frontend, and DevOps parallel work.
- Evidence: `df/artifacts/TASK-004/task.md`; `df/artifacts/TASK-004/solution-design.md`
- Next: Update framework instructions, role files, runtime subdashboards, and decision/risk records.

## 2026-05-23 11:25 local - sa - TASK-004

- State: ARCHITECTURE_IN_PROGRESS -> READY_FOR_QA
- Action: Split Dark Factory implementation ownership into `backend-dev`, `frontend-dev`, and `devops`; added separate subdashboards; documented lane routing, parallel-work rules, and lane-owned artifact folders so independent developers can update only their own implementation docs.
- Evidence: `AGENTS.md`; `df/00-start-here.md`; `df/01-operating-model.md`; `df/02-state-machine.md`; `df/03-orchestration-rules.md`; `df/04-documentation-standards.md`; `df/roles/backend-dev.md`; `df/roles/frontend-dev.md`; `df/roles/devops.md`; `df/runtime/backend-dev-board.md`; `df/runtime/frontend-dev-board.md`; `df/runtime/devops-board.md`; `df/artifacts/TASK-004/solution-design.md`; `df/artifacts/TASK-004/decision-005-development-lane-split.md`
- Result: PASS - framework documentation updated and ready for QA verification.
- Next: New session required. QA verifies the role split, subdashboard routing, and documentation ownership rules before PO review.
- Risks/blockers: Existing active tasks may still mention the retired generic `dev` owner until completed or explicitly migrated; root build/CI files remain shared and require SA sequencing when touched by multiple lanes.

## 2026-05-23 11:25 local - State change

- Task: TASK-004
- From: ARCHITECTURE_IN_PROGRESS
- To: READY_FOR_QA
- Role: sa
- Reason: SA-owned framework documentation changes and runtime artifacts are complete; QA must independently verify consistency and completeness.
- Evidence: `df/artifacts/TASK-004/handoffs.md`; `df/runtime/board.md`; `df/runtime/decisions.md`; `df/runtime/risks.md`
- Next: QA reviews TASK-004 in a new session.

## 2026-05-23 11:25 local - sa - TASK-004

- State: READY_FOR_QA
- Action: Ran documentation consistency checks for stale generic developer instructions and verified role/subdashboard references are wired through entrypoints, role docs, templates, and runtime files.
- Evidence: `rg -n "\bDev must|\bDev should|developer output|dev handoff|Dev -> QA|SA -> Dev|single developer" AGENTS.md CLAUDE.md JETBRAINS_AI.md df/00-start-here.md df/01-operating-model.md df/02-state-machine.md df/03-orchestration-rules.md df/04-documentation-standards.md df/roles df/templates`; `rg -n "backend-dev-board|frontend-dev-board|devops-board|df/roles/backend-dev.md|df/roles/frontend-dev.md|df/roles/devops.md" AGENTS.md df CLAUDE.md JETBRAINS_AI.md`; `df/artifacts/TASK-004/handoffs.md`
- Result: PASS - active framework instructions no longer route new work to one generic developer role.
- Next: New session required. QA independently verifies TASK-004.

## 2026-05-23 11:38 local - sa - TASK-004

- State: READY_FOR_QA
- Action: Added SA addendum for frontend project separation. The frontend lane now has three independent project scopes: `frontend/website`, `frontend/android`, and `frontend/ios`; website uses Next.js + React. Added backlog story `STORY-014` for future `frontend-dev` implementation.
- Evidence: `AGENTS.md`; `df/01-operating-model.md`; `df/03-orchestration-rules.md`; `df/04-documentation-standards.md`; `df/roles/frontend-dev.md`; `df/runtime/frontend-dev-board.md`; `df/backlog/architecture-direction.md`; `df/backlog/user-stories.md`; `df/backlog/open-questions.md`; `README.md`; `df/artifacts/TASK-004/decision-006-frontend-project-split.md`; `df/artifacts/TASK-004/handoffs.md`; `df/artifacts/STORY-010/solution-design.md`
- Result: PASS - architecture and routing docs updated; no application code was changed.
- Next: New session required. QA verifies TASK-004 including frontend website/Android/iOS project independence guidance.
- Risks/blockers: Actual Next.js, Android, and iOS scaffolds remain future `frontend-dev` work; cross-platform sharing must be explicit to avoid hidden coupling.

## 2026-05-23 11:46 local - sa - TASK-004

- State: READY_FOR_QA
- Action: Applied priority clarification that mobile applications are last priority. `STORY-014` now focuses on the Next.js + React website foundation, while Android and iOS foundations are split into low-priority later stories `STORY-015` and `STORY-016`.
- Evidence: `AGENTS.md`; `df/01-operating-model.md`; `df/03-orchestration-rules.md`; `df/roles/frontend-dev.md`; `df/runtime/frontend-dev-board.md`; `df/backlog/architecture-direction.md`; `df/backlog/user-stories.md`; `df/backlog/open-questions.md`; `README.md`; `df/artifacts/TASK-004/decision-007-mobile-last-priority.md`; `df/artifacts/TASK-004/handoffs.md`; `df/runtime/board.md`; `df/runtime/decisions.md`; `df/runtime/risks.md`
- Result: PASS - frontend sequencing is now website first, mobile last unless explicitly promoted.
- Next: New session required. QA verifies TASK-004 including mobile last-priority guidance.
- Risks/blockers: Mobile project scopes remain part of target architecture but should not preempt website frontend work.

## 2026-05-23 11:50 local - State change

- Task: STORY-010
- From: READY_FOR_QA
- To: QA_IN_PROGRESS
- Role: qa
- Reason: Started QA verification of the reworked Maven scaffold, including parent/backend/frontend/devops separation and targeted/full build evidence.
- Evidence: `df/artifacts/STORY-010/task.md`; `df/artifacts/STORY-010/dev-notes.md`; `df/artifacts/STORY-010/handoffs.md`; `df/artifacts/STORY-010/defects.md`; `git --no-pager status --short`
- Next: Run structure checks, targeted Maven builds, full Maven build, and update QA report.

## 2026-05-23 11:51 local - State change

- Task: STORY-010
- From: QA_IN_PROGRESS
- To: READY_FOR_PO
- Role: qa
- Reason: QA verification passed for the reworked Maven scaffold and prior defect retest.
- Evidence: `df/artifacts/STORY-010/qa-report.md`; `df/artifacts/STORY-010/defects.md`; `df/artifacts/STORY-010/handoffs.md`
- Next: PO reviews STORY-010 business acceptance in a new session.

## 2026-05-23 11:51 local - qa - STORY-010

- State: READY_FOR_PO
- Action: Verified the root parent plus independent backend/frontend/devops Maven project areas, required backend modules, four build paths, Spring Boot integration test, and absence of country/language/database scope leakage.
- Evidence: `java -version`; `Select-String -Path pom.xml,backend/pom.xml,frontend/pom.xml,devops/pom.xml -Pattern '<module>|<artifactId>|<packaging>|<java.version>|spring-boot'`; `rg --files backend frontend devops | Sort-Object`; `rg -n "Poland|Polish|pl-|country|language|locale|BCP|postgres|flyway|liquibase|jdbc|datasource" backend frontend devops pom.xml README.md`; `.\mvnw.cmd -f backend/pom.xml clean verify`; `.\mvnw.cmd -f frontend/pom.xml clean verify`; `.\mvnw.cmd -f devops/pom.xml clean verify`; `.\mvnw.cmd clean verify`; `df/artifacts/STORY-010/qa-report.md`
- Result: PASS - backend-only, frontend-only, DevOps-only, and full parent builds passed; integration test ran with 1 test, 0 failures, 0 errors, 0 skipped.
- Next: New session required. PO reviews and accepts or rejects STORY-010.
- Risks/blockers: Maven commands used the documented temporary trust-store workaround; Mockito dynamic-agent warnings remain informational; frontend and DevOps are scaffolds only until future implementation stories.

## 2026-05-23 11:55 local - State change

- Task: STORY-010
- From: READY_FOR_PO
- To: PO_REVIEW
- Role: po
- Reason: Started PO validation of the reworked Maven parent/backend/frontend/devops scaffold against acceptance criteria, QA evidence, and product intent.
- Evidence: `df/artifacts/STORY-010/task.md`; `df/artifacts/STORY-010/qa-report.md`; `df/artifacts/STORY-010/dev-notes.md`; `df/artifacts/STORY-010/handoffs.md`
- Next: Complete product review and accept or reject the scaffold.

## 2026-05-23 11:56 local - State change

- Task: STORY-010
- From: PO_REVIEW
- To: DONE
- Role: po
- Reason: PO accepted the Maven scaffold after confirming it satisfies the foundation story, the independent backend/frontend/devops rework requirement, QA evidence, and a full parent build verification.
- Evidence: `df/artifacts/STORY-010/po-review.md`; `df/artifacts/STORY-010/qa-report.md`; `.\mvnw.cmd clean verify`
- Next: New session required. QA should review `TASK-004` in `READY_FOR_QA`, or later SA/factory should promote `STORY-011` to continue unblocking `STORY-220`.

## 2026-05-23 11:56 local - po - STORY-010

- State: DONE
- Action: Completed PO product review and accepted the reworked Maven scaffold.
- Evidence: `df/artifacts/STORY-010/po-review.md`; `df/artifacts/STORY-010/handoffs.md`; `df/runtime/board.md`; `df/runtime/risks.md`
- Result: PASS - full parent Maven build succeeded across 12 projects and `EducationSystemApplicationIT` ran 1 test with 0 failures, 0 errors, 0 skipped.
- Next: New session required. Next actionable task is `TASK-004` in `READY_FOR_QA` for `qa`.
- Risks/blockers: Local Maven trust-store workaround remains an environment risk; `STORY-220` remains blocked until `STORY-011` provides database/migration foundation.

## 2026-05-23 12:05 local - State change

- Task: STORY-012
- From: OPEN
- To: NEEDS_ARCHITECTURE
- Role: sa
- Reason: The user selected the OpenAPI backlog story, `STORY-010` is accepted, and the story affects public backend API contract generation.
- Evidence: `df/backlog/user-stories.md`; `df/artifacts/STORY-012/task.md`; `df/runtime/board.md`
- Next: SA documents the OpenAPI generation architecture and lane routing.

## 2026-05-23 12:05 local - State change

- Task: STORY-012
- From: NEEDS_ARCHITECTURE
- To: ARCHITECTURE_IN_PROGRESS
- Role: sa
- Reason: The task affects public API documentation endpoints, backend dependencies, and integration-test strategy, so architecture is required before implementation.
- Evidence: `df/artifacts/STORY-012/task.md`
- Next: Produce `df/artifacts/STORY-012/solution-design.md`.

## 2026-05-23 12:05 local - State change

- Task: STORY-012
- From: ARCHITECTURE_IN_PROGRESS
- To: READY_FOR_DEV
- Role: sa
- Reason: Solution design, decision record, and backend lane routing are complete with no blocking questions.
- Evidence: `df/artifacts/STORY-012/solution-design.md`; `df/artifacts/STORY-012/decision-008-openapi-generation.md`; `df/artifacts/STORY-012/handoffs.md`; `df/runtime/backend-dev-board.md`
- Next: `backend-dev` implements Springdoc OpenAPI generation, `/api-docs`, Swagger UI, and backend integration tests in a new session.

## 2026-05-23 12:05 local - sa - STORY-012

- State: READY_FOR_DEV
- Action: Promoted OpenAPI contract generation from backlog, skipped refinement because acceptance criteria are explicit, completed architecture using Springdoc OpenAPI for Spring Boot 4, and routed the work to the `backend-dev` lane.
- Evidence: `df/artifacts/STORY-012/task.md`; `df/artifacts/STORY-012/solution-design.md`; `df/artifacts/STORY-012/decision-008-openapi-generation.md`; `df/artifacts/STORY-012/handoffs.md`; `df/runtime/board.md`; `df/runtime/backend-dev-board.md`; `df/runtime/decisions.md`; `df/runtime/risks.md`; https://springdoc.org/v4/
- Result: PASS
- Next: New session required. `backend-dev` moves `STORY-012` to `DEV_IN_PROGRESS` and implements the backend OpenAPI generation.
- Risks/blockers: `RISK-022`; later security policy for documentation endpoint exposure remains out of scope for this story.

## 2026-05-23 12:17 local - State change

- Task: TASK-005
- From: OPEN
- To: NEEDS_ARCHITECTURE
- Role: sa
- Reason: Explicit user request requires Dark Factory framework changes that affect role model, frontend gating, data responsibilities, runtime boards, and documentation standards.
- Evidence: `df/artifacts/TASK-005/task.md`
- Next: SA documents designer and data-engineer role architecture.

## 2026-05-23 12:17 local - State change

- Task: TASK-005
- From: NEEDS_ARCHITECTURE
- To: ARCHITECTURE_IN_PROGRESS
- Role: sa
- Reason: The task changes SDLC architecture and requires safe routing for UI design and country data population.
- Evidence: `df/artifacts/TASK-005/task.md`; `df/artifacts/TASK-005/solution-design.md`
- Next: Update framework instructions, role files, runtime boards, and decision/risk records.

## 2026-05-23 12:17 local - State change

- Task: TASK-005
- From: ARCHITECTURE_IN_PROGRESS
- To: READY_FOR_QA
- Role: sa
- Reason: SA-owned framework documentation changes are complete; QA must independently verify consistency and completeness.
- Evidence: `df/artifacts/TASK-005/task.md`; `df/artifacts/TASK-005/solution-design.md`; `df/artifacts/TASK-005/decision-009-designer-data-engineer-roles.md`; `df/artifacts/TASK-005/handoffs.md`
- Next: QA reviews TASK-005 in a new session.

## 2026-05-23 12:17 local - sa - TASK-005

- State: READY_FOR_QA
- Action: Added `designer` as a pre-frontend UI design gate and `data-engineer` as a source-backed data population lane. Updated role selection, state machine, orchestration, documentation standards, frontend-dev blockers, QA checks, runtime boards, decisions, risks, and adapter docs.
- Evidence: `AGENTS.md`; `df/00-start-here.md`; `df/01-operating-model.md`; `df/02-state-machine.md`; `df/03-orchestration-rules.md`; `df/04-documentation-standards.md`; `df/roles/designer.md`; `df/roles/data-engineer.md`; `df/roles/frontend-dev.md`; `df/roles/sa.md`; `df/roles/qa.md`; `df/roles/po.md`; `df/runtime/design-board.md`; `df/runtime/data-engineer-board.md`; `df/artifacts/TASK-005/handoffs.md`
- Result: PASS
- Next: New session required. QA verifies role consistency, frontend design gate enforcement, and data-engineering source/privacy rules.
- Risks/blockers: `RISK-023`; `RISK-024`

## 2026-05-23 12:17 local - sa - TASK-005

- State: READY_FOR_QA
- Action: Ran documentation consistency checks for stale three-lane/generic-dev language, new role/board/state wiring, data source/synthetic rules, and required file existence.
- Evidence: `rg -n 'three implementation|three required|six required|backend-dev, frontend-dev, or devops|implementation subdashboards|implementation lane|implementation lanes|implementation-lane|implementation-owned|return it to \`dev\`' AGENTS.md CLAUDE.md JETBRAINS_AI.md .github/copilot-instructions.md README.md df/00-start-here.md df/01-operating-model.md df/02-state-machine.md df/03-orchestration-rules.md df/04-documentation-standards.md df/roles df/templates df/runtime`; `rg -n 'designer|data-engineer|design-board|data-engineer-board|READY_FOR_DESIGN|DESIGN_IN_PROGRESS' ...`; `rg -n 'City, district, school, and subject names|city, district, school, and subject names|Teacher names, student names|teacher names, student names|fake/synthetic|synthetic' ...`; `Test-Path df/roles/designer.md; Test-Path df/roles/data-engineer.md; Test-Path df/runtime/design-board.md; Test-Path df/runtime/data-engineer-board.md; Test-Path df/artifacts/TASK-005/task.md`
- Result: PASS
- Next: New session required. QA independently verifies TASK-005 before PO review.
- Risks/blockers: None.

