# QA Report - STORY-030

## QA Result: PASS

- Task: `STORY-030`
- Acceptance criteria covered: Yes
- Unit tests: `./mvnw.cmd -f backend/pom.xml -pl platform-core -am clean verify` — PASS (`ScopePathTest`: 3/3; `TenantPropertiesTest`: 3/3)
- Integration tests: `./mvnw.cmd -f backend/pom.xml -pl platform-core "-Dtest=ScopePathTest,EducationSystemApplicationIT#countryLevelConfigurationResolvesAtInstitutionLevelWithoutOverride+institutionLevelOverrideTakesPrecedenceOverInheritedConfiguration+lockedCountryConfigurationRejectsLowerScopeOverride+extensibleConfigurationMergesInheritedAndLocalOptions+regionLevelConfigurationChangeFlowsToInstitutionWithinRegion+apiDocsContainsConfigurationResolveEndpoint" test`; `./mvnw.cmd -f backend/pom.xml -pl platform-core -am clean verify`; `./mvnw.cmd clean verify` — PASS (`EducationSystemApplicationIT`: focused 6/6 targeted scenarios; full backend verify 26/26 integration tests with Testcontainers-backed PostgreSQL)
- Manual checks: Inspected `ConfigurationController`, `ConfigurationService`, `ScopePath`, `ConfigurationValueType`, `V7__create_configuration_inheritance_tables.sql`, and the `EducationSystemApplicationIT` coverage; confirmed generic scope-path modeling rooted in the active deployment tenant, deterministic `REPLACE` / `EXTEND_SET` behavior, ancestor-lock enforcement, migration ordering `1..7`, and no country-specific or organization-module-specific hardcoding inside the new configuration package
- Regression checks: Re-ran the backend-focused and full-parent Maven verification successfully; confirmed Flyway remains at version `7`, the migration chain stays ordered `1..7`, `/api/v1/platform/configuration/resolve` is exposed in `/api-docs`, and the broader reactor still builds cleanly
- Risks: `RISK-010`, `RISK-019`, and `RISK-029` remain open but do not block PO review; non-failing JDK/Testcontainers/Mockito warnings were observed during test execution and are informational for future toolchain hardening
- Handoff: `READY_FOR_PO`

## Scope reviewed

- `backend/platform-core/src/main/java/com/darkfactory/education/platform/config/ObjectMapperConfiguration.java`
- `backend/platform-core/src/main/java/com/darkfactory/education/platform/configuration/`
- `backend/platform-core/src/main/resources/db/migration/V7__create_configuration_inheritance_tables.sql`
- `backend/platform-core/src/test/java/com/darkfactory/education/platform/EducationSystemApplicationIT.java`
- `backend/platform-core/src/test/java/com/darkfactory/education/platform/configuration/ScopePathTest.java`
- `df/artifacts/STORY-030/task.md`
- `df/artifacts/STORY-030/solution-design.md`
- `df/artifacts/STORY-030/backend/dev-notes.md`
- `df/artifacts/STORY-030/backend/handoff-to-qa.md`
- `df/runtime/board.md`
- `df/runtime/backend-dev-board.md`

## Environment

- OS: Windows
- Shell: Windows PowerShell 5.1
- Java: 25.0.2 LTS
- Maven Wrapper: 3.9.15
- Container runtime used by tests: Docker Desktop via Testcontainers
- Test database: `postgres:17-alpine`
- Branch snapshot: `master...origin/master` at `4ec8297` with many pre-existing unrelated workspace changes; QA review was scoped to the `STORY-030` backend and documentation paths

## Test cases and results

