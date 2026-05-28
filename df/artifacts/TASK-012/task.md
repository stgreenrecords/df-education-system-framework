# Task - TASK-012

## Summary

Automate the Dark Factory IntelliJ/JetBrains AI operator loop so the user can kick off work from `call-start-factory.bash` and avoid repeated manual UI acknowledgements for routine agent progression.

## Type

Task

## Priority

P0

## Current state

BLOCKED

## Business goal

Reduce operator friction by making the Dark Factory framework start, role handoff prompting, and recurring IDE acknowledgement clicks repository-owned automation instead of repeated manual actions.

## Acceptance criteria

- [ ] Running `call-start-factory.bash` kicks off the initial agent session with the prompt `pickup new task and start to work`.
- [ ] The repository contains role-specific launcher scripts that can start a new conversation with the appropriate prompt for the next role after the current role finishes, including at least the already-present root launchers such as `call-dev-backend.bash`, `call-dev-devops.bash`, `call-dev-frontend.bash`, `call-data-engineer.bash`, `call-qa.bash`, and `call-po.bash`.
- [ ] The automation design makes the next-role expectation explicit so that after one role finishes, the operator can run the matching role launcher script named in the handoff.
- [ ] `call-start-factory.bash` (directly or through a shared helper it starts) also watches the IntelliJ IDEA / JetBrains AI UI for recurring acknowledgement buttons needed to keep the session moving and automatically clicks the equivalents of `Keep All`, `Add to Git`, and `Continue` when they appear.
- [ ] The implementation documents supported environment assumptions, known IDE/UI fragility risks, how the watcher is started/stopped, and how to recover if UI automation fails.

## Out of scope

- Replacing the Dark Factory role/state model.
- Changing backend, frontend, data, or database behavior.
- Automating arbitrary IntelliJ actions beyond the requested agent-session acknowledgements.
- Guaranteeing cross-IDE or cross-OS support beyond the first documented environment baseline.

## Assumptions

- The user explicitly requested a repository-owned automation flow centered on `call-start-factory.bash` and the existing root `call-*.bash` launchers.
- The current workstation baseline is Windows with Git Bash available, so Bash entrypoints may delegate to Windows-native automation helpers when needed.
- IntelliJ IDEA / JetBrains AI Assistant do not expose a repository-local CLI flow here, so the first implementation may rely on desktop/UI automation against the live IDE window.
- Because the user asked specifically for automatic button-click handling, best-effort IDE UI automation is in scope even though the exact widget hierarchy may be version-sensitive.
- Refinement was skipped because the user provided explicit goal/behavior and the remaining uncertainty is technical/architectural rather than product ambiguity.

## Dependencies

- Existing root launcher files: `call-start-factory.bash`, `call-dev-backend.bash`, `call-dev-devops.bash`, `call-dev-frontend.bash`, `call-data-engineer.bash`, `call-qa.bash`, `call-po.bash`
- Windows host automation capabilities available to repository-owned scripts
- JetBrains / IntelliJ runtime on the operator workstation
- A verifiable Copilot/JetBrains chat input on the supported workstation baseline, or an explicitly approved and provisioned stronger desktop-automation technology

## Risks

- IntelliJ / JetBrains AI UI automation is inherently brittle across IDE versions, themes, window layouts, and localized button text.
- There may be no stable public CLI/API for starting a new JetBrains AI conversation, which increases reliance on desktop automation.
- Over-aggressive auto-click behavior could dismiss an unexpected dialog incorrectly if selectors are too broad.
- The repository may need to document a Windows-only first baseline before broader portability is attempted.
- Repeated rework with the current workstation/tool surface can consume time without improving acceptance if the Copilot widget still exposes no positively verifiable prompt target.

## Links

- Issue: n/a
- PR: n/a
- Design: `df/artifacts/TASK-012/solution-design.md`

## Implementation lane

- Lane: `devops`
- Subdashboard: `df/runtime/devops-board.md`
- Artifact folder for implementation notes: `df/artifacts/TASK-012/devops/`

## Role history

