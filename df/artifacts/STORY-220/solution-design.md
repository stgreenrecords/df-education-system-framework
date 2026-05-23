# Solution Design - STORY-220

## Summary

Implement a framework-wide `translation` persistence model plus generic lookup, fallback, cache, invalidation, and audit behavior. The design is data-driven: language codes, country defaults, and fallback choices come from configuration/data, not language-specific or country-specific source branches.

## Context

EPIC-22 requires every visible label, message, and text string to be translatable from database data. STORY-220 is the storage and resolution foundation for later API, UI, admin, language catalogue, and user preference stories.

The existing architecture direction establishes a Java Spring Boot modular monolith with PostgreSQL, Flyway or Liquibase migrations, Spring Data, OpenAPI, auditability, and a strict rule that country templates are data-only.

## Requirements and acceptance criteria

- Persist translation values by translation key, BCP 47 language code, optional namespace, value, version, and timestamps.
- Prevent duplicate `(translation_key, language_code, namespace)` combinations.
- Resolve missing translations through `country default -> English`.
- Load translations into a cache on application startup with configurable TTL.
- Invalidate affected cache entries when translations are updated via API.
- Produce an audit record for every translation change.
- Keep language and locale behavior data-driven; no language-specific or country-specific Java/Kotlin branches.

## Proposed solution

Create an `i18n` or `platform-core-i18n` module boundary inside the modular monolith.

Primary responsibilities:

- `Translation` entity/table for storage.
- `TranslationRepository` for key/language/namespace access.
- `TranslationService` for lookup, fallback, cache interaction, and update orchestration.
- `TranslationFallbackResolver` that receives requested language and country/deployment context, then builds a generic fallback list from data.
- `TranslationCache` abstraction backed initially by Spring Cache.
- `TranslationAuditPublisher` adapter that emits audit events or writes to the available audit mechanism.

Lookup behavior:

1. Normalize and validate the requested BCP 47 language code.
2. Build candidate languages in order: requested language, country default language if different, `en`.
3. Query by `(translation_key, namespace, language_code)` using the candidate order.
4. Return the first match.
5. If no value exists, return a structured missing-translation result without throwing for normal rendering paths.

Update behavior:

1. Upsert or update the translation row.
2. Increment `version`.
3. Publish/write audit data: actor, translation key, namespace, language code, old value, new value, timestamp.
4. Evict the exact cache key and any aggregate namespace/language cache entry affected by the change.

## Alternatives considered

- Property files/resource bundles: rejected because EPIC-22 requires database-backed translations and runtime administration.
- Hard-coded fallback branches per language or country: rejected because it violates the no-country-specific-code and no-language-specific-code invariants.
- Redis-first caching: deferred because Spring Cache abstraction keeps the design deployable in the MVP while preserving a later Redis provider.
- Event-sourced translation history: deferred; immutable audit events/table are sufficient for MVP unless the broader audit architecture chooses event sourcing.

## Files/components likely affected

- Future code module: `platform-core` or new `i18n` package/module under the Spring Boot application.
- Database migration for `translation`.
- Repository/entity/service classes for translation lookup and updates.
- Application startup cache warmup component.
- Configuration properties for TTL and fallback default.
- Tests for repository constraints, fallback resolution, cache invalidation, and audit publication.

## Data model changes

Required table:

```sql
translation
  id uuid primary key
  translation_key varchar not null
  language_code varchar not null
  namespace varchar null
  value text not null
  version integer not null default 1
  created_at timestamptz not null
  updated_at timestamptz not null
```

Required uniqueness:

```sql
unique (translation_key, language_code, namespace)
```

PostgreSQL note: because nullable columns can affect uniqueness semantics, Dev should either make `namespace` non-null with a reserved generic default such as `default`, or use an expression/partial unique index that treats null namespace consistently. Prefer a non-null `namespace` default for simpler application behavior if it does not conflict with the final migration style.

Recommended indexes:

- `(translation_key, namespace, language_code)` for direct lookup.
- `(language_code, namespace)` for namespace/language loading.

Audit data can be handled through the central audit table/event system when available. If STORY-013 is not implemented yet, Dev should add only the minimal translation audit adapter needed for this story and keep it compatible with the future platform audit contract.

## API/contract changes

No public REST API is required by this story beyond whatever internal service or provisional update path Dev needs to prove cache invalidation and audit. STORY-222 will define the public translation management API.

Internal contract should expose:

- Resolve single translation by key, namespace, requested language, and country/deployment context.
- Resolve namespace bundle by namespace, requested language, and country/deployment context.
- Update translation value with actor context for audit.

## UI/UX impact

No direct UI change in this story. Later UI stories will consume translation keys and rendered bundles.

## Security and privacy considerations

- Translation values are usually not PII, but audit actor identifiers are sensitive and must follow the platform audit/privacy rules.
- Update paths must require admin-level authorization when exposed by STORY-222.
- Logs must not dump full translation payloads by default.
- No secrets or country-specific values belong in source or migrations except generic seed data approved by product.

## Performance/scalability considerations

- Warm startup cache for active languages/namespaces where practical.
- TTL must be configurable through deployment configuration.
- Cache keys must include namespace and language code.
- The service must support provider replacement from local cache to Redis or another distributed cache without changing callers.
- Avoid loading the entire global language catalogue into memory for inactive languages during MVP if country configuration identifies active/default languages.

## Test strategy

Dev should add automated tests for:

- Migration creates the required table and uniqueness behavior.
- Duplicate `(translation_key, language_code, namespace)` entries are rejected.
- Fallback order returns requested language first, country default second, English third.
- Missing requested and country-default translation returns English without error.
- Cache is populated on startup or first load according to implementation choice and respects TTL configuration.
- Updating a translation invalidates the affected cache entry.
- Updating a translation creates an audit record/event with actor, old value, new value, and timestamp.
- Static/source inspection or architectural test proves no language-specific or country-specific branches exist for translation resolution.

## Deployment/migration plan

- Add a forward-only database migration for `translation`.
- Add seed data only for generic framework keys required to pass smoke tests.
- Keep deployment configuration responsible for country default language and cache TTL.
- Document the migration in Dev notes with rollback considerations.

## Rollback plan

- Revert the migration before production data exists.
- After production data exists, use a compensating migration only after export/backup of translation data.
- Disable translation update endpoints if cache/audit defects are found, while lookup can continue from existing data if safe.

## Risks and mitigations

- Risk: Namespace null uniqueness can allow duplicates in PostgreSQL. Mitigation: use non-null default namespace or expression index.
- Risk: Future multi-node deployment needs distributed invalidation. Mitigation: depend on cache abstraction and record distributed cache as a follow-up architecture concern.
- Risk: Audit system may not exist yet. Mitigation: implement a thin adapter/event interface compatible with the future audit module.
- Risk: English fallback data may be incomplete. Mitigation: add seed/coverage tests for keys used by smoke tests and report missing keys.

## Open questions

- None blocking Dev. The cache provider can start local through Spring Cache; distributed cache remains a later provider decision.

## SA decision

Approved for development: Yes
