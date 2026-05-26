# Dark Factory Decisions

- `DECISION-001` — No country-specific code change is allowed. Country templates are data-only and may not change framework code, structure, schemas, or API contracts.
  - Date: 2026-05-23
  - Status: Accepted
  - Related task: `TASK-002`
  - Record: `df/artifacts/TASK-002/decision-001-no-country-specific-code.md`

- `DECISION-002` - Database-backed i18n storage uses generic fallback and replaceable cache abstraction.
  - Date: 2026-05-23
  - Status: Accepted
  - Related task: `STORY-220`
  - Record: `df/artifacts/STORY-220/decision-002-i18n-storage-cache-fallback.md`

- `DECISION-003` - Initial backend foundation uses Java 21, Spring Boot, Maven, and a single-repository modular monolith.
  - Date: 2026-05-23
  - Status: Accepted
  - Related task: `STORY-010`
  - Record: `df/artifacts/STORY-010/decision-003-spring-boot-foundation-build.md`

- `DECISION-004` - Containerization is a Phase 1 foundation concern using Podman-compatible OCI images, Kubernetes-compatible deployment manifests, and OpenTofu-compatible IaC modules.
  - Date: 2026-05-23
  - Status: Accepted
  - Related task: `TASK-003`
  - Record: `df/artifacts/TASK-003/decision-004-containerization-phase-1.md`

- `DECISION-005` - Implementation ownership is split into backend, frontend, and DevOps lanes with separate runtime subdashboards and lane-owned artifacts.
  - Date: 2026-05-23
  - Status: Accepted
  - Related task: `TASK-004`
  - Record: `df/artifacts/TASK-004/decision-005-development-lane-split.md`

- `DECISION-006` - Frontend implementation is split into independent website, Android, and iOS projects; the website uses Next.js + React.
  - Date: 2026-05-23
  - Status: Accepted
  - Related task: `TASK-004`
  - Record: `df/artifacts/TASK-004/decision-006-frontend-project-split.md`

- `DECISION-007` - Android and iOS mobile applications are last-priority frontend work unless explicitly promoted.
  - Date: 2026-05-23
  - Status: Accepted
  - Related task: `TASK-004`
  - Record: `df/artifacts/TASK-004/decision-007-mobile-last-priority.md`

- `DECISION-008` - OpenAPI contract generation uses Springdoc OpenAPI in the backend platform core, with `/api-docs` and `/swagger-ui` exposed by configuration.
  - Date: 2026-05-23
  - Status: Accepted
  - Related task: `STORY-012`
  - Record: `df/artifacts/STORY-012/decision-008-openapi-generation.md`

- `DECISION-009` - Dark Factory uses a `designer` gate before UI-facing frontend implementation and a `data-engineer` lane for source-backed country/test datasets.
  - Date: 2026-05-23
  - Status: Accepted
  - Related task: `TASK-005`
  - Record: `df/artifacts/TASK-005/decision-009-designer-data-engineer-roles.md`

- `DECISION-010` - The database foundation uses PostgreSQL with Spring Boot JDBC configuration, Hikari-backed pooling, and Flyway-managed forward-only migrations in `backend/platform-core`.
  - Date: 2026-05-24
  - Status: Accepted
  - Related task: `STORY-011`
  - Record: `df/artifacts/STORY-011/decision-010-postgresql-flyway-foundation.md`

- `DECISION-011` - Translation storage stays in `backend/platform-core`, uses a non-null default namespace model, and records changes through a local audit bridge until the platform-wide audit subsystem exists.
  - Date: 2026-05-24
  - Status: Accepted
  - Related task: `STORY-220`
  - Record: `df/artifacts/STORY-220/decision-011-translation-foundation-placement-and-audit-bridge.md`

- `DECISION-012` - Country deployments are sovereign and isolated: each country owns its own environments, data, backups, access, and deployment execution while consuming provider-neutral release artifacts.
  - Date: 2026-05-24
  - Status: Accepted
  - Related task: `STORY-020`
  - Record: `df/artifacts/STORY-020/decision-012-country-sovereign-deployment-architecture.md`

- `DECISION-013` - The Kubernetes/IaC deployment baseline uses a provider-neutral application base plus provider-specific overlays/modules for AWS, Azure, Google Cloud, and self-hosted/on-prem targets.
  - Date: 2026-05-24
  - Status: Accepted
  - Related task: `STORY-023`
  - Record: `df/artifacts/STORY-023/decision-013-provider-neutral-kubernetes-iac-layering.md`

