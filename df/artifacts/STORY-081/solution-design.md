# Solution Design - STORY-081

## Summary

Add the first backend-only RBAC foundation on top of `STORY-080` so tenant-scoped users can receive predefined education roles, protected APIs can authorize access through generic scope-aware permission checks, and later school/person features can plug into the same authorization engine without country-specific code.

## Context

`STORY-080` established the first runnable authentication baseline in `backend/identity-access`: tenant-scoped users, bootstrap-admin creation, password hashing, JWT login, protected identity routes, and audit convergence. The roadmap and MVP definition still list the user/role model and security baseline as unfinished Phase 1 work. Without RBAC, every newly authenticated user is effectively under-modeled, and later features such as translation management, configuration administration, class/student workflows, and parent/student visibility would either stay over-permissive or introduce ad-hoc authorization logic.

The repository does not yet contain full school, institution, class, student, or parent domain models. The first RBAC implementation therefore must establish the reusable role and scope machinery now, while proving access boundaries through representative protected backend routes and generic resource-scope descriptors that later modules can reuse.

## Requirements and acceptance criteria

- Support predefined roles: `country-admin`, `region-admin`, `city-admin`, `institution-admin`, `teacher`, `student`, `parent`
- Ensure a user receives only the permissions associated with their assigned role(s)
- Deny a teacher when attempting to access another school's data
- Permit an institution-admin to manage resources belonging to their own school
- Restrict a parent to viewing only their own child's data

## Proposed solution

Implement the RBAC foundation primarily in `backend/identity-access`, with minimal request-authz wiring in `backend/platform-core` because that module hosts the runnable Spring Boot application.

### 1. Separate authentication identity from authorization assignments

Keep the `STORY-080` authentication baseline intact and layer authorization on top of it. The JWT login contract should remain stable (`POST /api/v1/identity/auth/login` still returns bearer access tokens), but the backend principal model should expand so authorization uses persisted role assignments instead of the current two-value bootstrap authority alone.

Recommended approach:

- keep the JWT payload minimal (user id, tenant id, username, expiry, and only minimal compatibility claims if needed)
- load current role assignments from persistence during authenticated request handling so role changes take effect without redesigning the login contract
- retain a bootstrap-compatible operational administrator path by mapping the bootstrap account to a tenant-rooted `COUNTRY_ADMIN` role automatically

This keeps tokens small, avoids stale long-lived role claims, and lets later stories extend authorization without breaking login consumers.

### 2. Introduce a predefined role catalogue

Add a generic predefined role catalogue in code, for example an enum or equivalent registry owned by `identity-access`:

- `COUNTRY_ADMIN`
- `REGION_ADMIN`
- `CITY_ADMIN`
- `INSTITUTION_ADMIN`
- `TEACHER`
- `STUDENT`
- `PARENT`

Each role should map to a stable permission set, for example:

- tenant-wide platform management for `COUNTRY_ADMIN`
- descendant-scope administration for `REGION_ADMIN` and `CITY_ADMIN`
- institution-scoped management for `INSTITUTION_ADMIN`
- teaching/academic access limited to assigned institution/class context for `TEACHER`
- self-read student visibility for `STUDENT`
- dependent-student read visibility for `PARENT`

The permission model must stay generic and avoid country-specific branches.

### 3. Persist role assignments with generic scope descriptors

Add forward-only RBAC persistence, likely via a new Flyway migration under `backend/platform-core/src/main/resources/db/migration/`, with a table such as `identity_role_assignment` containing at minimum:

- `assignment_id` UUID primary key
- `tenant_id` UUID not null
- `user_id` UUID not null referencing `identity_user`
- `role_code` varchar not null
- a generic resource scope descriptor (for example a serialized scope path or scope-type/scope-key pair)
- timestamps
- uniqueness constraints preventing duplicate role assignment for the same user and scope

