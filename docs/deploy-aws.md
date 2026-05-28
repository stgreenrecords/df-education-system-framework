# AWS deployment guide for `platform-core`

This repository includes an on-demand GitHub Actions workflow at `.github/workflows/deploy-aws-on-demand.yml` for building, publishing, and deploying `backend/platform-core` to a pre-existing AWS environment.

## Scope

The workflow is intentionally limited to the currently supported backend deployment baseline:

- build `backend/platform-core`
- build an OCI image from `devops/container/platform-core/Containerfile`
- push the image to Amazon ECR
- render the AWS Kubernetes overlay from `devops/kubernetes/platform-core/overlays/aws/`
- deploy the rendered manifests to an operator-selected Amazon EKS cluster

Out of scope:

- provisioning AWS infrastructure from scratch
- deploying `frontend/website`, Android, or iOS artifacts
- automatic deployment on every push or pull request

## Required GitHub secrets

Configure these repository secrets before running the workflow:

| Secret | Purpose |
|---|---|
| `AWS_ACCESS_KEY` | AWS access key id for an IAM principal that can push to ECR and access the target EKS cluster |
| `AWS_SECRET_KEY` | AWS secret access key for the same IAM principal |

Do not store AWS credentials in workflow inputs, Markdown files, or committed repository files.

## Required AWS prerequisites

The workflow assumes the following already exist:

- an AWS account with the target ECR registry/repository
- a reachable EKS cluster
- an IAM principal behind `AWS_ACCESS_KEY` / `AWS_SECRET_KEY` with permission to:
  - authenticate to ECR and push the built image
  - call `aws eks update-kubeconfig`
  - run `kubectl apply` and `kubectl rollout status` against the target cluster
- the runtime Kubernetes secret/config inputs expected by the base manifests, especially `platform-core-runtime-secrets`
- DNS and TLS management for the chosen ingress host

If the cluster uses IRSA, pass the role ARN as a workflow input so the rendered `ServiceAccount` annotation is populated at deploy time.

## Workflow inputs

Trigger the workflow manually from the GitHub Actions UI and supply these non-secret values:

| Input | Required | Description |
|---|---|---|
| `aws_region` | Yes | AWS region for ECR and EKS |
| `eks_cluster_name` | Yes | Name of the target EKS cluster |
| `ecr_registry` | Yes | Registry host such as `123456789012.dkr.ecr.eu-central-1.amazonaws.com` |
| `ecr_repository` | Yes | Repository name such as `education-system-framework/platform-core` |
| `kubernetes_namespace` | Yes | Target namespace; defaults to `platform-core` |
| `service_host` | Yes | Public host used in the ingress rule and TLS host list |
| `eks_role_arn` | No | IRSA role ARN to place on the `platform-core` service account |
| `image_tag` | No | Manual image tag override; default is `sha-<short-commit>` |
| `dry_run` | Yes | When `true`, render manifests only and skip AWS auth, ECR push, and EKS apply |

## Manual trigger steps

1. Open the GitHub repository.
2. Navigate to **Actions**.
3. Select **Deploy platform-core to AWS (on-demand)**.
4. Click **Run workflow**.
5. Enter the required inputs.
6. Start with `dry_run = true` to verify the rendered manifests.
7. After reviewing the dry-run output, rerun with `dry_run = false` for the live deployment.

## What the workflow does

1. Checks out the repository.
2. Makes the Maven wrapper executable on the Linux runner.
3. Sets up Java 25 and `kubectl`.
4. Builds the `platform-core` executable jar with Maven.
5. Resolves a deterministic image tag (`sha-<short-commit>` unless overridden).
6. When `dry_run = false`, authenticates to AWS using the repository secrets `AWS_ACCESS_KEY` and `AWS_SECRET_KEY`.
7. Builds the OCI image and, for live runs, pushes it to ECR.
8. Renders the AWS overlay with deploy-time values through `devops/kubernetes/platform-core/render-aws-deployment.ps1`.
9. When `dry_run = false`, updates kubeconfig for the target EKS cluster, performs a server-side preview apply, applies the manifests, and waits for the `platform-core` rollout to finish.
10. Writes a deployment summary to the GitHub Actions step summary.

## Expected outputs

Successful runs should show these checkpoints in the workflow logs:

- resolved image tag and image reference
- successful Maven package step for `platform-core`
- successful image build
- rendered manifest preview for the AWS overlay
- for live runs, successful ECR login, image push, EKS kubeconfig update, manifest apply, and rollout completion
- a GitHub step summary containing image tag, image reference, AWS region, cluster, namespace, service host, and dry-run status

## Local render helper

The repository includes a reusable helper for local rendering and validation of the AWS deployment manifests:

```powershell
.\devops\kubernetes\platform-core\render-aws-deployment.ps1 `
  -ImageReference "123456789012.dkr.ecr.eu-central-1.amazonaws.com/education-system-framework/platform-core:sha-demo123" `
  -ServiceHost "platform-core.example.edu" `
  -Namespace "platform-core" `
  -EksRoleArn "arn:aws:iam::123456789012:role/platform-core-irsa" `
  -OutputPath ".\devops\kubernetes\platform-core\rendered-aws-deployment.yaml" `
  -ValidateClientDryRun
```

This helper requires `kubectl` on `PATH` because it uses `kubectl kustomize` to render the AWS overlay locally.

The `-ValidateClientDryRun` switch performs an offline safety check on the rendered manifest output: it confirms the expected resources are present, confirms the requested namespace/image/host values landed, and verifies that the placeholder IRSA role ARN is either replaced or removed.

## Rollback guidance

If a deployment fails after manifests have been applied:

1. rerun the workflow with the previous known-good `image_tag`; or
2. use standard Kubernetes rollback commands against the target cluster, for example:

```powershell
aws eks update-kubeconfig --region <aws-region> --name <eks-cluster-name>
kubectl rollout undo deployment/platform-core --namespace <kubernetes-namespace>
kubectl rollout status deployment/platform-core --namespace <kubernetes-namespace> --timeout=180s
```

The base deployment manifest keeps `revisionHistoryLimit: 3`, so recent rollback history remains available.

## Known limitations and follow-ups

- Live deployment evidence depends on GitHub Actions and AWS access that are not available from every local workstation.
- The workflow uses access-key-based GitHub secrets because the repository currently exposes `AWS_ACCESS_KEY` and `AWS_SECRET_KEY`. This is acceptable for the current task, but GitHub OIDC federation would be the stronger future hardening path.
- The workflow does not create Kubernetes runtime secrets or database infrastructure; operators must provide those separately.

