# Task - STORY-220

## Summary

Design and implement database-backed translation storage for all user-visible UI text.

## Type

Story

## Priority

P0

## Current state

BLOCKED

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

## Role history

| Timestamp | Role | State | Summary |
|---|---|---|---|
| 2026-05-23 10:18 local | sa | OPEN -> NEEDS_ARCHITECTURE | Promoted EPIC-22 root story from documented backlog draft into runtime task; refinement skipped because backlog acceptance criteria are explicit and testable. |
| 2026-05-23 10:18 local | sa | ARCHITECTURE_IN_PROGRESS -> READY_FOR_DEV | Produced solution design and Dev handoff. |
| 2026-05-23 10:27 local | dev | READY_FOR_DEV -> DEV_IN_PROGRESS -> BLOCKED | Dev inspected the repository and confirmed the required Spring Boot/PostgreSQL application substrate is absent; implementation cannot proceed until dependencies STORY-010 and STORY-011 exist. |
