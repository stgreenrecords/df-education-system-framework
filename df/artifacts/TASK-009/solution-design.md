# Solution Design - TASK-009

## Summary

Add one cross-platform local launcher file that can start the currently implemented application stack on Windows, macOS, and Linux, while respecting the repository's existing backend/frontend/runtime boundaries.

## Context

The repository currently has working but fragmented local-run paths:

- backend startup uses Maven + Spring Boot from `backend/platform-core`
- local database startup can use Docker/PostgreSQL
- website startup requires `frontend/website` plus Node.js/npm
- existing automation under `devops/container/platform-core/` is PowerShell-oriented and container-stack-specific

The user explicitly requested one file that works across Windows, macOS, and Linux. This is broader than documentation-only guidance and belongs to runtime/startup automation.

## Requirements and acceptance criteria

- Provide one launcher file instead of separate `.cmd`, `.sh`, and `.ps1` entrypoints.
- Support Windows, macOS, and Linux.
- Start the local backend stack using the real repository module entrypoint and runtime configuration.
- Print or document the URLs a user should check after startup.
- Use safe local defaults and environment-variable overrides rather than real secrets.
- Handle website startup toolchain gaps gracefully if website startup is included.

## Proposed solution

### Launcher format

Implement one Java single-file source launcher at the repository root, for example `run-local.java`.

Reasoning:

1. The backend already requires Java 25+, so Java is the most defensible shared runtime assumption in this repository.
2. Java single-file source mode (`java run-local.java`) works cross-platform without a separate build step.
3. The launcher can use `ProcessBuilder` to choose:
   - `mvnw.cmd` on Windows
   - `sh ./mvnw ...` on macOS/Linux
4. The launcher can detect Docker availability, start PostgreSQL, start the backend, optionally start the website when `node`/`npm` are present, and stream subprocess output.

### Runtime behavior

The launcher should:

1. Detect the operating system.
2. Verify required tools for the backend path:
   - Java is already present because the launcher itself is running.
   - Docker for local PostgreSQL quick-start.
3. Start or recreate a local PostgreSQL container with safe defaults.
4. Select a host database port, ideally configurable and preferably with collision handling.
5. Export/pass safe local environment variables for:
   - `EDU_DB_URL`
   - `EDU_DB_USERNAME`
   - `EDU_DB_PASSWORD`
   - `EDU_AUTH_JWT_SECRET`
   - `EDU_AUTH_MFA_SECRET_ENCRYPTION_KEY`
   - optional bootstrap-admin settings
6. Start the backend using the verified module-level command:
   - `backend/platform-core/pom.xml`
7. Wait for backend readiness using `GET /platform/status`.
8. Print resulting URLs:
   - backend health
   - OpenAPI
   - optional website URL if started
9. Optionally start `frontend/website` only when `node` and `npm` are available, otherwise print a clear skip message.
10. Shut down child processes and/or print cleanup instructions on termination.

### Scope ownership

This task is routed to `devops` because it is startup/build/runtime automation and operational documentation, not backend business logic or frontend UI behavior.

## Files/components likely affected

- new root launcher file such as `run-local.java`
- `README.md`
- `docs/run-application.md`
- possibly `devops/container/platform-core/README.md` if cross-reference is helpful
- `df/artifacts/TASK-009/devops/dev-notes.md`
- `df/artifacts/TASK-009/devops/handoff-to-qa.md`

## Data/API contract changes

- None.

## Security/privacy considerations

- Do not hardcode real secrets.
- Use local-only demo defaults and allow environment-variable overrides.
- Avoid logging secrets to stdout/stderr.
- Keep the launcher local-development-focused; do not imply production suitability.

## Test strategy

`devops` should validate at minimum:

1. Static review of OS command branching.
2. Local launcher execution on the current macOS environment.
3. Backend readiness check via `/platform/status`.
4. OpenAPI reachability via `/api-docs`.
5. Graceful behavior when `node`/`npm` are absent.
6. Clear printed startup instructions/URLs.

If feasible, also validate command generation logic for Windows paths without requiring a full Windows environment in this session.

## Risks and mitigations

- **Risk:** cross-platform process invocation differs between Windows and Unix.
  - **Mitigation:** centralize OS detection and command assembly in one launcher.
- **Risk:** occupied local ports break startup.
  - **Mitigation:** make ports configurable and document defaults/fallbacks.
- **Risk:** website startup blocks the whole launcher when frontend tools are missing.
  - **Mitigation:** treat website startup as optional and print an actionable skip message.
- **Risk:** task drifts into backend/frontend source changes.
  - **Mitigation:** keep implementation limited to DevOps-owned launcher/runtime documentation unless a clear cross-lane blocker is discovered.

## Rollback plan

- Remove the new launcher file.
- Revert the related README/docs references.
- Fall back to the existing documented manual startup steps in `docs/run-application.md`.

## Open questions

- None blocking for DevOps execution. If implementation reveals that Java single-file mode is unsuitable in practice, `devops` should document the evidence and return to SA with an alternative single-file runtime proposal rather than improvising multi-file OS-specific wrappers.

