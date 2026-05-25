# Cloud-Portable Deployment Baseline - STORY-023

## Purpose

Define the deployment-layer contract that DevOps will implement so the same application image can be promoted across sovereign country deployments on AWS, Azure, Google Cloud, and self-hosted/on-prem infrastructure without changing application source code.

## Governing constraints

- `STORY-020` is authoritative for country-sovereign operations: each country owns environments, data, backups, secrets, observability, and deployment execution.
- `STORY-022` is authoritative for the OCI runtime/image baseline and the initial `/platform/status` readiness contract.
- No country-specific code, schema, API, or framework-structure changes are allowed.
- Provider-specific differences belong in deployment overlays and IaC modules, not in application code.

## Baseline layering

### Layer 1 - Provider-neutral application deployment

Own the reusable application deployment contract here.

Expected responsibilities:

- image repository/tag input
- namespace/workload naming conventions
- container port exposure
- readiness/liveness probes using `GET /platform/status`
- environment variable and secret/config references
- generic labels/annotations
- overridable resource defaults
- generic service definition

Forbidden content in this layer:

- provider-specific ingress/load-balancer annotations
- provider-specific registry authentication logic
- provider-specific IAM/service-account assumptions
- cloud-specific secret-store implementations
- managed-database vendor assumptions
- country-specific values or secrets

### Layer 2 - Provider-specific deployment overlays

Own per-provider deployment differences here.

Expected provider paths:

- AWS
- Azure
- Google Cloud
- Self-hosted / on-prem

Expected responsibilities:

- ingress/controller class and annotations
- registry coordinates and pull-secret expectations
- secret-store integration strategy
- networking and exposure specifics
- storage class / persistent volume specifics where needed
- observability/exporter wiring that depends on provider tooling

### Layer 3 - OpenTofu-compatible IaC modules

Own infrastructure composition and deployment-variable contracts here.

Expected module split:

- reusable provider-neutral module for the application baseline
- provider-specific entry modules/wrappers for AWS, Azure, Google Cloud, and self-hosted/on-prem

Expected responsibilities:

- cluster/namespace selection or creation hooks
- registry configuration variables
- secret/config input contract
- database endpoint wiring
- networking/DNS inputs
- observability integration inputs
- outputs needed by country operators

## Proposed repository layout

```text
devops/
├── kubernetes/
│   └── platform-core/
│       ├── base/
│       └── overlays/
│           ├── aws/
│           ├── azure/
│           ├── gcp/
│           └── self-hosted/
└── iac/
    ├── modules/
    │   └── platform-core-kubernetes-baseline/
    └── providers/
        ├── aws/
        ├── azure/
        ├── gcp/
        └── self-hosted/
```

Exact folder names may vary during implementation, but the separation of provider-neutral base from provider-specific overlays/modules must remain visible in the repository.

## Configuration matrix

The following concerns must remain deployment-configurable rather than code-configured:

| Concern | Provider-neutral base | Provider-specific overlay/module |
|---|---|---|
| Application image | Image reference variable/placeholders | Registry host/repository conventions |
| Secrets | Secret/config references only | Secret-store source and access method |
| Networking | Generic service and port model | Ingress/load balancer/controller specifics |
| Database | Environment variable contract only | Managed/external DB endpoint wiring |
| Observability | Generic hooks/labels/endpoints | Provider-specific exporter/collector integration |
| Scaling | Generic resource/probe defaults | Provider/autoscaling policy specifics |

## Validation expectations for DevOps

Minimum expected evidence:

1. Rendered Kubernetes base and overlays
2. Proof that provider-specific settings are absent from the base
3. Formatted/validated OpenTofu-compatible HCL
4. Documentation of any tooling gaps or unsupported local verification steps
5. If practical, one local Kubernetes smoke path using the existing OCI image/runtime contract

## Non-goals for this story

- live multi-cloud rollout
- production-grade secret values
- provider billing, IAM hardening, or enterprise policy completion
- release-manager automation beyond the baseline deployment structure

## Handoff note for DevOps

DevOps should implement the smallest working baseline that proves the layering model. Where a provider path cannot be fully exercised locally, document the path, variables, and assumptions honestly rather than weakening the provider-neutral boundary.

