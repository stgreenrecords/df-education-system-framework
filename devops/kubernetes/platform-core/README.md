# Platform Core Kubernetes Baseline

This directory contains the `STORY-023` Kubernetes deployment baseline for `backend/platform-core`.

## Layering model

- `base/` holds the provider-neutral application deployment contract.
- `overlays/aws/`, `overlays/azure/`, `overlays/gcp/`, and `overlays/self-hosted/` hold provider-specific ingress, identity, load-balancer, and secret-store examples.
- The base stays reusable across sovereign country deployments. Operators change overlays, image coordinates, registry access, secret sources, database endpoints, and observability wiring without changing application code.

## Provider-neutral base contract

The base intentionally contains only shared workload behavior:

- `Namespace`, `ServiceAccount`, `Deployment`, and `Service`
- image placeholder `education-system-framework/platform-core:latest`
- readiness and liveness probes using `GET /platform/status`
- secret/config references through `platform-core-runtime-config` and `platform-core-runtime-secrets`
- overridable resource defaults

Forbidden in `base/`:

- cloud-specific ingress or load-balancer annotations
- provider IAM/workload-identity bindings
- registry vendor assumptions
- secret-store vendor assumptions
- country-specific values or live credentials

## Render commands

```powershell
.\devops\kubernetes\platform-core\render-manifests.ps1
.\devops\kubernetes\platform-core\render-manifests.ps1 -Targets aws,azure,gcp,self-hosted
```

The render helper uses `kubectl kustomize` and also checks that the base files do not contain provider-specific markers.

## Overlay intent

| Overlay | What changes outside the base |
|---|---|
| `aws` | ALB/NLB examples, EKS workload identity placeholder, AWS-oriented secret-store note |
| `azure` | Application Gateway example, Azure workload identity placeholder, Azure load-balancer health probe note |
| `gcp` | GCE/GKE ingress example, GKE workload identity placeholder, Google Secret Manager note |
| `self-hosted` | NGINX + MetalLB example, self-managed registry/secret-store expectations |

These overlays are baseline examples, not production-ready country values.

## Runtime boundary

The same OCI image produced by `STORY-022` is reused here. Countries/operators must still provide:

- image registry/repository decisions
- secret material and sync mechanism
- database endpoint wiring
- ingress DNS/TLS values
- observability endpoints

## Notes

- The Kubernetes assets are renderable without live cloud credentials.
- A real cluster deployment is intentionally out of scope for this story; QA should validate rendering and separation boundaries.
- Provider-specific infrastructure composition lives under `devops/iac/`.

