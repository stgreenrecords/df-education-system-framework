# Task - STORY-082

## Summary

Add backend-only multi-factor authentication for administrator accounts so the accepted identity foundation requires a second factor for admin logins without breaking the existing non-admin login path.

## Type

Story

## Priority

P1

## Current state

DONE

## Business goal

Strengthen the accepted Phase 1 security foundation by requiring a second factor for administrator-level access while preserving the generic, tenant-scoped authentication model already established in `STORY-080` and `STORY-081`.

## Acceptance criteria

- [x] Given an admin account, when logging in, then MFA is required after password
- [x] Given MFA is configured, when a valid TOTP code is provided, then login succeeds
- [x] Given MFA is configured, when an invalid code is provided, then login is denied
- [x] Given a non-admin account, when logging in, then MFA is optional

## Out of scope

- SMS, email, push, hardware-key, or external IdP MFA providers
- Full password-reset, MFA recovery-code, device-management, or help-desk reset workflows
- Frontend enrollment UI or designer deliverables
- Mandatory MFA for non-administrator roles

## Assumptions

- Refinement is not required because the backlog story already provides explicit, testable acceptance criteria and the repository plus backlog documents establish that administrator MFA is part of the Phase 1 security baseline.
- The first MFA implementation should remain backend-only in `backend/identity-access` with minimal runtime security wiring in `backend/platform-core`.
- Time-based one-time passwords (TOTP) are the smallest viable Phase 1 MFA factor because they satisfy the backlog requirement without introducing paid providers or country-specific integrations.
- Administrator roles for MFA enforcement are the generic RBAC admin roles accepted in `STORY-081`: `COUNTRY_ADMIN`, `REGION_ADMIN`, `CITY_ADMIN`, and `INSTITUTION_ADMIN`.
- A minimal backend enrollment/activation flow is in scope because the login acceptance criteria cannot be satisfied safely without a way to provision and activate the second factor.

## Dependencies

- `STORY-080` for the accepted local-credentials and bearer-token authentication baseline
- `STORY-081` for the accepted generic administrator-role model used to decide which accounts require MFA
- `STORY-013` for shared audit recording of security-sensitive MFA enrollment/activation changes

## Risks

- If MFA enrollment is modeled as a full authenticated session before second-factor proof, administrator logins may remain over-permissive.
- If TOTP secrets are stored without adequate protection, the implementation could weaken the security baseline it is intended to improve.
- If the login contract changes too abruptly, non-admin callers or future clients may break unnecessarily.

## Links

- Issue: n/a
- PR: n/a
- Design: `df/artifacts/STORY-082/solution-design.md`

## Implementation lane

- Lane: `backend-dev`
- Subdashboard: `df/runtime/backend-dev-board.md`
- Artifact folder for implementation notes: `df/artifacts/STORY-082/backend/`

## Role history

| Timestamp | Role | State | Summary |
|---|---|---|---|
| 2026-05-25 14:22 local | sa | OPEN -> NEEDS_ARCHITECTURE | Selected `STORY-082` as the next strongest actionable Phase 1 follow-up because the accepted authentication and RBAC foundations now make administrator MFA the smallest high-value security increment inside the existing identity boundary without immediately expanding into broader cross-lane encryption/infrastructure work. |
| 2026-05-25 14:22 local | sa | NEEDS_ARCHITECTURE -> ARCHITECTURE_IN_PROGRESS | Started architecture because the story changes authentication flows, adds security-sensitive persistence, introduces new backend auth endpoints/contracts, and must preserve compatibility with the accepted Phase 1 auth/RBAC foundation. |
| 2026-05-25 14:22 local | sa | ARCHITECTURE_IN_PROGRESS -> READY_FOR_DEV | Completed the backend-only MFA design, recorded `DECISION-022`, updated the shared architecture direction, and routed implementation to `backend-dev`. |
| 2026-05-26 local | backend-dev | READY_FOR_DEV -> DEV_IN_PROGRESS | Started backend-only MFA implementation after confirming the accepted design, current auth/RBAC seams, integration-test surface, and repository status. |
| 2026-05-26 local | backend-dev | DEV_IN_PROGRESS -> READY_FOR_QA | Completed the backend-only MFA implementation, fixed the final integration-helper regression discovered during validation, passed focused `EducationSystemApplicationIT` verification (`48/48`) plus full backend `clean verify`, and handed the story to `qa`. |
| 2026-05-26 local | qa | READY_FOR_QA -> QA_IN_PROGRESS | Started independent QA verification for the backend-only administrator MFA story using the backend handoff evidence, focused reruns, source inspection, and regression checks before deciding whether the story can advance to PO review. |
| 2026-05-26 local | qa | QA_IN_PROGRESS -> READY_FOR_PO | Completed independent verification with focused `EducationSystemApplicationIT` rerun (`48/48`), full backend `clean verify` (`BUILD SUCCESS`), and direct inspection of challenge-token separation, protected-secret handling, migration `V12`, and `/api-docs` exposure; no defects were found. |
| 2026-05-26 local | po | READY_FOR_PO -> PO_REVIEW | Started backend-only product review after confirming the QA pass report, backend lane evidence, and the non-UI validation path for administrator MFA. |
| 2026-05-26 local | po | PO_REVIEW -> DONE | Accepted the backend-only administrator MFA story after independently rerunning focused product validation (`EducationSystemApplicationIT` `48/48`, `BUILD SUCCESS`), confirming screenshots are not applicable, and validating that admin logins now require second-factor proof while non-admin logins remain compatible. |

