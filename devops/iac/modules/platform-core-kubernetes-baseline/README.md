# Module: platform-core-kubernetes-baseline

This module captures the provider-neutral deployment contract for `platform-core`.

It does not create live infrastructure in this story. Instead, it standardizes the values that provider-specific wrappers must supply:

- Kubernetes base and overlay paths
- image reference inputs
- namespace and ingress class
- runtime config/secret names
- registry, secret-store, database, and observability endpoints
- provider-specific service and workload-identity annotations

The module is intentionally OpenTofu/Terraform-compatible and avoids provider plugins so the contract can be validated locally without cloud credentials.

