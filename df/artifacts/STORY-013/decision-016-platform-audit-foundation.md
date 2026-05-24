# Decision Record - DECISION-016

- Date: 2026-05-24
- Status: Accepted
- Owner role: SA
- Related task: STORY-013

## Context

The architecture direction already says the platform must be auditable, but the repository currently has only a temporary feature-specific translation audit bridge in `platform-core`. `STORY-011` provides the PostgreSQL/Flyway substrate, `STORY-021` provides the active deployment tenant context, and several current/future stories mutate persistent state. Without one generic audit foundation, downstream modules are likely to create incompatible audit tables, APIs, and semantics.

## Decision

Implement the first generic audit foundation in `backend/platform-core` using a tenant-scoped append-only audit event model, a shared backend audit write service, and minimal filtered query/export API contracts.

Key decisions:

- use one generic `audit_event` style persistence model instead of per-feature permanent audit tables;
- scope every audit row to the active deployment tenant from `TenantContextService`;
- keep the application contract append-only by exposing no audit update/delete operations;
- converge the temporary translation-specific audit bridge from `STORY-220` onto the generic platform audit path during `STORY-013` if feasible;
- defer final RBAC hardening of audit viewing/export to later identity/access stories while keeping the contract admin-oriented.

## Consequences

- Later backend modules can reuse one normalized audit writer and query/export contract instead of inventing their own audit structures.
- `platform-core` becomes the home of the generic audit foundation, which matches its role as the shared platform substrate.
- Existing feature-specific audit behavior may need migration or compatibility cleanup.
- Sensitive-value masking, retention, external SIEM shipping, and stronger access control remain follow-up work rather than blockers for the first foundation.

## Alternatives considered

- Keep feature-specific audit tables permanently — rejected because it fragments the platform-wide audit story.
- Wait until authentication/RBAC is finished — rejected because mutable features are already shipping and need an audit foundation now.
- Implement full event sourcing — rejected because it is too large and risky for the current MVP foundation scope.
- Place audit inside `identity-access` — rejected because the requirement is platform-wide and current mutable behavior already exists outside identity.

## Evidence

- `df/backlog/architecture-direction.md`
- `df/backlog/user-stories.md`
- `df/artifacts/STORY-220/solution-design.md`
- `df/artifacts/STORY-021/solution-design.md`
- `backend/platform-core/src/main/java/com/darkfactory/education/platform/translation/TranslationAuditRepository.java`
- `backend/platform-core/src/main/resources/db/migration/V4__create_translation_audit_table.sql`
- `backend/platform-core/src/main/java/com/darkfactory/education/platform/tenant/TenantContextService.java`

## Follow-up actions

- Route `STORY-013` to `backend-dev`.
- Update `df/backlog/architecture-direction.md` and runtime decision tracking.
- Require backend integration tests for immutable persistence, filtered query/export behavior, and at least one real mutation path writing audit rows.

