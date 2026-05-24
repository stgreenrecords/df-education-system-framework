# Solution Design - STORY-013

## Summary

Create a backend-only generic audit trail foundation in `backend/platform-core` that records immutable tenant-scoped audit events for meaningful state changes, exposes filtered query/export endpoints, and provides one reusable write path for current and later modules.

## Context

The architecture direction already requires that all meaningful state changes be auditable. `STORY-011` delivered the PostgreSQL/Flyway foundation, `STORY-021` established the sovereign deployment tenant context, `STORY-220` introduced a temporary translation-specific audit bridge, and `STORY-030` added new mutable configuration behavior that will also need durable auditability. The next missing Phase 1 foundation is a generic audit subsystem so modules do not keep inventing feature-specific audit tables and write logic.

The first implementation should stay small and backend-only: one generic append-only audit model, one write service, filtered read/export contracts, and at least one real platform-core integration path proving that the audit foundation is not hypothetical.

## Requirements and acceptance criteria

- Create an audit record with actor, timestamp, entity, action, old value, and new value when a meaningful entity change is saved
- Ensure audit records cannot be modified or deleted through the application
- Allow admin-oriented audit log review with filtering by entity type, actor, and time range
- Allow export of audit data with the fields needed for compliance review

## Proposed solution

Implement the first platform audit foundation under a new package such as `com.darkfactory.education.platform.audit` inside `backend/platform-core`.

### 1. Generic append-only audit event model

Add one generic audit table, for example `audit_event`, with fields similar to:

- `id` UUID primary key
- `tenant_id` UUID not null referencing `platform_tenant(tenant_id)`
- `entity_type` varchar not null
- `entity_id` varchar not null
- `action_type` varchar not null
- `actor` varchar not null
- `occurred_at` timestamptz not null
- `old_value_json` jsonb null
- `new_value_json` jsonb null
- `metadata_json` jsonb null

Recommended indexes:

- `(tenant_id, occurred_at desc)` for timeline queries
- `(tenant_id, entity_type, occurred_at desc)` for entity filtering
- `(tenant_id, actor, occurred_at desc)` for actor filtering

The model should stay generic and module-agnostic. `entity_type`, `action_type`, and `metadata_json` are intentionally data-driven so later modules can reuse the same audit substrate without schema forks.

### 2. Shared audit write service

Introduce a reusable backend service such as `AuditService` or `AuditEventWriter` that accepts a normalized command containing:

- entity type and entity id
- action type
- actor
- old/new payloads
- optional metadata

The service should resolve the active deployment tenant through `TenantContextService` so every audit row remains deployment-local and country-sovereign by default.

### 3. Converge existing temporary audit behavior

Replace or adapt the temporary translation-specific audit bridge from `STORY-220` so translation updates write through the new generic platform audit service instead of persisting into a permanent separate audit model. The implementation may either:

- migrate translation updates fully to the new generic audit table, or
- keep a short-lived compatibility layer only if backend-dev documents a clearly temporary migration step.

The preferred path is convergence onto the generic table now to avoid parallel audit systems.

### 4. Add minimal query and export API contracts

Provide the smallest backend API surface required to prove the acceptance criteria:

- `GET /api/v1/platform/audit/events`
  - supports filters for `entityType`, `actor`, `from`, `to`
  - returns immutable audit rows in descending time order
- `GET /api/v1/platform/audit/events/export`
  - supports the same filters
  - returns export-ready data (JSON or CSV) containing the compliance-relevant fields

Do not add mutation endpoints for audit records. The application contract remains append-only from the perspective of consumers.

### 5. Define the first meaningful integration scope

To satisfy “Given any entity change, when saved...” without overreaching into all modules, require backend-dev to integrate the audit foundation with at least one real mutable platform-core path. The minimum preferred integration scope is:

- translation updates from `STORY-220`
- optionally configuration value writes from `STORY-030` if the implementation stays small and does not destabilize scope

This proves the generic write path, filtered read behavior, and export path against real persisted changes.

## Alternatives considered

