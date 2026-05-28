# DevOps -> QA Handoff - TASK-012

- Timestamp: 2026-05-28 local
- Task: `TASK-012`
- From state: `DEV_IN_PROGRESS`
- To state: `READY_FOR_QA`
- Role: `devops`

## Summary

Completed the `RETURNED_TO_DEV` rework for the Windows-first Dark Factory launcher automation flow.

This rework directly addresses QA's critical defect on the supported Windows PowerShell + JetBrains path. The orchestrator now keeps its existing UIAutomation-first path but falls back to a repository-owned keyboard flow when the current IntelliJ build exposes no discoverable AI prompt input element through UI Automation.

Follow-up work already preserved:

- hardened the root launcher delegation after user feedback that `./call-start-factory.bash -DryRun -NoWatcher` opened Visual Studio Code on one workstation;
- added PowerShell-native root launchers for JetBrains PowerShell terminals.

New rework in this session:

- keyboard fallback inside `devops/automation/jetbrains-agent-orchestrator.ps1`
- repository-owned action-search/prompt timing config in `devops/automation/jetbrains-agent-config.json`
- live PowerShell launcher validation on this workstation for both `call-start-factory.ps1` and `call-dev-devops.ps1`
- temporary Java Access Bridge experiment reverted; the repository fix does not depend on it.

Delivered:

- root `call-*.bash` launchers for start, designer, SA, backend, devops, frontend, data-engineer, QA, and PO flows
- matching root `call-*.ps1` launchers for PowerShell / JetBrains PowerShell terminal usage across the same roles
- shared Git-Bash-to-PowerShell wrapper under `devops/automation/`
- shared PowerShell-to-PowerShell wrapper under `devops/automation/`
- PowerShell orchestrator with repository-owned prompt routing and background watcher management
- explicit allowlisted acknowledgement watcher for `Keep All`, `Add to Git`, and `Continue`
- operator documentation and README links

## Files to review

- `call-start-factory.bash`
- `call-start-factory.ps1`
- `call-designer.bash`
- `call-designer.ps1`
- `call-sa.bash`
- `call-sa.ps1`
- `call-dev-backend.bash`
- `call-dev-backend.ps1`
- `call-dev-devops.bash`
- `call-dev-devops.ps1`
- `call-dev-frontend.bash`
- `call-dev-frontend.ps1`
- `call-data-engineer.bash`
- `call-data-engineer.ps1`
- `call-qa.bash`
- `call-qa.ps1`
- `call-po.bash`
- `call-po.ps1`
- `devops/automation/call-jetbrains-agent.bash`
- `devops/automation/call-jetbrains-agent.ps1`
- `devops/automation/jetbrains-agent-orchestrator.ps1`
- `devops/automation/jetbrains-agent-config.json`
- `docs/jetbrains-dark-factory-automation.md`
- `README.md`
- `df/artifacts/TASK-012/devops/dev-notes.md`

## Validation completed by devops

