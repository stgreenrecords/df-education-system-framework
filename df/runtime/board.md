# Dark Factory Runtime Board

This is the live task queue. Agents must update it when task state changes.

| Priority | Task ID | Title | Type | State | Owner role | Blocked? | Last updated | Next action |
|---|---|---|---|---|---|---|---|---|
| P0 | TASK-001 | Transform initial prompt into roadmap and backlog | Story | DONE | factory | No | 2026-05-23 01:20 local | — |
| P0 | TASK-002 | Add strict no-country-specific-code rule | Chore | DONE | factory | No | 2026-05-23 | — |
| P0 | SPIKE-001 | Research Polish education system for country template | Spike | DONE | factory | No | 2026-05-23 09:55 local | Next session: PO reviews TASK-002 |
| P0 | STORY-010 | Initialize Spring Boot project with modular structure | Story | DONE | factory | No | 2026-05-23 11:56 local | Accepted by PO. |
| P0 | TASK-004 | Split developer role into backend, frontend, and DevOps lanes | Chore | READY_FOR_QA | qa | No | 2026-05-23 11:25 local | New session: QA verifies lane roles, subdashboards, artifact ownership, and routing docs. |
| P0 | TASK-005 | Add designer and data-engineer roles | Chore | READY_FOR_QA | qa | No | 2026-05-23 12:17 local | New session: QA verifies new role gates, data rules, boards, and documentation consistency. |
| P0 | STORY-220 | Design and implement database-backed translation storage | Story | BLOCKED | human/factory | Yes | 2026-05-23 11:56 local | Unblock by completing/promoting dependency `STORY-011`, or by adding an existing PostgreSQL/migration substrate to the repository. |
| P1 | STORY-012 | Implement OpenAPI contract generation | Story | READY_FOR_DEV | backend-dev | No | 2026-05-23 12:05 local | New session: backend-dev implements Springdoc OpenAPI generation and Swagger UI in `backend/platform-core`. |

## Queue notes

- TASK-001: Complete. All 13 deliverables produced, QA passed, PO accepted.
- TASK-002: Complete. Dev added the strict data-only guardrail across AGENTS.md, architecture-direction.md, decisions.md, and the Poland template. QA passed. PO accepted 2026-05-23.
- SPIKE-001: PO accepted the Poland Template v1 research deliverable. All 7 acceptance criteria passed, no UI/screenshots applied, and residual risks were explicitly accepted as follow-up validation items.
- EPIC-22 / i18n backlog entries added to `df/backlog/epics.md` and `df/backlog/user-stories.md` (STORY-220 through STORY-225). STORY-220 was promoted to runtime, but Dev blocked it because the repository does not yet contain the required PostgreSQL/migration foundation from `STORY-011`; `STORY-010` is now accepted. STORY-221 through STORY-225 remain DRAFT backlog entries.
- STORY-010 promoted from backlog to runtime as the root dependency for the blocked foundation/i18n work. SA completed architecture and handed off to Dev. Human preference changed the build tool from Gradle to Maven on 2026-05-23 10:36 local. Dev implemented the Maven multi-module Spring Boot scaffold and handed off to QA on 2026-05-23 10:52 local. Human rework on 2026-05-23 11:02 local required parent plus independent backend/frontend/devops Maven projects with partial and all-together build/deploy paths; Dev reworked the scaffold on 2026-05-23 11:13 local, QA passed it on 2026-05-23 11:51 local, and PO accepted it on 2026-05-23 11:56 local. STORY-011 remains the next foundation dependency to promote before STORY-220 can resume.
- TASK-003 containerization timing evaluation completed by SA. Phase 1 should include container-readiness in STORY-010/011, then new backlog stories STORY-022 (Podman-compatible OCI baseline) and STORY-023 (Kubernetes/OpenTofu-compatible cloud-portable deployment baseline).
- TASK-004 promoted from explicit user request and completed as an SA-owned framework documentation change. Dark Factory introduced `backend-dev`, `frontend-dev`, and `devops` delivery lanes with separate runtime subdashboards and lane-owned artifact rules. SA addendum on 2026-05-23 defines frontend project scopes: `frontend/website` with Next.js + React, `frontend/android`, and `frontend/ios`; Android and iOS mobile applications are last-priority work unless promoted. QA must verify consistency before PO review.
- TASK-005 promoted from explicit user request and completed as an SA-owned framework documentation change. Dark Factory now includes `designer` as a required pre-frontend UI design gate and `data-engineer` as the data population/source-traceability lane. QA must verify consistency before PO review.
- STORY-012 promoted from backlog after the user selected the OpenAPI story. SA skipped refinement because acceptance criteria are explicit, completed backend architecture, and routed it to `backend-dev`. The task uses Springdoc OpenAPI for Spring Boot 4, configures `/api-docs` and `/swagger-ui`, and requires backend integration-test evidence before QA.
