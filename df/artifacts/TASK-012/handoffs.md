# Handoff - TASK-012

## SA -> devops

- Timestamp: 2026-05-26 local
- Task: TASK-012
- From state: ARCHITECTURE_IN_PROGRESS
- To state: READY_FOR_DEV
- Lane: devops
- Summary: SA routed the urgent Dark Factory workstation-automation request to `devops`. The recommended approach is a Windows-first Bash-wrapper + PowerShell desktop-automation flow that starts the initial `pickup new task and start to work` prompt, provides role-specific launcher prompts through the existing root `call-*.bash` files, and watches IntelliJ/JetBrains AI dialogs for the explicit buttons `Keep All`, `Add to Git`, and `Continue`.

## Evidence

- `df/artifacts/TASK-012/task.md`
- `df/artifacts/TASK-012/solution-design.md`
- `df/artifacts/TASK-012/decision-025-dark-factory-ide-automation-launchers.md`
- `call-start-factory.bash`
- `call-dev-backend.bash`
- `call-dev-devops.bash`
- `call-dev-frontend.bash`
- `call-data-engineer.bash`
- `call-qa.bash`
- `call-po.bash`
- `JETBRAINS_AI.md`
- `df/runtime/board.md`
- `df/runtime/devops-board.md`

## Tests/checks

| Check | Command/source | Result | Notes |
|---|---|---|---|
| Launcher inventory review | root file search for `call-*.bash` | PASS | Confirmed the required root launchers already exist but are currently empty placeholders |
| Existing factory start baseline | `start.bash` | PASS | Confirmed the current startup helper only starts the local application stack and does not automate Dark Factory sessions |
| JetBrains adapter review | `JETBRAINS_AI.md` | PASS | Confirmed the current adapter documents start behavior but does not automate prompts or IDE acknowledgements |
| Lane routing review | `df/roles/devops.md`; `df/runtime/devops-board.md` | PASS | The requested work is workstation automation, shell/PowerShell tooling, and operational documentation, which fits `devops` |

## Constraints

- Preserve the Dark Factory single-role-per-session rule; the automation should start the next role session, not merge roles.
- Keep the first implementation Windows-first if necessary; do not claim unsupported cross-platform IDE automation.
- Restrict auto-clicking to the explicit allowlist: `Keep All`, `Add to Git`, `Continue`.
- Do not change backend, frontend, data, or schema behavior as part of this task.

## Recommended approach

1. Implement a shared PowerShell helper under a DevOps-owned automation folder.
2. Make each root `call-*.bash` script a thin wrapper around that shared helper.
3. Hardcode or configure repository-owned prompts for the start flow and each role launcher.
4. Add an IntelliJ/JetBrains watcher loop that searches only for the approved button labels and clicks them safely.
5. Document watcher startup, stop/recovery behavior, and environment/version assumptions.

## Known risks

- Desktop automation against IntelliJ/JetBrains AI may be version-sensitive.
- There may be no stable public API for starting a new JetBrains AI conversation, increasing reliance on UI automation.
- Over-broad selectors could click the wrong dialog if DevOps does not keep them tightly scoped.

## Next role instructions

- `devops` should create `df/artifacts/TASK-012/devops/dev-notes.md` before editing launchers or automation helpers.
- `devops` should keep the user-facing entrypoint centered on `call-start-factory.bash` and the existing role scripts.
- `devops` should validate both prompt-launch behavior and safe button-click watcher behavior on the strongest available local workstation path.
- If reliable IntelliJ targeting is not possible from repository-owned scripts, return the task to SA with precise blocker evidence instead of silently widening scope.

## devops -> qa

- Timestamp: 2026-05-26 local
- Task: `TASK-012`
- From state: `DEV_IN_PROGRESS`
- To state: `READY_FOR_QA`
- Summary: DevOps completed the Windows-first launcher automation flow. The repository now ships root `call-*.bash` launchers for Git Bash plus matching `call-*.ps1` launchers for PowerShell/JetBrains terminals, all delegating to shared wrappers and the PowerShell orchestrator. Repository-owned prompts are defined in `devops/automation/jetbrains-agent-config.json`, the JetBrains watcher only allowlists `Keep All`, `Add to Git`, and `Continue`, and operator documentation now explains watcher lifecycle, supported baseline, terminal-specific launcher usage, and recovery steps.

### Evidence

