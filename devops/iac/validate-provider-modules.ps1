[CmdletBinding()]
param(
    [string[]]$Providers = @('aws', 'azure', 'gcp', 'self-hosted')
)

$ErrorActionPreference = 'Stop'

$repoRoot = (Resolve-Path (Join-Path $PSScriptRoot '..\..')).Path
$iacRoot = Join-Path $repoRoot 'devops\iac'

$toolCommand = Get-Command tofu -ErrorAction SilentlyContinue
if (-not $toolCommand) {
    $toolCommand = Get-Command terraform -ErrorAction Stop
}
$iacTool = $toolCommand.Source

& $iacTool fmt -check -recursive $iacRoot
if ($LASTEXITCODE -ne 0) {
    throw 'Formatting check failed for devops/iac.'
}

foreach ($provider in $Providers) {
    $providerPath = Join-Path $iacRoot (Join-Path 'providers' $provider)
    if (-not (Test-Path $providerPath)) {
        throw "Unknown provider module '$provider'."
    }

    try {
        Push-Location $providerPath
        try {
            & $iacTool init -backend=false -input=false -no-color | Out-Null
            if ($LASTEXITCODE -ne 0) {
                throw "init failed for $provider"
            }

            & $iacTool validate -no-color
            if ($LASTEXITCODE -ne 0) {
                throw "validate failed for $provider"
            }

            Write-Host ("Validated provider module: {0}" -f $provider)
        }
        finally {
            Pop-Location
        }
    }
    finally {
        Remove-Item -Path (Join-Path $providerPath '.terraform') -Recurse -Force -ErrorAction SilentlyContinue
        Remove-Item -Path (Join-Path $providerPath '.terraform.lock.hcl') -Force -ErrorAction SilentlyContinue
    }
}

Write-Host ("Validation tool used: {0}" -f $iacTool)

