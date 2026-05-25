# Platform Core IaC Baseline

This directory contains the `STORY-023` infrastructure-as-code baseline for the cloud-portable deployment contract.

## Structure

- `modules/platform-core-kubernetes-baseline/` defines the provider-neutral application deployment contract.
- `providers/aws/`, `providers/azure/`, `providers/gcp/`, and `providers/self-hosted/` supply provider-specific wiring, defaults, and example variable contracts.

## Compatibility

The HCL is intentionally limited to OpenTofu/Terraform-compatible language features and does not require committing provider credentials or live country values.

Validation helper:

```powershell
.\devops\iac\validate-provider-modules.ps1
```

The script prefers `tofu` when available and falls back to `terraform`. It runs formatting checks plus `init -backend=false` and `validate` for each provider module, then removes local `.terraform` state from the provider folders.

## What the modules do in this story

The modules define the baseline deployment contract and provider-specific configuration boundary for:

- registry/image coordinates
- namespace and ingress selection
- secret-store integration names
- database endpoint wiring
- observability endpoint wiring
- provider-specific service and workload annotations

They intentionally stop short of creating live cloud resources because this story is a baseline contract, not a credentialed environment rollout.

