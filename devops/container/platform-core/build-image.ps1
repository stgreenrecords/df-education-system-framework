[CmdletBinding()]
param(
    [ValidateSet('podman', 'docker')]
    [string]$ContainerRuntime = 'podman',

    [string]$ImageName = 'education-system-framework/platform-core:local'
)

$ErrorActionPreference = 'Stop'

$repoRoot = (Resolve-Path (Join-Path $PSScriptRoot '..\..\..')).Path
$mavenWrapper = Join-Path $repoRoot 'mvnw.cmd'
$containerfile = Join-Path $PSScriptRoot 'Containerfile'
$jarPath = Join-Path $repoRoot 'backend\platform-core\target\platform-core-0.1.0-SNAPSHOT-exec.jar'

if (-not (Get-Command $ContainerRuntime -ErrorAction SilentlyContinue)) {
    throw "Container runtime '$ContainerRuntime' is not available on PATH."
}

Push-Location $repoRoot
try {
    & $mavenWrapper -f backend/pom.xml -pl platform-core -am clean package
    if ($LASTEXITCODE -ne 0) {
        throw "Maven package failed with exit code $LASTEXITCODE."
    }

    if (-not (Test-Path $jarPath)) {
        throw "Expected executable jar was not produced at '$jarPath'."
    }

    & $ContainerRuntime build --file $containerfile --tag $ImageName $repoRoot
    if ($LASTEXITCODE -ne 0) {
        throw "Image build failed with exit code $LASTEXITCODE."
    }
}
finally {
    Pop-Location
}

Write-Output "Built OCI image '$ImageName' using runtime '$ContainerRuntime'."

