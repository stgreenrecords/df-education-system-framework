# DevOps Runtime Subdashboard

This is the live queue for `devops` implementation tasks. The main board remains the overall task source of truth.

| Priority | Task ID | Parent task | Title | State | Owner role | Affected scope | Blocked? | Last updated | Next action |
|---|---|---|---|---|---|---|---|---|---|
| P0 | STORY-023 | - | Define cloud-portable Kubernetes and IaC deployment baseline | DONE | factory | `devops/kubernetes/**`, `devops/iac/**`, deployment docs, provider overlays/modules | No | 2026-05-24 20:26 local | Accepted by PO |
| P0 | STORY-022 | - | Implement Podman-compatible OCI container baseline | DONE | factory | `devops/**`, runtime packaging, OCI image/build assets, local PostgreSQL container baseline | No | 2026-05-24 19:52 local | Accepted by PO |

## Lane notes

- New DevOps implementation tasks must be added here before `devops` starts work.
- DevOps implementation notes belong under `df/artifacts/{task-id}/devops/`.
- Do not track design, backend, frontend, or data-engineering tasks on this subdashboard.
- `STORY-023` is the current active Phase 1 deployment-baseline task and must preserve the provider-neutral Kubernetes base vs provider-specific overlay/module split documented by SA.
