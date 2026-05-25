# Solution Design - STORY-031

## Summary

Extend the generic configuration engine in `backend/platform-core` with three backend-only capabilities: pre-write validation for blocked overrides, auditable inheritance-break request recording, and institution-scope compatibility reporting for ancestor configuration changes.

## Context

`STORY-030` established the first working configuration inheritance engine using generic tenant-rooted scope paths, persisted field metadata, and basic lock/merge behavior. The accepted decision explicitly deferred compatibility reporting and inheritance-break workflows to a later story. `STORY-031` is that follow-up.

The system still lacks a way to preview or surface lock violations cleanly, to record exceptional inheritance-break requests without silently bypassing guardrails, and to evaluate how ancestor-level changes would affect lower-scope overrides. Those capabilities now matter more because `STORY-040` has defined compatibility-checker expectations and `STORY-050` has formalized country-template versioning that will eventually rely on change-impact analysis.

## Requirements and acceptance criteria

- Return a validation error for locked-field override attempts
- Record inheritance-break requests with justification and audit trail
- Produce a compatibility report listing affected institutions when country/root configuration changes intersect institution overrides

## Proposed solution

Deliver this as a backend-only implementation routed to `backend-dev` in `backend/platform-core`.

### 1. Validation API and service behavior

Add an explicit validation/preview path on top of the existing configuration service so callers can detect blocked override attempts before or alongside writes.

Recommended shape:
- new request/response types for configuration validation
- backend endpoint such as `/api/v1/platform/configuration/validate`
- result should distinguish:
  - valid write
  - blocked by ancestor lock
  - missing field definition / invalid scope path / invalid value shape

The existing write path should continue to reject blocked overrides with a client-visible validation/conflict response, but the new explicit validation path gives QA/PO and later release tooling a stable way to evaluate config changes without mutating state.

### 2. Inheritance-break request recording

Add a new persistence model for **inheritance-break requests**, not automatic inheritance breaks.

Recommended stored fields:
- request id
- tenant id
- field key
- target scope path
- ancestor scope path being challenged
- proposed value (or value reference)
- justification
- requested by / request source when available
- status (`SUBMITTED` for this story; later approval states can extend it)
- timestamps

Recommended behavior:
- submitting a request does **not** bypass the current lock model automatically
- the request is stored as a traceable exception request
- submission writes through the shared audit foundation from `STORY-013`

Recommended endpoint:
- `/api/v1/platform/configuration/inheritance-break-requests`

This satisfies the story without inventing a full approval workflow too early.

### 3. Compatibility report for ancestor changes

Add a report-generation path that evaluates the impact of a proposed ancestor/root configuration update against existing descendant overrides.

Recommended behavior:
- input identifies the target field, current/proposed ancestor scope path, and proposed value
- service scans descendant configuration values for the same field
- for Phase 1, the report focuses on **institution-scope descendants** because the acceptance criteria explicitly call out affected institutions
- output includes:
  - impacted institution scope identifiers
  - whether the impact is informational, warning, or conflict
  - why the institution is affected (override exists, lock interaction, merge behavior difference, etc.)
  - suggested next action text for operators

Recommended endpoint:
- `/api/v1/platform/configuration/compatibility-report`

Because the organization module is unfinished, the report should stay generic and list institution scope ids/paths rather than requiring rich institution names from another bounded context.

## Files/components likely affected

- `df/artifacts/STORY-031/task.md`
- `df/artifacts/STORY-031/solution-design.md`
- `df/artifacts/STORY-031/decision-021-configuration-validation-and-impact-reporting.md`
- `df/artifacts/STORY-031/handoffs.md`
- `df/runtime/board.md`
- `df/runtime/backend-dev-board.md`
- `df/runtime/activity-log.md`
- `df/runtime/decisions.md`
- `backend/platform-core/src/main/java/com/darkfactory/education/platform/configuration/**`
- `backend/platform-core/src/main/resources/db/migration/**`
- `backend/platform-core/src/test/java/com/darkfactory/education/platform/**`

## Data/API contract changes

- Add new configuration validation request/response contract
- Add new inheritance-break request contract and persistence table
- Add new compatibility-report request/response contract
- Preserve existing generic scope-path model and existing `resolve` / `values` behavior

## Security/privacy considerations

- Inheritance-break requests must be auditable and must not silently bypass ancestor locks
- Compatibility reporting must reveal only generic scope identifiers needed for administrative analysis; avoid coupling to unrelated personal data
- All behavior must remain country-neutral and driven by generic configuration semantics

## Test strategy

`backend-dev` should provide:
- unit coverage for validation and impact-analysis logic
- integration coverage for:
  - locked-field validation failure
  - inheritance-break request persistence + audit event recording
  - compatibility report listing affected institution scope ids
  - `/api-docs` exposure for any new endpoints
- regression coverage proving existing configuration resolve/write behavior still works

QA should verify the new endpoints/contracts plus shared generic behavior.

## Risks and mitigations

- Risk: compatibility reporting may overreach into full organization modeling
  - Mitigation: keep Phase 1 output limited to generic institution scope ids/paths
- Risk: inheritance-break requests may be confused with approved exceptions
  - Mitigation: persist them with explicit request status only; do not auto-apply overrides
- Risk: validation could duplicate existing write-path logic inconsistently
  - Mitigation: centralize lock/field/scope validation in shared service methods reused by both preview and write flows

## Rollback plan

- Revert the new migration(s), request/response contracts, controller methods, and service/reporting logic
- Remove the new backend artifact notes for this story
- Return the story to `backend-dev` or `sa` if the implementation proves too tightly coupled to unfinished organization data

## Open questions

- Later stories can add approval/rejection workflows for inheritance-break requests; this story stops at traceable submission.
- Later organization work can enrich compatibility reports with canonical institution metadata without replacing the generic scope-id contract.

## SA decision

Approved for development: Yes — route to `backend-dev` as a backend-only implementation story.