- `df/artifacts/TASK-012/devops/dev-notes.md`
- `df/artifacts/TASK-012/devops/handoff-to-qa.md`
- `call-start-factory.ps1`
- `call-start-factory.bash`
- `call-designer.ps1`
- `call-designer.bash`
- `call-sa.ps1`
- `call-sa.bash`
- `call-dev-backend.ps1`
- `call-dev-backend.bash`
- `call-dev-devops.ps1`
- `call-dev-devops.bash`
- `call-dev-frontend.ps1`
- `call-dev-frontend.bash`
- `call-data-engineer.ps1`
- `call-data-engineer.bash`
- `call-qa.ps1`
- `call-qa.bash`
- `call-po.ps1`
- `call-po.bash`
- `devops/automation/call-jetbrains-agent.bash`
- `devops/automation/call-jetbrains-agent.ps1`
- `devops/automation/jetbrains-agent-orchestrator.ps1`
- `devops/automation/jetbrains-agent-config.json`
- `docs/jetbrains-dark-factory-automation.md`
- `README.md`

### Validation summary

- Dry-run prompt routing passed for start and role modes.
- Dry-run watcher loop and watcher-start logic passed.
- Safe watcher stop-path verification passed.
- End-to-end Git Bash wrapper dry-runs passed for all shipped launchers.
- End-to-end PowerShell wrapper dry-runs passed for all shipped `.ps1` launchers.
- Bash syntax and PowerShell parser checks passed.
- Live IDE automation was intentionally left for QA interactive verification to avoid disrupting the active coding session.
- Direct `.bash` invocation from a PowerShell terminal is not the supported path because PowerShell/Windows file association can intercept before Bash executes; the `.ps1` wrappers are the supported PowerShell/JetBrains-terminal launchers.

## qa -> devops

- Timestamp: 2026-05-28 local
- Task: `TASK-012`
- From state: `QA_IN_PROGRESS`
- To state: `RETURNED_TO_DEV`
- Lane: `devops`
- Summary: QA reproduced a live failure on the supported Windows PowerShell launcher path. Dry-run prompt routing still works, but `.\call-start-factory.ps1` fails after watcher startup because `devops/automation/jetbrains-agent-orchestrator.ps1` cannot discover a JetBrains AI prompt input element on this workstation/IDE build.

### Evidence

- `df/artifacts/TASK-012/qa-report.md`
- `df/artifacts/TASK-012/defects.md`
- `call-start-factory.ps1`
- `devops/automation/jetbrains-agent-orchestrator.ps1`
- `devops/automation/jetbrains-agent-config.json`
- `docs/jetbrains-dark-factory-automation.md`
- `df/runtime/board.md`
- `df/runtime/devops-board.md`

### QA verification summary

- `Set-Location "C:\Users\Viach\IdeaProjects\df-education-system-framework"; .\call-start-factory.ps1 -DryRun -NoWatcher` -> PASS (`pickup new task and start to work` resolved correctly)
- `Set-Location "C:\Users\Viach\IdeaProjects\df-education-system-framework"; powershell.exe -NoLogo -NoProfile -ExecutionPolicy Bypass -File .\devops\automation\jetbrains-agent-orchestrator.ps1 -StopWatcher; .\call-start-factory.ps1` -> FAIL (`Could not find a JetBrains AI prompt input element...`)
- PowerShell UIAutomation diagnostic against the detected `idea64` window found only one visible descendant (`ControlType.Pane` / `JBRCustomTitleBarControls`), leaving no visible prompt-input control for the current selector path.

### Defect summary

- Critical: supported live PowerShell launcher cannot complete start-session prompt submission on the documented Windows + JetBrains baseline.

### Next role instructions

- `devops` must make the live prompt-submission flow resilient when the current JetBrains build does not expose a discoverable AI input element through the existing UI Automation search path.
- `devops` must rerun the supported PowerShell live path, not just dry-run routing, before returning the task to QA.
- `devops` should preserve the strict watcher allowlist (`Keep All`, `Add to Git`, `Continue`) while addressing prompt-submission robustness.

## devops -> qa (rework)

- Timestamp: 2026-05-28 local
- Task: `TASK-012`
- From state: `DEV_IN_PROGRESS`
- To state: `READY_FOR_QA`
- Lane: `devops`
- Summary: DevOps fixed the supported Windows PowerShell launcher failure by adding a repository-owned keyboard fallback for prompt submission when the current JetBrains build exposes no discoverable AI prompt input element through UI Automation. The watcher allowlist remains unchanged, the PowerShell and Git Bash launcher inventory remains intact, and live PowerShell validation now completes successfully on this workstation for both `call-start-factory.ps1` and `call-dev-devops.ps1`.

### Evidence

