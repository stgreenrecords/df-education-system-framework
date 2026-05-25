# DevOps Notes - STORY-023

## Task

Define and implement the cloud-portable Kubernetes and IaC deployment baseline for `backend/platform-core` without changing application source code.

## Implementation summary

Implemented the first DevOps-owned Kubernetes and OpenTofu/Terraform-compatible deployment baseline for `platform-core`.

### What changed

| File/path | Change | Notes |
|---|---|---|
| `devops/kubernetes/platform-core/base/` | Created | Added the provider-neutral Kubernetes base with `Namespace`, `ServiceAccount`, `Deployment`, and `Service` plus `GET /platform/status` readiness/liveness probes and runtime secret/config references |
| `devops/kubernetes/platform-core/overlays/aws/` | Created | Added AWS overlay examples for ALB/NLB, EKS workload identity, and provider-specific ingress/service annotations |
| `devops/kubernetes/platform-core/overlays/azure/` | Created | Added Azure overlay examples for Application Gateway, Azure workload identity, and Azure-specific service annotations |
| `devops/kubernetes/platform-core/overlays/gcp/` | Created | Added GCP overlay examples for GCE/GKE ingress, GKE workload identity, and NEG wiring |
| `devops/kubernetes/platform-core/overlays/self-hosted/` | Created | Added self-hosted/on-prem overlay examples for NGINX ingress, MetalLB, and country-managed registry/secret expectations |
| `devops/kubernetes/platform-core/render-manifests.ps1` | Created | Added a manifest render/guardrail helper using `kubectl kustomize` and a provider-neutral base scan |
| `devops/kubernetes/platform-core/README.md` | Created | Documented the Kubernetes layer split, overlay responsibilities, and render commands |
| `devops/iac/modules/platform-core-kubernetes-baseline/` | Created | Added a provider-neutral HCL module that captures the deployment contract boundary without requiring live provider credentials |
| `devops/iac/providers/{aws,azure,gcp,self-hosted}/` | Created | Added provider wrapper modules for AWS, Azure, Google Cloud, and self-hosted/on-prem targets |
| `devops/iac/validate-provider-modules.ps1` | Created | Added an OpenTofu/Terraform-compatible validation helper with Windows PowerShell-safe directory handling |
| `devops/iac/README.md` | Created | Documented the IaC structure, compatibility goal, and validation flow |

## Important implementation details

### 1. Provider-neutral Kubernetes base

The base manifests intentionally contain only shared application deployment behavior:

- reusable image placeholder `education-system-framework/platform-core:latest`
- container port `8080`
- readiness and liveness probes against `GET /platform/status`
- runtime configuration boundary through `platform-core-runtime-config` and `platform-core-runtime-secrets`
- generic resource defaults only

The base does **not** contain cloud-specific ingress, load-balancer, IAM/workload-identity, registry, or secret-store assumptions.

### 2. Provider-specific overlays remain outside the base

Each overlay owns provider-specific ingress, service annotations, and workload-identity/secret-store expectations:

- `aws` -> ALB/NLB + EKS role placeholder
- `azure` -> Application Gateway + Azure workload identity placeholder
- `gcp` -> GCE/GKE ingress + GKE workload identity placeholder
- `self-hosted` -> NGINX + MetalLB + country-managed registry/secret expectations

### 3. IaC contract stays OpenTofu/Terraform-compatible

The HCL avoids provider resources and live credentials in this story. Instead, the reusable module and provider wrappers define the deployment contract boundary for:

- registry/image coordinates
- namespace and ingress class
- secret-store integration names
- database endpoint wiring
- observability endpoint wiring
- provider-specific service/workload annotations

This keeps the baseline locally valid with open tooling while remaining ready for later provider-specific infrastructure expansion.

### 4. Validation helpers needed two fixes during implementation

Initial validation found two issues:

1. `kubectl kustomize` emitted a deprecation warning for `commonLabels`, which PowerShell surfaced as a failure path.
   - Resolution: removed deprecated `commonLabels`/`commonAnnotations` usage from the base `kustomization.yaml`.
2. The first IaC validation helper used `terraform -chdir=...`, which failed on Windows PowerShell with spaced paths.
   - Resolution: changed the helper to `Push-Location` into each provider directory before running `init` and `validate`.

## Validation commands and results

### Tool availability snapshot

