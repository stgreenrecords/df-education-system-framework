[CmdletBinding()]
param(
    [string]$Mode = 'start-factory',

    [string]$ConfigPath,

    [switch]$DryRun,

    [switch]$NoWatcher,

    [switch]$HeadlessWatcher,

    [switch]$StopWatcher,

    [int]$WatcherIterations = 0,

    [int]$WatcherPollIntervalMs
)

$ErrorActionPreference = 'Stop'

$allowedModes = @(
    'start-factory',
    'role-designer',
    'role-backend-dev',
    'role-devops',
    'role-frontend-dev',
    'role-data-engineer',
    'role-qa',
    'role-po',
    'role-sa',
    'watch-only'
)

if (-not $StopWatcher -and $Mode -notin $allowedModes) {
    throw "Unsupported mode '$Mode'. Valid modes: $($allowedModes -join ', ')"
}

$scriptPath = $MyInvocation.MyCommand.Path
$automationRoot = Split-Path -Parent $scriptPath
$repoRoot = (Resolve-Path (Join-Path $automationRoot '..\..')).Path
$stateRoot = Join-Path $repoRoot '.dark-factory\automation'
$logPath = Join-Path $stateRoot 'jetbrains-agent-orchestrator.log'
$watcherPidPath = Join-Path $stateRoot 'jetbrains-agent-watcher.pid'

if (-not (Test-Path $stateRoot)) {
    New-Item -ItemType Directory -Path $stateRoot -Force | Out-Null
}

if ([string]::IsNullOrWhiteSpace($ConfigPath)) {
    $ConfigPath = Join-Path $automationRoot 'jetbrains-agent-config.json'
}

$config = Get-Content -Path $ConfigPath -Raw | ConvertFrom-Json

if ($PSVersionTable.PSEdition -eq 'Core') {
    Add-Type -AssemblyName System.Windows.Forms
}
else {
    Add-Type -AssemblyName System.Windows.Forms | Out-Null
}
Add-Type -AssemblyName UIAutomationClient | Out-Null
Add-Type -AssemblyName UIAutomationTypes | Out-Null

$nativeMethods = @"
using System;
using System.Runtime.InteropServices;

public static class DarkFactoryNativeMethods
{
    [DllImport("user32.dll")]
    public static extern bool SetForegroundWindow(IntPtr hWnd);

    [DllImport("user32.dll")]
    public static extern bool ShowWindowAsync(IntPtr hWnd, int nCmdShow);

    [DllImport("user32.dll")]
    public static extern bool SetCursorPos(int X, int Y);

    [DllImport("user32.dll")]
    public static extern void mouse_event(uint dwFlags, uint dx, uint dy, uint dwData, UIntPtr dwExtraInfo);
}
"@

Add-Type -TypeDefinition $nativeMethods | Out-Null

function Write-Log {
    param(
        [string]$Message,
        [string]$Level = 'INFO'
    )

    $timestamp = Get-Date -Format 'yyyy-MM-dd HH:mm:ss'
    $line = "[$timestamp] [$Level] $Message"
    Add-Content -Path $logPath -Value $line

    if (-not $HeadlessWatcher) {
        Write-Host $line
    }
}

function Get-ModeConfig {
    param([string]$RequestedMode)

    $property = $config.modes.PSObject.Properties | Where-Object { $_.Name -eq $RequestedMode } | Select-Object -First 1
    if (-not $property) {
        throw "Mode '$RequestedMode' is not defined in $ConfigPath."
    }

    return $property.Value
}

function Get-CurrentHostExecutable {
    $hostProcess = Get-Process -Id $PID -ErrorAction Stop
    if ($hostProcess.Path) {
        return $hostProcess.Path
    }

    if (Get-Command pwsh -ErrorAction SilentlyContinue) {
        return (Get-Command pwsh).Source
    }

    return (Get-Command powershell.exe -ErrorAction Stop).Source
}

function Get-JetBrainsProcesses {
    $knownNames = @($config.jetbrainsProcessNames | ForEach-Object { $_.ToLowerInvariant() })
    $titleKeywords = @($config.jetbrainsWindowTitleKeywords)

    return Get-Process |
        Where-Object {
            $matchesKeyword = $false
            foreach ($keyword in $titleKeywords) {
                if (-not [string]::IsNullOrWhiteSpace($keyword) -and $_.MainWindowTitle -like "*$keyword*") {
                    $matchesKeyword = $true
                    break
                }
            }

            $_.MainWindowHandle -ne 0 -and (
                ($knownNames -contains $_.ProcessName.ToLowerInvariant()) -or
                $matchesKeyword
            )
        } |
        Sort-Object StartTime -Descending
}

