# Solution Design - TASK-008

## Summary

Add a dedicated local run guide for the Education System Framework and link it from the root `README.md` so humans and agents can start the backend and website using the correct module/project entrypoints.

## Context

The repository contains multiple project areas (`backend`, `frontend`, and `devops`) plus existing task-specific notes, but the root documentation does not currently provide one consolidated, practical guide for starting the application locally. The explicit user request is for runnable documentation, not a new feature.

## Requirements and acceptance criteria

- Document the correct backend startup command from the executable `backend/platform-core` module.
- Document the backend environment variables needed for local startup.
- Include health-check validation and an optional bootstrap-admin login example.
- Document website startup from `frontend/website` and state the Node.js 20+ requirement.
- Link the new guide from the root `README.md`.

## Proposed solution

1. Create `docs/run-application.md` as the single, focused local run guide.
2. Base the instructions on repository evidence:
   - `backend/platform-core/pom.xml`
   - `backend/platform-core/src/main/resources/application.properties`
   - `frontend/website/package.json`
   - existing frontend and container READMEs
3. Include a Docker-backed PostgreSQL quick-start path because it is the smallest reproducible local database option available in the current repo/tooling.
4. Document the verified Spring Boot startup command using the module POM instead of the backend parent POM.
5. Add troubleshooting notes for the real failure modes observed during verification.
6. Update `README.md` to point readers to the dedicated guide.

Because this is a documentation-only repository change, no delivery lane applies after SA completion; the task moves directly to `READY_FOR_QA` for independent verification.

## Files/components likely affected

- `README.md`
- `docs/run-application.md`
- `df/artifacts/TASK-008/task.md`
- `df/artifacts/TASK-008/solution-design.md`
- `df/artifacts/TASK-008/handoffs.md`
- `df/runtime/board.md`
- `df/runtime/activity-log.md`

## Data/API contract changes

- None.

## Security/privacy considerations

- Documentation must not include real secrets.
- Sample values must be explicitly local/demo values only.
- The guide should encourage overriding secrets through environment variables rather than source edits.

## Test strategy

- Inspect the backend POM, application properties, frontend package metadata, and existing container README for alignment.
- Verify the documented backend startup path against a real PostgreSQL container.
- Verify `GET /platform/status` and `GET /api-docs` after startup.
- Verify the optional bootstrap-admin login example with demo environment variables.
- Run doc-file diagnostics after editing.

## Risks and mitigations

- Risk: documenting the wrong `spring-boot:run` location.
  - Mitigation: use the verified module-level command and explicitly warn against the failing parent-level variant.
- Risk: demo secrets accidentally trigger weak-key failures.
  - Mitigation: document example raw-string secrets that do not look like short base64 payloads and add troubleshooting guidance.
- Risk: readers assume the website can run without Node.js.
  - Mitigation: state the `frontend/website` engine requirement clearly and keep backend-only steps usable independently.

## Rollback plan

- Remove `docs/run-application.md`.
- Remove the README link/summary section.
- Revert the runtime/task documentation entries for `TASK-008`.

## Open questions

- None for SA execution. QA should confirm that the new guide stays documentation-only, reflects the actual module entrypoints, and does not overstate frontend validation beyond the declared prerequisites.

