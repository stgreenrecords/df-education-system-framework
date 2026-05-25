[CmdletBinding()]
param(
    [ValidateSet('podman', 'docker')]
    [string]$ContainerRuntime = 'podman',

    [string]$ImageName = 'education-system-framework/platform-core:local',
    [string]$NetworkName = 'df-platform-core-local',
    [string]$PostgresContainerName = 'df-platform-core-postgres',
    [string]$AppContainerName = 'df-platform-core-app',
    [int]$HostPort = 18084,
    [string]$DatabaseName = 'education_framework',
    [string]$DatabaseUser = 'education_framework',
    [string]$DatabasePassword = 'education_framework',
    [string]$TranslationDefaultLanguage = 'en',
    [string]$TranslationGlobalFallbackLanguage = 'en',
    [string]$TranslationCacheTtl = 'PT10M'
)

$ErrorActionPreference = 'Stop'

$containerRuntimeExecutable = (Get-Command $ContainerRuntime -ErrorAction Stop).Source

function Test-ContainerRuntimeCommand {
    param(
        [Parameter(Mandatory = $true)]
        [string[]]$Arguments
    )

    $stdoutFile = [System.IO.Path]::GetTempFileName()
    $stderrFile = [System.IO.Path]::GetTempFileName()
    try {
        $process = Start-Process -FilePath $containerRuntimeExecutable `
            -ArgumentList $Arguments `
            -Wait `
            -NoNewWindow `
            -PassThru `
            -RedirectStandardOutput $stdoutFile `
            -RedirectStandardError $stderrFile
        return ($process.ExitCode -eq 0)
    }
    finally {
        Remove-Item $stdoutFile, $stderrFile -ErrorAction SilentlyContinue
    }
}

if (-not (Get-Command $ContainerRuntime -ErrorAction SilentlyContinue)) {
    throw "Container runtime '$ContainerRuntime' is not available on PATH."
}

if (-not (Test-ContainerRuntimeCommand -Arguments @('image', 'inspect', $ImageName))) {
    throw "Image '$ImageName' was not found. Build it first with build-image.ps1."
}

if (-not (Test-ContainerRuntimeCommand -Arguments @('network', 'inspect', $NetworkName))) {
    & $ContainerRuntime network create $NetworkName | Out-Null
}

foreach ($name in @($AppContainerName, $PostgresContainerName)) {
    Test-ContainerRuntimeCommand -Arguments @('rm', '-f', $name) | Out-Null
}

& $ContainerRuntime run -d --name $PostgresContainerName --network $NetworkName `
    -e POSTGRES_DB=$DatabaseName `
    -e POSTGRES_USER=$DatabaseUser `
    -e POSTGRES_PASSWORD=$DatabasePassword `
    postgres:17-alpine | Out-Null

$deadline = (Get-Date).AddMinutes(2)
$postgresReady = $false
while ((Get-Date) -lt $deadline) {
    if (Test-ContainerRuntimeCommand -Arguments @('exec', $PostgresContainerName, 'pg_isready', '-U', $DatabaseUser, '-d', $DatabaseName)) {
        $postgresReady = $true
        break
    }

    Start-Sleep -Seconds 2
}

if (-not $postgresReady) {
    throw "PostgreSQL container '$PostgresContainerName' did not become ready within 2 minutes."
}

$jdbcUrl = "jdbc:postgresql://${PostgresContainerName}:5432/${DatabaseName}"

& $ContainerRuntime run -d --name $AppContainerName --network $NetworkName -p "${HostPort}:8080" `
    -e EDU_DB_URL=$jdbcUrl `
    -e EDU_DB_USERNAME=$DatabaseUser `
    -e EDU_DB_PASSWORD=$DatabasePassword `
    -e EDU_TRANSLATION_DEFAULT_LANGUAGE=$TranslationDefaultLanguage `
    -e EDU_TRANSLATION_GLOBAL_FALLBACK_LANGUAGE=$TranslationGlobalFallbackLanguage `
    -e EDU_TRANSLATION_CACHE_TTL=$TranslationCacheTtl `
    $ImageName | Out-Null

Write-Output "Started PostgreSQL container '$PostgresContainerName' and application container '$AppContainerName'."
Write-Output "Readiness URL: http://127.0.0.1:${HostPort}/platform/status"
Write-Output "Stop with: .\devops\container\platform-core\stop-local-stack.ps1 -ContainerRuntime $ContainerRuntime -NetworkName $NetworkName -PostgresContainerName $PostgresContainerName -AppContainerName $AppContainerName"

