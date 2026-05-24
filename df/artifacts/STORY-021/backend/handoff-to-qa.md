# Backend to QA Handoff - STORY-021

## Summary

Backend implementation completed the first sovereign deployment tenant foundation in `backend/platform-core`.

The implementation now provides:

- a persisted `platform_tenant` table created by Flyway migration `V6`
- externalized deployment tenant bootstrap properties (`EDU_TENANT_*`)
- idempotent startup bootstrap logic for the active deployment tenant
- a reusable `TenantContextService` for backend module consumption
- a minimal validation endpoint at `GET /api/v1/platform/tenant`
- unit and integration coverage for normalization, bootstrap, endpoint behavior, and migration/version expectations

## Files for QA review

- `backend/platform-core/src/main/java/com/darkfactory/education/platform/tenant/`
- `backend/platform-core/src/main/resources/application.properties`
- `backend/platform-core/src/main/resources/db/migration/V6__create_platform_tenant.sql`
- `backend/platform-core/src/test/java/com/darkfactory/education/platform/EducationSystemApplicationIT.java`
- `backend/platform-core/src/test/java/com/darkfactory/education/platform/tenant/TenantPropertiesTest.java`
- `df/artifacts/STORY-021/backend/dev-notes.md`

## Validation performed by backend-dev

| Check | Command/source | Result | Notes |
|---|---|---|---|
| Backend reactor verify | `.\mvnw.cmd -f backend\pom.xml -pl platform-core -am clean verify` | PASS | `BUILD SUCCESS`; 23 tests total (3 unit + 20 integration) |
| Full parent verify | `.\mvnw.cmd clean verify` | PASS | `BUILD SUCCESS`; broader multi-module verification remained green |
| Focused workspace status snapshot | `git --no-pager status --short --branch -- backend\platform-core df\artifacts\STORY-021 df\runtime` | PASS | Workspace still contains unrelated pre-existing changes outside `STORY-021` scope |

## QA focus areas

1. Confirm `platform_tenant` is created via Flyway and contains one active deployment tenant with country code, display name, timezone, and locale.
2. Re-run the backend and full-parent Maven verification commands.
3. Confirm tenant bootstrap remains idempotent and does not create duplicate tenant rows.
4. Confirm `GET /api/v1/platform/tenant` returns the active tenant metadata.
5. Confirm the implementation preserves the sovereign single-deployment-tenant model and does not introduce request-side multi-country switching.
6. Check that the changed backend scope remains generic and country-agnostic.

## Known risks / limitations

- `RISK-010`: migration robustness remains important as platform persistence expands.
- `RISK-019`: later backend stories may touch the same `platform-core` runtime/persistence scope.
- The broader schema-isolation strategy remains deferred and is intentionally outside this story.

## Recommended QA outcome criteria

- PASS if Flyway/bootstrap/API behavior is reproducible, the tenant context remains deployment-local, and no centralized multi-country runtime behavior appears.
- FAIL if bootstrap is non-idempotent, the endpoint does not reflect the active persisted tenant, or the implementation drifts toward request-selectable cross-country routing.

