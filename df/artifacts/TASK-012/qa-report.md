# QA Report - TASK-012

## QA Result: FAIL

- Task: `TASK-012`
- Role: `qa`
- Timestamp: 2026-05-28 local
- Starting state: `READY_FOR_QA`
- Ending state: `RETURNED_TO_DEV`

## QA cycle summary

- This retest covers the DevOps rework that added the keyboard fallback path for prompt submission.
- The previous `Could not find a JetBrains AI prompt input element...` exception no longer appeared in the latest live run.
- The task still fails because the fallback reported success after targeting the wrong JetBrains UI surface: the prompt text was pasted into IntelliJ Settings -> Plugins search instead of the JetBrains AI chat input.

## Scope reviewed

- `df/artifacts/TASK-012/task.md`
- `df/artifacts/TASK-012/handoffs.md`
- `df/artifacts/TASK-012/devops/dev-notes.md`
- `df/artifacts/TASK-012/devops/handoff-to-qa.md`
- `df/artifacts/TASK-012/defects.md`
- `call-start-factory.ps1`
- `call-start-factory.bash`
- `devops/automation/call-jetbrains-agent.ps1`
- `devops/automation/call-jetbrains-agent.bash`
- `devops/automation/jetbrains-agent-orchestrator.ps1`
- `devops/automation/jetbrains-agent-config.json`
- `docs/jetbrains-dark-factory-automation.md`
- `.dark-factory/automation/jetbrains-agent-orchestrator.log`
- User-provided screenshot from the current QA session showing IntelliJ Settings -> Plugins with `pickup new task and start to work` in the search field
- `df/runtime/board.md`
- `df/runtime/devops-board.md`

## Test environment

- OS: Windows
- Shell: `powershell.exe`
- IDE process found by automation: `idea64`
- IDE state during live repro: main IntelliJ project window open, with the Settings dialog visible on the Plugins page after the fallback ran

## Acceptance-criteria coverage

| Acceptance criterion | Result | Notes |
|---|---|---|
| Running `call-start-factory.bash` kicks off the initial agent session with the prompt `pickup new task and start to work`. | FAIL | Dry-run prompt routing still resolves correctly, but the supported live PowerShell path does not land the prompt in the JetBrains AI chat surface. The latest evidence shows the prompt text was pasted into IntelliJ Settings -> Plugins search instead. |
| The repository contains role-specific launcher scripts for next-role prompts. | PASS | Root `call-*.bash` and `call-*.ps1` launchers plus shared wrappers/config remain present. |
| The automation design makes the next-role expectation explicit. | PASS | Prompt modes and operator docs remain explicit. |
| The start flow watches IntelliJ IDEA / JetBrains AI for `Keep All`, `Add to Git`, and `Continue`. | PASS (code inspection only) | `devops/automation/jetbrains-agent-config.json` still restricts the allowlist to the three requested buttons. This retest did not approve the live watcher path because prompt targeting failed first. |
| The implementation documents supported assumptions, watcher lifecycle, fragility, and recovery. | PASS | `docs/jetbrains-dark-factory-automation.md` still documents the supported baseline, dry-run path, watcher stop path, fallback behavior, and recovery guidance. |

## Test execution

| Check | Command / source | Result | Notes |
|---|---|---|---|
| Supported PowerShell dry-run path | `Set-Location "C:\Users\Viach\IdeaProjects\df-education-system-framework"; .\call-start-factory.ps1 -DryRun -NoWatcher` | PASS | Reconfirmed the configured prompt is still `pickup new task and start to work`. |
| Supported PowerShell live path after DevOps rework | User-performed `./call-start-factory.ps1` interactive run evidenced by the current-session screenshot and `.dark-factory/automation/jetbrains-agent-orchestrator.log` lines `2026-05-28 10:59:40` through `10:59:46` | FAIL | The orchestrator log reports fallback success, but the screenshot shows IntelliJ Settings -> Plugins and the prompt text in the plugins search box instead of a JetBrains AI conversation. |
| Launcher log correlation | `Set-Location "C:\Users\Viach\IdeaProjects\df-education-system-framework"; Get-Content -Path ".dark-factory\automation\jetbrains-agent-orchestrator.log" -Tail 40` | PASS | Confirms the fallback sequence (`JetBrains AI Assistant` -> `New Chat` -> paste -> submit) and the false-positive success log entry for the failing interactive run. |
| Watcher allowlist inspection | `devops/automation/jetbrains-agent-config.json` | PASS | Allowlist remains exactly `Keep All`, `Add to Git`, `Continue`. |
| Watcher cleanup after repro | `Set-Location "C:\Users\Viach\IdeaProjects\df-education-system-framework"; powershell.exe -NoLogo -NoProfile -ExecutionPolicy Bypass -File .\devops\automation\jetbrains-agent-orchestrator.ps1 -StopWatcher` | PASS | Stopped the leftover watcher process (`PID 18336`) after the interactive repro evidence was collected. |

## Expected result

Running the supported Windows PowerShell launcher should focus JetBrains AI, open or focus the AI chat surface, submit `pickup new task and start to work` into the JetBrains AI prompt input, and leave the session ready for the next Dark Factory loop step.

## Actual result

The latest live run no longer throws the old prompt-input exception, but the fallback still does not complete the required behavior. The user-provided screenshot shows IntelliJ Settings -> Plugins with the search field containing `pickup new task and start to work`, while `.dark-factory/automation/jetbrains-agent-orchestrator.log` still records `Submitted prompt for mode 'start-factory' through the keyboard fallback path.` This is a false-positive success signal: no valid JetBrains AI session was started.

## Defects

- See `df/artifacts/TASK-012/defects.md`.

## QA conclusion

`TASK-012` still does not satisfy the required live start-session behavior on the supported Windows PowerShell + JetBrains path. The previous exception was mitigated, but the replacement fallback is not deterministic and can paste the prompt into the wrong JetBrains surface while logging success. The task is returned to `devops` for rework.

## Handoff

- New state: `RETURNED_TO_DEV`
- Next role: `devops`
- Required next action: replace the ambiguous action-search fallback with a deterministic AI-chat targeting path, add positive verification that the prompt target is really the JetBrains AI chat surface before reporting success, rerun the supported live PowerShell path, and provide fresh evidence that the prompt lands in chat rather than another IDE field.