```text
Command/source: Get-Command kubectl|kustomize|tofu|terraform|docker|podman
Result:
- kubectl=C:\Program Files\Docker\Docker\resources\bin\kubectl.exe
- kustomize=MISSING
- tofu=MISSING
- terraform=C:\Users\Viach\AppData\Local\Microsoft\WinGet\Packages\Hashicorp.Terraform_Microsoft.Winget.Source_8wekyb3d8bbwe\terraform.exe
- docker=C:\Program Files\Docker\Docker\resources\bin\docker.exe
- podman=MISSING
Notes: Validation used `kubectl kustomize` and Terraform because standalone `kustomize`, `tofu`, and `podman` were not available locally.
```

### Kubernetes render validation

```text
Command: powershell -ExecutionPolicy Bypass -File .\devops\kubernetes\platform-core\render-manifests.ps1
Result: PASS
Observed:
- Rendered base: 4 document(s)
- Rendered aws: 5 document(s)
- Rendered azure: 5 document(s)
- Rendered gcp: 5 document(s)
- Rendered self-hosted: 5 document(s)
- Provider-neutral base check: PASS
```

### IaC formatting

```text
Command: terraform fmt -recursive .\devops\iac
Result: PASS
Observed: formatted `devops\iac\modules\platform-core-kubernetes-baseline\main.tf` and `devops\iac\providers\aws\main.tf` during the first validation pass; subsequent validation ran against the formatted result.
```

### IaC validation

```text
Command: powershell -ExecutionPolicy Bypass -File .\devops\iac\validate-provider-modules.ps1
Result: PASS
Observed:
- Success! The configuration is valid.
- Validated provider module: aws
- Validated provider module: azure
- Validated provider module: gcp
- Validated provider module: self-hosted
- Validation tool used: C:\Users\Viach\AppData\Local\Microsoft\WinGet\Packages\Hashicorp.Terraform_Microsoft.Winget.Source_8wekyb3d8bbwe\terraform.exe
```

### Workspace status snapshot

```text
Command: git --no-pager status --short --branch -- devops df/artifacts/STORY-023 df/runtime
Result: PASS
Observed branch: ## master...origin/master
Notes: Repository contains unrelated pre-existing workspace changes outside `STORY-023`; DevOps changes for this task are limited to deployment-baseline files plus runtime/task documentation.
```

## Acceptance criteria status

| AC | Status | Evidence |
|---|---|---|
| 1. Application code remains unchanged across AWS, Azure, Google Cloud, and self-hosted/on-prem targets | PASS | Only `devops/**` deployment assets and task/runtime docs were changed; the baseline reuses the existing OCI/runtime contract from `STORY-022` without backend source changes |
| 2. Kubernetes manifests separate provider-neutral application deployment from provider-specific infrastructure concerns | PASS | `devops/kubernetes/platform-core/base/**` vs `devops/kubernetes/platform-core/overlays/{aws,azure,gcp,self-hosted}/**`; render helper also checks that provider-specific markers are absent from the base |
| 3. Provider-specific IaC modules exist or are planned for AWS, Azure, Google Cloud, and self-hosted/on-prem | PASS | `devops/iac/providers/aws/`, `azure/`, `gcp/`, and `self-hosted/` all exist and validate successfully |
| 4. IaC strategy supports an open-source OpenTofu-compatible path and can accommodate Terraform if required | PASS | HCL stays provider-neutral and validated locally with Terraform; `README.md` and validation helper explicitly prefer `tofu` when available and fall back to Terraform |
| 5. Registries, secret stores, networking, databases, and observability remain configurable per provider without changing application source code | PASS | The reusable module contract plus overlay/provider wrappers externalize registry host, secret-store name, ingress class, database endpoint, observability endpoint, and provider annotations |

## Environment notes

- OS: Windows
- Kubernetes renderer available: `kubectl kustomize`
- Standalone `kustomize`: not available locally
- OpenTofu (`tofu`): not available locally
- Terraform: available locally and used for HCL validation
- Docker: available locally but not required for this story's validation path
- Podman: not available locally

## Risks and limitations

- `RISK-028` remains open: validation used `kubectl kustomize` and Terraform because standalone `kustomize` and OpenTofu were not installed locally.
- No live Kubernetes cluster deployment was performed; the story scope and current environment support render/validation evidence rather than credentialed multi-cloud rollout.
- Overlay/provider values remain placeholders/examples and must be replaced by country/operator-owned deployment values later.

