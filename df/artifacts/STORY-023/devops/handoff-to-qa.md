# DevOps to QA Handoff - STORY-023

## Summary

DevOps completed the first cloud-portable Kubernetes and IaC deployment baseline for `platform-core`.

The implementation now provides:

- a provider-neutral Kubernetes base under `devops/kubernetes/platform-core/base/`
- provider-specific overlays for AWS, Azure, Google Cloud, and self-hosted/on-prem under `devops/kubernetes/platform-core/overlays/`
- an OpenTofu/Terraform-compatible module contract under `devops/iac/modules/platform-core-kubernetes-baseline/`
- provider wrapper modules for AWS, Azure, Google Cloud, and self-hosted/on-prem under `devops/iac/providers/`
- validation helpers for manifest rendering and provider-module validation

## Files for QA review

- `devops/kubernetes/platform-core/base/kustomization.yaml`
- `devops/kubernetes/platform-core/base/resources.yaml`
- `devops/kubernetes/platform-core/overlays/aws/`
- `devops/kubernetes/platform-core/overlays/azure/`
- `devops/kubernetes/platform-core/overlays/gcp/`
- `devops/kubernetes/platform-core/overlays/self-hosted/`
- `devops/kubernetes/platform-core/render-manifests.ps1`
- `devops/kubernetes/platform-core/README.md`
- `devops/iac/modules/platform-core-kubernetes-baseline/`
- `devops/iac/providers/aws/`
- `devops/iac/providers/azure/`
- `devops/iac/providers/gcp/`
- `devops/iac/providers/self-hosted/`
- `devops/iac/validate-provider-modules.ps1`
- `devops/iac/README.md`
- `df/artifacts/STORY-023/devops/dev-notes.md`

## Validation performed by DevOps

| Check | Command/source | Result | Notes |
|---|---|---|---|
| Tool availability review | `Get-Command kubectl`, `terraform`, `tofu`, `kustomize`, `docker`, `podman` | PASS | `kubectl` and Terraform were available; `tofu`, `kustomize`, and `podman` were missing locally |
| Kubernetes base + overlay render | `powershell -ExecutionPolicy Bypass -File .\devops\kubernetes\platform-core\render-manifests.ps1` | PASS | Base and all four overlays rendered successfully; provider-neutral base scan passed |
| IaC formatting | `terraform fmt -recursive .\devops\iac` | PASS | Formatting applied before final validation |
| IaC provider-module validation | `powershell -ExecutionPolicy Bypass -File .\devops\iac\validate-provider-modules.ps1` | PASS | AWS, Azure, GCP, and self-hosted wrapper modules all validated successfully |
| Workspace status snapshot | `git --no-pager status --short --branch -- devops df/artifacts/STORY-023 df/runtime` | PASS | Unrelated workspace changes remain outside `STORY-023` scope |

## QA focus areas

1. Confirm the provider-neutral Kubernetes base does not contain provider-specific ingress, load-balancer, IAM/workload-identity, registry, or secret-store assumptions.
2. Confirm all four overlays exist and keep provider-specific configuration outside the base.
3. Re-run `render-manifests.ps1` and verify the base scan still passes.
4. Re-run `validate-provider-modules.ps1` and confirm the provider wrapper modules remain valid with the locally available HCL tool.
5. Check that no application source code was changed for this story and that deployment variability stays in `devops/**` only.
6. Confirm the documented fallback behavior is honest: Terraform was used locally because `tofu` was unavailable, and there was no live Kubernetes cluster deployment in this environment.

## Known risks / limitations

- `RISK-028`: local validation used `kubectl kustomize` and Terraform because standalone `kustomize` and OpenTofu were unavailable.
- No live multi-cloud or local-cluster deployment was performed because this story scope is the baseline contract, not a credentialed rollout.
- Overlay values remain placeholders/examples and must be replaced by country/operator-managed values later.

## Recommended QA outcome criteria

- PASS if rendering and HCL validation can be reproduced and the provider-neutral vs provider-specific separation is preserved.
- FAIL if provider-specific assumptions appear in the base, a provider path is missing/broken, or the IaC contract stops being OpenTofu/Terraform-compatible.

