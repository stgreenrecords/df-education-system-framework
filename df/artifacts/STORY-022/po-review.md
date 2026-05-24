# PO Review - STORY-022

## Product decision

ACCEPTED

## Business outcome

The story delivers the intended Phase 1 product outcome: the framework now has a portable OCI packaging baseline for `backend/platform-core`, a reproducible local PostgreSQL-backed container workflow, and an initial health/readiness contract that downstream deployment work can reuse without introducing country-specific or cloud-specific application code.

## Acceptance criteria review

| Criterion | Result | Notes |
|---|---|---|
| Given the Maven application build, when the container image is built, then an OCI-compatible application image is produced | PASS | PO confirmed the previously QA-built image `df-platform-core:qa022` is available locally and aligns with the documented OCI-compatible `Containerfile` workflow. |
| Given a developer or country operator uses Podman, when they run the application image with externalized configuration, then the application starts successfully | PASS with environment note | Podman remains the default/runtime-neutral contract in the assets and documentation. Local PO validation used Docker only because `podman` is not installed on this machine. |
| Given PostgreSQL is required, when running the local container baseline, then the application connects to a containerized PostgreSQL instance using environment-provided configuration | PASS | PO started the local stack and confirmed the application reached `UP` status while the companion PostgreSQL container was running. |
| Given the container definition is reviewed, then no secrets, country-specific code, or cloud-specific code are embedded in the image | PASS | PO reviewed QA evidence plus the image/runtime contract and found only runtime-injected configuration, with no embedded secrets or provider/country-specific behavior. |
| Given the image is inspected, then it exposes health/readiness behavior suitable for later orchestration | PASS | PO confirmed the image exposes `8080/tcp`, uses `/platform/status`, and reaches a healthy container state during live validation. |

## End-to-end validation

- Scenario: Start the shipped local stack using the accepted OCI image, let the application connect to containerized PostgreSQL through runtime-provided environment variables, confirm readiness/health, then clean up the temporary resources.
- Expected: The application container becomes healthy, `/platform/status` returns the expected JSON payload, database settings are externalized instead of baked into the image, and cleanup removes the temporary containers/network.
- Actual: `df-platform-core-postgres` and `df-platform-core-app` started successfully via `run-local-stack.ps1` using image `df-platform-core:qa022`; the app reached `Up ... (healthy)` on host port `18086`; `Invoke-WebRequest http://127.0.0.1:18086/platform/status` returned `{"service":"education-system-framework","status":"UP"}`; redacted runtime inspection confirmed `EDU_DB_URL`, `EDU_DB_USERNAME`, and `EDU_DB_PASSWORD` were injected at runtime; `stop-local-stack.ps1` removed the temporary stack resources.
- Result: PASS

## Screenshots / visual evidence

| Path | What it proves |
|---|---|
| n/a | This is a non-UI DevOps/backend runtime story. No screenshots are applicable; terminal/runtime evidence is the appropriate product-review proof. |

## Product quality notes

- QA pass evidence is complete and aligned with the story scope.
- The delivered baseline stays intentionally narrow: local OCI image + PostgreSQL workflow only. Kubernetes, registry, and IaC expansion remain future work in `STORY-023`.
- The Docker fallback is acceptable for this PO decision because the task assumptions explicitly allowed another OCI-compatible runtime when Podman is unavailable locally, and the shipped assets remain Podman-first rather than Docker-daemon-specific.

## Rework request if rejected

- n/a

## Risks accepted

- `RISK-027` — local Podman execution is still unproven on this workstation because the runtime is not installed; the accepted evidence uses the documented Docker OCI fallback.
- `RISK-015` — this story intentionally stops at the local OCI baseline; broader orchestration and IaC scope remains for `STORY-023`.

## Next action

- Accepted: `sa` / factory should pick the next highest-priority actionable task, with `STORY-023` remaining the documented Phase 1 containerization follow-up candidate.
