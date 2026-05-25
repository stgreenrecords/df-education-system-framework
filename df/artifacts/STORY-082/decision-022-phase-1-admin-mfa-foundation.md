# Decision Record - DECISION-022

- Date: 2026-05-25
- Status: Accepted
- Owner role: SA
- Related task: STORY-082

## Context

The accepted Phase 1 identity foundation now includes local credential login (`STORY-080`) and generic administrator role modeling (`STORY-081`), but administrator authentication is still single-factor. The MVP definition and product prompt explicitly require stronger administrator authentication and MFA for admin roles. The next step must strengthen privileged access without rewriting the existing non-admin login flow, introducing UI scope, or depending on external providers.

## Decision

Phase 1 administrator MFA will remain backend-only and will use TOTP as the first and only MFA factor for this story. The existing password login flow will evolve into a challenge-based model:

- non-admin users continue to receive an access token directly after password validation;
- admin users with active MFA receive an MFA-verification challenge instead of an access token;
- admin users without active MFA receive an MFA-enrollment challenge and must activate TOTP before receiving an access token.

MFA enforcement will be derived from the accepted generic RBAC admin roles rather than a separate admin flag. TOTP secrets will be stored encrypted with an externalized key, and the full implementation will be routed to `backend-dev`.

## Consequences

- The login contract becomes richer, but non-admin login remains backward-compatible.
- The backend must add MFA factor persistence, protected secret handling, and challenge/verification endpoints.
- Admin accounts will no longer receive privileged access tokens after password alone.
- Full recovery-code, help-desk reset, hardware-key, and external IdP MFA remain future stories.

## Alternatives considered

- Delay MFA until broader encryption or federation stories: rejected because admin MFA is explicitly part of the Phase 1 security baseline.
- Require MFA for every user now: rejected because it expands scope beyond the backlog intent.
- Use SMS/email MFA first: rejected because it introduces provider dependencies and operational complexity too early.
- Issue a full access token before MFA and rely on later step-up checks: rejected because it weakens the admin security boundary.

## Evidence

- `df/backlog/user-stories.md` (`STORY-082`)
- `df/backlog/mvp-definition.md`
- `df/backlog/roadmap.md`
- `df/backlog/architecture-direction.md`
- `df/artifacts/STORY-080/solution-design.md`
- `df/artifacts/STORY-081/solution-design.md`
- `backend/identity-access/src/main/java/com/darkfactory/education/identityaccess/auth/AuthenticationController.java`
- `backend/identity-access/src/main/java/com/darkfactory/education/identityaccess/auth/IdentityAuthenticationService.java`
- `backend/identity-access/src/main/java/com/darkfactory/education/identityaccess/auth/IdentityRoleAssignmentService.java`

## Follow-up actions

- Route `STORY-082` to `backend-dev` in `READY_FOR_DEV`.
- Update the shared architecture direction with the accepted Phase 1 MFA approach.
- Require focused integration coverage for enrollment, activation, verification, non-admin regression, and `/api-docs` exposure.

