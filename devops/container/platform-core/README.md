# Platform Core OCI Baseline

This folder contains the first Podman-compatible OCI baseline for `backend/platform-core`.

## Scope

- builds an OCI image for the existing Spring Boot `platform-core` application
- runs the application against a containerized PostgreSQL dependency using environment-backed configuration
- documents `/platform/status` as the initial readiness endpoint for later orchestration work

## Assets

- `Containerfile` - OCI image definition
- `build-image.ps1` - Maven package + OCI image build helper
- `run-local-stack.ps1` - local PostgreSQL + application container startup helper
- `stop-local-stack.ps1` - cleanup helper

## Podman-first workflow

```powershell
.\devops\container\platform-core\build-image.ps1
.\devops\container\platform-core\run-local-stack.ps1
Invoke-WebRequest -UseBasicParsing http://127.0.0.1:18084/platform/status
.\devops\container\platform-core\stop-local-stack.ps1
```

## Docker fallback for environments without Podman

The assets are kept OCI-compatible and avoid Docker-daemon-specific assumptions. If Podman is not available locally, the same helpers can be used with Docker as an environment fallback:

```powershell
.\devops\container\platform-core\build-image.ps1 -ContainerRuntime docker
.\devops\container\platform-core\run-local-stack.ps1 -ContainerRuntime docker
Invoke-WebRequest -UseBasicParsing http://127.0.0.1:18084/platform/status
.\devops\container\platform-core\stop-local-stack.ps1 -ContainerRuntime docker
```

## Runtime contract

The application container expects environment-backed configuration only.

Required database variables:

- `EDU_DB_URL`
- `EDU_DB_USERNAME`
- `EDU_DB_PASSWORD`

Optional translation-related examples used by the helper script:

- `EDU_TRANSLATION_DEFAULT_LANGUAGE`
- `EDU_TRANSLATION_GLOBAL_FALLBACK_LANGUAGE`
- `EDU_TRANSLATION_CACHE_TTL`

Readiness endpoint:

- `GET /platform/status`
- container port: `8080`

## Notes

- Build the image from the repository root context so the packaged executable jar at `backend/platform-core/target/platform-core-0.1.0-SNAPSHOT-exec.jar` is available to the `Containerfile`.
- Do not put secrets into the `Containerfile`, scripts, or source control. Override credentials at runtime.
- Kubernetes and IaC provider overlays are intentionally out of scope here and belong to `STORY-023`.

