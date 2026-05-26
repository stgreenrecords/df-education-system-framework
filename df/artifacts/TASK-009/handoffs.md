# Handoff - TASK-009

## SA -> devops

- Timestamp: 2026-05-26 local
- Task: TASK-009
- From state: ARCHITECTURE_IN_PROGRESS
- To state: READY_FOR_DEV
- Lane: devops
- Subdashboard: `df/runtime/devops-board.md`
- Summary: SA routed the user's request for one Windows/macOS/Linux startup file to `devops` as local runtime automation. The recommended approach is one Java single-file source launcher that orchestrates local PostgreSQL, backend startup from `backend/platform-core`, readiness checks, and optional website startup when the frontend toolchain is available.

## Evidence

- `df/artifacts/TASK-009/task.md`
- `df/artifacts/TASK-009/solution-design.md`
- `docs/run-application.md`
- `README.md`
- `backend/platform-core/pom.xml`
- `backend/platform-core/src/main/resources/application.properties`
- `frontend/website/package.json`
- `devops/container/platform-core/README.md`
- `df/runtime/board.md`
- `df/runtime/devops-board.md`

## Tests/checks

| Check | Command/source | Result | Notes |
|---|---|---|---|
| Existing runtime-path review | `docs/run-application.md`; `README.md`; `backend/platform-core/pom.xml`; `backend/platform-core/src/main/resources/application.properties`; `frontend/website/package.json`; `devops/container/platform-core/README.md` | PASS | Confirmed the repository currently has manual/documented local-run paths but no single cross-platform launcher file |
| Lane routing review | `df/runtime/devops-board.md`; `df/roles/devops.md` | PASS | The requested work fits DevOps scope: startup/build/runtime automation and operational documentation |
| Cross-platform runtime assumption review | repository requirements + `java.version=25` | PASS | Java is already a hard prerequisite for the backend, making a Java single-file launcher the safest cross-platform single-file assumption currently available |

## Constraints

- Do not edit backend or frontend source files unless a real cross-lane blocker forces rerouting.
- Keep secrets local/demo only and allow environment-variable overrides.
- Avoid multi-file OS-specific wrappers; the task goal is one launcher file.
- Website startup must degrade gracefully when `node`/`npm` are unavailable.

## Recommended approach

1. Add one root launcher file, preferably `run-local.java`.
2. Detect Windows vs Unix-like command execution at runtime.
3. Start local PostgreSQL through Docker with configurable defaults.
4. Start backend from `backend/platform-core` using the verified module-level Maven command.
5. Wait for `/platform/status` and print local URLs.
6. Optionally start `frontend/website` only if `node` and `npm` are available.
7. Update root/docs instructions to use the new launcher.

## Risks

- Windows command branching may differ from Unix command branching.
- Fixed local ports may already be occupied.
- Website startup may not be possible in the current environment because `node`/`npm` are absent.

## Next role instructions

- `devops` should implement the single-file launcher and validate it locally on the current macOS environment.
- `devops` should document exact commands, runtime behavior, and limitations in `df/artifacts/TASK-009/devops/dev-notes.md`.
- If implementation succeeds, hand off to QA with reproducible verification steps.
- If the single-file constraint proves impossible with the current repository/runtime assumptions, return the task to SA with concrete evidence instead of substituting multi-file wrappers.