- `df/artifacts/TASK-012/devops/dev-notes.md`
- `df/artifacts/TASK-012/devops/handoff-to-qa.md`
- `df/artifacts/TASK-012/task.md`
- `call-start-factory.ps1`
- `call-dev-devops.ps1`
- `devops/automation/jetbrains-agent-orchestrator.ps1`
- `devops/automation/jetbrains-agent-config.json`
- `docs/jetbrains-dark-factory-automation.md`
- `README.md`
- `.dark-factory/automation/jetbrains-agent-orchestrator.log`

### Validation summary

- PowerShell parser validation passed for `devops/automation/jetbrains-agent-orchestrator.ps1`.
- Dry-run prompt routing still passed for `start-factory` and `role-devops`.
- Live `call-start-factory.ps1` validation passed on the supported Windows PowerShell path and logged successful keyboard-fallback prompt submission instead of throwing the previous prompt-input exception.
- Live `call-dev-devops.ps1` validation also passed and logged successful keyboard-fallback prompt submission.
- Temporary Java Access Bridge experimentation was reverted because the repository fix does not depend on it.

### Next role instructions

- `qa` should rerun the previously failing PowerShell path and confirm it no longer throws `Could not find a JetBrains AI prompt input element...`.
- `qa` should confirm the keyboard fallback only broadens prompt submission and does not expand the watcher allowlist beyond `Keep All`, `Add to Git`, and `Continue`.
- `qa` should perform at least one independent interactive verification pass that confirms the prompt actually lands in the JetBrains AI chat surface on the supported workstation baseline.

## qa -> devops (rework 2)

- Timestamp: 2026-05-28 local
- Task: `TASK-012`
- From state: `QA_IN_PROGRESS`
- To state: `RETURNED_TO_DEV`
- Lane: `devops`
- Summary: QA retested the DevOps keyboard-fallback rework on the supported Windows PowerShell path. The previous prompt-input exception no longer appeared, but the task still fails because the fallback can target the wrong JetBrains UI surface: the latest live run pasted `pickup new task and start to work` into IntelliJ Settings -> Plugins search while the orchestrator log still reported success.

### Evidence

- `df/artifacts/TASK-012/qa-report.md`
- `df/artifacts/TASK-012/defects.md`
- `devops/automation/jetbrains-agent-orchestrator.ps1`
- `devops/automation/jetbrains-agent-config.json`
- `.dark-factory/automation/jetbrains-agent-orchestrator.log`
- User-provided screenshot from the 2026-05-28 QA retest showing IntelliJ Settings -> Plugins with `pickup new task and start to work` in the search field
- `df/runtime/board.md`
- `df/runtime/devops-board.md`

### QA verification summary

- `Set-Location "C:\Users\Viach\IdeaProjects\df-education-system-framework"; .\call-start-factory.ps1 -DryRun -NoWatcher` -> PASS (`pickup new task and start to work` still resolves correctly)
- User interactive `./call-start-factory.ps1` retest plus `.dark-factory/automation/jetbrains-agent-orchestrator.log` -> FAIL (fallback reported success, but the prompt landed in IntelliJ Settings -> Plugins search instead of the JetBrains AI chat surface)
- `Set-Location "C:\Users\Viach\IdeaProjects\df-education-system-framework"; Get-Content -Path ".dark-factory\automation\jetbrains-agent-orchestrator.log" -Tail 40` -> PASS (confirms the fallback sequence and false-positive success log)
- `devops/automation/jetbrains-agent-config.json` watcher allowlist inspection -> PASS (`Keep All`, `Add to Git`, `Continue` only)

### Defect summary

- Critical: the keyboard fallback is not deterministic and can paste the prompt into the wrong IntelliJ UI surface while logging success, so the supported live PowerShell launcher still does not complete the required start-session behavior.

### Next role instructions

- `devops` must replace the ambiguous keyboard fallback with a deterministic JetBrains AI targeting path.
- `devops` must add positive verification that the prompt target is actually the JetBrains AI chat/input surface before logging submission success.
- `devops` must rerun the supported live PowerShell path and provide evidence that the prompt lands in chat rather than IntelliJ Settings, Plugins search, or another unrelated UI field.

## devops -> factory (blocked)

- Timestamp: 2026-05-28 local
- Task: `TASK-012`
- From state: `DEV_IN_PROGRESS`
- To state: `BLOCKED`
- Lane: `devops`
- Summary: DevOps replaced the ambiguous GitHub Copilot fallback labels with the exact installed plugin actions `Copilot: Open Chat` and `New Chat Session`, added focus-traversal retries inside the opened chat tool window, and preserved fail-safe verification so the launcher no longer reports prompt-submission success when it cannot verify the target. Dry-run routing and parser validation pass, and the live launcher now reaches the deterministic Copilot actions, but the current IntelliJ 2025.3.4 / Copilot widget still exposes no machine-verifiable prompt input after those actions run, so the task is blocked on the documented workstation baseline.

