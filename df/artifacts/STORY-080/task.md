# Task - STORY-080

## Summary

Implement the first backend authentication foundation in `backend/identity-access` so a deployment-local administrator can register users and authenticated clients can obtain and present secure access tokens for protected APIs.

## Type

Story

## Priority

P0

## Current state

DONE

## Business goal

Establish the minimum secure identity foundation required for the MVP so later RBAC, MFA, translation-management authorization, and user-facing workflows can rely on a real authenticated user model instead of anonymous or ad-hoc access.

## Acceptance criteria

- [ ] Given valid credentials, when a user logs in, then a JWT/session token is issued
- [ ] Given invalid credentials, when login is attempted, then access is denied with appropriate error
- [ ] Given an expired token, when an API is called, then a 401 is returned
- [ ] Given a new user, when registered by admin, then they can log in with provided credentials

## Out of scope

- Full hierarchical RBAC for country/region/city/institution/teacher/student/parent roles; that belongs to `STORY-081`
- MFA flows and recovery codes; that belongs to `STORY-082`
- External IdP, OAuth2 federation, SAML, or social login integration
- Frontend login/registration UI or any designer-scoped visual work
- Password-reset, account-recovery, email delivery, or self-service profile management beyond the minimal backend auth foundation

## Assumptions

- Refinement is not required because the backlog story already contains explicit, testable acceptance criteria and the MVP/security context is documented in the backlog and roadmap
- Architecture is required because the story affects authentication, persisted credentials, backend API protection, security configuration, and future authorization foundations
- The first safe implementation is backend-only and belongs to the `backend-dev` lane, primarily in `backend/identity-access` with minimal `backend/platform-core` security wiring and tests
- Because no frontend login UI exists yet, the acceptance path for this story is backend API behavior and automated verification rather than screenshots or designed visual flows
- The accepted deployment-tenant context from `STORY-021` should scope user accounts to the active sovereign deployment rather than introducing centralized multi-country identity routing
- The generic audit foundation from `STORY-013` should record meaningful identity mutations such as admin-created users without expanding this story into full security-event analytics

## Dependencies

- `STORY-010`
- `STORY-011`
- alignment with `STORY-021`, `STORY-012`, and `STORY-013`

## Risks

- Security scope can sprawl into full RBAC, MFA, external IdP, password reset, and UI login work unless the first implementation stays tightly focused
- Authentication secrets and bootstrap-admin behavior must remain externalized so no deployment secrets are hardcoded into source control or OCI images
- Later authorization work must extend this foundation without forcing a breaking rewrite of the login token contract or user persistence model

## Links

- Issue: n/a
- PR: n/a
- Design: `df/artifacts/STORY-080/solution-design.md`

## Implementation lane

- Lane: `backend-dev`
- Subdashboard: `df/runtime/backend-dev-board.md`
- Artifact folder for implementation notes: `df/artifacts/STORY-080/backend/`

## Role history

| Timestamp | Role | State | Summary |
|---|---|---|---|
| 2026-05-24 22:31 local | sa | OPEN -> NEEDS_ARCHITECTURE | Selected `STORY-080` as the next highest-priority actionable backlog story after `STORY-014` acceptance because it is a Critical Phase 1 dependency root, its direct prerequisites (`STORY-010`, `STORY-011`) and supporting tenant/audit/OpenAPI foundations are accepted, it delivers the missing authentication baseline called out in the roadmap/MVP scope, and it unblocks later RBAC, MFA, translation-management authorization, and user-bound product features. |
| 2026-05-24 22:31 local | sa | NEEDS_ARCHITECTURE -> ARCHITECTURE_IN_PROGRESS | Started architecture because the story affects persisted identity data, password handling, token issuance/validation, backend API protection, tenant scoping, and future authorization extensibility. |
| 2026-05-24 22:31 local | sa | ARCHITECTURE_IN_PROGRESS -> READY_FOR_DEV | Completed the backend-oriented authentication solution design, recorded the Phase 1 auth-foundation decision, updated shared architecture guidance, and routed the story to `backend-dev` as a backend-only implementation task. |
| 2026-05-24 22:39 local | backend-dev | READY_FOR_DEV -> DEV_IN_PROGRESS | Started implementation after reviewing the SA design/handoff, runtime lane queue, repository status, and current tenant/audit/runtime code patterns. Workspace contains unrelated pre-existing accepted-story changes, so this session remains narrowly scoped to `STORY-080` backend auth files plus required runtime/artifact updates. |
| 2026-05-24 22:56 local | backend-dev | DEV_IN_PROGRESS -> READY_FOR_QA | Fixed the partial live auth implementation by restoring the `identity-access`/`platform-core` module boundary through tenant/audit ports, added the required runtime adapters and JWT security wiring, extended integration coverage for bootstrap admin, login, protected access, expired-token `401`, admin-created registration, audit creation, and `/api-docs` exposure, and passed focused plus full backend verification. |
| 2026-05-24 23:00 local | qa | READY_FOR_QA -> QA_IN_PROGRESS | Started independent QA verification for the backend-only auth foundation after reviewing the task, backend handoff, implementation notes, runtime board/subdashboard state, and key auth/security/migration files. |
| 2026-05-24 23:01 local | qa | QA_IN_PROGRESS -> READY_FOR_PO | Independently reran the focused backend auth integration suite and the full backend reactor verification, inspected the `V9` identity migration plus the `identity-access`/`platform-core` boundary and security wiring directly, found no defects, and approved the story for PO review. |
| 2026-05-24 23:05 local | po | READY_FOR_PO -> PO_REVIEW | Started product review after confirming the QA pass, backend implementation notes, backend handoff, runtime queue state, and the non-UI evidence path for the backend-only auth foundation. |
| 2026-05-24 23:12 local | po | PO_REVIEW -> DONE | Accepted the backend-only auth foundation after independently rerunning the focused integration suite (`EducationSystemApplicationIT` 37/37), confirming all four acceptance criteria through the runnable backend path, and documenting the non-UI product evidence in `po-review.md`. |