Use generic scope descriptors rather than country- or school-specific tables. A practical first approach is a scope path aligned with the configuration-engine hierarchy ideas from `STORY-030`, extended so authorization can express paths such as:

- tenant root
- region
- city
- institution
- class
- student/self/dependent-student

This allows later modules to supply resource scope context without changing the RBAC data model.

### 4. Authorization service and policy evaluation

Create an authorization service in `identity-access` that:

- loads active role assignments for the authenticated user and tenant
- resolves whether a requested action is permitted for a supplied resource scope path and permission key
- supports descendant-scope matching (for example institution-admin can manage resources inside their own institution but not another institution)
- supports parent/self constraints through explicit resource-subject scope matching rather than hardcoded one-off controller logic

The first implementation can remain code-driven, with a static mapping from predefined role to permission set, while resource scope matching stays data-driven.

### 5. Minimal admin role-management API

To make the RBAC foundation testable and operable, expose a minimal backend-only admin path for role assignment, such as:

- `POST /api/v1/identity/role-assignments`
- `GET /api/v1/identity/users/{userId}/role-assignments`

Recommended behavior:

- only `COUNTRY_ADMIN` or another explicitly authorized administrative role can assign roles initially
- assigning a role validates tenant consistency and scope format
- duplicate assignments are rejected cleanly
- each assignment/revocation is audited through the generic platform audit foundation from `STORY-013`

This story does not require a polished role-management workflow; it only needs enough backend contract to support the acceptance criteria and future stories.

### 6. Representative protected backend routes for Phase 1 proof

Because full school/student/person modules do not yet exist, prove the acceptance criteria through representative protected backend routes that carry explicit resource-scope context rather than waiting for later domain stories.

Recommended approach:

- protect selected existing mutable APIs such as translation/configuration administration with administrative permissions where appropriate
- add minimal representative RBAC test routes if necessary (for example institution-scoped management and dependent-student visibility probes) under an identity-local or platform-local namespace clearly documented as authorization proof endpoints
- ensure the representative routes express real authorization decisions against scope paths, not trivial hardcoded responses

This keeps the story aligned with the backlog while avoiding premature invention of the full school domain.

### 7. Audit and OpenAPI convergence

Role assignment mutations are meaningful security changes and should record audit events via the existing platform audit service. New RBAC endpoints and representative protected routes must also appear in `/api-docs`.

### 8. Module and package placement

Expected code placement:

- `backend/identity-access/src/main/java/.../auth/**` or `.../authorization/**` for role enums, permission model, role-assignment repository/service, and authorization evaluator
- `backend/platform-core/src/main/java/.../security/**` only for incremental Spring Security runtime wiring needed to consult the authorization service
- Flyway migration(s) under `backend/platform-core/src/main/resources/db/migration/`
- backend integration coverage primarily in `backend/platform-core/src/test/java/.../EducationSystemApplicationIT.java`

## Alternatives considered

- **Delay RBAC until school/person modules exist**: rejected because MVP security and later protected features need a reusable authorization foundation before domain expansion
- **Encode all roles and scopes directly into JWT claims**: rejected as the default because it risks large tokens, stale role changes, and a harder-to-evolve login contract
- **Implement a fully data-driven policy engine now**: rejected because it is larger than the story needs; a predefined role catalogue with generic scope matching is the smallest viable Phase 1 step
- **Hardcode one-off teacher/parent checks per endpoint**: rejected because it would create brittle, non-reusable authorization logic that later modules would need to replace

## Files/components likely affected

- `df/artifacts/STORY-081/task.md`
- `df/artifacts/STORY-081/solution-design.md`
- `df/artifacts/STORY-081/decision-018-phase-1-rbac-foundation.md`
- `df/artifacts/STORY-081/handoffs.md`
- `df/backlog/architecture-direction.md`
- `df/runtime/board.md`
- `df/runtime/backend-dev-board.md`
- `df/runtime/activity-log.md`
- `df/runtime/decisions.md`
- expected backend implementation targets under `backend/identity-access/src/main/java/**`
- likely incremental security wiring under `backend/platform-core/src/main/java/**`
- likely Flyway migration(s) under `backend/platform-core/src/main/resources/db/migration/`
- backend tests under `backend/platform-core/src/test/**`

