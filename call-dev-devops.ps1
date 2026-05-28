[CmdletBinding()]
param(
    [string]$ConfigPath,

    [switch]$DryRun,

    [switch]$NoWatcher,

    [switch]$HeadlessWatcher,

    [switch]$StopWatcher,

    [int]$WatcherIterations,

    [int]$WatcherPollIntervalMs
)

$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$invokeParams = @{} + $PSBoundParameters
$invokeParams['Mode'] = 'role-devops'
& (Join-Path $scriptDir 'devops/automation/call-jetbrains-agent.ps1') @invokeParams
exit $LASTEXITCODE


