# QA Report - STORY-081

## QA summary

PASS

## Environment

- OS: Windows
- Runtime: Java 25.0.2, Maven Wrapper (`mvnw.cmd`), Spring Boot 4.1.0-SNAPSHOT backend, MockMvc integration tests, Testcontainers PostgreSQL `17-alpine`
- Branch/commit: `master` @ `b7543d9481300073ad5cdb2cf46e6d6237722d83`
- Test data: Active deployment tenant bootstrap data plus synthetic institution/student scope identifiers (`school-01`, `school-02`, `student-01`, `student-02`) created by `EducationSystemApplicationIT`

## Acceptance criteria coverage

| Criterion | Result | Evidence |
|---|---|---|
| Given roles (`country-admin`, `region-admin`, `city-admin`, `institution-admin`, `teacher`, `student`, `parent`), when assigned, then the user has only permissions matching the role | PASS | Source inspection of `backend/identity-access/src/main/java/com/darkfactory/education/identityaccess/auth/IdentityRoleCode.java`, `IdentityPermission.java`, `IdentityAuthorizationService.java`, `IdentityRoleAssignmentService.java`; automated coverage in `EducationSystemApplicationIT#countryAdminCanAssignTeacherRoleAndTeacherCannotAccessAnotherInstitution`; focused suite passed `40/40` |
| Given a teacher role, when they try to access another school's data, then access is denied | PASS | `EducationSystemApplicationIT#countryAdminCanAssignTeacherRoleAndTeacherCannotAccessAnotherInstitution`; representative route `GET /api/v1/identity/access/institutions/{institutionKey}/teaching-view`; focused suite passed |
| Given an institution-admin, when they manage their school, then all school operations are permitted | PASS | `EducationSystemApplicationIT#institutionAdminCanManageOwnInstitutionButNotAnother`; representative route `POST /api/v1/identity/access/institutions/{institutionKey}/management`; focused suite passed |
| Given a parent role, when they view data, then only their child's data is visible | PASS | `EducationSystemApplicationIT#parentCanViewOnlyOwnChildStudentScope`; representative route `GET /api/v1/identity/access/students/{institutionKey}/{studentKey}/view`; focused suite passed |

## Automated tests

| Test suite | Command/source | Result | Notes |
|---|---|---|---|
| Focused RBAC integration verification | `Set-Location "C:\Users\Viach\IdeaProjects\DF Education System Framework"; .\mvnw.cmd -f backend\pom.xml -pl platform-core -am "-Dit.test=EducationSystemApplicationIT" verify` | PASS | `EducationSystemApplicationIT` passed `40/40`; verified migration `V10`, bootstrap country-admin reconciliation, teacher/institution-admin/parent scope boundaries, audit-event convergence, and `/api-docs` exposure |
| Full backend reactor verification | `Set-Location "C:\Users\Viach\IdeaProjects\DF Education System Framework"; .\mvnw.cmd -f backend\pom.xml clean verify` | PASS | Full backend reactor remained green after the RBAC changes |
| IDE/static sanity scan | `get_errors` on the RBAC migration, authorization, controller, filter, and integration-test files | PASS | No IDE errors found |

## Integration tests

| Scenario | Result | Evidence |
|---|---|---|
| Flyway applies RBAC migration after existing identity baseline and keeps version ordering stable | PASS | `EducationSystemApplicationIT#flywayBootstrapMigrationsAreAppliedOnStartup`, `#flywayAppliesMigrationsInVersionOrder`; migration file `backend/platform-core/src/main/resources/db/migration/V10__create_identity_role_assignment_table.sql` |
| Bootstrap admin is reconciled into tenant-root `COUNTRY_ADMIN` assignment | PASS | `EducationSystemApplicationIT#bootstrapAdminIsCreatedForTheActiveDeploymentTenant`; source inspection of `IdentityRoleAssignmentService#ensureBootstrapCountryAdmin` |
| JWT-authenticated requests are enriched with persisted role assignments server-side | PASS | Source inspection of `backend/identity-access/src/main/java/com/darkfactory/education/identityaccess/auth/AuthenticatedPrincipalRoleService.java` and `backend/platform-core/src/main/java/com/darkfactory/education/platform/security/JwtAuthenticationFilter.java`; focused suite behavior confirms loaded roles affect access decisions |
| Role-assignment mutations create audit events | PASS | `EducationSystemApplicationIT#countryAdminCanAssignTeacherRoleAndTeacherCannotAccessAnotherInstitution`; audit query expectations in the integration test |
| OpenAPI documents the RBAC endpoints | PASS | `EducationSystemApplicationIT#apiDocsContainsIdentityEndpoints`; `/api-docs` assertions cover role-assignment and access-proof routes |

## Manual checks

| Scenario | Result | Evidence |
|---|---|---|
| Migration `V10` remains framework-generic and constrained to predefined roles with tenant/user indexes and uniqueness protection | PASS | Manual review of `backend/platform-core/src/main/resources/db/migration/V10__create_identity_role_assignment_table.sql` |
| Role loading and authorization stay backend-only and generic, without country-specific branching | PASS | Manual review of `IdentityRoleCode.java`, `IdentityScopePath.java`, `IdentityAuthorizationService.java`, `JwtAuthenticationFilter.java` |
| Lane artifact ownership is respected and QA inputs are present | PASS | Reviewed `df/artifacts/STORY-081/backend/dev-notes.md`, `df/artifacts/STORY-081/backend/handoff-to-qa.md`, `df/runtime/backend-dev-board.md`, and current task/runtime state |

## Defects

- None.

## Risks

- Representative authorization-proof endpoints are still temporary backend-only evidence paths until later domain stories provide richer institution/student resources.
- Existing non-identity endpoints were intentionally not broadly re-scoped in this story to avoid silent authorization expansion across previously accepted work.

## QA decision

Ready for PO: Yes

