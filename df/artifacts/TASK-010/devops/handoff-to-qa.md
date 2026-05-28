# Handoff to QA - TASK-010

- From role: `devops`
- To role: `qa`
- From state: `DEV_IN_PROGRESS`
- To state: `READY_FOR_QA`
- Timestamp: 2026-05-26 local

## What changed

Implemented the DevOps-owned AWS deployment automation for `platform-core`.

### Deliverables

- Manual GitHub Actions deployment workflow: `.github/workflows/deploy-aws-on-demand.yml`
- AWS deployment runbook: `docs/deploy-aws.md`
- Root README link to the AWS deployment runbook: `README.md`
- Reusable AWS render helper for local/workflow manifest generation: `devops/kubernetes/platform-core/render-aws-deployment.ps1`
- Kubernetes README update for the new helper: `devops/kubernetes/platform-core/README.md`

## What QA should verify

1. Confirm `.github/workflows/deploy-aws-on-demand.yml` is manual-only (`workflow_dispatch`) and uses the repository secrets `AWS_ACCESS_KEY` and `AWS_SECRET_KEY` rather than hardcoded credentials.
2. Confirm the workflow builds `backend/platform-core`, builds the image from `devops/container/platform-core/Containerfile`, logs in to ECR, and pushes to the operator-supplied ECR target.
3. Confirm the workflow renders the AWS Kubernetes overlay with deployment-time values, updates kubeconfig for the target EKS cluster, previews the apply, applies the manifests, and waits for rollout.
4. Confirm `docs/deploy-aws.md` documents prerequisites, secrets, inputs, manual trigger steps, expected outputs, rollback guidance, and known limitations.
5. If GitHub/AWS access is available, run at least:
   - one `dry_run = true` execution to confirm rendering-only behavior; and
   - ideally one `dry_run = false` execution against a non-production AWS target.
6. Confirm no secrets are printed in logs or committed to repository files.

## DevOps validation evidence

See `df/artifacts/TASK-010/devops/dev-notes.md` for the full validation log.

### Strongest local evidence captured in this session

- PowerShell parser check passed for `devops/kubernetes/platform-core/render-aws-deployment.ps1`.
- Existing `render-manifests.ps1 -Targets aws` check passed.
- Offline render validation passed for the new helper with:
  - a supplied IRSA role ARN; and
  - an omitted IRSA role ARN, proving the placeholder annotation is removed.
- Workflow YAML structure parsed successfully via Python.
- Maven build path used by the workflow passed with `BUILD SUCCESS`.

## Limitations / risks for QA attention

- The local Docker daemon was unavailable, so the local image build could not be completed despite the correct command path being verified.
- Live GitHub Actions and AWS execution were not available from this workstation, so QA should capture workflow-run evidence if access exists.
- If QA cannot access GitHub Actions or AWS, document the limitation clearly rather than assuming success.

## Acceptance expectation for QA

Pass the task only if:

- all five acceptance criteria from `df/artifacts/TASK-010/task.md` are covered by repository evidence; and
- any missing live AWS evidence is either reproduced by QA or explicitly documented as an environment limitation with the strongest available substitute evidence.

