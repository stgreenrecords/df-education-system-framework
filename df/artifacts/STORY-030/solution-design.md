# Solution Design - STORY-030

## Summary

Implement the first generic hierarchical configuration engine inside `backend/platform-core` so backend modules can resolve effective settings across an ordered scope path (`country -> region -> city -> institution -> unit`) with support for direct overrides, inherited defaults, lower-scope override rejection when an ancestor locks the field, and extensible-field merging.

## Context

`STORY-021` established the sovereign deployment-tenant foundation and provides one active deployment tenant per country-operated runtime. The roadmap and MVP definition place the configuration/inheritance engine on the Phase 1 critical path because later organization, grading, schedule, meal, and release-compatibility stories depend on shared configuration semantics rather than feature-specific ad-hoc settings.

The current repository already has a running Spring Boot backend in `backend/platform-core`, PostgreSQL/Flyway migrations, OpenAPI exposure, and a deployment-tenant context. The organization module does not yet provide a finished runtime hierarchy graph, so the smallest safe solution is to implement a generic configuration engine that resolves along a caller-supplied ordered scope path while keeping the data model reusable for later organization-module integration.

## Requirements and acceptance criteria

- Return a country-level value when an institution-level lookup has no more specific override
- Return an institution-level value when a more specific override exists
- Reject lower-scope override attempts when an inherited ancestor value is marked locked
- Support extensible fields so lower levels can add options without discarding inherited options
- Recalculate effective institution-level results when region-level configuration changes
- Keep the implementation framework-generic and country-agnostic

## Proposed solution

Implement the initial inheritance engine under a new package boundary such as `com.darkfactory.education.platform.configuration` inside `backend/platform-core`. Do not create a new Maven module yet; the scope is still foundation-level and benefits from staying close to the tenant/runtime infrastructure already present in `platform-core`.

### Core model

Use three cooperating concepts:

1. **Field definition metadata**
   - Defines the configuration key, value type, merge strategy, and whether lower-scope overrides are generally allowed.
   - Keeps field behavior data-driven and generic instead of scattering feature-specific branching throughout services.

2. **Scoped configuration values**
   - Stores one value for a given `(tenant, field_key, scope_type, scope_key)`.
   - Carries lock state at the value level so a country/region/city/institution entry can explicitly prevent deeper overrides for that field.
   - Stores the value in JSON/JSONB so the engine can support scalar and collection-like settings without schema forks for each future module.

3. **Scope-path resolution**
   - Resolves an effective value by traversing an ordered path from least specific to most specific scopes.
   - The root country scope is anchored to the active deployment tenant from `STORY-021`.
   - Lower scopes (`REGION`, `CITY`, `INSTITUTION`, `UNIT`) are represented by opaque identifiers in this story so the engine does not block on unfinished organization-module persistence.

### Recommended data model

Suggested tables:

```sql
configuration_field_definition
  field_key varchar primary key
  value_type varchar not null
  merge_strategy varchar not null
  overrides_allowed boolean not null default true
  created_at timestamptz not null
  updated_at timestamptz not null

configuration_value
  id uuid primary key
  tenant_id uuid not null references platform_tenant(tenant_id)
  field_key varchar not null references configuration_field_definition(field_key)
  scope_type varchar not null
  scope_key varchar not null
  value_json jsonb not null
  locked boolean not null default false
  created_at timestamptz not null
  updated_at timestamptz not null
  unique (tenant_id, field_key, scope_type, scope_key)
```

Recommended indexes:

- `(tenant_id, field_key, scope_type, scope_key)` unique lookup path
- `(tenant_id, scope_type, scope_key)` for listing scope-specific values

`scope_key` should use a reserved stable token such as `country` for the country/root scope and opaque caller-supplied identifiers for lower levels. That keeps the first implementation generic and independent from unfinished organization persistence.

### Merge strategies

Support the smallest viable generic set:

- `REPLACE` — the most specific value wins unless an ancestor lock rejects the override attempt
- `EXTEND_SET` — inherited array/set values are merged with lower-level additions, deduplicated while preserving ancestor-first order

Do not add more merge strategies until a concrete downstream story needs them.

### Service behavior

Recommended backend components:

- `ConfigurationFieldDefinitionRepository`
- `ConfigurationValueRepository`
- `ScopePath` / `ConfigurationScope` model representing the ordered target hierarchy
- `ConfigurationResolutionService` for effective-value lookup
- `ConfigurationCommandService` for upsert/update validation
- `ConfigurationMergeStrategy` abstraction for `REPLACE` and `EXTEND_SET`
- Optional `ConfigurationController` with the smallest API surface needed to prove behavior end-to-end in integration tests

Resolution algorithm:

1. Normalize and validate the requested scope path.
2. Resolve the active deployment tenant from `TenantContextService`.
3. Load all values for the requested field across the scopes in the path.
4. Traverse from country/root toward the target scope.
5. For `REPLACE`, keep the most specific applicable value.
6. For `EXTEND_SET`, merge inherited + local values at each step.
7. Return the effective value plus metadata describing which scope supplied the effective result and whether inheritance/merge occurred.