| Test case | Command/source | Result | Notes |
|---|---|---|---|
| Focused repository status snapshot | `git --no-pager status --short --branch -- backend/platform-core df/artifacts/STORY-030 df/runtime` | PASS | Confirms QA kept review scoped to the declared backend/task/runtime paths while the workspace still contains many unrelated pre-existing tracked changes |
| Configuration migration schema inspection | `backend/platform-core/src/main/resources/db/migration/V7__create_configuration_inheritance_tables.sql` | PASS | Verified the new `configuration_field_definition` and `configuration_value` tables, tenant FK, value/merge check constraints, allowed scope-type check, and supporting indexes |
| Generic configuration service inspection | `ConfigurationController`; `ConfigurationService`; `ScopePath`; `ConfigurationValueType` | PASS | Verified country-rooted ordered scope resolution, lower-scope override validation, ancestor lock rejection, normalized `STRING` / `STRING_SET` values, deterministic `REPLACE` / `EXTEND_SET` semantics, and no organization-module coupling |
| Configuration package hardcoding scan | `grep` on `backend/platform-core/src/main/java/com/darkfactory/education/platform/configuration/**/*.java` | PASS | No country names, sample scope keys, or organization-module references were found in the new configuration package |
| Focused contract test pass | `./mvnw.cmd -f backend/pom.xml -pl platform-core "-Dtest=ScopePathTest,EducationSystemApplicationIT#countryLevelConfigurationResolvesAtInstitutionLevelWithoutOverride+institutionLevelOverrideTakesPrecedenceOverInheritedConfiguration+lockedCountryConfigurationRejectsLowerScopeOverride+extensibleConfigurationMergesInheritedAndLocalOptions+regionLevelConfigurationChangeFlowsToInstitutionWithinRegion+apiDocsContainsConfigurationResolveEndpoint" test` | PASS | `BUILD SUCCESS`; 9 tests total, including 6 targeted acceptance/OpenAPI integration scenarios; finished `2026-05-24T21:12:14+02:00` |
| Backend reactor verification | `./mvnw.cmd -f backend/pom.xml -pl platform-core -am clean verify` | PASS | `BUILD SUCCESS`; 6 unit tests + 26 integration tests; Flyway validated and applied migrations `1..7`; finished `2026-05-24T21:12:33+02:00` |
| Full parent verification | `./mvnw.cmd clean verify` | PASS | `BUILD SUCCESS`; full reactor stayed green with `platform-core` still running 6 unit tests + 26 integration tests; finished `2026-05-24T21:15:24+02:00` |
| Country default inheritance | `EducationSystemApplicationIT.countryLevelConfigurationResolvesAtInstitutionLevelWithoutOverride` | PASS | Confirms institution-level resolution returns the country value when no lower-scope override exists |
| Institution override precedence | `EducationSystemApplicationIT.institutionLevelOverrideTakesPrecedenceOverInheritedConfiguration` | PASS | Confirms the institution-scoped value wins over the inherited country value |
| Ancestor lock rejection | `EducationSystemApplicationIT.lockedCountryConfigurationRejectsLowerScopeOverride` | PASS | Confirms lower-scope override attempts fail with HTTP `409 Conflict` when an ancestor value is locked |
| Extensible merge behavior | `EducationSystemApplicationIT.extensibleConfigurationMergesInheritedAndLocalOptions` | PASS | Confirms `EXTEND_SET` preserves inherited entries, appends local additions, deduplicates overlap, and reports contributing scopes |
| Region propagation | `EducationSystemApplicationIT.regionLevelConfigurationChangeFlowsToInstitutionWithinRegion` | PASS | Confirms a region-scoped value is inherited by institutions in that region and updates propagate on re-resolution |
| Flyway regression | `EducationSystemApplicationIT.flywayBootstrapMigrationsAreAppliedOnStartup`; `reRunningFlywayDoesNotReapplyExistingMigrations`; `flywayAppliesMigrationsInVersionOrder` | PASS | Confirms the migration chain is ordered `1..7`, lands on version `7`, and remains idempotent |
| OpenAPI regression | `EducationSystemApplicationIT.apiDocsContainsConfigurationResolveEndpoint` | PASS | Confirms `/api/v1/platform/configuration/resolve` is present in generated `/api-docs` |

## Acceptance criteria coverage

| Criterion | Result | Evidence |
|---|---|---|
| Given a country-level setting, when queried at institution level, then the country value is returned if not overridden | PASS | `EducationSystemApplicationIT.countryLevelConfigurationResolvesAtInstitutionLevelWithoutOverride`; `ConfigurationService.resolveReplace` |
| Given an institution-level override, when queried, then the institution value takes precedence | PASS | `EducationSystemApplicationIT.institutionLevelOverrideTakesPrecedenceOverInheritedConfiguration`; ordered scope resolution in `ConfigurationService.resolveReplace` |
| Given a locked field at country level, when a lower level tries to override, then the override is rejected | PASS | `EducationSystemApplicationIT.lockedCountryConfigurationRejectsLowerScopeOverride`; `ConfigurationService.validateNoLockedAncestors` |
| Given an extensible field, when a lower level adds options, then both inherited and local options are available | PASS | `EducationSystemApplicationIT.extensibleConfigurationMergesInheritedAndLocalOptions`; `ConfigurationService.resolveExtendSet` |
| Given a configuration change at region level, when queried at institution level within that region, then the new value is inherited | PASS | `EducationSystemApplicationIT.regionLevelConfigurationChangeFlowsToInstitutionWithinRegion`; upsert + re-resolve path through `ConfigurationController` / `ConfigurationService` |

## Notes on limitations

- QA used automated integration coverage plus direct source inspection instead of launching a separate long-running local server because the integration suite already exercises the new configuration endpoints, migration lifecycle, OpenAPI exposure, and PostgreSQL-backed persistence against a real ephemeral database.
- Existing tenant bootstrap fixtures still use a concrete deployment example from `STORY-021` (`PL`, `Europe/Warsaw`, `pl-PL`) for the active tenant context. QA verified that the new configuration engine itself stays framework-generic because the configuration package contains no country-specific logic and resolves only against the active tenant abstraction.