| Check | Command | Result |
|---|---|---|
| Start prompt dry-run | `pwsh -NoLogo -NoProfile -File .\devops\automation\jetbrains-agent-orchestrator.ps1 -Mode start-factory -DryRun -NoWatcher` | PASS |
| Role prompt dry-run | `pwsh -NoLogo -NoProfile -File .\devops\automation\jetbrains-agent-orchestrator.ps1 -Mode role-devops -DryRun -NoWatcher` | PASS |
| Watcher loop dry-run | `pwsh -NoLogo -NoProfile -File .\devops\automation\jetbrains-agent-orchestrator.ps1 -Mode watch-only -DryRun -WatcherIterations 1` | PASS |
| Watcher startup dry-run | `pwsh -NoLogo -NoProfile -File .\devops\automation\jetbrains-agent-orchestrator.ps1 -Mode start-factory -DryRun` | PASS |
| Watcher stop path | `pwsh -NoLogo -NoProfile -File .\devops\automation\jetbrains-agent-orchestrator.ps1 -StopWatcher` | PASS |
| Exact regression repro path after fix | `C:\Program Files\Git\bin\bash.exe -lc 'cd "/c/Users/Viach/IdeaProjects/df-education-system-framework" && ./call-start-factory.bash -DryRun -NoWatcher'` | PASS |
| PowerShell-native start launcher | `./call-start-factory.ps1 -DryRun -NoWatcher` | PASS |
| Git Bash launcher dry-runs | `C:\Program Files\Git\bin\bash.exe -lc 'cd "/c/Users/Viach/IdeaProjects/df-education-system-framework" && ./call-*.bash -DryRun -NoWatcher'` | PASS |
| PowerShell launcher dry-runs | `./call-*.ps1 -DryRun -NoWatcher` | PASS |
| Shell / PowerShell syntax | `bash.exe -n ...`; PowerShell parser API | PASS |
| Supported PowerShell live start path after rework | `Set-Location "C:\Users\Viach\IdeaProjects\df-education-system-framework"; powershell.exe -NoLogo -NoProfile -ExecutionPolicy Bypass -File .\devops\automation\jetbrains-agent-orchestrator.ps1 -StopWatcher; .\call-start-factory.ps1; powershell.exe -NoLogo -NoProfile -ExecutionPolicy Bypass -File .\devops\automation\jetbrains-agent-orchestrator.ps1 -StopWatcher` | PASS |
| Supported PowerShell live role path after rework | `Set-Location "C:\Users\Viach\IdeaProjects\df-education-system-framework"; powershell.exe -NoLogo -NoProfile -ExecutionPolicy Bypass -File .\devops\automation\jetbrains-agent-orchestrator.ps1 -StopWatcher; .\call-dev-devops.ps1; powershell.exe -NoLogo -NoProfile -ExecutionPolicy Bypass -File .\devops\automation\jetbrains-agent-orchestrator.ps1 -StopWatcher` | PASS |
| Launcher log confirmation | `.dark-factory/automation/jetbrains-agent-orchestrator.log` | PASS |

## QA checklist

1. Confirm `call-start-factory.bash` resolves to the exact initial prompt `pickup new task and start to work`.
2. Confirm `./call-start-factory.bash -DryRun -NoWatcher` works from Git Bash and no longer opens an editor on the supported Bash path.
3. Confirm `./call-start-factory.ps1 -DryRun -NoWatcher` works from the JetBrains PowerShell terminal and is the documented supported PowerShell invocation path.
4. Confirm each shipped role launcher, including `call-designer.*` and `call-sa.*`, starts the appropriate role-scoped prompt through the shared helper.
5. Review the watcher implementation and verify the allowlist is still restricted to `Keep All`, `Add to Git`, and `Continue`.
6. Review the keyboard fallback implementation/config and confirm it only broadens prompt submission, not watcher button-click scope.
7. On a supported Windows + JetBrains workstation, perform at least one interactive verification pass that:
   - runs `./call-start-factory.bash`
     or `./call-start-factory.ps1` from the matching terminal type
   - confirms prompt submission reaches the JetBrains AI chat input
   - confirms the watcher handles the expected acknowledgement buttons without broad false-positive clicks
8. Re-run the live PowerShell path that previously failed and confirm it now completes through the keyboard fallback instead of throwing `Could not find a JetBrains AI prompt input element...`.
9. Confirm the docs explain environment assumptions, watcher start/stop, fragility, Bash-vs-PowerShell launcher usage, the keyboard fallback behavior, and recovery behavior.

## Known limitations

- Live IDE automation was exercised from this DevOps session for the supported PowerShell start and role launcher paths, but QA should still independently verify that the prompt lands in the expected chat surface and that no unintended UI target receives the pasted text.
- Direct `.bash` invocation from a PowerShell terminal remains subject to PowerShell/Windows file-association behavior before Bash can run, so the `.ps1` launchers are the supported PowerShell path.

