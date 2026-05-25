# QA Report - STORY-031

## QA summary

PASS

## Environment

- OS: macOS
- Runtime: Java 26.0.1, Maven Wrapper (`sh ./mvnw`), Spring Boot 4.1.0-SNAPSHOT, Testcontainers with Docker Desktop, PostgreSQL 17.10
- Branch/commit: `master` @ `2a18f8c`
- Test data: isolated Testcontainers PostgreSQL database migrated by Flyway `V1..V11`; configuration/audit/inheritance-break scenarios created by `EducationSystemApplicationIT`

## Acceptance criteria coverage

| Criterion | Result | Evidence |
|---|---|---|
| Given a locked field override attempt, when submitted, then a validation error is returned | PASS | Code inspection of `ConfigurationController#validate` and `ConfigurationService#validateValue`; automated verification in `EducationSystemApplicationIT#validationEndpointReturnsConflictForLockedLowerScopeOverrideAttempt`; focused `sh ./mvnw -f backend/pom.xml -pl platform-core -am "-Dit.test=EducationSystemApplicationIT" verify` passed with `44/44` |
| Given an inheritance break request, when submitted with justification, then it is recorded with audit trail | PASS | Code inspection of `ConfigurationService#submitInheritanceBreakRequest`, `V11__create_configuration_inheritance_break_request_table.sql`, and audit write path; automated verification in `EducationSystemApplicationIT#inheritanceBreakRequestIsRecordedAndAudited`; focused suite passed with `44/44` |
| Given a country config update, when institutions have overrides, then a compatibility report lists affected institutions | PASS | Code inspection of `ConfigurationService#generateCompatibilityReport` / `#buildCompatibilityImpact`; automated verification in `EducationSystemApplicationIT#compatibilityReportListsAffectedInstitutionOverridesForCountryChange`; focused suite passed with `44/44` |

## Automated tests

| Test suite | Command/source | Result | Notes |
|---|---|---|---|
| File-level static/error scan | `get_errors` on `ConfigurationController.java`, `ConfigurationService.java`, `V11__create_configuration_inheritance_break_request_table.sql`, and `EducationSystemApplicationIT.java` | PASS | No Java/test errors. SQL file reported only an IDE warning that no data source is configured for advanced assistance. |
| Focused STORY-031 integration verification | `cd "/Users/Viachaslau_Karnaushanka/Downloads/DF Education System Framework" && sh ./mvnw -f backend/pom.xml -pl platform-core -am "-Dit.test=EducationSystemApplicationIT" verify` | PASS | `EducationSystemApplicationIT` passed `44/44`; independently verified acceptance criteria, `/api-docs` exposure, Flyway startup, and nearby configuration/audit regressions. |
| Full backend regression | `cd "/Users/Viachaslau_Karnaushanka/Downloads/DF Education System Framework" && sh ./mvnw -f backend/pom.xml clean verify` | PASS | Entire backend reactor built and verified successfully after a clean rebuild. |

## Integration tests

| Scenario | Result | Evidence |
|---|---|---|
| Locked lower-scope override is rejected by validation endpoint | PASS | `EducationSystemApplicationIT#validationEndpointReturnsConflictForLockedLowerScopeOverrideAttempt`; focused integration suite `44/44` |
| Inheritance-break request persists and emits audit evidence | PASS | `EducationSystemApplicationIT#inheritanceBreakRequestIsRecordedAndAudited`; focused integration suite `44/44` |
| Compatibility report lists institution-scope impacts for a projected country update | PASS | `EducationSystemApplicationIT#compatibilityReportListsAffectedInstitutionOverridesForCountryChange`; focused integration suite `44/44` |
| OpenAPI publishes the new configuration endpoints | PASS | `EducationSystemApplicationIT#apiDocsContainsConfigurationValidationEndpoints`; focused integration suite `44/44` |
| Migration chain applies through `V11` on a fresh database and remains idempotent on rerun | PASS | Focused and full verification logs show Flyway validating/applying `V1..V11` on empty schema and reporting schema version `11` as up to date on later startup |

## Manual checks

| Scenario | Result | Evidence |
|---|---|---|
| New endpoints map to expected HTTP semantics | PASS | `ConfigurationController.java` exposes `POST /validate`, `POST /inheritance-break-requests`, `POST /compatibility-report`; validation returns `409` on blocked overrides and request submission returns `201` |
| Inheritance-break workflow stays request-only and does not bypass locks | PASS | `ConfigurationService#submitInheritanceBreakRequest` requires a locked ancestor, stores status `SUBMITTED`, records audit, and does not write a configuration override |
| Compatibility reporting remains framework-generic and avoids organization coupling | PASS | `ConfigurationService#generateCompatibilityReport` is limited to `COUNTRY` scope input and returns generic institution scope identifiers/paths rather than organization metadata |
| Delivery-lane documentation is present and lane ownership stayed backend-only | PASS | `df/artifacts/STORY-031/backend/dev-notes.md`, `df/artifacts/STORY-031/backend/handoff-to-qa.md`, `df/runtime/backend-dev-board.md`; no frontend/devops/data/design artifact folders were used |

## Defects

- None.

## Risks

- Compatibility reporting is intentionally limited to projected `COUNTRY`-scope updates in this story; later stories may expand the model if product requirements require broader ancestor analysis.
- Non-blocking verification warnings were observed from Jansi/native-access, Spring Boot generated development password logging, SpringDoc enabled-by-default notices, Mockito agent loading, and Testcontainers credential-helper lookup before successful fallback.

## QA decision

Ready for PO: Yes

## QA Result: PASS

- Task: `STORY-031`
- Acceptance criteria covered: Yes — all three criteria were independently verified through focused integration tests plus code/migration inspection.
- Unit tests: `get_errors` file-level scan plus Maven unit-test phase in both verification commands; PASS.
- Integration tests: Focused `EducationSystemApplicationIT` verify and full backend `clean verify`; PASS.
- Manual checks: Controller/service/migration/OpenAPI and lane-artifact inspection completed; PASS.
- Regression checks: Full backend clean verify passed after the focused STORY-031 verification.
- Risks: Limited country-scope compatibility-report coverage only; non-blocking tooling/runtime warnings documented above.
- Handoff: `READY_FOR_PO`

