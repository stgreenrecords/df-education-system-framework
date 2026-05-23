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

Use `df/templates/decision-record.md` for new decision records.

