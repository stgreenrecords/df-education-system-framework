[CmdletBinding()]
param(
    [Parameter(Mandatory = $true)]
    [string]$Mode,

    [string]$ConfigPath,

    [switch]$DryRun,

    [switch]$NoWatcher,

    [switch]$HeadlessWatcher,

    [switch]$StopWatcher,

    [int]$WatcherIterations,

    [int]$WatcherPollIntervalMs
)

$ErrorActionPreference = 'Stop'

$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$orchestrator = Join-Path $scriptDir 'jetbrains-agent-orchestrator.ps1'

$invokeParams = @{ Mode = $Mode }

foreach ($key in @('ConfigPath', 'DryRun', 'NoWatcher', 'HeadlessWatcher', 'StopWatcher', 'WatcherIterations', 'WatcherPollIntervalMs')) {
    if ($PSBoundParameters.ContainsKey($key)) {
        $invokeParams[$key] = $PSBoundParameters[$key]
    }
}

& $orchestrator @invokeParams
exit $LASTEXITCODE