- **Keep per-feature audit tables**: rejected because it creates inconsistent audit contracts, duplicates query/export logic, and undermines the platform-wide audit principle.
- **Wait until auth/RBAC is complete**: rejected because many mutable platform stories are already shipping and need a stable audit foundation now; access-control hardening can layer on later.
- **Adopt full event sourcing now**: rejected because it is too large for the current Phase 1 foundation scope and would overcomplicate the first audit deliverable.
- **Place audit only inside `identity-access`**: rejected because the audit requirement is platform-wide, not identity-only, and existing mutable behavior already lives in `platform-core`.

## Files/components likely affected

- `df/artifacts/STORY-013/task.md`
- `df/artifacts/STORY-013/solution-design.md`
- `df/artifacts/STORY-013/decision-016-platform-audit-foundation.md`
- `df/artifacts/STORY-013/handoffs.md`
- `df/backlog/architecture-direction.md`
- `df/runtime/board.md`
- `df/runtime/backend-dev-board.md`
- `df/runtime/activity-log.md`
- `df/runtime/decisions.md`
- `df/runtime/risks.md`
- expected backend implementation targets under `backend/platform-core/src/main/java/com/darkfactory/education/platform/audit/**`
- expected Flyway migration(s) under `backend/platform-core/src/main/resources/db/migration/`
- likely adaptation of `backend/platform-core/src/main/java/com/darkfactory/education/platform/translation/**`
- possible extension of `backend/platform-core/src/main/java/com/darkfactory/education/platform/configuration/**`
- backend tests under `backend/platform-core/src/test/**`

## Data/API contract changes

### Data model changes

- Add a generic append-only `audit_event` table in PostgreSQL via Flyway
- Prefer JSONB payload columns for before/after snapshots and metadata to keep the first implementation module-agnostic
- Scope audit rows by the active deployment tenant rather than introducing request-side tenant selection

### API/contract changes

- Add minimal backend read/query endpoint(s) for filtered audit log access
- Add a minimal export endpoint for compliance-style extraction
- Introduce one internal shared backend contract for writing audit events
- Do not add update/delete endpoints for audit records

## Security/privacy considerations

- Audit payloads may contain sensitive values; backend-dev should avoid logging full audit payloads to application logs by default
- The first implementation may expose query/export endpoints before final auth/RBAC exists, but the contract must be documented as administrator-oriented and ready for later `identity-access` hardening
- Keep the audit model country-agnostic and deployment-local; no centralized cross-country audit plane is allowed
- Prefer metadata fields that help later redaction/masking policies without blocking the initial foundation

## Test strategy

`backend-dev` should validate the strongest practical backend path:

- integration tests proving the Flyway migration creates the generic audit table and indexes
- integration tests proving a real mutable path (at minimum translation update) creates an audit row with actor, timestamp, entity, action, old value, and new value
- API tests proving audit records are queryable with `entityType`, `actor`, and time-range filters
- API/export tests proving the export response includes the compliance-relevant fields
- negative checks proving the application exposes no audit update/delete capability
- regression checks proving tenant-scoped and framework-generic behavior with no country-specific logic
- rerun relevant backend and full-parent Maven verification if shared backend/test scope is affected

## Risks and mitigations

- **Risk:** Audit payloads become too large or expose sensitive data
  - **Mitigation:** keep the first schema generic but bounded, avoid verbose logging, and document future masking/retention work explicitly
- **Risk:** The temporary translation audit bridge remains permanent and duplicates the new foundation
  - **Mitigation:** require backend-dev to converge translation updates onto the generic audit service during this story unless a clearly temporary compatibility step is documented
- **Risk:** The story balloons into full compliance/reporting/security work
  - **Mitigation:** keep the scope to append-only persistence, filtered backend retrieval/export, and one real integration path only

## Rollback plan

- Revert the generic audit migration and backend audit package if the design is rejected before downstream dependency work starts
- If partial implementation lands, disable the new read/export endpoints and keep mutation paths operating without the generic audit integration only as a temporary rollback while redesigning
- If the temporary translation-specific bridge was converged, document the rollback path carefully so no change history is silently lost during redesign

## Open questions

- None blocking `backend-dev`. If implementation discovers that a second mutable integration path is required to keep the audit API meaningfully testable, backend-dev should document that in lane notes and keep the work inside backend scope unless shared-lane impact appears.

## Implementation lane

- Lane: `backend-dev`
- Subdashboard: `df/runtime/backend-dev-board.md`
- Artifact folder for implementation notes: `df/artifacts/STORY-013/backend/`

## SA decision

Approved for development: Yes

