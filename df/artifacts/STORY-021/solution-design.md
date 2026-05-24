# Solution Design - STORY-021

## Summary

Create a backend-only tenant/deployment configuration foundation in `backend/platform-core` that persists one sovereign deployment tenant record, bootstraps it from deployment configuration, and exposes a reusable tenant context/settings abstraction for current and later modules.

## Context

`STORY-020` established that deployments are sovereign and country-owned rather than centrally hosted multi-country SaaS tenants. `STORY-011` provided the PostgreSQL/Flyway persistence baseline, and `STORY-023` completed the portable deployment contract. The next missing Phase 1 backend foundation is the country deployment identity/configuration model that later configuration inheritance, organization, security, and release-management work can rely on.

The architecture must preserve the sovereign boundary: one deployment corresponds to one active country/ministry tenant in Phase 1. This is not the moment to introduce a centralized request-selectable tenant router or cross-country runtime plane.

## Requirements and acceptance criteria

- Persist a tenant record with country code, name, timezone, and locale when a new deployment is initialized
- Scope API behavior to the active deployment tenant
- Provide tenant configuration/settings to current and later modules

## Proposed solution

Implement the minimal sovereign deployment tenant foundation as a backend-only slice in `backend/platform-core`.

1. **Persist one active deployment tenant record**
   - Add a tenant table in Flyway, for example `platform_tenant`, with fields such as:
     - `tenant_id` (UUID primary key)
     - `country_code`
     - `display_name`
     - `default_timezone`
     - `default_locale`
     - creation/update timestamps
   - Keep the schema generic and country-agnostic: the table stores deployment metadata, not country-specific functional rules.
   - Enforce uniqueness on `country_code` so the deployment cannot accidentally create duplicate sovereign tenant records.

2. **Bootstrap tenant metadata from deployment configuration**
   - Introduce strongly typed Spring configuration properties for the active deployment tenant, for example environment/application properties for country code, display name, timezone, and locale.
   - On startup, run an idempotent bootstrap/initializer that creates the tenant record if it does not yet exist and reuses the existing record if it already matches the configured sovereign deployment.
   - Fail fast when required tenant bootstrap fields are absent or invalid.

3. **Introduce a reusable backend tenant context abstraction**
   - Add a `TenantContext`/`TenantContextService` in `platform-core` that resolves the single active deployment tenant for the current runtime.
   - In Phase 1 this context is deployment-scoped, not request-selectable across countries. API scoping means all requests in the deployment resolve to that active tenant context.
   - This gives later stories one stable abstraction for tenant-aware persistence, configuration, authorization, and audit decisions.

4. **Expose the tenant configuration to modules and operators**
   - Add a minimal backend API such as `GET /api/v1/platform/tenant` (or equivalent platform-core endpoint) to expose the active deployment tenant metadata for validation and future module consumption.
   - The response should include the persisted country code, name, timezone, and locale only; avoid leaking secrets or deployment-internal infrastructure settings.
   - Keep the contract generic so later modules can consume tenant metadata without coupling to one country.

5. **Prepare future tenant-aware persistence without overreaching**
   - Do not retrofit every existing Phase 1 table in this story.
   - Instead, document that new tenant-aware domain/configuration tables should consume the `TenantContext` abstraction and attach tenant identity explicitly when they become multi-record domain models.
   - Preserve the deferred architecture decision about broader schema isolation strategy; this story only establishes the active deployment tenant baseline.

## Alternatives considered

- **Central multi-country SaaS tenant router now**: rejected because it conflicts with the accepted sovereign deployment operating model and would introduce unnecessary cross-country runtime complexity.
- **Pure runtime config only, no persisted tenant record**: rejected because later modules need a stable persisted deployment identity and QA needs something verifiable in the database/API layer.
- **Implement full country/region/city/institution hierarchy here**: rejected because it would collapse `STORY-021` into later organization/configuration stories and expand scope beyond the minimal Phase 1 foundation.

## Files/components likely affected

