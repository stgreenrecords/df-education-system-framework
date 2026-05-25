# Backend Handoff to QA - STORY-081

## Summary

`backend-dev` completed the backend-only Phase 1 RBAC foundation on top of the accepted authentication baseline from `STORY-080`.

The implementation adds tenant-scoped predefined role assignments, generic scope-aware authorization checks, bootstrap-admin reconciliation into the new RBAC model, minimal role-assignment APIs, representative backend authorization-proof routes, audit convergence for role-assignment mutations, and expanded integration coverage.

## Acceptance criteria mapping

1. **Given roles (`country-admin`, `region-admin`, `city-admin`, `institution-admin`, `teacher`, `student`, `parent`), when assigned, then the user has only permissions matching the role**
   - Verified by `EducationSystemApplicationIT#countryAdminCanAssignTeacherRoleAndTeacherCannotAccessAnotherInstitution`
   - Teacher receives `teacher` role assignment, can access the teaching-view route in their own institution, and is denied the institution-management route.
   - Role assignment listing is verified through `GET /api/v1/identity/users/{userId}/role-assignments`.

2. **Given a teacher role, when they try to access another school's data, then access is denied**
   - Verified by `EducationSystemApplicationIT#countryAdminCanAssignTeacherRoleAndTeacherCannotAccessAnotherInstitution`
   - Endpoint: `GET /api/v1/identity/access/institutions/{institutionKey}/teaching-view`

3. **Given an institution-admin, when they manage their school, then all school operations are permitted**
   - Verified by `EducationSystemApplicationIT#institutionAdminCanManageOwnInstitutionButNotAnother`
   - Endpoint: `POST /api/v1/identity/access/institutions/{institutionKey}/management`

4. **Given a parent role, when they view data, then only their child's data is visible**
   - Verified by `EducationSystemApplicationIT#parentCanViewOnlyOwnChildStudentScope`
   - Endpoint: `GET /api/v1/identity/access/students/{institutionKey}/{studentKey}/view`

## Files changed / created

- `backend/platform-core/src/main/resources/db/migration/V10__create_identity_role_assignment_table.sql`
- `backend/identity-access/src/main/java/com/darkfactory/education/identityaccess/auth/AuthenticatedPrincipalRoleService.java`
- `backend/identity-access/src/main/java/com/darkfactory/education/identityaccess/auth/AuthenticatedUserPrincipal.java`
- `backend/identity-access/src/main/java/com/darkfactory/education/identityaccess/auth/AuthorizationDeniedException.java`
- `backend/identity-access/src/main/java/com/darkfactory/education/identityaccess/auth/CreateRoleAssignmentRequest.java`
- `backend/identity-access/src/main/java/com/darkfactory/education/identityaccess/auth/CurrentUserResponse.java`
- `backend/identity-access/src/main/java/com/darkfactory/education/identityaccess/auth/DuplicateRoleAssignmentException.java`
- `backend/identity-access/src/main/java/com/darkfactory/education/identityaccess/auth/IdentityAccessProbeController.java`
- `backend/identity-access/src/main/java/com/darkfactory/education/identityaccess/auth/IdentityAccessProbeResponse.java`
- `backend/identity-access/src/main/java/com/darkfactory/education/identityaccess/auth/IdentityAuditPort.java`
- `backend/identity-access/src/main/java/com/darkfactory/education/identityaccess/auth/IdentityAuthorizationService.java`
- `backend/identity-access/src/main/java/com/darkfactory/education/identityaccess/auth/IdentityBootstrapService.java`
- `backend/identity-access/src/main/java/com/darkfactory/education/identityaccess/auth/IdentityPermission.java`
- `backend/identity-access/src/main/java/com/darkfactory/education/identityaccess/auth/IdentityRoleAssignmentController.java`
- `backend/identity-access/src/main/java/com/darkfactory/education/identityaccess/auth/IdentityRoleAssignmentRecord.java`
- `backend/identity-access/src/main/java/com/darkfactory/education/identityaccess/auth/IdentityRoleAssignmentRepository.java`
- `backend/identity-access/src/main/java/com/darkfactory/education/identityaccess/auth/IdentityRoleAssignmentResponse.java`
- `backend/identity-access/src/main/java/com/darkfactory/education/identityaccess/auth/IdentityRoleAssignmentService.java`
- `backend/identity-access/src/main/java/com/darkfactory/education/identityaccess/auth/IdentityRoleCode.java`
- `backend/identity-access/src/main/java/com/darkfactory/education/identityaccess/auth/IdentityScopeNode.java`
- `backend/identity-access/src/main/java/com/darkfactory/education/identityaccess/auth/IdentityScopePath.java`
- `backend/identity-access/src/main/java/com/darkfactory/education/identityaccess/auth/IdentityScopeType.java`
- `backend/identity-access/src/main/java/com/darkfactory/education/identityaccess/auth/IdentityUserController.java`
- `backend/identity-access/src/main/java/com/darkfactory/education/identityaccess/auth/IdentityUserRepository.java`
- `backend/identity-access/src/main/java/com/darkfactory/education/identityaccess/auth/IdentityUserService.java`
- `backend/platform-core/src/main/java/com/darkfactory/education/platform/identity/PlatformIdentityAuditPort.java`
- `backend/platform-core/src/main/java/com/darkfactory/education/platform/security/JwtAuthenticationFilter.java`
- `backend/platform-core/src/main/java/com/darkfactory/education/platform/security/SecurityConfiguration.java`
- `backend/platform-core/src/test/java/com/darkfactory/education/platform/EducationSystemApplicationIT.java`

## Tests run

| Check | Command | Result | Notes |
|---|---|---|---|
| Focused RBAC integration verification | `Set-Location "C:\Users\Viach\IdeaProjects\DF Education System Framework"; .\mvnw.cmd -f backend\pom.xml -pl platform-core -am "-Dit.test=EducationSystemApplicationIT" verify` | PASS | `EducationSystemApplicationIT` passed `40/40`, including migration `V10`, bootstrap country-admin reconciliation, role assignment audit events, teacher/institution-admin/parent scope checks, and `/api-docs` exposure |
| Full backend regression | `Set-Location "C:\Users\Viach\IdeaProjects\DF Education System Framework"; .\mvnw.cmd -f backend\pom.xml clean verify` | PASS | Backend reactor remained green after the RBAC changes |
| IDE error scan | `get_errors` on edited/new RBAC Java, SQL, and test files | PASS | No Java/test errors remained; SQL file reported only the expected datasource-assistance warning |

## QA focus areas

- Confirm the new `V10` migration applies cleanly after `V9` and bootstrap admin reconciliation creates a tenant-root `COUNTRY_ADMIN` role assignment.
- Inspect `JwtAuthenticationFilter` plus `AuthenticatedPrincipalRoleService` to confirm role assignments are loaded server-side during authenticated requests.
- Verify role assignment endpoints and representative access-proof endpoints appear in `/api-docs`.
- Re-run the focused integration suite and confirm teacher/institution-admin/parent scope boundaries behave exactly as documented.
- Confirm the RBAC changes stay backend-only and framework-generic, with no country-specific authorization logic.

## Known non-blocking warnings

- Spring Boot still prints the generated security-password warning during startup.
- SpringDoc enabled-by-default warnings remain visible.
- Mockito/Testcontainers warnings remain visible in test output.
- These warnings were observed during successful verification and did not affect the RBAC acceptance criteria.

