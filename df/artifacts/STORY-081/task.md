# Task - STORY-081

## Summary

Implement the first role-based access control foundation on top of the new authentication layer so predefined education roles can be assigned within the active sovereign deployment and backend APIs can enforce least-privilege access with generic scope-aware authorization rules.

## Type

Story

## Priority

P0

## Current state

READY_FOR_QA

## Business goal

Complete the next missing MVP security baseline after authentication by introducing a generic user-role model that downstream school, attendance, gradebook, translation-management, and reporting workflows can reuse without country-specific authorization code.

## Acceptance criteria

- [ ] Given roles (`country-admin`, `region-admin`, `city-admin`, `institution-admin`, `teacher`, `student`, `parent`), when assigned, then the user has only permissions matching the role
- [ ] Given a teacher role, when they try to access another school's data, then access is denied
- [ ] Given an institution-admin, when they manage their school, then all school operations are permitted
- [ ] Given a parent role, when they view data, then only their child's data is visible

## Out of scope

- MFA, recovery codes, or step-up authentication; that belongs to `STORY-082`
- External IdP, OAuth2 federation, SAML, or social login integration
- Frontend role-management UI, role-assignment screens, or designer-scoped visual work
- Full attribute-based access control (ABAC) beyond the minimal resource-scope checks needed to prove the RBAC contract
- Completing unfinished school/person domain modules solely to support authorization checks; this story should provide reusable authorization primitives and representative protected backend coverage

## Assumptions

- Refinement is not required because the backlog story already contains explicit, testable acceptance criteria and the roadmap/MVP/security guidance already establish RBAC as a remaining Phase 1 platform foundation
- Architecture is required because the story affects identity persistence, authorization policy design, protected backend API behavior, and the contract between JWT-authenticated principals and backend permission checks
- The first safe implementation is backend-only and belongs to the `backend-dev` lane, primarily in `backend/identity-access` with only minimal authorization wiring in `backend/platform-core`
- The existing `STORY-080` JWT login contract should remain stable; role/permission enforcement should layer on without forcing a breaking authentication redesign
- Because school/person modules are not fully implemented yet, acceptance evidence may use representative backend routes and generic resource-scope descriptors to prove teacher, institution-admin, and parent visibility boundaries before downstream domain stories attach real school/class/student data to the same authorization engine
- The generic scope-path concepts introduced in `STORY-030` should be reused where practical so authorization stays framework-generic and avoids country-specific hierarchy code

## Dependencies

- `STORY-080`
- `STORY-021`
- `STORY-030`
- `STORY-013`
- alignment with `STORY-012`

## Risks

- Authorization scope can sprawl into full domain modeling, school CRUD, or parent/student relationship management unless the implementation stays focused on the reusable RBAC foundation plus representative enforcement paths
- If role claims are modeled carelessly, later changes could bloat tokens or force re-login semantics that are hard to evolve
- Hardcoding one country's structure into role checks would violate the framework invariants; authorization must remain generic and data/scope driven

## Links

- Issue: n/a
- PR: n/a
- Design: `df/artifacts/STORY-081/solution-design.md`

## Implementation lane

- Lane: `backend-dev`
- Subdashboard: `df/runtime/backend-dev-board.md`
- Artifact folder for implementation notes: `df/artifacts/STORY-081/backend/`

## Role history

| Timestamp | Role | State | Summary |
|---|---|---|---|
| 2026-05-24 23:19 local | sa | OPEN -> NEEDS_ARCHITECTURE | Selected `STORY-081` as the next highest-priority actionable backlog story after `STORY-080` reached `DONE` because it is a Critical Phase 1 dependency-root follow-up to authentication, it is explicitly called out by the roadmap/MVP security baseline, and it unblocks later protected backend features more directly than the remaining draft stories. |
| 2026-05-24 23:19 local | sa | NEEDS_ARCHITECTURE -> ARCHITECTURE_IN_PROGRESS | Started architecture because RBAC affects identity persistence, authorization policy design, protected APIs, and future domain-module security behavior. |
| 2026-05-24 23:19 local | sa | ARCHITECTURE_IN_PROGRESS -> READY_FOR_DEV | Completed the backend-oriented RBAC solution design, recorded the Phase 1 RBAC decision, updated shared architecture guidance, and routed the story to `backend-dev` as a backend-only implementation task. |
| 2026-05-24 23:25 local | backend-dev | READY_FOR_DEV -> DEV_IN_PROGRESS | Started backend implementation after reviewing the SA design/handoff, runtime lane queue, and current `STORY-080` auth/security/test seams. The implementation will stay tightly scoped to tenant-scoped role assignments, generic scope-aware authorization checks, representative proof endpoints, and required runtime/artifact updates. |
| 2026-05-24 23:32 local | backend-dev | DEV_IN_PROGRESS -> READY_FOR_QA | Completed the backend-only RBAC implementation by adding migration `V10`, tenant-scoped predefined role assignments, server-side principal role enrichment, generic scope-path authorization evaluation, bootstrap country-admin reconciliation, minimal role-assignment APIs, representative institution/student authorization-proof endpoints, audit convergence for role assignments, and focused plus full backend verification. |