- `df/artifacts/STORY-021/task.md`
- `df/artifacts/STORY-021/solution-design.md`
- `df/artifacts/STORY-021/decision-014-sovereign-deployment-tenant-model.md`
- `df/artifacts/STORY-021/handoffs.md`
- `df/backlog/architecture-direction.md`
- `df/backlog/user-stories.md`
- `df/runtime/board.md`
- `df/runtime/backend-dev-board.md`
- `df/runtime/activity-log.md`
- `df/runtime/decisions.md`
- expected backend implementation targets under `backend/platform-core/src/main/java/com/darkfactory/education/platform/tenant/**`
- expected Flyway migration(s) under `backend/platform-core/src/main/resources/db/migration/`
- possible platform configuration updates under `backend/platform-core/src/main/resources/application.properties`
- backend tests under `backend/platform-core/src/test/**`

## Data model changes

- Add a new persisted tenant/deployment metadata table in PostgreSQL via Flyway
- No country-specific schema forks or provider-specific database structures
- The broader schema-per-module vs schema-per-tenant decision remains deferred; this story only introduces one generic deployment-tenant table

## API/contract changes

- Add a minimal backend endpoint (or equivalent application service contract) to expose the active deployment tenant metadata
- Introduce runtime configuration properties for tenant bootstrap metadata
- Internal contract: modules can resolve active tenant metadata through a shared `TenantContext` abstraction

## UI/UX impact

- None

## Security and privacy considerations

- Do not allow request clients to select arbitrary country tenants inside one deployment; keep the active deployment tenant server-controlled
- Do not store secrets in tenant metadata
- Preserve the no-country-specific-code rule by keeping tenant data/config generic
- Future auth/RBAC stories must consume the same tenant context instead of inventing parallel scoping models

## Performance/scalability considerations

- The active deployment tenant lookup should be cached or otherwise efficient because later modules may resolve it frequently
- The bootstrap path must be idempotent and cheap after the first successful initialization
- The design should allow future expansion to explicit tenant foreign keys on new tables without changing the public deployment model

## Test strategy

`backend-dev` should validate the strongest practical backend path:

- unit tests for configuration property validation and tenant context behavior
- integration tests that start PostgreSQL, run Flyway, and confirm the tenant bootstrap record is created idempotently
- API/integration tests for the tenant metadata endpoint
- regression checks proving no country-specific logic or request-selectable cross-country routing was introduced
- rerun relevant backend and full-parent Maven verification if shared backend build/test scope is affected

## Deployment/migration plan

- Add the tenant table through a new Flyway migration
- Add deployment bootstrap properties with safe placeholder/default documentation
- Initialize the tenant record automatically at startup in each sovereign deployment
- Keep all deployment-specific values externalized through configuration rather than code changes

## Rollback plan

- Revert the tenant table migration and backend tenant slice if the model is rejected before dependent stories start
- Remove the new endpoint/configuration binding and fall back to deployment configuration only while redesigning
- If partial implementation lands, disable tenant bootstrap until the corrected design is ready

## Risks and mitigations

- **Risk:** The story may drift toward centralized SaaS multi-tenancy
  - **Mitigation:** explicitly model one active sovereign deployment tenant per deployment and prohibit request-side country switching
- **Risk:** Later stories may create parallel scoping models
  - **Mitigation:** define `TenantContext` as the shared backend abstraction now
- **Risk:** Existing sparse backend functionality may not yet need tenant foreign keys everywhere
  - **Mitigation:** limit this story to the persisted deployment tenant baseline and shared context, leaving deeper table adoption to later stories

## Open questions

- None blocking the backend implementation. If backend discovery shows that an existing module must immediately become explicitly tenant-keyed to satisfy safe API scoping, `backend-dev` should document that need and hand back to SA only if the single-lane scope must change.

## Implementation lane

- Lane: `backend-dev`
- Subdashboard: `df/runtime/backend-dev-board.md`
- Artifact folder for implementation notes: `df/artifacts/STORY-021/backend/`

## SA decision

Approved for development: Yes

