# Backend Handoff to QA - STORY-220

## Task

STORY-220 — Design and implement database-backed translation storage in `backend/platform-core`

## From state

`DEV_IN_PROGRESS`

## To state

`READY_FOR_QA`

## Lane

`backend-dev`

## Summary

Implementation is complete. `backend/platform-core` now includes a generic translation-storage slice with PostgreSQL-backed storage, ordered Flyway migrations, configuration-driven fallback order, configurable cache TTL with startup warmup, a minimal lookup/update API, and a local translation-audit bridge.

Existing OpenAPI/Swagger integration behavior continues to pass with the new translation endpoints included in the generated spec.

## Files changed

| File | Change |
|---|---|
| `backend/platform-core/pom.xml` | Added caching dependencies |
| `backend/platform-core/src/main/resources/application.properties` | Added translation configuration properties |
| `backend/platform-core/src/main/resources/db/migration/V3__create_translation_table.sql` | Created translation schema |
| `backend/platform-core/src/main/resources/db/migration/V4__create_translation_audit_table.sql` | Created translation audit schema |
| `backend/platform-core/src/main/resources/db/migration/V5__seed_translation_smoke_data.sql` | Created generic seed data for smoke coverage |
| `backend/platform-core/src/main/java/com/darkfactory/education/platform/config/TranslationCacheConfiguration.java` | Added cache manager configuration |
| `backend/platform-core/src/main/java/com/darkfactory/education/platform/translation/*` | Added translation backend slice |
| `backend/platform-core/src/test/java/com/darkfactory/education/platform/EducationSystemApplicationIT.java` | Expanded integration coverage to 15 tests |
| `df/artifacts/STORY-220/backend/dev-notes.md` | Added implementation evidence |

## Test evidence

### Backend build

```text
Command: .\mvnw.cmd -f backend/pom.xml clean verify
Result: BUILD SUCCESS
Tests run: 15, Failures: 0, Errors: 0, Skipped: 0
Timestamp: 2026-05-24T19:08:04+02:00
```

### Full parent build

```text
Command: .\mvnw.cmd clean verify
Result: BUILD SUCCESS
Tests run: 15, Failures: 0, Errors: 0, Skipped: 0
Timestamp: 2026-05-24T19:08:19+02:00
```

## Acceptance criteria evidence

| AC | Evidence |
|---|---|
| 1. `translation` table exists with required columns | `V3__create_translation_table.sql`; Flyway startup logs to version `5` |
| 2. Fallback chain follows deployment default then English | `resolveEndpointFallsBackToDeploymentDefaultLanguage()` and `resolveEndpointFallsBackToGlobalLanguageWhenDefaultLanguageIsMissing()` |
| 3. Duplicate `(key, language, namespace)` combinations are rejected | DB unique constraint in V3; `duplicateTranslationKeyLanguageAndNamespaceIsRejected()` |
| 4. Translations load into cache on startup with configurable TTL | `TranslationCacheWarmup`; `TranslationCacheConfiguration`; `translationsAreLoadedIntoCacheOnStartupWithConfiguredTtl()` |
| 5. Update via API invalidates cache and serves new value within TTL | `TranslationService.updateTranslation()`; `updateEndpointInvalidatesCachedEntryAndCreatesAuditRecord()` |
| 6. Audit record created on translation update | `translation_audit` table and `TranslationAuditRepository`; `updateEndpointInvalidatesCachedEntryAndCreatesAuditRecord()` |
| 7. No language/country-specific branching | source inspection plus scoped grep over `backend/platform-core/src/main/java` returned no language-specific/country-specific branches in translation code |

## Known risks for QA

- `RISK-013`: cache provider is local/Caffeine for MVP; distributed invalidation is deferred.
- `RISK-026`: translation audit currently uses a local bridge table that future audit work may consolidate.
- IDE SQL warnings on migration files are editor-only and did not affect build/test success.
- Springdoc and Mockito/JDK warnings remain non-blocking and pre-existing/future-scope.

## QA focus areas

1. Re-run backend and full-parent builds
2. Verify Flyway creates `translation` and `translation_audit` and reaches version `5`
3. Confirm fallback order behaves as requested language -> deployment default -> English
4. Confirm startup cache warmup and update invalidation behavior
5. Confirm update API writes audit rows correctly
6. Confirm no country-specific or language-specific branching was introduced
7. Confirm `/api-docs` still works and includes the translation resolve endpoint

## Blockers

None.

