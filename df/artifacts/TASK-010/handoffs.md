# Handoff - TASK-010

## SA -> devops

- Timestamp: 2026-05-26 local
- Task: TASK-010
- From state: ARCHITECTURE_IN_PROGRESS
- To state: READY_FOR_DEV
- Lane: devops
- Summary: SA routed the explicit user request for an on-demand AWS deployment pipeline to `devops`. The recommended approach is one manual GitHub Actions `workflow_dispatch` pipeline that uses the existing GitHub secrets `AWS_ACCESS_KEY` and `AWS_SECRET_KEY`, builds and pushes the `backend/platform-core` OCI image to ECR, renders the existing AWS Kubernetes overlay with deployment-time values, and deploys it to an operator-specified EKS target.

## Evidence

- `df/artifacts/TASK-010/task.md`
- `df/artifacts/TASK-010/solution-design.md`
- `df/artifacts/TASK-010/decision-024-on-demand-aws-github-actions-deployment.md`
- `devops/container/platform-core/Containerfile`
- `devops/container/platform-core/README.md`
- `devops/kubernetes/platform-core/overlays/aws/kustomization.yaml`
- `devops/kubernetes/platform-core/overlays/aws/provider-patch.yaml`
- `devops/kubernetes/platform-core/overlays/aws/ingress.yaml`
- `devops/iac/providers/aws/main.tf`
- `.github/copilot-instructions.md`
- `df/runtime/board.md`
- `df/runtime/devops-board.md`

## Tests/checks

| Check | Command/source | Result | Notes |
|---|---|---|---|
| Existing GitHub workflow inventory | `.github/` directory listing + workflow file search | PASS | Confirmed the repository currently has no `.github/workflows/*.yml` or `.yaml` deployment workflow to extend |
| OCI baseline review | `devops/container/platform-core/Containerfile`; `devops/container/platform-core/README.md` | PASS | Confirmed `backend/platform-core` already has a reusable OCI image path for CI/CD |
| AWS deployment baseline review | `devops/kubernetes/platform-core/overlays/aws/*`; `devops/iac/providers/aws/*` | PASS | Confirmed the accepted AWS target is Kubernetes/EKS-oriented and already uses AWS-specific ingress/service-account patterns |
| Lane routing review | `df/roles/devops.md`; `df/runtime/devops-board.md` | PASS | The requested work is pure DevOps scope: GitHub Actions, AWS credentials wiring, image publication, deployment manifests, and operational docs |

## Constraints

- Keep AWS credentials, kubeconfig data, and country-specific values out of source control and Markdown evidence.
- Limit scope to the currently deployable `backend/platform-core` application and the existing AWS Kubernetes baseline.
- Do not provision new AWS infrastructure as part of this task; assume ECR, EKS, IAM access, and supporting environment resources already exist.
- Do not edit backend or frontend source files unless a real cross-lane blocker forces rerouting.

## Recommended approach

1. Add one manual GitHub Actions workflow under `.github/workflows/`.
2. Use `AWS_ACCESS_KEY` and `AWS_SECRET_KEY` for AWS authentication inside the workflow.
3. Build the existing `backend/platform-core` executable jar and OCI image via the current `Containerfile` path.
4. Push the image to ECR with a deterministic tag.
5. Render the AWS Kubernetes overlay using workflow-time patches/substitutions for image reference and environment-specific values.
6. Authenticate to the target EKS cluster, apply the manifests, and wait for rollout/readiness where safe.
7. Document prerequisites, manual trigger steps, expected outputs, and rollback guidance.

## Known risks

- The access-key secret model is workable but weaker than GitHub OIDC federation.
- Live deployment proof will depend on GitHub Actions and AWS environment access that is not available from the local workstation.
- The AWS overlay contains placeholder values today, so careless parameterization could accidentally hardcode environment details if DevOps is not deliberate.

## Next role instructions

- `devops` should create `df/artifacts/TASK-010/devops/dev-notes.md` before editing infrastructure/workflow files.
- `devops` should implement the manual workflow plus the smallest supporting AWS overlay/doc changes needed to keep deployment values externalized.
- `devops` should run strong local validation for image build and manifest rendering, then capture workflow-run evidence if a GitHub/AWS execution path is available.
- If the current AWS overlay cannot be deployed safely without broader architecture changes, return the task to SA with exact blocker evidence instead of widening scope silently.

## Blockers

- None currently. Live AWS deployment validation may still depend on GitHub repository access and AWS permissions when DevOps executes the workflow.

## devops -> qa

- Timestamp: 2026-05-26 local
- Task: TASK-010
- From state: DEV_IN_PROGRESS
- To state: READY_FOR_QA
- Lane: devops
- Summary: DevOps implemented the manual GitHub Actions AWS deployment pipeline for `platform-core`, added the AWS deployment runbook, introduced a reusable `render-aws-deployment.ps1` helper for deployment-time manifest parameterization, and documented local validation plus environment limits for QA follow-up.

## Evidence

- `.github/workflows/deploy-aws-on-demand.yml`
- `docs/deploy-aws.md`
- `README.md`
- `devops/kubernetes/platform-core/render-aws-deployment.ps1`
- `devops/kubernetes/platform-core/README.md`
- `df/artifacts/TASK-010/devops/dev-notes.md`
- `df/artifacts/TASK-010/devops/handoff-to-qa.md`
- `df/artifacts/TASK-010/task.md`
- `df/runtime/board.md`
- `df/runtime/devops-board.md`
- `df/runtime/activity-log.md`

## Tests/checks

| Check | Command/source | Result | Notes |
|---|---|---|---|
| PowerShell syntax check | PowerShell parser over `devops/kubernetes/platform-core/render-aws-deployment.ps1` | PASS | No syntax errors detected |
| Baseline AWS overlay render | `./devops/kubernetes/platform-core/render-manifests.ps1 -Targets aws` | PASS | Provider-neutral base check passed |
| AWS render helper with IRSA role | `render-aws-deployment.ps1 ... -EksRoleArn ... -ValidateClientDryRun` | PASS | Verified namespace/image/host injection and IRSA placeholder replacement |
| AWS render helper without IRSA role | `render-aws-deployment.ps1 ... -ValidateClientDryRun` | PASS | Verified IRSA annotation removal when no role ARN is supplied |
| Workflow YAML structure | Python `yaml.BaseLoader` parse of `.github/workflows/deploy-aws-on-demand.yml` | PASS | Confirmed workflow name, `workflow_dispatch`, and `deploy` job |
| Workflow build path | `./mvnw.cmd -f backend/pom.xml -pl platform-core -am clean package -DskipTests` | PASS | `BUILD SUCCESS`; exec jar produced |
| Workflow container build path | `docker build --file devops/container/platform-core/Containerfile --tag education-system-framework/platform-core:task-010-validate .` | PARTIAL | Docker daemon unavailable on this workstation, so command path verified but live build blocked by environment |

## QA focus

- Confirm all five acceptance criteria in `df/artifacts/TASK-010/task.md` are covered.
- If GitHub Actions and AWS access exist, run at least one `dry_run=true` workflow execution and ideally one non-production live deploy.
- Confirm the workflow uses the repository secrets `AWS_ACCESS_KEY` and `AWS_SECRET_KEY` without exposing them in logs.
- Confirm the local Docker daemon limitation is treated as environment evidence, not a defect in the workflow path itself.

