# Decision Record - DECISION-002

## Title

Database-backed i18n storage uses generic fallback and replaceable cache abstraction

## Status

Accepted

## Date

2026-05-23

## Context

EPIC-22 requires all visible UI text to be persisted in the database and resolved without hard-coded language-specific or country-specific framework logic. STORY-220 introduces the persistence and lookup foundation, including fallback, cache, and audit behavior.

## Decision

Use a PostgreSQL `translation` table keyed by translation key, BCP 47 language code, and namespace. Resolve missing values through a generic data-driven fallback chain of requested language, country default language, and English. Cache translation lookups through a Spring Cache-compatible abstraction with configurable TTL, and invalidate affected entries when translations change.

## Consequences

- Translation behavior can be changed through data/configuration rather than source branches.
- The initial cache can be local for MVP while allowing Redis or another distributed provider later.
- Dev must handle PostgreSQL uniqueness semantics for namespace explicitly.
- Audit integration must be preserved for translation changes.

## Alternatives Considered

- Resource bundle/property files: rejected because runtime database storage is required.
- Language/country-specific fallback branches: rejected because they violate framework invariants.
- Redis-first distributed cache: deferred to avoid premature infrastructure dependency while preserving the provider abstraction.

## Related

- Task: STORY-220
- Design: `df/artifacts/STORY-220/solution-design.md`
