[CmdletBinding()]
param(
    [string[]]$Targets = @('base', 'aws', 'azure', 'gcp', 'self-hosted')
)

$ErrorActionPreference = 'Stop'

$repoRoot = (Resolve-Path (Join-Path $PSScriptRoot '..\..\..')).Path
$kubernetesRoot = Join-Path $repoRoot 'devops\kubernetes\platform-core'
$kubectlExecutable = (Get-Command kubectl -ErrorAction Stop).Source

$targetPaths = [ordered]@{
    'base' = Join-Path $kubernetesRoot 'base'
    'aws' = Join-Path $kubernetesRoot 'overlays\aws'
    'azure' = Join-Path $kubernetesRoot 'overlays\azure'
    'gcp' = Join-Path $kubernetesRoot 'overlays\gcp'
    'self-hosted' = Join-Path $kubernetesRoot 'overlays\self-hosted'
}

$forbiddenBaseMarkers = @(
    'aws',
    'azure',
    'gcp',
    'google',
    'alb',
    'appgw',
    'gce',
    'nginx',
    'metallb',
    'eks.amazonaws.com',
    'azure.workload.identity',
    'iam.gke.io'
)

$baseContent = Get-ChildItem -Path (Join-Path $kubernetesRoot 'base') -File -Filter '*.yaml' |
    ForEach-Object { Get-Content -Path $_.FullName -Raw }

$lowerBaseContent = ($baseContent -join "`n").ToLowerInvariant()
$baseViolations = $forbiddenBaseMarkers | Where-Object { $lowerBaseContent.Contains($_) }
if ($baseViolations) {
    throw "Provider-specific markers found in provider-neutral base: $($baseViolations -join ', ')"
}

foreach ($target in $Targets) {
    if (-not $targetPaths.Contains($target)) {
        throw "Unknown target '$target'. Valid targets: $($targetPaths.Keys -join ', ')"
    }

    $targetPath = $targetPaths[$target]
    $renderedOutput = & $kubectlExecutable kustomize $targetPath 2>&1
    if ($LASTEXITCODE -ne 0) {
        throw "kubectl kustomize failed for '$target': $renderedOutput"
    }

    $documentCount = (($renderedOutput | Select-String '^---$').Matches.Count) + 1
    Write-Host ("Rendered {0}: {1} document(s) from {2}" -f $target, $documentCount, $targetPath)
}

Write-Host 'Provider-neutral base check: PASS'

