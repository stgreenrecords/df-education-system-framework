# DevOps Notes - TASK-010

## Summary

Implemented the on-demand AWS deployment path for `backend/platform-core` using a manual GitHub Actions workflow plus an offline-safe AWS manifest render helper.

## Files changed

- `.github/workflows/deploy-aws-on-demand.yml`
- `docs/deploy-aws.md`
- `README.md`
- `devops/kubernetes/platform-core/render-aws-deployment.ps1`
- `devops/kubernetes/platform-core/README.md`

## Implementation details

1. Added `.github/workflows/deploy-aws-on-demand.yml` with `workflow_dispatch` inputs for:
   - `aws_region`
   - `eks_cluster_name`
   - `ecr_registry`
   - `ecr_repository`
   - `kubernetes_namespace`
   - `service_host`
   - `eks_role_arn`
   - `image_tag`
   - `dry_run`
2. Wired AWS authentication to the repository secrets `AWS_ACCESS_KEY` and `AWS_SECRET_KEY` via `aws-actions/configure-aws-credentials@v4`.
3. Reused the accepted build and image paths:
   - Maven build: `./mvnw -f backend/pom.xml -pl platform-core -am clean package -DskipTests`
   - Container build: `devops/container/platform-core/Containerfile`
4. Added `devops/kubernetes/platform-core/render-aws-deployment.ps1` so both local validation and the GitHub Actions workflow render the AWS overlay the same way.
5. Kept deployment-time values externalized by applying them at render time instead of committing environment-specific values into the AWS overlay.
6. Documented prerequisites, trigger steps, expected outputs, and rollback in `docs/deploy-aws.md` and linked the guide from `README.md`.

## Validation log

### 1. PowerShell syntax and baseline Kubernetes render

Command:

```powershell
[System.Management.Automation.Language.Parser]::ParseFile((Resolve-Path ".\devops\kubernetes\platform-core\render-aws-deployment.ps1"), [ref]$null, [ref]$parseErrors) | Out-Null
.\devops\kubernetes\platform-core\render-manifests.ps1 -Targets aws
```

Result:

- PASS — PowerShell parser found no syntax errors in `render-aws-deployment.ps1`.
- PASS — `render-manifests.ps1 -Targets aws` rendered the AWS overlay and confirmed the provider-neutral base check passed.

### 2. Offline render validation with IRSA role ARN

Command:

```powershell
.\devops\kubernetes\platform-core\render-aws-deployment.ps1 `
  -ImageReference "123456789012.dkr.ecr.eu-central-1.amazonaws.com/education-system-framework/platform-core:sha-demo123" `
  -ServiceHost "platform-core.example.edu" `
  -Namespace "platform-core-demo" `
  -EksRoleArn "arn:aws:iam::123456789012:role/platform-core-irsa" `
  -OutputPath ".\devops\kubernetes\platform-core\rendered-aws-deployment.yaml" `
  -ValidateClientDryRun
```

Result:

- PASS — rendered manifest validation passed.
- PASS — verified rendered output contains:
  - `name: platform-core-demo`
  - `image: 123456789012.dkr.ecr.eu-central-1.amazonaws.com/education-system-framework/platform-core:sha-demo123`
  - `platform-core.example.edu`
  - `eks.amazonaws.com/role-arn: arn:aws:iam::123456789012:role/platform-core-irsa`
- PASS — confirmed placeholder `REPLACE_WITH_COUNTRY_EKS_ROLE_ARN` does not remain in the rendered output.

### 3. Offline render validation without IRSA role ARN

Command:

```powershell
.\devops\kubernetes\platform-core\render-aws-deployment.ps1 `
  -ImageReference "123456789012.dkr.ecr.eu-central-1.amazonaws.com/education-system-framework/platform-core:sha-demo124" `
  -ServiceHost "platform-core-no-role.example.edu" `
  -Namespace "platform-core-demo" `
  -OutputPath ".\devops\kubernetes\platform-core\rendered-aws-deployment-no-role.yaml" `
  -ValidateClientDryRun
```

Result:

- PASS — rendered manifest validation passed.
- PASS — verified placeholder IRSA annotation is removed entirely when `EksRoleArn` is omitted.
- PASS — verified the rendered output contains the expected image and service host.

### 4. Workflow YAML structure

Command:

```powershell
python -c "import yaml, pathlib; data = yaml.load(pathlib.Path('.github/workflows/deploy-aws-on-demand.yml').read_text(encoding='utf-8'), Loader=yaml.BaseLoader); print('workflow_name=' + data['name']); print('trigger=' + ','.join(data['on'].keys())); print('job=' + ','.join(data['jobs'].keys()))"
```

Result:

- PASS — parsed the workflow structure successfully.
- PASS — confirmed workflow name, `workflow_dispatch` trigger, and `deploy` job are present.

### 5. Maven build path used by the workflow

Command:

```powershell
.\mvnw.cmd -f backend/pom.xml -pl platform-core -am clean package -DskipTests
```

Result:

- PASS — `BUILD SUCCESS`.
- PASS — confirmed `backend/platform-core/target/platform-core-0.1.0-SNAPSHOT-exec.jar` is produced.

### 6. Container build path used by the workflow

Command:

```powershell
docker build --file devops/container/platform-core/Containerfile --tag education-system-framework/platform-core:task-010-validate .
```

Result:

- BLOCKED/ENV LIMITATION — Docker CLI is installed, but the local Docker daemon is not running.
- Observed error:
  - `failed to connect to the docker API at npipe:////./pipe/dockerDesktopLinuxEngine`
- Interpretation:
  - The container build command itself is correct and matches the workflow path, but this workstation could not complete a live image build because Docker Desktop's Linux engine was unavailable.

## Acceptance-criteria mapping

| Acceptance criterion | Evidence |
|---|---|
| Manual workflow under `.github/workflows/` using `workflow_dispatch` | `.github/workflows/deploy-aws-on-demand.yml` |
| Uses `AWS_ACCESS_KEY` and `AWS_SECRET_KEY` without hardcoding credentials | `configure-aws-credentials@v4` step in `.github/workflows/deploy-aws-on-demand.yml` |
| Builds `backend/platform-core` image from `devops/container/platform-core/Containerfile` and pushes to ECR | workflow build/push steps; validated Maven path; Docker daemon limitation documented |
| Deploys AWS Kubernetes baseline to EKS with deployment-time values | `devops/kubernetes/platform-core/render-aws-deployment.ps1`; workflow render/apply steps; offline render evidence |
| Documentation explains secrets, inputs, manual trigger flow, outputs, and rollback | `docs/deploy-aws.md`; `README.md` link |

## Known limitations

- Live GitHub Actions execution was not available from this workstation, so no real workflow-run URL or ECR/EKS deployment log could be captured in this session.
- The Docker daemon was not running locally, so the container build path could not be completed end-to-end on this workstation.
- QA should capture at least one dry-run or live GitHub Actions execution if repository/AWS access is available.