function Get-PrimaryJetBrainsProcess {
    $process = Get-JetBrainsProcesses | Select-Object -First 1
    if (-not $process) {
        throw 'Could not find a visible IntelliJ / JetBrains window. Open the IDE and make sure the main window is visible before running the launcher.'
    }

    return $process
}

function Focus-ProcessWindow {
    param([System.Diagnostics.Process]$Process)

    if ($DryRun) {
        Write-Log "DRY RUN: would focus process '$($Process.ProcessName)' with title '$($Process.MainWindowTitle)'."
        return
    }

    [DarkFactoryNativeMethods]::ShowWindowAsync($Process.MainWindowHandle, 5) | Out-Null
    Start-Sleep -Milliseconds 150
    [DarkFactoryNativeMethods]::SetForegroundWindow($Process.MainWindowHandle) | Out-Null
    Start-Sleep -Milliseconds 150
}

function Get-WindowAutomationElement {
    param([System.Diagnostics.Process]$Process)

    return [System.Windows.Automation.AutomationElement]::FromHandle($Process.MainWindowHandle)
}

function Get-ElementsByControlType {
    param(
        [System.Windows.Automation.AutomationElement]$Root,
        [System.Windows.Automation.ControlType]$ControlType,
        [System.Windows.Automation.TreeScope]$Scope = [System.Windows.Automation.TreeScope]::Descendants
    )

    $condition = New-Object System.Windows.Automation.PropertyCondition(
        [System.Windows.Automation.AutomationElement]::ControlTypeProperty,
        $ControlType
    )

    return $Root.FindAll($Scope, $condition)
}

function Get-EnabledVisibleElementByNames {
    param(
        [System.Windows.Automation.AutomationElement]$Root,
        [string[]]$Names,
        [System.Windows.Automation.ControlType]$ControlType
    )

    $elements = Get-ElementsByControlType -Root $Root -ControlType $ControlType
    for ($index = 0; $index -lt $elements.Count; $index++) {
        $element = $elements.Item($index)
        if (-not $element.Current.IsEnabled -or $element.Current.IsOffscreen) {
            continue
        }

        foreach ($name in $Names) {
            if ($element.Current.Name -eq $name) {
                return $element
            }
        }
    }

    return $null
}

function Invoke-AutomationElement {
    param([System.Windows.Automation.AutomationElement]$Element)

    if ($DryRun) {
        Write-Log "DRY RUN: would invoke UI element '$($Element.Current.Name)'."
        return $true
    }

    $pattern = $null
    if ($Element.TryGetCurrentPattern([System.Windows.Automation.InvokePattern]::Pattern, [ref]$pattern)) {
        $pattern.Invoke()
        return $true
    }

    if ($Element.TryGetCurrentPattern([System.Windows.Automation.SelectionItemPattern]::Pattern, [ref]$pattern)) {
        $pattern.Select()
        return $true
    }

    $rect = $Element.Current.BoundingRectangle
    if ($rect.Width -gt 0 -and $rect.Height -gt 0) {
        $x = [int]($rect.Left + ($rect.Width / 2))
        $y = [int]($rect.Top + ($rect.Height / 2))
        [DarkFactoryNativeMethods]::SetCursorPos($x, $y) | Out-Null
        Start-Sleep -Milliseconds 100
        [DarkFactoryNativeMethods]::mouse_event(0x0002, 0, 0, 0, [UIntPtr]::Zero)
        [DarkFactoryNativeMethods]::mouse_event(0x0004, 0, 0, 0, [UIntPtr]::Zero)
        return $true
    }

    return $false
}

function Ensure-ChatSurfaceVisible {
    param([System.Windows.Automation.AutomationElement]$Window)

    $chatElement = Get-EnabledVisibleElementByNames -Root $Window -Names @($config.chatSurfaceButtonNames) -ControlType ([System.Windows.Automation.ControlType]::Button)
    if ($chatElement) {
        Write-Log "Ensuring JetBrains chat surface is visible through '$($chatElement.Current.Name)'."
        Invoke-AutomationElement -Element $chatElement | Out-Null
        Start-Sleep -Milliseconds 250
    }
}