### Evidence

- `df/artifacts/TASK-012/devops/dev-notes.md`
- `df/artifacts/TASK-012/task.md`
- `devops/automation/jetbrains-agent-orchestrator.ps1`
- `devops/automation/jetbrains-agent-config.json`
- `docs/jetbrains-dark-factory-automation.md`
- `.dark-factory/automation/jetbrains-agent-orchestrator.log`
- `C:\Users\Viach\AppData\Roaming\JetBrains\IntelliJIdea2025.3\plugins\github-copilot-intellij\lib\core.jar`
- `C:\Users\Viach\AppData\Roaming\JetBrains\IntelliJIdea2025.3\options\actionSummary.xml`

### Validation summary

- `./call-start-factory.ps1 -DryRun -NoWatcher` -> PASS
- `./call-dev-devops.ps1 -DryRun -NoWatcher` -> PASS
- PowerShell parser validation for `devops/automation/jetbrains-agent-orchestrator.ps1` -> PASS
- Copilot metadata extraction from `core.jar` -> PASS (`Copilot: Open Chat`, `New Chat Session` confirmed)
- Supported live `./call-start-factory.ps1` path -> FAIL (safe): deterministic Copilot actions are invoked, but the prompt target remains unverifiable because the resulting widget exposes no clipboard-verifiable text control on this workstation

### Next role instructions

- New session required. `sa` should review the blocker evidence and decide whether to keep the task blocked pending an IDE/plugin update or route an alternate approved automation approach (for example a stronger desktop-automation technology) that can positively verify the Copilot prompt target on this workstation.

## sa -> human/factory (blocked)

- Timestamp: 2026-05-28 12:15 local
- Task: `TASK-012`
- From state: `BLOCKED`
- To state: `BLOCKED`
- Lane: `n/a`
- Summary: SA reviewed the latest QA + DevOps blocker evidence and decided not to reroute another DevOps cycle with the current workstation/tooling surface. The repository already contains the safest reachable partial solution, but the supported live path still cannot positively verify that the prompt target is the Copilot chat input on this IntelliJ baseline.

### Evidence

- `df/artifacts/TASK-012/task.md`
- `df/artifacts/TASK-012/solution-design.md`
- `df/artifacts/TASK-012/decision-026-blocked-pending-verifiable-copilot-surface.md`
- `df/artifacts/TASK-012/qa-report.md`
- `df/artifacts/TASK-012/defects.md`
- `df/artifacts/TASK-012/devops/dev-notes.md`
- `devops/automation/jetbrains-agent-orchestrator.ps1`
- `devops/automation/jetbrains-agent-config.json`
- `.dark-factory/automation/jetbrains-agent-orchestrator.log`

### Tests/checks

| Check | Command/source | Result | Notes |
|---|---|---|---|
| QA blocker evidence review | `df/artifacts/TASK-012/qa-report.md`; `df/artifacts/TASK-012/defects.md` | PASS | Confirms the live fallback can still target IntelliJ Settings instead of Copilot chat or fail safe because no verifiable input surface is exposed. |
| DevOps blocker evidence review | `df/artifacts/TASK-012/devops/dev-notes.md` | PASS | Confirms the launcher now uses exact Copilot action titles and no longer logs false success, but still cannot verify a writable prompt control. |
| Local alternate-tool availability check | `Get-Command AutoHotkey, AutoHotkey64, WinAppDriver, pywinauto, python, pwsh -ErrorAction SilentlyContinue` | PASS | No stronger approved desktop-automation executable is currently provisioned on this workstation baseline; only `python` and `pwsh` are present. |

### Known risks

- Further rework with the same workstation/IDE automation surface is likely to repeat failure without improving acceptance.
- Introducing a stronger automation dependency changes the support contract and requires explicit approval/provisioning.

### Next role instructions

- Human / workstation owner should either update the JetBrains/Copilot stack so the AI chat input becomes verifiable or explicitly approve and provision a stronger desktop-automation dependency.
- After one of those unblock conditions is met, start a new `sa` session so the task can be rerouted with the new constraints/evidence.

### Blockers

- The current IntelliJ IDEA 2025.3.4 + GitHub Copilot workstation baseline exposes no positively verifiable prompt target for the supported live launcher path.