| Timestamp | Role | State | Summary |
|---|---|---|---|
| 2026-05-26 local | sa | OPEN -> NEEDS_ARCHITECTURE | Promoted the user's urgent Dark Factory framework automation request into a new task. Refinement was skipped because the requested behavior is explicit enough for architecture and routing. |
| 2026-05-26 local | sa | NEEDS_ARCHITECTURE -> ARCHITECTURE_IN_PROGRESS | Started architecture because the request affects workstation automation, IDE interaction, launcher entrypoints, and operator-control/risk boundaries. |
| 2026-05-26 local | sa | ARCHITECTURE_IN_PROGRESS -> READY_FOR_DEV | Selected a DevOps-owned Windows-first launcher + desktop-automation approach that keeps the role model intact while automating prompts and recurring IDE acknowledgements. |
| 2026-05-26 local | devops | READY_FOR_DEV -> DEV_IN_PROGRESS | Started DevOps implementation after confirming the SA design, current launcher placeholders, and the requested JetBrains acknowledgement allowlist. |
| 2026-05-26 local | devops | DEV_IN_PROGRESS -> READY_FOR_QA | Completed the Windows-first launcher automation, shared orchestrator/config, and operator documentation; dry-run wrapper/orchestrator validation passed and interactive JetBrains verification is handed to QA. |
| 2026-05-26 local | devops | READY_FOR_QA -> DEV_IN_PROGRESS | User reported that invoking the `.bash` launcher from a JetBrains PowerShell terminal still opened Visual Studio Code via file association, so DevOps resumed implementation for launcher rework. |
| 2026-05-26 local | devops | DEV_IN_PROGRESS -> READY_FOR_QA | Hardened Bash delegation and added PowerShell-native `call-*.ps1` launchers plus shared PowerShell wrapper support so JetBrains PowerShell terminals have a supported repository-owned invocation path. |
| 2026-05-28 local | qa | READY_FOR_QA -> QA_IN_PROGRESS | Started QA verification from the supported Windows PowerShell path and reviewed the DevOps handoff, launcher files, orchestrator, config, and operator docs before interactive validation. |
| 2026-05-28 local | qa | QA_IN_PROGRESS -> QA_FAILED | Reproduced a live failure on `.\call-start-factory.ps1`: the watcher started, but the orchestrator threw `Could not find a JetBrains AI prompt input element...` instead of submitting the initial prompt. |
| 2026-05-28 local | qa | QA_FAILED -> RETURNED_TO_DEV | Returned the task to `devops` with a critical defect and reproduction evidence because the supported live PowerShell launcher path does not complete the required start-session behavior. |
| 2026-05-28 local | devops | RETURNED_TO_DEV -> READY_FOR_QA | Added a keyboard-driven prompt-submission fallback for JetBrains builds that expose no discoverable AI prompt input through UI Automation, reran the supported live PowerShell start/role launcher paths successfully, and refreshed the operator/docs/handoff evidence for QA retest. |
| 2026-05-28 local | qa | READY_FOR_QA -> QA_IN_PROGRESS | Started the post-rework QA retest, reviewed the refreshed DevOps handoff, launcher log, current keyboard-fallback implementation/config, and the interactive workstation evidence from the latest live run. |
| 2026-05-28 local | qa | QA_IN_PROGRESS -> QA_FAILED | Confirmed the previous exception no longer appears, but the live keyboard fallback still fails acceptance because it pasted `pickup new task and start to work` into IntelliJ Settings -> Plugins search while logging prompt-submission success. |
| 2026-05-28 local | qa | QA_FAILED -> RETURNED_TO_DEV | Returned the task to `devops` again with a new critical defect because the supported live PowerShell launcher still does not start the JetBrains AI session end-to-end on the documented workstation baseline. |
| 2026-05-28 local | devops | RETURNED_TO_DEV -> BLOCKED | Reworked the fallback to use exact GitHub Copilot action titles (`Copilot: Open Chat`, `New Chat Session`) plus focus-traversal retries and fail-safe verification. Dry-run routing and parser validation still pass, and the live launcher now reaches the deterministic Copilot actions without the earlier false-success path, but the current IntelliJ/Copilot widget still exposes no machine-verifiable prompt input after those actions run, so the task is blocked pending a stable provider/IDE automation surface or approved alternate automation approach. |
| 2026-05-28 12:15 local | sa | BLOCKED -> BLOCKED | Reviewed the QA + DevOps blocker evidence and the current workstation tool availability. Decided not to reroute another DevOps cycle with the same tool surface; the task remains blocked until the JetBrains/Copilot stack exposes a verifiable prompt target or a stronger desktop-automation dependency is explicitly approved and provisioned. |

