# Country-Sovereign Deployment Architecture - STORY-020

## Purpose

Define how the Education System Framework is deployed and operated when each country/ministry fully owns its own infrastructure, data, environments, and release decisions.

## Core operating model

Each country deployment is an isolated operating estate.

That means:

- each country owns its own infrastructure
- each country owns its own environments
- each country owns its own PostgreSQL data and backups
- each country controls its own secrets, access, observability, and operational procedures
- no country shares a production runtime or data plane with another country

The framework vendor provides portable release artifacts, migration guidance, compatibility guidance, and documentation, but does not act as the default central operator of country environments.

## Environment topology per country

Every country/ministry deployment should maintain its own environment ladder:

- `dev` — country-local development/integration environment
- `qa` — country-local verification environment
- `stage` — country-local pre-production rehearsal environment
- `prod` — country-local production environment

These environments exist inside one country-owned deployment boundary. They may run on AWS, Azure, Google Cloud, private cloud, or on-premises infrastructure, but the application package and deployment contract remain the same at the framework level.

## Release flow

The release flow is:

`vendor -> package -> country receives -> country tests -> country deploys`

Detailed interpretation:

1. The framework vendor publishes a portable release package, documentation, compatibility notes, and migration guidance.
2. The country receives the release package inside its own controlled process.
3. The country validates the release in its own `dev`, `qa`, and/or `stage` environments.
4. The country decides when and how to deploy to `prod`.
5. The country retains operational control of rollout, rollback, secrets, infrastructure changes, and backups.

## Country-owned responsibilities

The country/ministry owns:

- runtime infrastructure
- Kubernetes clusters or equivalent deployment platform
- databases and storage
- backup and disaster-recovery execution
- environment-specific configuration
- secret management
- access control and operator permissions
- monitoring, logging, and alerting
- release scheduling and deployment approval inside country governance

## Framework-vendor responsibilities

The framework vendor owns:

- application source and release packaging
- portable OCI image/release contract
- migration scripts and compatibility guidance
- architecture and deployment documentation
- advisory support for adoption and upgrades

The vendor does not need continuous privileged access into country data or production infrastructure for the architecture to work.

## Isolation requirements

The architecture forbids cross-country production data flow.

Required isolation properties:

- no shared multi-country production database
- no shared cross-country tenant data plane
- no country-specific application fork in source code
- no provider-specific application code branch for one country
- no operational dependency on a vendor-hosted central runtime for normal country operation

Cross-country sharing is limited to release artifacts, generic documentation, and optional support processes that do not require country data to leave sovereign control.

## Portability boundary

The following elements should stay provider-neutral:

- application source code
- OpenAPI contracts
- database connection contract
- OCI image contract
- base Kubernetes deployment contract
- release and upgrade documentation structure

The following elements may vary by provider or country operations team:

- IAM / operator identity integration
- secret-store implementation
- networking and ingress model
- container registry choice
- managed vs self-hosted PostgreSQL
- monitoring stack implementation
- backup tooling
- load balancers, DNS, certificates, and firewall controls

## Relation to later stories

- `STORY-021` can implement tenant/deployment configuration on top of this sovereign operating model.
- `STORY-023` can define Kubernetes base manifests, overlays, and OpenTofu/Terraform-compatible IaC structure using this document as the governing deployment architecture.
- Security, audit, and release-management work must preserve the same country-owned boundary.

## Non-goals for this story

This story does not yet define:

- actual Kubernetes YAML or Kustomize overlays
- actual OpenTofu/Terraform modules
- CI/CD product selection
- provider-specific networking templates
- country-specific runtime customization in application code

