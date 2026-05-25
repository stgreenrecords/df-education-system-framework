# QA Report - STORY-220

## QA summary

PASS

## Environment

- OS: Windows
- Runtime: Java 25.0.2, Maven Wrapper 3.9.15, Spring Boot 4.1.0-SNAPSHOT, Docker Desktop 29.2.1, PostgreSQL 17.10 (Testcontainers and isolated live container), Springdoc OpenAPI, Caffeine cache
- Branch/commit: `master...origin/master` (local workspace with uncommitted task files)
- Test data: Generic translation smoke data from `V5__seed_translation_smoke_data.sql`, plus one live QA update to `ui.greeting` English value

## Acceptance criteria coverage

| Criterion | Result | Evidence |
|---|---|---|
| 1. `translation` table exists with required columns | PASS | Read `backend/platform-core/src/main/resources/db/migration/V3__create_translation_table.sql`; live PostgreSQL `information_schema.columns` inspection showed `id`, `translation_key`, `language_code`, `namespace`, `value`, `version`, `created_at`, `updated_at` |
| 2. Missing requested language falls back to deployment default then English | PASS | Automated tests `resolveEndpointFallsBackToDeploymentDefaultLanguage()` and `resolveEndpointFallsBackToGlobalLanguageWhenDefaultLanguageIsMissing()` passed; live API checks returned `fr` for `ui.greeting` when `lang=de` and `en` for `ui.status.ready` when default-language data was missing |
| 3. No duplicate `(key + language_code + namespace)` combinations exist | PASS | Unique constraint in `V3__create_translation_table.sql`; automated test `duplicateTranslationKeyLanguageAndNamespaceIsRejected()` passed; live SQL duplicate-group query returned `0` |
| 4. Translations are loaded and cached with configurable TTL on startup | PASS | `TranslationCacheConfiguration.java` reads `edu.translation.cache-ttl`; `TranslationCacheWarmup.java` warms all translations on `ApplicationReadyEvent`; automated test `translationsAreLoadedIntoCacheOnStartupWithConfiguredTtl()` passed; live first-resolve responses returned `cacheHit=true` |
| 5. Update via API invalidates cache and serves the new value within configured TTL | PASS | `TranslationService.updateTranslation()` evicts then repopulates the cache; automated test `updateEndpointInvalidatesCachedEntryAndCreatesAuditRecord()` passed; live `PUT /api/v1/translations/{id}` changed `ui.greeting` English from `Hello` to `Hello from QA`, and the next resolve returned the new value with `cacheHit=true` |
| 6. Translation change creates audit record with actor, old value, new value, and timestamp | PASS | Read `V4__create_translation_audit_table.sql`; automated test `updateEndpointInvalidatesCachedEntryAndCreatesAuditRecord()` passed; live SQL inspection showed actor `qa-reviewer`, `old_value=Hello`, `new_value=Hello from QA`, and populated `changed_at` |
| 7. No language or locale logic resides in Java/Kotlin code as language-specific branching | PASS | Read `TranslationFallbackResolver.java`, `TranslationService.java`, `LanguageTagNormalizer.java`, and `application.properties`; fallback is driven by normalized request language plus configured default/fallback languages. Scoped Java grep for `Poland|Polish|locale\s*==|language\s*==|"en"|"fr"|"pl"` returned no matches in `backend/platform-core/src/main/java/**/*.java` |

## Automated tests

| Test suite | Command/source | Result | Notes |
|---|---|---|---|
| Backend reactor verify | `./mvnw.cmd -f backend/pom.xml clean verify` | PASS | 9 backend reactor modules built successfully; `EducationSystemApplicationIT` ran 15 tests with 0 failures, 0 errors, 0 skipped |
| Full parent verify | `./mvnw.cmd clean verify` | PASS | 12-project parent reactor built successfully; backend integration tests still passed with 15/0/0/0 |
| IDE/static issue check | IDE diagnostics for changed Java files | PASS | `get_errors` returned no errors for `TranslationCacheConfiguration.java`, `TranslationService.java`, `TranslationFallbackResolver.java`, `TranslationController.java`, and `EducationSystemApplicationIT.java` |
| Generic-scope source inspection | Scoped Java grep over translation backend source | PASS | No country-specific or language-specific branch markers found in `backend/platform-core/src/main/java/**/*.java` |

## Integration tests

| Scenario | Result | Evidence |
|---|---|---|
| Flyway applies all five migrations on startup | PASS | QA rerun logs show PostgreSQL 17.10 startup and Flyway applying versions `1` through `5`; live SQL query of `flyway_schema_history` confirmed the same sequence with all `success = true` |
| Translation cache warmup is active at runtime | PASS | Live `GET /api/v1/translations/resolve?key=ui.greeting&lang=fr&namespace=default` returned the seeded French value with `cacheHit=true` immediately after startup |
| Resolve API returns requested translation when present | PASS | Live resolve of `ui.greeting` with `lang=fr` returned translation id `00000000-0000-0000-0000-000000000302`, `value=Bonjour`, `fallbackApplied=false`, `cacheHit=true` |
| Resolve API follows deployment-default fallback | PASS | Live resolve of `ui.greeting` with `lang=de` returned `resolvedLanguage=fr` and `value=Bonjour` |
| Resolve API follows global English fallback after deployment-default miss | PASS | Live resolve of `ui.status.ready` with `lang=de` returned `resolvedLanguage=en` and `value=Ready` |
| Update API increments version, invalidates cache, and records audit | PASS | Live `PUT /api/v1/translations/00000000-0000-0000-0000-000000000301` returned `version=2`; subsequent resolve returned `Hello from QA`; live SQL query showed one matching audit row |
| Invalid BCP 47 input is rejected | PASS | Live resolve with `lang=invalid_tag!` returned HTTP 400 Bad Request |
| OpenAPI still exposes translation endpoints | PASS | Live `GET /api-docs` returned OpenAPI 3.1.0 with `/api/v1/translations/resolve` and `/api/v1/translations/{translationId}` paths |

## Manual checks

| Scenario | Result | Evidence |
|---|---|---|
| Backend lane artifacts are present in the correct folder | PASS | `df/artifacts/STORY-220/backend/dev-notes.md`; `df/artifacts/STORY-220/backend/handoff-to-qa.md` |
| Runtime board and backend subdashboard match the backend lane task | PASS | `df/runtime/board.md`; `df/runtime/backend-dev-board.md` |
| Changed source/resources remain generic and data-driven | PASS | Read `application.properties`, `V3__create_translation_table.sql`, `V4__create_translation_audit_table.sql`, `V5__seed_translation_smoke_data.sql`, `TranslationFallbackResolver.java`, `TranslationService.java`, `TranslationController.java`, `LanguageTagNormalizer.java`, and `EducationSystemApplicationIT.java` |
| No relevant IDE errors remain in the changed QA scope | PASS | `get_errors` returned no issues for the changed Java/test files |

## Defects

- None

## Risks

- `RISK-013` remains open: cache invalidation is local/Caffeine for MVP and will need a distributed provider strategy in future multi-node deployments.
- `RISK-026` remains open: translation auditing currently uses a local bridge table that later platform-wide audit work may consolidate.
- Maven/JDK warnings about restricted native access and Mockito dynamic-agent loading were non-blocking during QA and did not affect results.
- Springdoc still warns that `/api-docs` and `/swagger-ui` are enabled by default; this is pre-existing future-scope hardening work and did not block the story.

## QA decision

Ready for PO: Yes

