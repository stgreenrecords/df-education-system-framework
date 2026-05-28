# DevOps Notes - TASK-012

## Session

- Date: 2026-05-28 local
- Role: `devops`
- Starting state: `RETURNED_TO_DEV`
- Current state: `BLOCKED`

## Start note

Resumed implementation after QA returned the task with a critical live-workstation defect on the supported Windows PowerShell launcher path.

## Initial constraints confirmed

- Keep the implementation Windows-first and repository-owned.
- Preserve the Dark Factory single-role-per-session rule.
- Restrict UI auto-clicking to the explicit allowlist: `Keep All`, `Add to Git`, `Continue`.
- Avoid backend/frontend/data/code changes outside DevOps-owned launcher and automation files.
- Fix the live PowerShell + JetBrains prompt-submission path on this workstation/IDE build instead of widening the watcher allowlist or relying only on dry-run evidence.

## QA-return defect analysis

- QA reproduced `./call-start-factory.ps1` failing after watcher startup because `devops/automation/jetbrains-agent-orchestrator.ps1` could not discover a JetBrains AI prompt input element through the current UI Automation search path.
- Reproduced the same workstation constraint locally: the active IntelliJ 2025.3.4 window (`SunAwtFrame`) exposes only one visible UIAutomation descendant (`ControlType.Pane` / `JBRCustomTitleBarControls`) and no visible `Edit` control for the AI prompt input.
- Additional diagnostics confirmed that even when Java Access Bridge binaries are present in the bundled JBR, the active top-level IntelliJ window on this workstation does not surface a Java-accessible HWND that the current bridge probe can target safely. A temporary `jabswitch /enable` experiment did not change the prompt-discovery result and was reverted with `jabswitch /disable`.
- Conclusion: the safe repository fix is a keyboard-driven fallback that keeps the watcher allowlist unchanged and only broadens prompt submission, not button clicking.

## Implementation completed

- Wired the root launchers `call-start-factory.bash`, `call-designer.bash`, `call-sa.bash`, `call-dev-backend.bash`, `call-dev-devops.bash`, `call-dev-frontend.bash`, `call-data-engineer.bash`, `call-qa.bash`, and `call-po.bash` to a shared DevOps-owned Bash wrapper so the operator has a matching launcher for every Dark Factory handoff role.
- Added matching PowerShell-native launchers `call-start-factory.ps1`, `call-designer.ps1`, `call-sa.ps1`, `call-dev-backend.ps1`, `call-dev-devops.ps1`, `call-dev-frontend.ps1`, `call-data-engineer.ps1`, `call-qa.ps1`, and `call-po.ps1`, plus a shared `devops/automation/call-jetbrains-agent.ps1` wrapper, so JetBrains PowerShell terminals can start the automation without relying on `.bash` file associations.
- Added `devops/automation/call-jetbrains-agent.bash` as the thin Windows/Git-Bash entrypoint that delegates to PowerShell and rejects unsupported Bash environments.
- Implemented `devops/automation/jetbrains-agent-orchestrator.ps1` with repository-owned mode/prompt routing, JetBrains window detection, prompt submission flow, background watcher management, explicit allowlisted acknowledgement handling, debounce protection, and local logging.
- Added `devops/automation/jetbrains-agent-config.json` so prompts, JetBrains window hints, prompt-input hints, and the acknowledgement allowlist remain repository-owned and easy to adjust.
- Added operator-facing documentation in `docs/jetbrains-dark-factory-automation.md` and linked it from `README.md` so the next-role launcher expectation, watcher lifecycle, supported baseline, recovery steps, and fragility limits are explicit.
- Added a repository-owned keyboard fallback inside `devops/automation/jetbrains-agent-orchestrator.ps1` so the launcher can still submit prompts when JetBrains exposes no discoverable AI input element through UI Automation on this workstation/build.
- Added repository-owned keyboard fallback configuration in `devops/automation/jetbrains-agent-config.json`, including the `Ctrl+Shift+A` action-search shortcut plus ordered action-search commands for opening the AI chat surface and starting a new chat before pasting/submitting the prompt.
- Kept the watcher allowlist unchanged (`Keep All`, `Add to Git`, `Continue`) and limited the fallback scope to prompt submission only.
- Reworked the GitHub Copilot provider profile to stop guessing generic action labels and instead use the installed plugin's exact action titles `Copilot: Open Chat` and `New Chat Session`, derived from the local Copilot plugin metadata.
- Added configurable focus-traversal retries to the keyboard fallback so the launcher can tab within the opened chat tool window and re-verify the prompt target before submission.
- Preserved the fail-safe rule: if the launcher cannot positively verify that the prompt landed in a writable chat input, it now throws instead of logging prompt-submission success.

