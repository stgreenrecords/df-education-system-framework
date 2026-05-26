# Solution Design - TASK-010

## Summary

Create a DevOps-owned, on-demand GitHub Actions pipeline that builds the accepted `backend/platform-core` OCI image, publishes it to AWS ECR, and deploys the AWS Kubernetes overlay to a target EKS cluster using GitHub-managed AWS secrets and operator-supplied non-secret environment values.

## Context

The repository already has two key deployment foundations:

- `STORY-022` introduced the OCI image baseline for `backend/platform-core` in `devops/container/platform-core/`.
- `STORY-023` introduced a provider-neutral Kubernetes base plus provider-specific AWS overlay and IaC baseline in `devops/kubernetes/platform-core/overlays/aws/` and `devops/iac/providers/aws/`.

The current repository does not yet contain any GitHub Actions workflow files, so there is no repo-owned CI/CD path for AWS deployment. The user has now created GitHub secrets named `AWS_ACCESS_KEY` and `AWS_SECRET_KEY` and requested an on-demand pipeline to deploy the application to AWS.

This task should stay within DevOps scope, reuse the existing AWS/EKS/ECR-oriented deployment baseline, avoid committing real environment values, and provide a manual operator-controlled trigger rather than auto-deploy-on-push behavior.

## Requirements and acceptance criteria

- Add a GitHub Actions workflow that can be manually triggered through `workflow_dispatch`
- Use GitHub secrets `AWS_ACCESS_KEY` and `AWS_SECRET_KEY` for AWS authentication without exposing credentials in source control or logs
- Reuse the existing `devops/container/platform-core/Containerfile` image-build path for `backend/platform-core`
- Publish the built image to AWS ECR with deterministic tagging
- Reuse the existing AWS Kubernetes overlay for `platform-core` and deploy it to a target EKS cluster using deployment-time substitutions instead of committed country-specific values
- Document required secrets, non-secret inputs/variables, how to trigger the workflow, how to inspect success/failure, and how to roll back to a previous image tag

## Proposed solution

Implement one manual GitHub Actions workflow, for example `.github/workflows/deploy-aws-on-demand.yml`, with the following structure.

1. **Manual trigger and inputs**
   - Use `workflow_dispatch` so deployment happens only when a human/operator explicitly triggers it.
   - Accept non-secret deployment inputs such as:
     - `aws_region`
     - `eks_cluster_name`
     - `ecr_registry`
     - `ecr_repository`
     - `kubernetes_namespace` (default `platform-core`)
     - `service_host`
     - optional `image_tag` override
     - optional `dry_run` flag if DevOps can add a safe render-only mode without overcomplicating the first implementation
   - Stable non-secret defaults may be sourced from GitHub repository variables when that reduces manual input, but the workflow must not depend on committed country-specific values.

2. **AWS authentication**
   - Use the repository secrets exactly as created by the user: `AWS_ACCESS_KEY` and `AWS_SECRET_KEY`.
   - Map those secrets into the standard AWS credential environment expected by the GitHub AWS credential action or AWS CLI.
   - Keep credentials masked and out of emitted logs.
   - Note in documentation that GitHub OIDC would be the preferred future hardening path, but do not block this task on changing the user’s chosen secret model.

3. **Build and publish the application image**
   - Check out the repository.
   - Set up Java/Maven as needed to produce `backend/platform-core/target/platform-core-0.1.0-SNAPSHOT-exec.jar`.
   - Build the OCI image from `devops/container/platform-core/Containerfile`.
   - Tag the image deterministically, with the default based on commit SHA or workflow run metadata unless a manual override is supplied.
   - Authenticate to ECR and push the image to the operator-specified ECR registry/repository.

4. **Render and deploy the AWS Kubernetes overlay**
   - Reuse `devops/kubernetes/platform-core/overlays/aws/` as the deployment base.
   - Avoid committing real environment values into the overlay. Instead, generate temporary workflow-time patches or substitutions for items such as image reference, namespace, service host, and any other operator-owned deployment settings.
   - Authenticate to the target EKS cluster using AWS CLI + `aws eks update-kubeconfig` or an equivalent safe runner-side method.
   - Apply the rendered manifests with `kubectl` and wait for rollout/readiness where feasible.
   - Prefer small, reversible manifest-generation changes over introducing a second deployment format.

5. **Observability and safety**
   - Emit clear workflow log sections for image tag, target namespace, and target cluster without exposing secrets.
   - Add concurrency or environment-serialization safeguards if practical so two manual deploys do not race against the same target.
   - Include a rollback note in documentation describing how to rerun the workflow with a previous image tag or revert the workflow/manifests.

6. **Documentation**
   - Add deployment documentation under `docs/`, plus a root `README.md` link/update if needed.
   - Document:
     - required GitHub secrets (`AWS_ACCESS_KEY`, `AWS_SECRET_KEY`)
     - recommended non-secret variables or required dispatch inputs
     - the manual trigger flow in GitHub Actions
     - expected workflow outputs/log checkpoints
     - rollback/redeploy guidance
     - known limitations, including that this task assumes pre-existing AWS infrastructure and permissions

## Alternatives considered

