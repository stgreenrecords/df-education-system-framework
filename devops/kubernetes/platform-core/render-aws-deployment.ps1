[CmdletBinding()]
param(
    [Parameter(Mandatory = $true)]
    [string]$ImageReference,

    [Parameter(Mandatory = $true)]
    [string]$ServiceHost,

    [string]$Namespace = 'platform-core',

    [string]$EksRoleArn,

    [string]$OutputPath,

    [switch]$ValidateClientDryRun
)

$ErrorActionPreference = 'Stop'

$kubernetesRoot = $PSScriptRoot
$overlayPath = Join-Path $kubernetesRoot 'overlays\aws'
$kubectlExecutable = (Get-Command kubectl -ErrorAction Stop).Source

if (-not (Test-Path $overlayPath)) {
    throw "AWS overlay path was not found at '$overlayPath'."
}

if ([string]::IsNullOrWhiteSpace($OutputPath)) {
    $OutputPath = Join-Path $kubernetesRoot 'rendered-aws-deployment.yaml'
}

$outputDirectory = Split-Path -Parent $OutputPath
if (-not [string]::IsNullOrWhiteSpace($outputDirectory) -and -not (Test-Path $outputDirectory)) {
    New-Item -ItemType Directory -Path $outputDirectory -Force | Out-Null
}

$tempDirectory = Join-Path $kubernetesRoot ('.tmp-aws-render-' + [Guid]::NewGuid().ToString('N'))
New-Item -ItemType Directory -Path $tempDirectory -Force | Out-Null

$kustomizationPath = Join-Path $tempDirectory 'kustomization.yaml'
$relativeOverlayPath = '../overlays/aws'

$escapedNamespace = $Namespace.Replace("'", "''")
$escapedImageReference = $ImageReference.Replace("'", "''")
$escapedServiceHost = $ServiceHost.Replace("'", "''")

$kustomization = @"
apiVersion: kustomize.config.k8s.io/v1beta1
kind: Kustomization
resources:
  - $relativeOverlayPath
namespace: '$escapedNamespace'
patches:
  - target:
      kind: Namespace
      name: platform-core
    patch: |-
      - op: replace
        path: /metadata/name
        value: '$escapedNamespace'
  - target:
      kind: Deployment
      name: platform-core
    patch: |-
      - op: replace
        path: /spec/template/spec/containers/0/image
        value: '$escapedImageReference'
  - target:
      kind: Ingress
      name: platform-core
    patch: |-
      - op: replace
        path: /spec/rules/0/host
        value: '$escapedServiceHost'
      - op: replace
        path: /spec/tls/0/hosts/0
        value: '$escapedServiceHost'
"@

Set-Content -Path $kustomizationPath -Value $kustomization -NoNewline

try {
    $renderedOutput = & $kubectlExecutable kustomize $tempDirectory 2>&1
    if ($LASTEXITCODE -ne 0) {
        throw "kubectl kustomize failed: $renderedOutput"
    }

    $renderedText = [string]::Join([Environment]::NewLine, $renderedOutput)
    $renderedText = $renderedText.Replace('platform-core.country.example.edu', $ServiceHost)

    if ([string]::IsNullOrWhiteSpace($EksRoleArn)) {
        $renderedText = [System.Text.RegularExpressions.Regex]::Replace(
            $renderedText,
            '(?m)^\s*eks\.amazonaws\.com/role-arn:.*(?:\r?\n)?',
            ''
        )
    }
    else {
        $renderedText = $renderedText.Replace('REPLACE_WITH_COUNTRY_EKS_ROLE_ARN', $EksRoleArn)
    }

    Set-Content -Path $OutputPath -Value $renderedText

    if ($ValidateClientDryRun) {
        $requiredMarkers = @(
            'kind: Namespace',
            'kind: ServiceAccount',
            'kind: Deployment',
            'kind: Service',
            'kind: Ingress',
            "name: $Namespace",
            "image: $ImageReference",
            $ServiceHost
        )

        foreach ($marker in $requiredMarkers) {
            if (-not $renderedText.Contains($marker)) {
                throw "Rendered manifest validation failed: expected marker '$marker' was not found."
            }
        }

        if ($renderedText.Contains('REPLACE_WITH_COUNTRY_EKS_ROLE_ARN')) {
            throw 'Rendered manifest validation failed: placeholder IRSA role ARN value is still present.'
        }
    }
}
finally {
    if (Test-Path $tempDirectory) {
        Remove-Item -Path $tempDirectory -Recurse -Force
    }
}

Write-Host "Rendered AWS deployment manifests to '$OutputPath'."
if ($ValidateClientDryRun) {
    Write-Host 'Offline rendered manifest validation: PASS'
}

