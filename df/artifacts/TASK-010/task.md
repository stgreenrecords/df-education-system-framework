# Task - TASK-010

## Summary

Add an on-demand GitHub Actions pipeline that deploys the current application to AWS using repository-managed automation and GitHub secrets.

## Type

Task

## Priority

P0

## Current state

READY_FOR_QA

## Business goal

Reduce manual cloud-deployment work by giving operators a repository-owned, on-demand GitHub workflow that can build, publish, and deploy the currently shippable application baseline to AWS without committing credentials or country-specific values.

## Acceptance criteria

- [ ] The repository contains a GitHub Actions workflow under `.github/workflows/` that can be triggered on demand (`workflow_dispatch`) to deploy the currently supported backend application baseline.
- [ ] The workflow authenticates to AWS using GitHub secrets `AWS_ACCESS_KEY` and `AWS_SECRET_KEY` without hardcoding credentials in source control or logs.
- [ ] The workflow builds the existing `backend/platform-core` OCI image from `devops/container/platform-core/Containerfile`, tags it deterministically, and pushes it to AWS ECR.
- [ ] The workflow deploys the existing AWS Kubernetes baseline for `platform-core` to an operator-specified EKS target using input- or variable-driven values instead of committed environment-specific values.
- [ ] The repository documentation explains required secrets, non-secret inputs/variables, manual trigger steps, expected outputs, and rollback guidance.

## Out of scope

- Provisioning a new AWS account, VPC, EKS cluster, RDS database, IAM roles, DNS records, or TLS certificates from scratch.
- Deploying `frontend/website`, Android, or iOS artifacts.
- Automatic deployment on every push, PR, or tag.
- Replacing the existing provider-neutral Kubernetes and IaC baseline from `STORY-023`.
- QA or PO acceptance in this session.

## Assumptions

- Refinement was skipped because the explicit user request maps cleanly to one DevOps automation task and the repository already contains the relevant OCI plus AWS deployment baselines.
- The current deployable application scope for AWS is `backend/platform-core`, which already has an OCI baseline and AWS Kubernetes overlay.
- GitHub secrets `AWS_ACCESS_KEY` and `AWS_SECRET_KEY` correspond to an IAM principal that is allowed to push to the target ECR repository and obtain Kubernetes access to the target EKS cluster.
- Non-secret deployment values such as AWS region, EKS cluster name, ECR registry/repository, namespace, and public host can be supplied as workflow-dispatch inputs and/or GitHub repository variables.

## Dependencies

- `STORY-022` for the accepted OCI image baseline in `devops/container/platform-core/`
- `STORY-023` for the accepted AWS Kubernetes and IaC deployment baseline in `devops/kubernetes/platform-core/overlays/aws/` and `devops/iac/providers/aws/`
- `.github/` repository metadata path

## Risks

- Long-lived AWS access keys in GitHub secrets carry higher rotation and blast-radius risk than GitHub OIDC federation.
- Live deployment cannot be fully proven from the local workstation; DevOps and QA will need workflow-run evidence and safe local render/build checks.
- The current AWS overlay still uses placeholder environment values, so DevOps must parameterize deployment-time substitutions carefully without committing country-specific settings.

## Links

- Issue: n/a
- PR: n/a
- Design: `df/artifacts/TASK-010/solution-design.md`

## Implementation lane

- Lane: `devops`
- Subdashboard: `df/runtime/devops-board.md`
- Artifact folder for implementation notes: `df/artifacts/TASK-010/devops/`

## Role history

| Timestamp | Role | State | Summary |
|---|---|---|---|
| 2026-05-26 local | sa | OPEN -> NEEDS_ARCHITECTURE | Promoted the explicit user request for an on-demand AWS deployment pipeline into a new task. Refinement was skipped because the request is already narrow, testable, and anchored to the accepted AWS/OCI deployment baseline in the repository. |
| 2026-05-26 local | sa | NEEDS_ARCHITECTURE -> ARCHITECTURE_IN_PROGRESS | Started architecture because the task affects CI/CD, cloud credential handling, container publication, Kubernetes deployment rollout, and rollback/verification expectations. |
| 2026-05-26 local | sa | ARCHITECTURE_IN_PROGRESS -> READY_FOR_DEV | Selected a DevOps-owned manual GitHub Actions `workflow_dispatch` pipeline that reuses the existing `platform-core` OCI baseline plus AWS Kubernetes overlay and routed implementation to `devops`. |
| 2026-05-26 local | devops | READY_FOR_DEV -> READY_FOR_QA | Implemented `.github/workflows/deploy-aws-on-demand.yml`, added the AWS deployment runbook and reusable manifest render helper, validated Maven plus offline manifest rendering, and prepared QA handoff evidence while documenting the local Docker/AWS runtime limitations. |