function Try-OpenNewConversation {
    param([System.Windows.Automation.AutomationElement]$Window)

    $newChat = Get-EnabledVisibleElementByNames -Root $Window -Names @($config.newConversationButtonNames) -ControlType ([System.Windows.Automation.ControlType]::Button)
    if ($newChat) {
        Write-Log "Opening a new JetBrains AI conversation through '$($newChat.Current.Name)'."
        Invoke-AutomationElement -Element $newChat | Out-Null
        Start-Sleep -Milliseconds 300
        return $true
    }

    Write-Log 'Did not find an explicit New Chat / New Conversation button. The prompt will be sent to the currently available JetBrains AI input if one can be focused.' 'WARN'
    return $false
}

function Get-PromptInputElement {
    param([System.Windows.Automation.AutomationElement]$Window)

    $edits = Get-ElementsByControlType -Root $Window -ControlType ([System.Windows.Automation.ControlType]::Edit)
    $fallback = $null

    for ($index = 0; $index -lt $edits.Count; $index++) {
        $element = $edits.Item($index)
        if (-not $element.Current.IsEnabled -or $element.Current.IsOffscreen) {
            continue
        }

        if (-not $fallback) {
            $fallback = $element
        }

        foreach ($hint in @($config.promptInputNameHints)) {
            if (-not [string]::IsNullOrWhiteSpace($hint) -and $element.Current.Name -like "*$hint*") {
                return $element
            }
        }
    }

    return $fallback
}

function Set-ClipboardText {
    param([string]$Value)

    if ($DryRun) {
        Write-Log "DRY RUN: would copy text to the clipboard."
        return
    }

    Set-Clipboard -Value $Value
}

function Get-ClipboardTextValue {
    try {
        return Get-Clipboard -Raw -TextFormatType Text -ErrorAction Stop
    }
    catch {
        return $null
    }
}

function Restore-ClipboardText {
    param([AllowNull()][string]$Value)

    try {
        if ($null -eq $Value) {
            Set-Clipboard -Value ''
            return
        }

        Set-Clipboard -Value $Value
    }
    catch {
        Write-Log "Could not restore clipboard contents after keyboard probe: $($_.Exception.Message)" 'WARN'
    }
}

function Get-NormalizedPromptText {
    param([AllowNull()][string]$Value)

    if ($null -eq $Value) {
        return ''
    }

    return ((($Value -replace "`r", '') -replace "`n", ' ').Trim())
}

function Get-ActiveControlTextViaClipboardProbe {
    $existingClipboard = Get-ClipboardTextValue
    $sentinel = "__DF_CLIPBOARD_PROBE__::$([Guid]::NewGuid().ToString())"

    try {
        Set-Clipboard -Value $sentinel
        Send-KeysWithDelay -Keys '^a' -DelayMs 90 -Description 'selecting text in the currently focused IDE control for verification'
        Send-KeysWithDelay -Keys '^c' -DelayMs 130 -Description 'copying text from the currently focused IDE control for verification'
        $copied = Get-ClipboardTextValue
        if ($copied -eq $sentinel) {
            return ''
        }

        return [string]$copied
    }
    finally {
        Restore-ClipboardText -Value $existingClipboard
    }
}

function Test-PluginDirectoryInstalled {
    param([string[]]$PluginDirectoryNames)

    if (-not $PluginDirectoryNames -or $PluginDirectoryNames.Count -eq 0) {
        return $false
    }

    $jetbrainsRoots = @(
        (Join-Path $env:APPDATA 'JetBrains'),
        (Join-Path $env:LOCALAPPDATA 'JetBrains')
    ) | Where-Object { -not [string]::IsNullOrWhiteSpace($_) -and (Test-Path $_) }

    foreach ($root in $jetbrainsRoots) {
        $productRoots = @(Get-ChildItem -Path $root -Directory -ErrorAction SilentlyContinue)
        foreach ($productRoot in $productRoots) {
            foreach ($pluginDirectoryName in $PluginDirectoryNames) {
                if ([string]::IsNullOrWhiteSpace($pluginDirectoryName)) {
                    continue
                }

                $pluginPath = Join-Path $productRoot.FullName (Join-Path 'plugins' $pluginDirectoryName)
                if (Test-Path $pluginPath) {
                    return $true
                }
            }
        }
    }

    return $false
}

