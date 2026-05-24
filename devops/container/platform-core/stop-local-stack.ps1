[CmdletBinding()]
param(
    [ValidateSet('podman', 'docker')]
    [string]$ContainerRuntime = 'podman',

    [string]$NetworkName = 'df-platform-core-local',
    [string]$PostgresContainerName = 'df-platform-core-postgres',
    [string]$AppContainerName = 'df-platform-core-app'
)

$ErrorActionPreference = 'Stop'

$containerRuntimeExecutable = (Get-Command $ContainerRuntime -ErrorAction Stop).Source

function Invoke-ContainerRuntimeQuietly {
    param(
        [Parameter(Mandatory = $true)]
        [string[]]$Arguments
    )

    $stdoutFile = [System.IO.Path]::GetTempFileName()
    $stderrFile = [System.IO.Path]::GetTempFileName()
    try {
        Start-Process -FilePath $containerRuntimeExecutable `
            -ArgumentList $Arguments `
            -Wait `
            -NoNewWindow `
            -PassThru `
            -RedirectStandardOutput $stdoutFile `
            -RedirectStandardError $stderrFile | Out-Null
    }
    finally {
        Remove-Item $stdoutFile, $stderrFile -ErrorAction SilentlyContinue
    }
}

if (-not (Get-Command $ContainerRuntime -ErrorAction SilentlyContinue)) {
    throw "Container runtime '$ContainerRuntime' is not available on PATH."
}

foreach ($name in @($AppContainerName, $PostgresContainerName)) {
    Invoke-ContainerRuntimeQuietly -Arguments @('rm', '-f', $name)
}

Invoke-ContainerRuntimeQuietly -Arguments @('network', 'rm', $NetworkName)

Write-Output "Stopped local stack for '$ContainerRuntime' (containers: '$AppContainerName', '$PostgresContainerName'; network: '$NetworkName')."

