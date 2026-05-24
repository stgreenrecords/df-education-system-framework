# Task - STORY-220

## Summary

Design and implement database-backed translation storage for all user-visible UI text.

## Type

Story

## Priority

P0

## Current state

DONE

## Business goal

Enable the framework to serve every visible label, message, and UI text value from data so language coverage can be managed per deployment without hard-coded language or country behavior.

## Acceptance criteria

- [ ] Given the schema, when inspected, then a `translation` table exists with columns: id, translation_key, language_code (BCP 47), namespace, value, version, created_at, updated_at
- [ ] Given a translation key, when queried for a language that has no entry, then a fallback chain (country default -> English) is followed and the result is returned without error
- [ ] Given all translation keys, when queried, then no duplicate (key + language_code + namespace) combinations exist
- [ ] Given translations, when the application starts, then they are loaded and cached with a configurable TTL
- [ ] Given a translation value, when updated via API, then the cache is invalidated and the new value is served within the configured TTL
- [ ] Given audit requirements, when a translation is changed, then an audit record is created with actor, old value, new value, and timestamp
- [ ] Given the data model, when reviewed, then no language or locale logic resides in application Java/Kotlin code

## Out of scope

- Translation administration UI.
- Bulk import/export endpoints.
- Full global language catalogue and country-level active-language subset.
- Per-user language preference.
- Front-end component migration to translation keys.

## Assumptions

- BCP 47 syntax validation can be implemented generically and must not branch by individual language.
- English is the universal final fallback for MVP unless a later PO decision changes the global default.
- Country default language is read from country configuration data, not from source code.
- Translation cache can use Spring Cache initially with a local in-process provider; distributed cache can be introduced later through configuration.
- Acceptance criterion 7 is interpreted as forbidding language-specific or locale-specific conditional branches in application code; generic translation lookup, validation, and fallback orchestration remain in scope.
- Because `STORY-013` is not yet implemented, this story may add a minimal generic translation-audit persistence mechanism that can later be bridged or migrated into the platform-wide audit subsystem.

## Dependencies

- STORY-010 - Initialize Spring Boot project with modular structure.
- STORY-011 - Implement PostgreSQL database configuration and migration framework.
- Existing architecture direction: Java Spring Boot, PostgreSQL, modular monolith, API-first.

## Risks

- Cache invalidation may behave differently in future multi-node deployments; design keeps provider replaceable.
- Audit implementation depends on whether the platform-wide audit trail exists when this story is implemented.
- Missing English fallback entries could still return no user-friendly value; seed/coverage validation should be added during implementation.

## Links

- Issue: n/a
- PR: n/a
- Design: `df/artifacts/STORY-220/solution-design.md`

## Implementation lane

- Lane: `backend-dev`
- Subdashboard: `df/runtime/backend-dev-board.md`
- Artifact folder for implementation notes: `df/artifacts/STORY-220/backend/`

## Role history

| Timestamp | Role | State | Summary |
|---|---|---|---|
| 2026-05-23 10:18 local | sa | OPEN -> NEEDS_ARCHITECTURE | Promoted EPIC-22 root story from documented backlog draft into runtime task; refinement skipped because backlog acceptance criteria are explicit and testable. |
| 2026-05-23 10:18 local | sa | ARCHITECTURE_IN_PROGRESS -> READY_FOR_DEV | Produced solution design and Dev handoff. |
| 2026-05-23 10:27 local | dev | READY_FOR_DEV -> DEV_IN_PROGRESS -> BLOCKED | Dev inspected the repository and confirmed the required Spring Boot/PostgreSQL application substrate is absent; implementation cannot proceed until dependencies STORY-010 and STORY-011 exist. |
| 2026-05-24 18:54 local | po/factory | BLOCKED -> NEEDS_ARCHITECTURE | `STORY-011` is now PO-accepted, so the database-foundation blocker is resolved. The task returns to `sa` for lane-refresh/orchestration because the prior owner was the retired generic `dev` role. |
| 2026-05-24 19:00 local | sa | NEEDS_ARCHITECTURE -> ARCHITECTURE_IN_PROGRESS -> READY_FOR_DEV | Refreshed the old i18n architecture against the accepted Spring Boot/PostgreSQL/Flyway foundation, routed the work to `backend-dev`, and clarified minimal audit/update-path expectations so delivery can resume without the retired generic `dev` lane. |
| 2026-05-24 19:05 local | backend-dev | READY_FOR_DEV -> DEV_IN_PROGRESS | Backend implementation started after reviewing the refreshed SA guidance, current runtime boards, repository status, and existing `platform-core` code/test structure. |
| 2026-05-24 19:09 local | backend-dev | DEV_IN_PROGRESS -> READY_FOR_QA | Implemented the translation storage foundation, migration set, cache warmup/invalidation, minimal lookup/update API, local audit bridge, and 15 integration tests; backend and full-parent Maven verification both passed. |
| 2026-05-24 19:24 local | qa | READY_FOR_QA -> QA_IN_PROGRESS -> READY_FOR_PO | QA reran backend/full Maven verification, confirmed no changed-file IDE errors, inspected the generic backend scope, and completed live PostgreSQL/API validation for schema, fallback, cache warmup/invalidation, audit rows, invalid-language rejection, and `/api-docs` exposure. |
| 2026-05-24 19:27 local | po | READY_FOR_PO -> PO_REVIEW -> DONE | Reviewed QA evidence, ran the application against an isolated PostgreSQL container, validated the live translation resolve/update paths plus OpenAPI/Swagger exposure, confirmed Flyway versions `1` through `5`, duplicate-key protection, and translation audit output, and accepted the story as sufficient for the MVP translation foundation. |