## Data model changes

- add tenant-scoped role-assignment persistence for predefined RBAC roles
- add generic scope-descriptor storage suitable for institution/self/dependent-student visibility boundaries
- preserve compatibility with the existing `identity_user` table and bootstrap-admin login path from `STORY-080`

## API/contract changes

- add minimal admin role-assignment endpoints
- extend authenticated principal/authorization handling so protected routes can enforce permissions based on persisted role assignments
- add representative protected routes or strengthen selected existing routes to prove institution, teacher, and parent authorization boundaries
- expose the new RBAC contract through OpenAPI

## UI/UX impact

- None in this story. The accepted scope is backend-only authorization foundation with no frontend role-management screens or designer deliverables.

## Security and privacy considerations

- preserve externalized auth secrets from `STORY-080`; do not introduce hardcoded secrets or country-specific authorization logic
- ensure least-privilege defaults: users without the required role assignment must be denied
- ensure parent/student visibility checks use explicit scoped relationships rather than broad tenant-wide read access
- audit security-relevant mutations such as role assignment and revocation
- defer MFA, external federation, and full ABAC intentionally rather than introducing partial versions here

## Performance/scalability considerations

- request-time role loading is acceptable for the first implementation because current traffic and assignment counts are low
- add efficient tenant/user lookup and uniqueness constraints so authorization checks stay cheap
- if repeated authorization lookups become noisy, later stories may add short-lived caches without changing the persisted RBAC model

## Test strategy

`backend-dev` should provide strong backend verification, including:

- migration tests proving RBAC persistence is created correctly and in order after `V9`
- role-assignment tests proving predefined roles can be assigned and queried within the active tenant
- authorization tests proving a teacher is denied access outside their school scope
- authorization tests proving an institution-admin can manage resources in their own institution scope
- authorization tests proving a parent can view only their own dependent-student scope and is denied other students
- regression checks proving login remains compatible and new endpoints appear in `/api-docs`
- audit verification proving role-assignment mutations create audit events where feasible within scope
- focused backend verification plus backend reactor verification

## Deployment/migration plan

- add forward-only Flyway migration(s) for RBAC persistence after the existing identity-user migration
- bootstrap or reconcile the initial deployment administrator into the new RBAC model so the system remains operable after migration
- keep the change backward-compatible with the current `STORY-080` login path

## Rollback plan

- revert the new RBAC migration(s), authorization service, and route protections if the role foundation is rejected before downstream stories depend on it
- if rollback is required temporarily, keep authenticated routes functional under the simpler `STORY-080` auth baseline rather than leaving partially enforced authorization behavior in place

## Risks and mitigations

- **Risk:** role/scope modeling grows into unfinished domain modeling
  - **Mitigation:** keep the story focused on predefined role assignments, generic scope descriptors, representative protected routes, and reusable authorization services only
- **Risk:** bootstrap-admin migration breaks operability after `STORY-080`
  - **Mitigation:** reconcile the bootstrap administrator automatically into the new RBAC model and retain backward-compatible login behavior
- **Risk:** the authorization design becomes too static for later phases
  - **Mitigation:** keep role definitions stable in code now, but store assignments/scopes generically so later ABAC or richer domain modules can plug in without schema replacement

## Open questions

- None blocking `backend-dev`. If implementation discovers that representative protected routes need a small shared authorization-test fixture to express institution and dependent-student scope safely, document it in backend notes and keep the addition minimal.

## Implementation lane

- Lane: `backend-dev`
- Subdashboard: `df/runtime/backend-dev-board.md`
- Artifact folder for implementation notes: `df/artifacts/STORY-081/backend/`

## SA decision

Approved for development: Yes

