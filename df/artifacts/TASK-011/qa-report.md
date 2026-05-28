# QA Report - TASK-011

## QA Result: PASS

- Task: `TASK-011`
- Acceptance criteria covered: Yes
- Unit tests: Not applicable — data-only task, no code changes delivered in this lane.
- Integration tests: Not applicable — backend schema/API and frontend integration are explicitly out of scope for this task.
- Manual checks: Completed by repository artifact inspection and dataset/header spot checks.
- Regression checks: Confirmed the task remains data-only and does not require backend/frontend/framework code changes.
- Risks: Runtime persistence and homepage selector behavior still require follow-up backend/frontend implementation after SA routing.
- Handoff: `READY_FOR_PO`

## Scope under QA

This QA pass covers the `data-engineer` lane output only:

- `data/list-of-schools-poland/institutions_pl_2026_05_26_country_agnostic.csv`
- `df/artifacts/TASK-011/data/data-notes.md`
- `df/artifacts/TASK-011/data/source-map.md`
- `df/artifacts/TASK-011/data/handoff-to-qa.md`
- `data/list-of-schools-poland/institution-directory-contract.md`

No backend schema/API or frontend UI behavior is included in this QA decision.

## Test cases

### TC-1 — Lane ownership and state routing
- Verified `TASK-011` is owned by `data-engineer` in `df/runtime/data-engineer-board.md` and was handed to QA from `READY_FOR_QA` on the main runtime board.
- Result: PASS

### TC-2 — Normalized dataset exists and exposes the required generic filter fields
- Verified the normalized dataset path is documented in both `data-notes.md` and `handoff-to-qa.md`.
- Verified the CSV header contains all required filter-ready generic fields by direct header inspection via repository search:
  - `institution_name`
  - `address_line`
  - `state_name`
  - `city_name`
  - `postcode`
  - `town_name`
  - `institution_type`
- Verified the full normalized contract header also includes:
  - `source_system`
  - `source_record_id`
  - `country_code`
  - `postal_place`
  - `public_status`
  - `operational_status`
  - `source_last_verified_at`
- Result: PASS

### TC-3 — Source traceability exists and includes retrieval date
- Verified `df/artifacts/TASK-011/data/source-map.md` links the normalized dataset back to `data/list-of-schools-poland/rspo_2026_05_26.csv`.
- Verified retrieval date `2026-05-26` is documented in the source map and data notes.
- Result: PASS

### TC-4 — Country-agnostic contract compliance
- Verified `data/list-of-schools-poland/institution-directory-contract.md` defines a generic institution schema and generic homepage filters.
- Verified `source-map.md` explicitly states that the normalization uses neutral target fields and does not introduce Poland-specific schema branches.
- Verified `data-notes.md` describes a reusable contract and keeps country-specific semantics inside source values instead of framework/schema changes.
- Result: PASS

### TC-5 — Data-only boundary and downstream handoff clarity
- Verified `data-notes.md` explicitly states that downstream backend/devops must handle persistence/bootstrap, API exposure, and indexing.
- Verified `handoff-to-qa.md` explicitly states that runtime DB persistence and homepage filter behavior still require follow-up implementation.
- Verified no backend/frontend/framework implementation evidence was required or claimed by this lane package.
- Result: PASS

## Evidence reviewed

- `df/runtime/board.md`
- `df/runtime/data-engineer-board.md`
- `df/artifacts/TASK-011/task.md`
- `df/artifacts/TASK-011/data/data-notes.md`
- `df/artifacts/TASK-011/data/source-map.md`
- `df/artifacts/TASK-011/data/handoff-to-qa.md`
- `data/list-of-schools-poland/institution-directory-contract.md`
- Header and sample-row inspection of `data/list-of-schools-poland/institutions_pl_2026_05_26_country_agnostic.csv`

## Notes on verification environment

- The IDE file-search tooling was sufficient to verify the normalized CSV header and sample records.
- The terminal tool returned stale output from an earlier shell session during attempts to collect additional numeric metrics, so QA relied on direct repository artifact inspection plus file-search evidence rather than shell-generated row-count output for this pass.
- This limitation does not block acceptance of the current lane task because the acceptance criteria are satisfied by the delivered dataset package, source traceability, and documented handoff.