function Get-KeyboardFallbackStrategies {
    $keyboardConfig = $config.keyboardFallback
    $strategies = @()

    if ($keyboardConfig.providerStrategies) {
        foreach ($strategy in @($keyboardConfig.providerStrategies)) {
            $strategies += [pscustomobject]@{
                name = if ($strategy.name) { [string]$strategy.name } else { 'unnamed-strategy' }
                pluginDirectoryNames = @($strategy.pluginDirectoryNames | ForEach-Object { [string]$_ })
                openChatShortcuts = @($strategy.openChatShortcuts | ForEach-Object { [string]$_ })
                chatSurfaceActionSearchCommands = @($strategy.chatSurfaceActionSearchCommands | ForEach-Object { [string]$_ })
                newConversationActionSearchCommands = @($strategy.newConversationActionSearchCommands | ForEach-Object { [string]$_ })
                installed = Test-PluginDirectoryInstalled -PluginDirectoryNames @($strategy.pluginDirectoryNames | ForEach-Object { [string]$_ })
            }
        }
    }

    if ($strategies.Count -eq 0) {
        $strategies += [pscustomobject]@{
            name = 'legacy-generic'
            pluginDirectoryNames = @()
            openChatShortcuts = @()
            chatSurfaceActionSearchCommands = @($keyboardConfig.chatSurfaceActionSearchCommands | ForEach-Object { [string]$_ })
            newConversationActionSearchCommands = @($keyboardConfig.newConversationActionSearchCommands | ForEach-Object { [string]$_ })
            installed = $true
        }
    }

    $preferred = @($strategies | Where-Object { $_.installed })
    $fallback = @($strategies | Where-Object { -not $_.installed })
    return @($preferred + $fallback)
}

function Reset-KeyboardFallbackUi {
    Write-Log 'Resetting transient keyboard-driven UI state before the next fallback attempt.'
    Send-KeysWithDelay -Keys '{ESC}' -DelayMs 140 -Description 'dismissing any open action-search or transient JetBrains popups'
    Send-KeysWithDelay -Keys '{ESC}' -DelayMs 140 -Description 'dismissing any remaining transient JetBrains popups'
}