- **Automatic deploy on push to `main`**: rejected because the user asked for on-demand deployment and the first safe step is an explicit manual trigger.
- **Provision AWS infrastructure in the same workflow**: rejected because the current AWS IaC baseline is a portable contract, not a live country-environment rollout, and provisioning new EKS/RDS/network/IAM resources would expand scope significantly.
- **Deploy to AWS ECS/App Runner instead of Kubernetes**: rejected because the accepted repository baseline already targets Kubernetes/EKS for AWS.
- **Require GitHub OIDC instead of access-key secrets immediately**: rejected for this task because the user has already configured access-key secrets and requested a working on-demand pipeline now. OIDC can be a follow-up hardening task.

## Files/components likely affected

- `.github/workflows/deploy-aws-on-demand.yml`
- `README.md`
- `docs/` deployment guidance (likely a new AWS deployment doc)
- `devops/kubernetes/platform-core/overlays/aws/**`
- possibly helper/validation scripts under `devops/` if DevOps adds safe render/deploy support
- `df/artifacts/TASK-010/task.md`
- `df/artifacts/TASK-010/solution-design.md`
- `df/artifacts/TASK-010/handoffs.md`
- `df/artifacts/TASK-010/decision-024-on-demand-aws-github-actions-deployment.md`
- `df/runtime/board.md`
- `df/runtime/devops-board.md`
- `df/runtime/activity-log.md`
- `df/runtime/decisions.md`

## Data model changes

- None. This task is deployment automation only.

## API/contract changes

- No business API changes.
- New operational contract: the repository will expose a manual GitHub Actions workflow with documented AWS deployment inputs and outputs.
- The deployment contract continues to use the existing `GET /platform/status` readiness endpoint and the accepted AWS Kubernetes overlay structure.

## UI/UX impact

- None.

## Security and privacy considerations

- Do not commit AWS credentials, kubeconfig files, or country-specific environment values.
- Keep workflow logs free of secrets and sensitive tokens.
- Limit the task to a pre-provisioned AWS environment and least-privilege IAM permissions for ECR push plus EKS deployment access.
- Prefer masked secrets, minimal workflow permissions, and runner-side temporary manifest generation over committed environment-specific files.
- Document access-key rotation/OIDC as future hardening, but do not invent an unvalidated credential model in this task.

## Performance/scalability considerations

- Image builds may be slow on first run because Maven dependencies and OCI layers are prepared on the runner.
- Deterministic image tags and reusable build/cache steps should reduce redeploy ambiguity.
- The workflow should remain reusable across multiple AWS environments by changing inputs/variables rather than copying workflow files.

## Test strategy

DevOps should run the strongest safe verification path available and document any GitHub/AWS execution limits explicitly:

- verify the workflow YAML structure and action configuration by direct inspection and, if available, linting/validation tooling
- build the backend jar locally or on CI using the same Maven path the workflow will use
- build the OCI image locally using the existing `Containerfile` path
- render the AWS Kubernetes overlay with temporary deployment-time substitutions and inspect the output for image, namespace, ingress host, and provider annotations
- if `kubectl` is available locally, run client-side validation such as `kubectl apply --dry-run=client` on the rendered manifests
- when the workflow is implemented, capture at least one manual GitHub Actions run (preferably against a non-production AWS target) as DevOps/QA evidence
- if local GitHub Actions or AWS execution cannot be reproduced from the workstation, document the limitation instead of fabricating success

## Deployment/migration plan

- Add the manual GitHub Actions workflow and any minimal supporting render/deploy helpers.
- Keep all live environment values externalized through GitHub secrets, repository variables, and/or workflow-dispatch inputs.
- Reuse the accepted OCI image and AWS overlay baseline rather than introducing a different deployment substrate.
- Document the manual deployment runbook and rollback path.

## Rollback plan

- Revert the workflow and related documentation if the approach is rejected.
- If a deployment run causes issues, rerun the workflow with the last known-good image tag or apply the previous rendered manifest set.
- Remove any temporary AWS deployment helper files added for this task if the solution is replaced.

## Risks and mitigations

- **Risk:** Access-key-based GitHub secrets are less secure than OIDC.
  - **Mitigation:** keep scope narrow, require least-privilege IAM, document rotation, and treat OIDC as a follow-up hardening task.
- **Risk:** Existing AWS overlay placeholders may not be deployment-ready without parameterization.
  - **Mitigation:** have DevOps generate runner-side patches/substitutions instead of committing country values.
- **Risk:** Live AWS validation depends on external infrastructure and GitHub Actions runtime.
  - **Mitigation:** require strong local build/render evidence plus documented workflow-run evidence when implementation starts.
- **Risk:** The repository may need small shared-file changes under `.github/` and `README.md` in addition to DevOps-owned assets.
  - **Mitigation:** keep the scope within DevOps-owned CI/CD and deployment docs only; if backend/frontend source changes become necessary, return to SA.

## Open questions

- None blocking. If DevOps discovers that the current AWS overlay cannot be parameterized safely without redesigning the repository’s deployment baseline, return the task to SA with concrete evidence.

## Implementation lane

- Lane: `devops`
- Subdashboard: `df/runtime/devops-board.md`
- Artifact folder for implementation notes: `df/artifacts/TASK-010/devops/`

## SA decision

Approved for development: Yes

