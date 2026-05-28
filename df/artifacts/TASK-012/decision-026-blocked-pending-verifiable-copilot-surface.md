# Decision Record - DECISION-026

- Date: 2026-05-28
- Status: Accepted
- Owner role: SA
- Related task: TASK-012

## Context

`TASK-012` has already completed two DevOps rework cycles and two QA cycles on the documented Windows + IntelliJ IDEA 2025.3.4 + GitHub Copilot workstation baseline.

Current evidence shows:

- UIAutomation can see only the IntelliJ top-level shell (`JBRCustomTitleBarControls`) and not a writable Copilot prompt control.
- the first keyboard fallback removed the earlier prompt-input exception but could paste the prompt into IntelliJ Settings -> Plugins search instead of chat.
- the second fallback rework now uses the exact installed Copilot action titles (`Copilot: Open Chat`, `New Chat Session`) and fails safe instead of logging a false success, but the resulting Copilot widget still exposes no machine-verifiable prompt target on this workstation.
- no stronger repository-ready automation dependency is currently available on the workstation baseline for immediate rerouting (`AutoHotkey`, `WinAppDriver`, and similar approved desktop-automation executables are not installed).

## Decision

Keep `TASK-012` in `BLOCKED` state.

Do not route another DevOps implementation cycle until one of these unblock conditions is met:

1. the JetBrains/Copilot/IDE stack is updated or reconfigured so the Copilot chat surface exposes a positively verifiable prompt target on this workstation; or
2. a stronger desktop-automation technology is explicitly approved, provisioned on the workstation, and accepted as part of the repository support contract with clear verification criteria.

## Consequences

- The existing launcher inventory, watcher allowlist, and documentation remain as the best safe partial solution.
- The task is not marked done because acceptance criterion 1 still fails on the supported live path.
- Future work should resume with fresh blocker-resolution evidence instead of repeating the same fallback experiments.

## Alternatives considered

- **Immediate reroute back to DevOps with the current toolset:** rejected because QA and DevOps already exhausted the safe UIAutomation + keyboard-fallback path without reaching a verifiable prompt target.
- **Introduce an unapproved external automation stack immediately:** rejected for now because it changes the workstation support contract, is not currently provisioned on this workstation, and would need explicit approval plus new validation criteria.
- **Accept best-effort prompt submission without positive verification:** rejected because QA already proved that this can target the wrong IntelliJ surface while falsely claiming success.

## Evidence

- `df/artifacts/TASK-012/qa-report.md`
- `df/artifacts/TASK-012/defects.md`
- `df/artifacts/TASK-012/devops/dev-notes.md`
- `df/artifacts/TASK-012/handoffs.md`
- `devops/automation/jetbrains-agent-orchestrator.ps1`
- `devops/automation/jetbrains-agent-config.json`
- `.dark-factory/automation/jetbrains-agent-orchestrator.log`
- Local workstation tool check on 2026-05-28 12:15 local:
  - `MISSING AutoHotkey`
  - `MISSING AutoHotkey64`
  - `MISSING WinAppDriver`
  - `MISSING pywinauto`
  - `FOUND python -> C:\Python314\python.exe`
  - `FOUND pwsh -> C:\Program Files\PowerShell\7\pwsh.exe`

## Follow-up actions

- Human / workstation owner should decide whether to update the JetBrains/Copilot stack or approve and provision a stronger automation dependency.
- After an unblock condition is met, start a new `sa` session to reroute `TASK-012` with updated constraints.