## Environment and assumptions used for validation

- Windows workstation.
- IntelliJ IDEA 2025.3.4 (`idea64`) with active project window title `df-education-system-framework – TASK-012\task.md` during rework validation.
- Git Bash available at `C:\Program Files\Git\bin\bash.exe`.
- `pwsh.exe` and `powershell.exe` available on `PATH`.
- Validation now includes an additional live PowerShell launcher rerun after switching to the exact Copilot action titles and adding focus traversal. The launcher still reaches the correct provider actions on this workstation, but the resulting Copilot chat widget does not expose a clipboard-verifiable prompt control, so the launcher now fails safe instead of logging a false success.

## Validation evidence

| Check | Command | Result | Notes |
|---|---|---|---|
| PowerShell start mode dry-run | `pwsh -NoLogo -NoProfile -File .\devops\automation\jetbrains-agent-orchestrator.ps1 -Mode start-factory -DryRun -NoWatcher` | PASS | Confirmed the initial prompt resolves to `pickup new task and start to work`. |
| PowerShell role mode dry-run | `pwsh -NoLogo -NoProfile -File .\devops\automation\jetbrains-agent-orchestrator.ps1 -Mode role-devops -DryRun -NoWatcher` | PASS | Confirmed role prompt routing is repository-owned and explicit. |
| Watcher dry-run | `pwsh -NoLogo -NoProfile -File .\devops\automation\jetbrains-agent-orchestrator.ps1 -Mode watch-only -DryRun -WatcherIterations 1` | PASS | Confirmed watcher loop startup, poll configuration, and clean exit without clicking the IDE. |
| Watcher start-path dry-run | `pwsh -NoLogo -NoProfile -File .\devops\automation\jetbrains-agent-orchestrator.ps1 -Mode start-factory -DryRun` | PASS | Confirmed the normal launcher path would start the background watcher process. |
| Watcher stop path | `pwsh -NoLogo -NoProfile -File .\devops\automation\jetbrains-agent-orchestrator.ps1 -StopWatcher` | PASS | Confirmed safe no-op behavior when no watcher PID file exists. |
| Git Bash launcher end-to-end dry-runs | `C:\Program Files\Git\bin\bash.exe -lc 'cd "/c/Users/Viach/IdeaProjects/df-education-system-framework" && ./call-start-factory.bash -DryRun -NoWatcher'` and equivalent commands for all role launchers including `call-designer.bash` and `call-sa.bash` | PASS | Confirmed each root launcher delegates correctly through the shared Bash wrapper into the PowerShell orchestrator. |
| Bash syntax validation | `bash.exe -n` against all root launchers plus `devops/automation/call-jetbrains-agent.bash` | PASS | No shell syntax errors found. |
| PowerShell parser validation | `[System.Management.Automation.Language.Parser]::ParseFile(...)` for `devops/automation/jetbrains-agent-orchestrator.ps1` | PASS | No PowerShell parse errors found. |
| UIAutomation repro diagnostic | direct UIAutomation descendant survey against the active IntelliJ window | PASS | Reconfirmed the QA finding that only `JBRCustomTitleBarControls` is exposed, justifying the keyboard fallback. |
| Supported PowerShell live start path after rework | `Set-Location "C:\Users\Viach\IdeaProjects\df-education-system-framework"; powershell.exe -NoLogo -NoProfile -ExecutionPolicy Bypass -File .\devops\automation\jetbrains-agent-orchestrator.ps1 -StopWatcher; .\call-start-factory.ps1; powershell.exe -NoLogo -NoProfile -ExecutionPolicy Bypass -File .\devops\automation\jetbrains-agent-orchestrator.ps1 -StopWatcher` | PASS | Launcher no longer throws `Could not find a JetBrains AI prompt input element...`; log records keyboard fallback and `Submitted prompt for mode 'start-factory' through the keyboard fallback path.` |
| Supported PowerShell live role launcher after rework | `Set-Location "C:\Users\Viach\IdeaProjects\df-education-system-framework"; powershell.exe -NoLogo -NoProfile -ExecutionPolicy Bypass -File .\devops\automation\jetbrains-agent-orchestrator.ps1 -StopWatcher; .\call-dev-devops.ps1; powershell.exe -NoLogo -NoProfile -ExecutionPolicy Bypass -File .\devops\automation\jetbrains-agent-orchestrator.ps1 -StopWatcher` | PASS | Log records keyboard fallback and `Submitted prompt for mode 'role-devops' through the keyboard fallback path.` |
| Temporary Access Bridge experiment rollback | `C:\Program Files\JetBrains\IntelliJ IDEA 2025.3.4\jbr\bin\jabswitch.exe /disable` | PASS | Restored the workstation setting after confirming the repository fix does not require Java Access Bridge. |
| Copilot provider metadata extraction | `jar xf ... core.jar copilot/copilot.properties`; inspect `action.copilot.chat.open.title` and `copilot.agent.session.action.new.conversation` | PASS | Confirmed the installed Copilot plugin registers `Copilot: Open Chat` and `New Chat Session`, replacing the earlier ambiguous fallback labels. |
| PowerShell parser validation after focus-traversal rework | `[System.Management.Automation.Language.Parser]::ParseFile(...)` for `devops/automation/jetbrains-agent-orchestrator.ps1` | PASS | No parse errors after adding provider focus traversal. |
| Supported PowerShell live start path after exact Copilot-action rework | `Set-Location "C:\Users\Viach\IdeaProjects\df-education-system-framework"; powershell.exe -NoLogo -NoProfile -ExecutionPolicy Bypass -File .\devops\automation\jetbrains-agent-orchestrator.ps1 -StopWatcher; .\call-start-factory.ps1` | FAIL (safe) | Log shows deterministic Copilot action-search steps `Copilot: Open Chat` and `New Chat Session`, but pre-submit verification still reads an empty control value on this workstation, so the launcher now throws instead of claiming success. |
| Focused-element automation probe | temporary `.dark-factory\automation\focus-probe.ps1` snapshot of `AutomationElement.FocusedElement` before/after Copilot action-search commands | FAIL (environment) | The current IntelliJ 2025.3.4 build exposes only the top-level `SunAwtFrame` as the focused element throughout the sequence, so UIAutomation cannot distinguish the Copilot prompt widget from the window shell. |

