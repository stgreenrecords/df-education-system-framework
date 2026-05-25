# Backend Dev Notes - STORY-220

## Task

Implement database-backed translation storage for all user-visible UI text in `backend/platform-core`.

## Implementation summary

Implemented the first generic translation-storage slice inside `backend/platform-core`.

### What changed

| File | Change | Notes |
|---|---|---|
| `backend/platform-core/pom.xml` | Updated | Added Spring cache starter and Caffeine dependency for configurable translation caching |
| `backend/platform-core/src/main/resources/application.properties` | Updated | Added deployment-driven translation default language, global fallback language, default namespace, and cache TTL properties |
| `backend/platform-core/src/main/resources/db/migration/V3__create_translation_table.sql` | Created | Added the `translation` table, uniqueness constraint, and lookup indexes |
| `backend/platform-core/src/main/resources/db/migration/V4__create_translation_audit_table.sql` | Created | Added the local `translation_audit` table for MVP audit coverage |
| `backend/platform-core/src/main/resources/db/migration/V5__seed_translation_smoke_data.sql` | Created | Seeded generic smoke-test translations used for runtime and integration validation |
| `backend/platform-core/src/main/java/com/darkfactory/education/platform/config/TranslationCacheConfiguration.java` | Created | Configured Caffeine-backed translation cache with TTL from properties |
| `backend/platform-core/src/main/java/com/darkfactory/education/platform/translation/*` | Created | Added translation properties, validation, repository, fallback, cache, warmup, service, DTOs, and REST controller |
| `backend/platform-core/src/test/java/com/darkfactory/education/platform/EducationSystemApplicationIT.java` | Updated | Expanded integration coverage from 8 to 15 tests covering migrations, uniqueness, fallback, cache warmup, update audit, update invalidation, and OpenAPI regression |

## Important implementation details

### 1. Generic translation storage and uniqueness

- Added a `translation` table with:
  - `id`
  - `translation_key`
  - `language_code`
  - `namespace`
  - `value`
  - `version`
  - `created_at`
  - `updated_at`
- Implemented a non-null `namespace` with the generic default `default`.
- Added a unique constraint on `(translation_key, language_code, namespace)`.

### 2. Generic fallback behavior

Fallback is entirely data/configuration-driven:

1. requested language
2. deployment default language from configuration
3. global fallback language from configuration

No language-specific or country-specific conditional branch was added.

### 3. Configurable caching and startup warmup

- Added a Caffeine-backed translation cache with TTL from `edu.translation.cache-ttl`.
- Added startup warmup that loads all stored translations into the cache when the application is ready.
- Added cache eviction/refresh behavior on translation update.

### 4. Minimal API surface for this story

Added only the smallest backend endpoints needed to prove acceptance criteria:

- `GET /api/v1/translations/resolve`
- `PUT /api/v1/translations/{translationId}`

This keeps broader translation-management behavior deferred to `STORY-222`.

### 5. Local audit bridge

Because `STORY-013` is not implemented yet, this story writes a generic `translation_audit` row on update with:

- actor
- old value
- new value
- timestamp

The structure is intentionally simple so future audit consolidation can bridge or migrate it.

## Debugging / adjustments made during implementation

### Adjustment A: Test context did not provide `ObjectMapper`

The first backend verify failed because the test class autowired `ObjectMapper`, but the current test context did not expose that bean.

Resolution:
- removed the `ObjectMapper` dependency from the integration test
- used an inline JSON payload for the update endpoint test instead

### Adjustment B: Path-variable binding required an explicit name

The second backend verify failed on the translation update endpoint because the current build/test setup did not provide method parameter-name metadata for path-variable binding.

Resolution:
- changed `@PathVariable UUID translationId` to `@PathVariable("translationId") UUID translationId`

## Validation commands and results

### Backend-only verification

```text
Command: .\mvnw.cmd -f backend/pom.xml clean verify
Result: BUILD SUCCESS
Tests run: 15, Failures: 0, Errors: 0, Skipped: 0
Finished at: 2026-05-24T19:08:04+02:00
```

### Full parent verification

```text
Command: .\mvnw.cmd clean verify
Result: BUILD SUCCESS
Tests run: 15, Failures: 0, Errors: 0, Skipped: 0
Finished at: 2026-05-24T19:08:19+02:00
```

### Generic-scope inspection

```text
Command/source: grep search in backend/platform-core/src/main/java for Poland|Polish|country|locale\s*==|language\s*==|"en"|"fr"|"pl"
Result: PASS
Notes: The only source match was the existing protective comment in PlatformStatusController; no language-specific or country-specific branch was introduced in translation code.
```

## Acceptance criteria status

| AC | Status | Evidence |
|---|---|---|
| 1. `translation` table exists with required columns | PASS | `V3__create_translation_table.sql`; integration test `flywayBootstrapMigrationsAreAppliedOnStartup()` plus direct migration validation |
| 2. Fallback chain uses country default -> English | PASS | `resolveEndpointFallsBackToDeploymentDefaultLanguage()` and `resolveEndpointFallsBackToGlobalLanguageWhenDefaultLanguageIsMissing()` |
| 3. No duplicate `(key + language_code + namespace)` combinations exist | PASS | unique constraint in `V3__create_translation_table.sql`; integration test `duplicateTranslationKeyLanguageAndNamespaceIsRejected()` |
| 4. Translations are loaded and cached with configurable TTL on startup | PASS | `TranslationCacheWarmup`; Caffeine TTL configuration from `edu.translation.cache-ttl`; integration test `translationsAreLoadedIntoCacheOnStartupWithConfiguredTtl()` |
| 5. Updating via API invalidates cache and serves the new value within TTL | PASS | `TranslationService.updateTranslation()` evicts and repopulates cache; integration test `updateEndpointInvalidatesCachedEntryAndCreatesAuditRecord()` |
| 6. Translation changes create an audit record with actor, old value, new value, and timestamp | PASS | `translation_audit` migration + `TranslationAuditRepository`; integration test `updateEndpointInvalidatesCachedEntryAndCreatesAuditRecord()` |
| 7. No language or locale logic resides as language-specific branching in Java/Kotlin code | PASS | `TranslationFallbackResolver` and `TranslationService` use generic candidate ordering from configuration only; generic-scope grep inspection found no language-specific/country-specific branches |

## Notes for QA

- The SQL files still show IDE-only “no data source configured” warnings; Maven verification passed and these warnings are non-blocking.
- Springdoc endpoint-exposure warnings remain future-scope and pre-existing.
- Mockito/native-access warnings were non-blocking during verification and did not affect test results.

