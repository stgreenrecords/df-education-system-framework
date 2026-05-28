# Defects - TASK-012

### Defect 1: Supported PowerShell launcher cannot submit the start prompt in live JetBrains automation

- Severity: Critical
- Status: Superseded by Defect 2 after 2026-05-28 QA retest
- Environment: Windows workstation, `powershell.exe`, IntelliJ IDEA / JetBrains AI (`idea64`), repository root `C:\Users\Viach\IdeaProjects\df-education-system-framework`
- Steps to reproduce:
  1. Open IntelliJ IDEA with the repository and JetBrains AI available.
  2. Open a PowerShell terminal in the repository root.
  3. Run `powershell.exe -NoLogo -NoProfile -ExecutionPolicy Bypass -File .\devops\automation\jetbrains-agent-orchestrator.ps1 -StopWatcher`.
  4. Run `.\call-start-factory.ps1`.
  5. Observe the orchestrator output.
- Expected result: The launcher focuses the IDE, reaches the JetBrains AI chat input, submits `pickup new task and start to work`, and leaves the session running with the watcher active.
- Actual result: The watcher starts, but the orchestrator throws `Could not find a JetBrains AI prompt input element. Make sure the AI chat/tool window is visible and the IDE is not covered by another modal dialog.` and no prompt is submitted.
- Evidence:
  - `df/artifacts/TASK-012/qa-report.md`
  - `devops/automation/jetbrains-agent-orchestrator.ps1:431-445`
  - Live repro command output from 2026-05-28 local:
    - `Set-Location "C:\Users\Viach\IdeaProjects\df-education-system-framework"; powershell.exe -NoLogo -NoProfile -ExecutionPolicy Bypass -File .\devops\automation\jetbrains-agent-orchestrator.ps1 -StopWatcher; .\call-start-factory.ps1`
  - UI Automation diagnostic result from 2026-05-28 local:
    - detected window title: `df-education-system-framework – README.md`
    - visible descendants exposed to the diagnostic script: `1`
    - only visible descendant: `ControlType.Pane` / class `JBRCustomTitleBarControls`
- Suspected area: `devops/automation/jetbrains-agent-orchestrator.ps1` prompt-input discovery and lack of a resilient fallback when the current JetBrains build exposes little or no accessible UI tree for the AI chat input.

Retest note on 2026-05-28 local: the exact prompt-input exception no longer appeared after the keyboard-fallback rework, but the task still fails because the fallback can target the wrong IDE surface while logging success. See Defect 2.

### Defect 2: Keyboard fallback pastes the start prompt into IntelliJ Settings instead of the JetBrains AI chat

- Severity: Critical
- Status: Open
- Environment: Windows workstation, `powershell.exe`, IntelliJ IDEA / JetBrains AI (`idea64`), repository root `C:\Users\Viach\IdeaProjects\df-education-system-framework`
- Steps to reproduce:
  1. Open IntelliJ IDEA with the repository and JetBrains AI available.
  2. Open a PowerShell terminal in the repository root.
  3. Run `powershell.exe -NoLogo -NoProfile -ExecutionPolicy Bypass -File .\devops\automation\jetbrains-agent-orchestrator.ps1 -StopWatcher`.
  4. Run `./call-start-factory.ps1`.
  5. Observe the JetBrains UI after the keyboard fallback runs.
- Expected result: The launcher opens or focuses the JetBrains AI chat surface, starts a new chat if needed, pastes `pickup new task and start to work` into the AI prompt input, and begins the agent session.
- Actual result: The orchestrator log reports keyboard-fallback success, but the JetBrains UI ends up on IntelliJ Settings -> Plugins and the prompt text is pasted into the plugins search field instead of the AI chat input.
- Evidence:
  - `df/artifacts/TASK-012/qa-report.md`
  - `.dark-factory/automation/jetbrains-agent-orchestrator.log`
  - User-provided screenshot from the 2026-05-28 QA retest showing IntelliJ Settings -> Plugins with `pickup new task and start to work` in the search field
  - Live log lines from 2026-05-28 local:
    - `[2026-05-28 10:59:41] [INFO] Trying keyboard fallback action-search command 'JetBrains AI Assistant' for opening the JetBrains AI chat surface.`
    - `[2026-05-28 10:59:43] [INFO] Trying keyboard fallback action-search command 'New Chat' for starting a new JetBrains AI conversation.`
    - `[2026-05-28 10:59:46] [INFO] Submitted prompt for mode 'start-factory' through the keyboard fallback path.`
- Suspected area: `devops/automation/jetbrains-agent-config.json` keyboard fallback action-search command list and `devops/automation/jetbrains-agent-orchestrator.ps1` fallback flow, which currently assumes success after sending keystrokes and does not verify that the target UI is actually the JetBrains AI chat surface.
