# Dark Factory JetBrains launcher automation

This guide documents the repository-owned workstation automation added for Dark Factory role/session startup in IntelliJ IDEA / JetBrains AI environments.

## Scope

The automation covers:

- starting the first Dark Factory session from `call-start-factory.bash`
- starting role-specific follow-up sessions from the existing root `call-*.bash` scripts
- watching IntelliJ / JetBrains dialogs for these explicit acknowledgement buttons only:
  - `Keep All`
  - `Add to Git`
  - `Continue`

The automation does **not** replace the Dark Factory one-role-per-session rule. Each role still ends with a handoff to the next role. The launcher scripts simply reduce the repeated manual prompt entry and recurring acknowledgement clicks around that workflow.

## Supported baseline

The first supported environment baseline is:

- Windows workstation
- Git Bash available to run the root `.bash` scripts
- `pwsh` or `powershell.exe` available on `PATH`
- IntelliJ IDEA / another JetBrains IDE open with JetBrains AI Assistant available in the UI

This is a Windows-first implementation because the current repository/operator environment is Windows and the UI automation relies on Windows desktop automation APIs.

## Files involved

### User-facing launchers

- PowerShell / JetBrains terminal launchers:
  - `call-start-factory.ps1`
  - `call-designer.ps1`
  - `call-sa.ps1`
  - `call-dev-backend.ps1`
  - `call-dev-devops.ps1`
  - `call-dev-frontend.ps1`
  - `call-data-engineer.ps1`
  - `call-qa.ps1`
  - `call-po.ps1`
- Git Bash launchers:
- `call-start-factory.bash`
- `call-designer.bash`
- `call-sa.bash`
- `call-dev-backend.bash`
- `call-dev-devops.bash`
- `call-dev-frontend.bash`
- `call-data-engineer.bash`
- `call-qa.bash`
- `call-po.bash`

### Shared automation helper

- `devops/automation/call-jetbrains-agent.bash`
- `devops/automation/jetbrains-agent-orchestrator.ps1`
- `devops/automation/jetbrains-agent-config.json`

## Prompts sent by each launcher

| Launcher | Mode | Prompt |
|---|---|---|
| `call-start-factory.ps1` / `call-start-factory.bash` | `start-factory` | `pickup new task and start to work` |
| `call-designer.ps1` / `call-designer.bash` | `role-designer` | Starts a new `designer` session prompt |
| `call-sa.ps1` / `call-sa.bash` | `role-sa` | Starts a new `sa` session prompt |
| `call-dev-backend.ps1` / `call-dev-backend.bash` | `role-backend-dev` | Starts a new `backend-dev` session prompt |
| `call-dev-devops.ps1` / `call-dev-devops.bash` | `role-devops` | Starts a new `devops` session prompt |
| `call-dev-frontend.ps1` / `call-dev-frontend.bash` | `role-frontend-dev` | Starts a new `frontend-dev` session prompt |
| `call-data-engineer.ps1` / `call-data-engineer.bash` | `role-data-engineer` | Starts a new `data-engineer` session prompt |
| `call-qa.ps1` / `call-qa.bash` | `role-qa` | Starts a new `qa` session prompt |
| `call-po.ps1` / `call-po.bash` | `role-po` | Starts a new `po` session prompt |

The exact prompt text lives in `devops/automation/jetbrains-agent-config.json` so it remains repository-owned and easy to adjust.

## Normal usage

### From PowerShell or the JetBrains PowerShell terminal

Run the PowerShell-native launcher:

```powershell
./call-start-factory.ps1
```

After one role finishes and tells you which role should go next, run the matching PowerShell launcher, for example:

```powershell
./call-designer.ps1
./call-sa.ps1
./call-dev-backend.ps1
./call-dev-devops.ps1
./call-dev-frontend.ps1
./call-data-engineer.ps1
./call-qa.ps1
./call-po.ps1
```

### From Git Bash

Run the Bash launcher from Git Bash:

```bash
./call-start-factory.bash
```

After one role finishes and tells you which role should go next, run the matching launcher, for example:

```bash
./call-designer.bash
./call-sa.bash
./call-dev-backend.bash
./call-dev-devops.bash
./call-dev-frontend.bash
./call-data-engineer.bash
./call-qa.bash
./call-po.bash
```

## What `call-start-factory.bash` does

1. delegates to the shared PowerShell orchestrator
2. ensures the acknowledgement watcher is running
3. focuses the JetBrains IDE window
4. tries to expose the AI chat surface
5. tries to open a new AI conversation if a `New Chat` / `New Conversation` style button is available
6. if UI Automation can see the AI input, pastes the initial prompt `pickup new task and start to work` into that input and submits it
7. if UI Automation cannot see the AI input on the current JetBrains build, falls back to a keyboard-driven path that opens JetBrains action search (`Ctrl+Shift+A`), runs repository-owned action-search commands to expose the AI chat and start a new conversation, then pastes/submits the prompt

The root launchers explicitly invoke the shared helper through the active Bash interpreter instead of executing the nested `.bash` file directly. This avoids Windows file-association behavior on workstations where `.bash` files are associated with an editor such as Visual Studio Code.

If you are in a PowerShell-based terminal, use the `.ps1` launchers instead of the `.bash` launchers. Running `./call-start-factory.bash` from PowerShell is controlled by PowerShell/Windows file-association behavior before the Bash script can execute, which can open an editor when `.bash` is associated with one.

## Prompt-submission fallback behavior

The orchestrator now uses two prompt-submission layers on the supported Windows baseline:

1. **UIAutomation-first path** — if JetBrains exposes a visible prompt `Edit` control, the launcher focuses that input and submits the prompt directly.
2. **Provider-aware keyboard fallback path** — if the JetBrains window exposes no usable prompt input through UI Automation, the launcher falls back to a repository-owned keyboard path that prefers known installed chat providers before generic action-search guesses.

On this workstation, the launcher now detects the installed GitHub Copilot JetBrains plugin and prioritizes its registered chat actions from the installed plugin metadata:

- action-search command `Copilot: Open Chat`
- exact follow-up action-search command `New Chat Session`
- fallback Copilot labels `GitHub Copilot Chat`, `Open Chat`, and `Copilot Chat`

If no known provider profile is installed, the launcher falls back to the generic JetBrains AI action-search commands such as `AI Chat`, `JetBrains AI Assistant`, `AI Assistant`, and `Chat`, followed by `New Chat` / `New Conversation` when configured.

The ordered provider strategies, action-search commands, focus-traversal sequences, and timing values are stored in `devops/automation/jetbrains-agent-config.json`, so fallback behavior remains repository-owned and adjustable without changing the launcher entrypoints.

## Prompt-target verification

The keyboard fallback no longer logs success immediately after sending keystrokes.

Before success is reported, the launcher now performs a keyboard-only verification pass against the currently focused control:

1. paste the prompt
2. copy the focused control contents and confirm they exactly match the prompt before submit
3. submit with `Enter`
4. copy the focused control contents again after submit
5. fail the fallback if the focused control still contains the original prompt text

If the first focused control inside the opened chat surface is not writable/verifiable, the launcher now also walks a small configured focus-traversal sequence (for example `Tab` / `Shift+Tab`) and retries the same verification before giving up.

This check is designed to catch the exact QA failure where the prompt was pasted into IntelliJ Settings -> Plugins search and remained there while the log incorrectly reported success.

## Watcher behavior

The watcher runs as a background PowerShell process and polls JetBrains windows for the explicit allowlist buttons:

- `Keep All`
- `Add to Git`
- `Continue`

Safety controls:

- only the allowlisted button names are auto-clicked
- generic buttons such as `OK`, `Yes`, or `Accept` are intentionally ignored
- clicks are debounced so the same button is not repeatedly clicked in a tight loop
- watcher activity is logged to `.dark-factory/automation/jetbrains-agent-orchestrator.log`
- the watcher PID is stored in `.dark-factory/automation/jetbrains-agent-watcher.pid`

The prompt-submission keyboard fallback does **not** expand the watcher allowlist. It only changes how the initial/role prompt is delivered when the AI input is not discoverable through UI Automation.

## Stopping the watcher

If you need to stop the watcher manually, run:

```powershell
pwsh -NoLogo -NoProfile -File .\devops\automation\jetbrains-agent-orchestrator.ps1 -StopWatcher
```

If you use Windows PowerShell instead of `pwsh`:

```powershell
powershell.exe -NoLogo -NoProfile -ExecutionPolicy Bypass -File .\devops\automation\jetbrains-agent-orchestrator.ps1 -StopWatcher
```

## Dry-run / validation commands

You can validate the prompt routing and watcher startup logic without clicking the live IDE by using `-DryRun`.

Examples:

```powershell
pwsh -NoLogo -NoProfile -File .\devops\automation\jetbrains-agent-orchestrator.ps1 -Mode start-factory -DryRun -NoWatcher
pwsh -NoLogo -NoProfile -File .\devops\automation\jetbrains-agent-orchestrator.ps1 -Mode role-qa -DryRun -NoWatcher
pwsh -NoLogo -NoProfile -File .\devops\automation\jetbrains-agent-orchestrator.ps1 -Mode watch-only -DryRun -WatcherIterations 1
```

## Known limitations

- JetBrains UI automation is version-sensitive and may require prompt/button-name tuning when the IDE or AI plugin changes.
- The first implementation assumes a Windows desktop session with the IDE already open and visible.
- Some JetBrains builds expose almost no usable UIAutomation tree for the AI prompt input. In that case the launcher uses the keyboard fallback described above instead of widening unknown UI selectors.
- If the IDE does not expose a detectable `New Chat` / `New Conversation` button, the orchestrator falls back to using the currently available AI prompt input.
- The keyboard fallback assumes JetBrains action search is available and that the configured provider action titles still resolve on the current IDE/plugin build.
- On the documented IntelliJ 2025.3.4 workstation, the launcher now targets the exact Copilot actions successfully but still fails safe if the resulting prompt widget does not expose a clipboard-verifiable text control. In that case the launcher intentionally throws instead of logging a false success.
- The post-submit verification is a strong heuristic, but it still depends on the focused control behaving enough like a prompt input for clipboard-based verification. If a future IDE/plugin build changes that behavior, the configured provider strategy, focus traversal, or verification mechanism may need adjustment.
- This automation does not guarantee support for macOS, Linux, or non-JetBrains editors.

## Recovery guidance

If the automation fails or clicks do not happen as expected:

1. stop the watcher using the command above
2. inspect `.dark-factory/automation/jetbrains-agent-orchestrator.log`
3. confirm the JetBrains IDE window is visible and not covered by another modal dialog
4. rerun a launcher with `-DryRun` to verify prompt resolution and watcher startup logic
5. if live runs still fail, check whether the log shows the UIAutomation path, the keyboard fallback path, or an action-search step that no longer matches the current JetBrains build
6. if necessary, temporarily continue with the manual Dark Factory flow and treat the automation issue as a follow-up DevOps defect