function Invoke-KeyboardFallbackStrategy {
    param(
        [pscustomobject]$Strategy,
        [string]$Prompt,
        [System.Diagnostics.Process]$Process
    )

    $keyboardConfig = $config.keyboardFallback
    $focusDelayMs = if ($keyboardConfig.promptFocusDelayMs) { [int]$keyboardConfig.promptFocusDelayMs } else { 400 }
    $pasteDelayMs = if ($keyboardConfig.promptPasteDelayMs) { [int]$keyboardConfig.promptPasteDelayMs } else { 200 }
    $submitDelayMs = if ($keyboardConfig.promptSubmitDelayMs) { [int]$keyboardConfig.promptSubmitDelayMs } else { 200 }
    $verificationDelayMs = if ($keyboardConfig.postSubmitVerificationDelayMs) { [int]$keyboardConfig.postSubmitVerificationDelayMs } else { 500 }

    function Try-SubmitPromptInFocusedControl {
        param([string]$AttemptDescription)

        Write-Log "Submitting the prompt through keyboard fallback strategy '$($Strategy.name)' ($AttemptDescription)."
        Set-ClipboardText -Value $Prompt
        Start-Sleep -Milliseconds $focusDelayMs
        Send-KeysWithDelay -Keys '^a' -DelayMs 80 -Description 'selecting any existing prompt text in the chat input'
        Send-KeysWithDelay -Keys '{BACKSPACE}' -DelayMs 80 -Description 'clearing any existing prompt text in the chat input'
        Send-KeysWithDelay -Keys '^v' -DelayMs $pasteDelayMs -Description 'pasting the Dark Factory prompt into the chat input'

        $preSubmitText = Get-NormalizedPromptText (Get-ActiveControlTextViaClipboardProbe)
        $normalizedPrompt = Get-NormalizedPromptText $Prompt
        if ($preSubmitText -ne $normalizedPrompt) {
            Write-Log "Keyboard fallback strategy '$($Strategy.name)' failed pre-submit verification during $AttemptDescription. Focused control text was '$preSubmitText' instead of the expected prompt." 'WARN'
            return $false
        }

        Send-KeysWithDelay -Keys '{ENTER}' -DelayMs $submitDelayMs -Description 'submitting the prompt through the keyboard fallback'
        Start-Sleep -Milliseconds $verificationDelayMs

        $postSubmitText = Get-NormalizedPromptText (Get-ActiveControlTextViaClipboardProbe)
        if ($postSubmitText -eq $normalizedPrompt) {
            Write-Log "Keyboard fallback strategy '$($Strategy.name)' failed post-submit verification during $AttemptDescription because the focused control still contains the original prompt. This indicates the prompt likely landed in the wrong IDE field." 'WARN'
            return $false
        }

        Write-Log "Keyboard fallback strategy '$($Strategy.name)' passed prompt-target verification during $AttemptDescription."
        return $true
    }

    Focus-ProcessWindow -Process $Process
    Reset-KeyboardFallbackUi

    Write-Log "Trying keyboard fallback strategy '$($Strategy.name)'."

    foreach ($shortcut in @($Strategy.openChatShortcuts)) {
        if ([string]::IsNullOrWhiteSpace($shortcut)) {
            continue
        }

        Focus-ProcessWindow -Process $Process
        Reset-KeyboardFallbackUi
        Write-Log "Trying provider shortcut '$shortcut' for opening the chat surface in strategy '$($Strategy.name)'."
        Send-KeysWithDelay -Keys $shortcut -DelayMs $focusDelayMs -Description "opening the chat surface for strategy '$($Strategy.name)'"
        if (Try-SubmitPromptInFocusedControl -AttemptDescription "provider shortcut '$shortcut'") {
            return $true
        }
    }

    Focus-ProcessWindow -Process $Process
    Reset-KeyboardFallbackUi

    foreach ($command in @($Strategy.chatSurfaceActionSearchCommands)) {
        if (Invoke-ActionSearchCommand -CommandText ([string]$command) -Purpose "opening the chat surface for strategy '$($Strategy.name)'") {
            break
        }
    }

    foreach ($command in @($Strategy.newConversationActionSearchCommands)) {
        if (Invoke-ActionSearchCommand -CommandText ([string]$command) -Purpose "starting a new conversation for strategy '$($Strategy.name)'") {
            break
        }
    }

    $submitted = Try-SubmitPromptInFocusedControl -AttemptDescription 'provider action-search path'
    if ($submitted) {
        return $true
    }

    Reset-KeyboardFallbackUi
    return $false
}


function Send-KeysWithDelay {
    param(
        [string]$Keys,
        [int]$DelayMs = 150,
        [string]$Description = 'keyboard input'
    )

    if ($DryRun) {
        Write-Log "DRY RUN: would send keys '$Keys' for $Description."
        return
    }

    [System.Windows.Forms.SendKeys]::SendWait($Keys)
    Start-Sleep -Milliseconds $DelayMs
}

function Invoke-ActionSearchCommand {
    param(
        [string]$CommandText,
        [string]$Purpose
    )

    if ([string]::IsNullOrWhiteSpace($CommandText)) {
        return $false
    }

    $keyboardConfig = $config.keyboardFallback
    $commandDelayMs = if ($keyboardConfig.actionSearchCommandDelayMs) { [int]$keyboardConfig.actionSearchCommandDelayMs } else { 200 }
    $openDelayMs = if ($keyboardConfig.actionSearchOpenDelayMs) { [int]$keyboardConfig.actionSearchOpenDelayMs } else { 250 }
    $settleDelayMs = if ($keyboardConfig.actionSearchSettleDelayMs) { [int]$keyboardConfig.actionSearchSettleDelayMs } else { 600 }
    $shortcut = if ($keyboardConfig.actionSearchShortcut) { [string]$keyboardConfig.actionSearchShortcut } else { '^+a' }

    Write-Log "Trying keyboard fallback action-search command '$CommandText' for $Purpose."
    Send-KeysWithDelay -Keys $shortcut -DelayMs $openDelayMs -Description 'opening the JetBrains action search'
    Set-ClipboardText -Value $CommandText
    Send-KeysWithDelay -Keys '^a' -DelayMs 80 -Description 'selecting existing action-search text'
    Send-KeysWithDelay -Keys '{BACKSPACE}' -DelayMs 80 -Description 'clearing existing action-search text'
    Send-KeysWithDelay -Keys '^v' -DelayMs $commandDelayMs -Description "pasting action-search command '$CommandText'"
    Send-KeysWithDelay -Keys '{ENTER}' -DelayMs $settleDelayMs -Description "executing action-search command '$CommandText'"
    return $true
}