## Known limitations and risk notes

- Live JetBrains UI automation remains version-sensitive because it depends on visible window titles, prompt-input discovery, and explicit button text.
- The current fix assumes JetBrains action search is available on the documented Windows baseline and that the configured action labels remain recognizable enough for keyboard-driven invocation.
- QA should still perform an independent interactive workstation verification pass and confirm that the AI chat really receives the prompt content, not only that the launcher completes and logs the fallback path.
- QA should also confirm that `Keep All`, `Add to Git`, and `Continue` remain the only watcher-driven auto-clicks inside JetBrains windows.
- The latest rework removes the earlier false-positive success path, but the supported workstation is still blocked because the Copilot chat widget does not currently expose a machine-verifiable prompt input through UIAutomation or clipboard-based focus probing after the exact Copilot actions run.
- Further safe automation progress now depends on an external change or decision: a stable provider shortcut/API, a more capable approved desktop-automation technology, or an IDE/plugin update that exposes a verifiable prompt control.

## User-reported regression follow-up

- The user reported that running `./call-start-factory.bash -DryRun -NoWatcher` opened Visual Studio Code instead of simply delegating to the shared automation helper.
- The follow-up report showed the command was being run from a JetBrains PowerShell terminal, where `./call-start-factory.bash` is resolved by PowerShell/Windows file association before the Bash script can execute.
- The most likely root cause is Windows file-association behavior on workstations where `.bash` files are associated with an editor. The root launchers were previously executing the nested helper path directly, which can allow Windows/editor association to win instead of Bash interpreting the nested script.
- Fix applied: all root `call-*.bash` launchers now delegate with `exec "${BASH:-bash}" .../call-jetbrains-agent.bash ...` so the nested helper is always interpreted by Bash explicitly rather than opened via file association.
- Additional fix applied: added PowerShell-native root launchers plus a shared PowerShell wrapper so PowerShell-based terminals have a first-class supported invocation path that avoids `.bash` association behavior entirely.
- Revalidated the exact reported command using Git Bash after the Bash-side hardening and confirmed the command now reaches the orchestrator dry-run path correctly there.
- Revalidated the PowerShell-native path using `./call-start-factory.ps1 -DryRun -NoWatcher` from PowerShell and confirmed the command reaches the orchestrator dry-run path without opening an editor.

## Completion note

DevOps completed the deterministic-Copilot-targeting rework and removed the earlier false-positive success condition, but `TASK-012` is not ready for QA. The task is now blocked on the documented workstation because the launcher can reach the exact Copilot actions yet still cannot positively verify a writable prompt input after those actions run.

