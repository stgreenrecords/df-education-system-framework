# Decision Record - DECISION-024

- Date: 2026-05-26
- Status: Accepted
- Owner role: SA
- Related task: TASK-010

## Context

The repository already contains a reusable OCI image baseline for `backend/platform-core` (`STORY-022`) and an accepted AWS Kubernetes/IaC deployment baseline (`STORY-023`), but it has no GitHub Actions workflow for cloud deployment. The user explicitly requested an on-demand AWS deployment pipeline and has already created GitHub repository secrets named `AWS_ACCESS_KEY` and `AWS_SECRET_KEY`.

The next step should therefore add repository-owned deployment automation that reuses the existing AWS/EKS/ECR-oriented baseline, avoids committing credentials or country values, and keeps the first delivery step narrow and operator-controlled.

## Decision

For `TASK-010`, the repository will use a manual GitHub Actions `workflow_dispatch` deployment pipeline as the first AWS deployment automation path.

The workflow will:

1. authenticate to AWS using the GitHub secrets `AWS_ACCESS_KEY` and `AWS_SECRET_KEY`;
2. build the accepted `backend/platform-core` OCI image from `devops/container/platform-core/Containerfile`;
3. push that image to AWS ECR with a deterministic tag; and
4. deploy the existing AWS Kubernetes overlay for `platform-core` to a target EKS cluster using externalized, deployment-time values rather than committed environment-specific settings.

This task remains limited to the current backend application baseline and pre-provisioned AWS infrastructure. It does not include automatic deploy-on-push behavior or infrastructure provisioning from scratch.

## Consequences

- `devops` owns the workflow, supporting manifest/render changes, and deployment documentation.
- The first repository AWS deployment path will be operator-triggered and reversible rather than continuously automatic.
- Existing AWS access-key secrets can be used immediately, but the repository should treat GitHub OIDC as a future hardening improvement rather than the current baseline.
- QA evidence will likely need a mix of local build/render checks and GitHub Actions run evidence because live AWS deployment cannot be fully proven from the local workstation alone.

## Alternatives considered

- Automatic deploy on push: rejected because the user requested on-demand deployment and manual triggering is the safer first delivery step.
- Provision EKS/ECR/network/database resources in the same workflow: rejected because it would exceed the current scope and the accepted AWS baseline is a contract, not a full live-environment rollout.
- Use AWS ECS/App Runner instead of Kubernetes: rejected because the accepted repository baseline already targets Kubernetes/EKS for AWS.
- Block the task until GitHub OIDC replaces access-key secrets: rejected because the user has already configured access-key secrets and asked for a working deployment pipeline now.

## Evidence

- `df/artifacts/TASK-010/task.md`
- `df/artifacts/TASK-010/solution-design.md`
- `df/artifacts/STORY-023/solution-design.md`
- `df/artifacts/STORY-023/decision-013-provider-neutral-kubernetes-iac-layering.md`
- `devops/container/platform-core/Containerfile`
- `devops/container/platform-core/README.md`
- `devops/kubernetes/platform-core/overlays/aws/kustomization.yaml`
- `devops/kubernetes/platform-core/overlays/aws/provider-patch.yaml`
- `devops/kubernetes/platform-core/overlays/aws/ingress.yaml`
- `devops/iac/providers/aws/main.tf`

## Follow-up actions

- Route `TASK-010` to `devops` in `READY_FOR_DEV`.
- Require the implementation to keep secrets externalized and document all non-secret workflow inputs/variables clearly.
- Prefer temporary workflow-time substitutions over committing country-specific AWS values into repository manifests.
- Consider a future hardening task to replace access-key secrets with GitHub OIDC federation once the first deployment path is working.

