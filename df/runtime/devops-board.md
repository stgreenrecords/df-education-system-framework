# DevOps Runtime Subdashboard

This is the live queue for `devops` implementation tasks. The main board remains the overall task source of truth.

| Priority | Task ID | Parent task | Title | State | Owner role | Affected scope | Blocked? | Last updated | Next action |
|---|---|---|---|---|---|---|---|---|---|
| P0 | TASK-009 | - | Create single file for Windows mac and Linux to start application | READY_FOR_DEV | devops | root-level cross-platform local launcher, local runtime orchestration, startup/readiness output, related run documentation | No | 2026-05-26 local | Implement one cross-platform launcher file, validate the local backend stack startup path, and hand off to QA with exact runtime evidence. |
| P0 | STORY-023 | - | Define cloud-portable Kubernetes and IaC deployment baseline | DONE | factory | `devops/kubernetes/**`, `devops/iac/**`, deployment docs, provider overlays/modules | No | 2026-05-24 20:26 local | Accepted by PO |
| P0 | STORY-022 | - | Implement Podman-compatible OCI container baseline | DONE | factory | `devops/**`, runtime packaging, OCI image/build assets, local PostgreSQL container baseline | No | 2026-05-24 19:52 local | Accepted by PO |

## Lane notes

- 2026-05-26 local: SA routed `TASK-009` to `devops` from an explicit user request for one startup file that works across Windows, macOS, and Linux. The recommended solution is a single Java source launcher that orchestrates local PostgreSQL, backend startup from `backend/platform-core`, readiness checks, and optional website startup when `node`/`npm` are available.
- New DevOps implementation tasks must be added here before `devops` starts work.
- DevOps implementation notes belong under `df/artifacts/{task-id}/devops/`.
- Do not track design, backend, frontend, or data-engineering tasks on this subdashboard.
- `STORY-023` is the current active Phase 1 deployment-baseline task and must preserve the provider-neutral Kubernetes base vs provider-specific overlay/module split documented by SA.