function Invoke-KeyboardPromptFallback {
    param(
        [string]$Prompt,
        [System.Diagnostics.Process]$Process
    )

    $keyboardConfig = $config.keyboardFallback
    if (-not $keyboardConfig -or -not $keyboardConfig.enabled) {
        Write-Log 'Keyboard prompt fallback is disabled in configuration.' 'WARN'
        return $false
    }

    $strategies = @(Get-KeyboardFallbackStrategies)
    foreach ($strategy in $strategies) {
        $submitted = Invoke-KeyboardFallbackStrategy -Strategy $strategy -Prompt $Prompt -Process $Process
        if ($submitted) {
            return $true
        }
    }

    return $false
}

function Set-ClipboardAndSubmitPrompt {
    param(
        [System.Windows.Automation.AutomationElement]$InputElement,
        [string]$Prompt
    )

    if ($DryRun) {
        Write-Log "DRY RUN: would focus the JetBrains prompt input and send: $Prompt"
        return
    }

    $InputElement.SetFocus()
    Start-Sleep -Milliseconds 150
    Set-ClipboardText -Value $Prompt
    [System.Windows.Forms.SendKeys]::SendWait('^a')
    Start-Sleep -Milliseconds 80
    [System.Windows.Forms.SendKeys]::SendWait('{BACKSPACE}')
    Start-Sleep -Milliseconds 80
    [System.Windows.Forms.SendKeys]::SendWait('^v')
    Start-Sleep -Milliseconds 120
    [System.Windows.Forms.SendKeys]::SendWait('{ENTER}')
}

function Ensure-WatcherRunning {
    param([string]$RequestedMode)

    if ($NoWatcher) {
        Write-Log 'Watcher startup skipped because -NoWatcher was supplied.'
        return
    }

    $modeConfig = Get-ModeConfig -RequestedMode $RequestedMode
    if (-not $modeConfig.ensureWatcher) {
        return
    }

    $existingPid = $null
    if (Test-Path $watcherPidPath) {
        $existingPid = (Get-Content -Path $watcherPidPath -ErrorAction SilentlyContinue | Select-Object -First 1)
    }

    if ($existingPid) {
        $existingProcess = Get-Process -Id $existingPid -ErrorAction SilentlyContinue
        if ($existingProcess) {
            Write-Log "Watcher already running with PID $existingPid."
            return
        }
    }

    $hostExecutable = Get-CurrentHostExecutable
    $argumentList = @(
        '-NoLogo',
        '-NoProfile',
        '-File',
        $scriptPath,
        '-Mode',
        'watch-only',
        '-HeadlessWatcher'
    )

    if ($ConfigPath) {
        $argumentList += @('-ConfigPath', $ConfigPath)
    }

    if ($WatcherPollIntervalMs -gt 0) {
        $argumentList += @('-WatcherPollIntervalMs', $WatcherPollIntervalMs)
    }

    if ($DryRun) {
        Write-Log "DRY RUN: would start watcher process using '$hostExecutable' with arguments '$($argumentList -join ' ')'."
        return
    }

    $process = Start-Process -FilePath $hostExecutable -ArgumentList $argumentList -WindowStyle Hidden -PassThru
    Set-Content -Path $watcherPidPath -Value $process.Id
    Write-Log "Started watcher process with PID $($process.Id)."
}

function Stop-WatcherProcess {
    if (-not (Test-Path $watcherPidPath)) {
        Write-Log 'No watcher PID file exists. Nothing to stop.'
        return
    }

    $watcherPid = Get-Content -Path $watcherPidPath | Select-Object -First 1
    if (-not $watcherPid) {
        Remove-Item -Path $watcherPidPath -Force -ErrorAction SilentlyContinue
        Write-Log 'Watcher PID file was empty and has been cleaned up.' 'WARN'
        return
    }

    $process = Get-Process -Id $watcherPid -ErrorAction SilentlyContinue
    if ($process) {
        if ($DryRun) {
            Write-Log "DRY RUN: would stop watcher process PID $watcherPid."
        }
        else {
            Stop-Process -Id $watcherPid -Force
            Write-Log "Stopped watcher process PID $watcherPid."
        }
    }
    else {
        Write-Log "Watcher PID $watcherPid is not running; cleaning stale PID file." 'WARN'
    }

    if (-not $DryRun) {
        Remove-Item -Path $watcherPidPath -Force -ErrorAction SilentlyContinue
    }
}

