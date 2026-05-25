# Backend Developer Runtime Subdashboard

This is the live queue for `backend-dev` implementation tasks. The main board remains the overall task source of truth.

| Priority | Task ID | Parent task | Title | State | Owner role | Affected scope | Blocked? | Last updated | Next action |
|---|---|---|---|---|---|---|---|---|---|
| P0 | STORY-081 | - | Implement role-based access control (RBAC) | DONE | factory | `backend/identity-access`, incremental `backend/platform-core` authorization wiring, Flyway migration(s), backend RBAC API/tests | No | 2026-05-25 12:05 local | Accepted by PO after an independent focused RBAC verification pass confirmed the backend-only role/scope contract and non-UI evidence path. |
| P0 | STORY-080 | - | Implement user registration and authentication | DONE | factory | `backend/identity-access`, minimal `backend/platform-core` security wiring, Flyway migration(s), backend auth API/tests | No | 2026-05-24 23:12 local | Accepted by PO after an independent focused auth verification pass confirmed the backend-only auth contract and non-UI product evidence. |
| P0 | STORY-013 | - | Implement audit trail system | DONE | factory | `backend/platform-core`, Flyway migration(s), generic audit service/repository/API, translation audit convergence, backend audit tests | No | 2026-05-24 21:49 local | Accepted by PO. Backend lane work is complete for this story. |
| P0 | STORY-030 | - | Implement hierarchical configuration with inheritance | DONE | factory | `backend/platform-core`, Flyway migration(s), configuration field definitions, scoped values, resolution/validation services, minimal backend configuration API | No | 2026-05-24 21:19 local | Accepted by PO. Backend lane work is complete for this story. |
| P0 | STORY-021 | - | Implement basic tenant/deployment configuration | DONE | factory | `backend/platform-core`, Flyway migration(s), tenant bootstrap/config, backend tenant API/context | No | 2026-05-24 20:50 local | Accepted by PO. Backend lane work is complete for this story. |
| P0 | STORY-220 | - | Design and implement database-backed translation storage | DONE | factory | `backend/platform-core`, Flyway migrations, translation API/cache/audit slice | No | 2026-05-24 19:27 local | Accepted by PO |
| P0 | STORY-011 | - | Implement PostgreSQL database configuration and migration framework | DONE | factory | `backend/platform-core`, root `pom.xml`, `db/migration` | No | 2026-05-24 18:54 local | Accepted by PO |
| P1 | STORY-012 | - | Implement OpenAPI contract generation | DONE | factory | `backend/platform-core` | No | 2026-05-24 18:18 local | Accepted by PO |

## Lane notes

- New backend implementation tasks must be added here before `backend-dev` starts work.
- Backend implementation notes belong under `df/artifacts/{task-id}/backend/`.
- Do not track design, frontend, DevOps, or data-engineering tasks on this subdashboard.
- STORY-220 implementation is backend-only and should stay generic: translation fallback is data-driven, namespace uniqueness is normalized through a default namespace, and temporary translation-audit persistence must remain compatible with future platform audit work.
- STORY-011 architecture and SA handoff live under `df/artifacts/STORY-011/`; backend implementation notes should be created under `df/artifacts/STORY-011/backend/` when development starts.
- STORY-012 implementation notes and QA handoff belong under `df/artifacts/STORY-012/backend/`.