- `DECISION-014` - Phase 1 tenant/deployment modeling uses one active sovereign deployment tenant per country-operated runtime, resolved through a backend tenant context rather than a centralized multi-country SaaS tenant switch.
  - Date: 2026-05-24
  - Status: Accepted
  - Related task: `STORY-021`
  - Record: `df/artifacts/STORY-021/decision-014-sovereign-deployment-tenant-model.md`

- `DECISION-015` - Phase 1 configuration inheritance uses a generic scope-path resolver rooted in the active deployment tenant, with persisted field-definition metadata and minimal merge strategies (`REPLACE`, `EXTEND_SET`) instead of hard dependencies on unfinished organization persistence.
  - Date: 2026-05-24
  - Status: Accepted
  - Related task: `STORY-030`
  - Record: `df/artifacts/STORY-030/decision-015-generic-configuration-scope-path-and-field-behavior.md`

- `DECISION-016` - Phase 1 audit uses a generic tenant-scoped append-only platform audit foundation in `platform-core`, with shared write/query/export behavior instead of permanent feature-specific audit tables.
  - Date: 2026-05-24
  - Status: Accepted
  - Related task: `STORY-013`
  - Record: `df/artifacts/STORY-013/decision-016-platform-audit-foundation.md`

- `DECISION-017` - Phase 1 authentication uses a backend-only tenant-scoped local credential foundation in `identity-access`, with externalized bootstrap-admin configuration, secure password hashing, and signed bearer-token authentication before later RBAC/MFA expansion.
  - Date: 2026-05-24
  - Status: Accepted
  - Related task: `STORY-080`
  - Record: `df/artifacts/STORY-080/decision-017-phase-1-auth-foundation.md`

- `DECISION-018` - Phase 1 RBAC layers on the accepted authentication foundation with a predefined generic role catalogue, tenant-scoped role assignments, and scope-aware backend authorization checks that stay framework-generic and backend-only before later MFA/ABAC expansion.
  - Date: 2026-05-24
  - Status: Accepted
  - Related task: `STORY-081`
  - Record: `df/artifacts/STORY-081/decision-018-phase-1-rbac-foundation.md`

- `DECISION-019` - Country templates use a generic manifest-plus-sections schema and builder concept with immutable version history, explicit approval status, source traceability, and no country-specific framework code.
  - Date: 2026-05-25
  - Status: Accepted
  - Related task: `STORY-050`
  - Record: `df/artifacts/STORY-050/decision-019-country-template-schema-and-builder.md`

- `DECISION-020` - Framework updates use a generic release-package contract with manifest, release notes, migration references, compatibility metadata, rollback guidance, and a manifest/rule-based compatibility checker that emits structured `PASS` / `WARN` / `FAIL` outcomes.
  - Date: 2026-05-25
  - Status: Accepted
  - Related task: `STORY-040`
  - Record: `df/artifacts/STORY-040/decision-020-release-package-format-and-compatibility-checker.md`

- `DECISION-021` - Phase 1 configuration follow-up work stays in `backend/platform-core` and adds explicit validation, auditable inheritance-break requests, and generic institution-scope compatibility reporting without auto-bypassing locks or depending on unfinished organization persistence.
  - Date: 2026-05-25
  - Status: Accepted
  - Related task: `STORY-031`
  - Record: `df/artifacts/STORY-031/decision-021-configuration-validation-and-impact-reporting.md`

- `DECISION-022` - Phase 1 administrator MFA should remain backend-only, use TOTP as the first factor, derive enforcement from the accepted RBAC admin roles, and require a challenge-based step-up flow before administrator access tokens are issued.
  - Date: 2026-05-25
  - Status: Accepted
  - Related task: `STORY-082`
  - Record: `df/artifacts/STORY-082/decision-022-phase-1-admin-mfa-foundation.md`

- `DECISION-023` - Root design assets use `design/{page-slug}/` instead of `design/{task-id}/{page-slug}/`, while task-owned design documentation remains under `df/artifacts/{task-id}/design/`.
  - Date: 2026-05-25
  - Status: Accepted
  - Related task: `TASK-007`
  - Record: `df/artifacts/TASK-007/decision-023-flat-design-asset-root.md`

- `DECISION-024` - The first repository-owned AWS deployment path will be a manual GitHub Actions `workflow_dispatch` pipeline that uses GitHub secrets `AWS_ACCESS_KEY` and `AWS_SECRET_KEY`, publishes the accepted `platform-core` OCI image to ECR, and deploys the existing AWS Kubernetes overlay to EKS with externalized deployment-time values.
  - Date: 2026-05-26
  - Status: Accepted
  - Related task: `TASK-010`
  - Record: `df/artifacts/TASK-010/decision-024-on-demand-aws-github-actions-deployment.md`

Use `df/templates/decision-record.md` for new decision records.