function Invoke-ModePrompt {
    param([string]$RequestedMode)

    $modeConfig = Get-ModeConfig -RequestedMode $RequestedMode
    $prompt = [string]$modeConfig.prompt

    if ([string]::IsNullOrWhiteSpace($prompt)) {
        throw "Mode '$RequestedMode' does not define a prompt."
    }

    Write-Log "Preparing to send prompt for mode '$RequestedMode'."

    if ($DryRun) {
        Ensure-WatcherRunning -RequestedMode $RequestedMode
        Write-Log "DRY RUN prompt: $prompt"
        return
    }

    Ensure-WatcherRunning -RequestedMode $RequestedMode

    $process = Get-PrimaryJetBrainsProcess
    Focus-ProcessWindow -Process $process
    $window = Get-WindowAutomationElement -Process $process
    Ensure-ChatSurfaceVisible -Window $window
    Try-OpenNewConversation -Window $window | Out-Null
    Start-Sleep -Milliseconds 200
    $window = Get-WindowAutomationElement -Process $process
    $promptInput = Get-PromptInputElement -Window $window

    if ($promptInput) {
        Set-ClipboardAndSubmitPrompt -InputElement $promptInput -Prompt $prompt
        Write-Log "Submitted prompt for mode '$RequestedMode' through the UI Automation input path."
        return
    }

    Write-Log 'Could not discover a JetBrains AI prompt input element through UI Automation. Falling back to keyboard-driven prompt submission.' 'WARN'
    $submitted = Invoke-KeyboardPromptFallback -Prompt $prompt -Process $process
    if (-not $submitted) {
        throw 'Could not find a JetBrains AI prompt input element, and the configured keyboard fallback path is disabled or unavailable. Make sure the AI chat/tool window is visible and the IDE is not covered by another modal dialog.'
    }

    Write-Log "Submitted prompt for mode '$RequestedMode' through the keyboard fallback path."
}

function Start-WatcherLoop {
    $watcherConfig = $config.watcher
    $pollIntervalMs = if ($WatcherPollIntervalMs -gt 0) { $WatcherPollIntervalMs } else { [int]$watcherConfig.pollIntervalMs }
    $debounceMs = [int]$watcherConfig.debounceMs
    $lastClickByKey = @{}
    $iteration = 0

    Write-Log "Watcher loop started. Poll interval: $pollIntervalMs ms. Debounce: $debounceMs ms."

    while ($true) {
        $iteration++
        $processes = @(Get-JetBrainsProcesses)

        foreach ($process in $processes) {
            $window = Get-WindowAutomationElement -Process $process
            foreach ($buttonName in @($config.watchedButtonNames)) {
                $button = Get-EnabledVisibleElementByNames -Root $window -Names @($buttonName) -ControlType ([System.Windows.Automation.ControlType]::Button)
                if (-not $button) {
                    continue
                }

                $key = "$($process.Id)|$buttonName"
                $shouldInvoke = $true
                if ($lastClickByKey.ContainsKey($key)) {
                    $elapsed = (Get-Date) - $lastClickByKey[$key]
                    if ($elapsed.TotalMilliseconds -lt $debounceMs) {
                        $shouldInvoke = $false
                    }
                }

                if (-not $shouldInvoke) {
                    continue
                }

                Write-Log "Watcher detected '$buttonName' in '$($process.MainWindowTitle)'."
                $clicked = Invoke-AutomationElement -Element $button
                if ($clicked) {
                    $lastClickByKey[$key] = Get-Date
                    Write-Log "Watcher clicked '$buttonName'."
                    Start-Sleep -Milliseconds 250
                }
            }
        }

        if ($DryRun -and $WatcherIterations -le 0 -and $iteration -ge 1) {
            Write-Log 'Dry-run watcher completed one iteration.'
            break
        }

        if ($WatcherIterations -gt 0 -and $iteration -ge $WatcherIterations) {
            Write-Log "Watcher completed the requested $WatcherIterations iteration(s)."
            break
        }

        Start-Sleep -Milliseconds $pollIntervalMs
    }
}

if ($StopWatcher) {
    Stop-WatcherProcess
    exit 0
}

if ($Mode -eq 'watch-only') {
    Start-WatcherLoop
    exit 0
}

Invoke-ModePrompt -RequestedMode $Mode