Write/update validation:

1. Load the field definition.
2. For non-root writes, inspect ancestor values for the same field along the declared path.
3. If any ancestor marks the field as locked, reject the write with a deterministic validation error.
4. If the field uses `EXTEND_SET`, validate collection shape before storing.
5. Upsert the scoped value.

### Minimal API scope

The story needs enough executable surface to prove the acceptance criteria, but not a full admin product.

Recommended minimal contract:

- one endpoint to upsert or replace a field definition for tests/bootstrap
- one endpoint to upsert a scoped configuration value
- one endpoint to resolve the effective value for a requested scope path

Keep the contract under `/api/v1/platform/configuration/**` and document it through OpenAPI. Avoid final-product CRUD breadth, UI workflows, import/export, compatibility reporting, or organization-graph browsing in this story.

## Alternatives considered

- **Wait for the full `organization` module first**: rejected because it would stall a Phase 1 critical-path foundation story that unblocks many downstream modules.
- **Hardcode inheritance per feature or per country**: rejected because it violates the framework-generic and no-country-specific-code rules.
- **Store every field in dedicated columns/tables now**: rejected because it creates schema sprawl before downstream field catalogs are known.
- **Support all future merge strategies immediately**: rejected because it adds complexity before the first concrete needs are proven.

## Files/components likely affected

- `backend/platform-core/src/main/resources/application.properties`
- `backend/platform-core/src/main/resources/db/migration/*`
- `backend/platform-core/src/main/java/com/darkfactory/education/platform/configuration/**`
- `backend/platform-core/src/test/java/com/darkfactory/education/platform/**`
- `df/artifacts/STORY-030/backend/*`

## Data/API contract changes

Expected persistence additions:

- `configuration_field_definition`
- `configuration_value`

Expected REST/backend contract additions:

- minimal configuration field-definition setup/update contract for testable bootstrap
- scoped configuration upsert contract
- effective configuration resolution contract

Contract expectations:

- no request-side country selection outside the server-controlled active tenant context
- scope path remains generic and opaque; callers provide identifiers rather than country-specific semantics
- effective resolution response should indicate the source scope and whether inheritance/merge occurred so QA can verify behavior explicitly

## Security/privacy considerations

- Configuration values may later include sensitive operational rules; logs should avoid dumping full payloads by default
- The provisional API surface should stay minimal and ready to be hardened by later identity/access stories
- Validation errors should be deterministic but should not leak irrelevant internal state
- No country-specific values or personal data should be seeded in source code or migrations

## Performance/scalability considerations

- Initial resolution can query PostgreSQL directly because the first implementation is a foundation story, but the service boundary should allow future caching if repeated reads become hot
- Use targeted indexes to avoid repeated full-table scans by tenant/field/scope
- Keep merge strategy evaluation bounded to the scope-depth path rather than scanning unrelated scopes

## Test strategy

`backend-dev` should add automated tests for:

- migration creates the required tables and uniqueness constraints
- country-level value resolves at institution level when no override exists
- institution-level override wins over country/region/city values
- locked ancestor value rejects lower-scope override attempts
- extensible field merges inherited + local options deterministically
- region-level updates change effective institution-level results within that region
- OpenAPI includes the minimal configuration endpoint(s) if REST surface is added
- the implementation remains tenant-scoped and framework-generic

## Deployment/migration plan

- Add forward-only Flyway migration(s) for field-definition and scoped-value tables
- Keep country/root scope tied to the active deployment tenant from `STORY-021`
- Seed only the smallest generic definitions/data necessary for tests if runtime bootstrap requires any; otherwise let tests create their own field definitions and values

## Rollback plan

- Revert before production data exists if the implementation is still local/dev-only
- After production data exists, use compensating migrations or targeted cleanup only after exporting configuration data
- If defects appear in write behavior, disable the provisional configuration write endpoint(s) while preserving safe read paths if possible

## Risks and mitigations

- Risk: the engine grows into a full compatibility/reporting platform too early. Mitigation: limit this story to inheritance, locking, merge, and minimal proof APIs only.
- Risk: missing organization-module hierarchy causes coupling confusion. Mitigation: use generic scope-path identifiers now and document later organization integration as follow-up work.
- Risk: JSONB values weaken type guarantees. Mitigation: require field-definition metadata with explicit value type and validate payload shape in service tests.

## Open questions

- None blocking `backend-dev`. The design intentionally keeps compatibility reporting, inheritance-break workflows, and deeper organization integration in later stories.

## Implementation lane

- Lane: `backend-dev`
- Subdashboard: `df/runtime/backend-dev-board.md`
- Artifact folder for implementation notes: `df/artifacts/STORY-030/backend/`

## SA decision

Approved for development: Yes

