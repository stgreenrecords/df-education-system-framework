# Solution Design - STORY-220

## Summary

Implement a framework-wide `translation` persistence model plus generic lookup, fallback, cache, invalidation, and audit behavior. The design is data-driven: language codes, country defaults, and fallback choices come from configuration/data, not language-specific or country-specific source branches.

## Context

EPIC-22 requires every visible label, message, and text string to be translatable from database data. STORY-220 is the storage and resolution foundation for later API, UI, admin, language catalogue, and user preference stories.

The existing architecture direction establishes a Java Spring Boot modular monolith with PostgreSQL, Flyway or Liquibase migrations, Spring Data, OpenAPI, auditability, and a strict rule that country templates are data-only.

`STORY-010` and `STORY-011` are now accepted, so the repository has a running Spring Boot application in `backend/platform-core`, PostgreSQL datasource configuration, Flyway migrations, and Testcontainers-backed integration testing. The previous blocker is resolved; this refresh aligns the older i18n design with the actual active codebase and delivery-lane model.

## Requirements and acceptance criteria

- Persist translation values by translation key, BCP 47 language code, optional namespace, value, version, and timestamps.
- Prevent duplicate `(translation_key, language_code, namespace)` combinations.
- Resolve missing translations through `country default -> English`.
- Load translations into a cache on application startup with configurable TTL.
- Invalidate affected cache entries when translations are updated via API.
- Produce an audit record for every translation change.
- Keep language and locale behavior data-driven; no language-specific or country-specific Java/Kotlin branches.

## Proposed solution

Implement the first translation-storage slice inside `backend/platform-core` under a dedicated package boundary such as `com.darkfactory.education.platform.translation`. Do not create a new Maven module yet; the codebase is still in an early foundation phase and the smallest viable path is to add the i18n slice inside the existing running Spring Boot application.

Primary responsibilities:

- `Translation` persistence model and Flyway migration for storage.
- `TranslationRepository` for key/language/namespace access.
- `TranslationService` for lookup, fallback, cache interaction, startup warmup, and update orchestration.
- `TranslationFallbackResolver` that receives requested language and deployment-default language, then builds a generic fallback list from data.
- `TranslationCache` abstraction backed initially by Spring Cache plus a local TTL-aware implementation/configuration.
- `TranslationAuditRepository` or equivalent adapter that writes a minimal generic translation-audit record until the broader audit subsystem exists.
- A minimal backend API surface sufficient to prove lookup/update behavior and cache invalidation while staying aligned with later translation-management work.

Lookup behavior:

1. Normalize and validate the requested BCP 47 language code.
2. Normalize missing or blank namespace input to a reserved generic namespace such as `default`.
3. Build candidate languages in order: requested language, deployment default language if different, `en`.
4. Query by `(translation_key, namespace, language_code)` using the candidate order.
5. Return the first match.
6. If no value exists, return a structured missing-translation result without throwing for normal rendering paths.

Update behavior:

1. Upsert or update the translation row.
2. Increment `version`.
3. Publish/write audit data: actor, translation key, namespace, language code, old value, new value, timestamp.
4. Evict the exact cache key and any aggregate namespace/language cache entry affected by the change.

Minimal API scope for this story:

- Add only the smallest REST surface needed to satisfy the story acceptance criteria and to verify runtime behavior end-to-end.
- Recommended shape:
  - `GET /api/v1/translations/resolve?key={key}&lang={code}&namespace={ns}&defaultLanguage={code}` for lookup/fallback proof.
  - `PUT /api/v1/translations/{id}` for updating a translation value and proving cache invalidation + audit.
- Keep the API generic and future-compatible, but do not implement full admin CRUD, bulk import/export, coverage reporting, or role model breadth here; those belong to `STORY-222`.

## Alternatives considered

- Property files/resource bundles: rejected because EPIC-22 requires database-backed translations and runtime administration.
- Hard-coded fallback branches per language or country: rejected because it violates the no-country-specific-code and no-language-specific-code invariants.
- Redis-first caching: deferred because Spring Cache abstraction keeps the design deployable in the MVP while preserving a later Redis provider.
- Event-sourced translation history: deferred; immutable audit events/table are sufficient for MVP unless the broader audit architecture chooses event sourcing.

## Files/components likely affected

- `backend/platform-core/pom.xml`
- `backend/platform-core/src/main/resources/application.properties`
- `backend/platform-core/src/main/resources/db/migration/*`
- `backend/platform-core/src/main/java/com/darkfactory/education/platform/translation/**`
- Optional supporting packages under `backend/platform-core/src/main/java/com/darkfactory/education/platform/config/**`
- `backend/platform-core/src/test/java/com/darkfactory/education/platform/**`
- `df/artifacts/STORY-220/backend/*`

## Data/API contract changes

Required table:

```sql
translation
  id uuid primary key
  translation_key varchar not null
  language_code varchar not null
  namespace varchar not null default 'default'
  value text not null
  version integer not null default 1
  created_at timestamptz not null
  updated_at timestamptz not null
```

Required uniqueness:

```sql
unique (translation_key, language_code, namespace)
```

Chosen PostgreSQL approach: make `namespace` non-null with a reserved generic default such as `default`. This preserves the “optional namespace” product behavior at the API/service level while keeping uniqueness semantics simple and deterministic.

Recommended indexes:

- `(translation_key, namespace, language_code)` for direct lookup.
- `(language_code, namespace)` for namespace/language loading.

Required temporary audit table for this story:

```sql
translation_audit
  id uuid primary key
  translation_id uuid not null
  actor varchar not null
  translation_key varchar not null
  language_code varchar not null
  namespace varchar not null
  old_value text null
  new_value text not null
  changed_at timestamptz not null
```

This local audit table satisfies the story acceptance criteria now and must be designed so a later platform-wide audit implementation can consume, bridge, or migrate it without changing translation behavior.

API shape added by this story should remain minimal and backend-focused:

- Translation resolution endpoint returning the resolved value plus the language actually used.
- Translation update endpoint that accepts actor context and persists a versioned change.
- No country-specific or language-specific route variants.

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
- Until the full authorization model exists, any provisional update path must stay minimal, clearly documented, and ready to be tightened by `STORY-222`/security stories.
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
- Minimal REST lookup/update paths behave correctly under the running application context.
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
- Risk: Platform-wide audit does not exist yet. Mitigation: implement a thin local translation-audit table/repository now and keep its contract easy to bridge or migrate later.
- Risk: English fallback data may be incomplete. Mitigation: add seed/coverage tests for keys used by smoke tests and report missing keys.

## Open questions

- None blocking Dev. The cache provider can start local through Spring Cache; distributed cache remains a later provider decision.

## Implementation lane

- Lane: `backend-dev`
- Subdashboard: `df/runtime/backend-dev-board.md`
- Artifact folder for implementation notes: `df/artifacts/STORY-220/backend/`

## SA decision

Approved for development: Yes
