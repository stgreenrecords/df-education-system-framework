# Solution Design - TASK-012

## Summary

Implement a Windows-first Dark Factory workstation automation layer where `call-start-factory.bash` launches the first agent prompt plus a lightweight IntelliJ/JetBrains AI acknowledgement watcher, and the other root `call-*.bash` scripts start new role-specific conversations using repository-owned prompts.

## Context

The repository already contains empty launcher placeholders at the root:

- `call-start-factory.bash`
- `call-dev-backend.bash`
- `call-dev-devops.bash`
- `call-dev-frontend.bash`
- `call-data-engineer.bash`
- `call-qa.bash`
- `call-po.bash`

The user reports four recurring pain points in the current Dark Factory workflow:

1. manually typing start prompts for each role/session;
2. manually approving file changes;
3. manually clicking add-to-git prompts;
4. manually pressing continue when the AI agent hits its limit.

The requested target behavior is that the user only runs `call-start-factory.bash`, which kicks off the initial `pickup new task and start to work` prompt and keeps the workstation moving by auto-clicking recurring IDE buttons such as `Keep All`, `Add to Git`, and `Continue`. Role completion should still preserve the Dark Factory role boundary, but the next step should be explicit through matching role launcher scripts.

## Requirements and acceptance criteria

- `call-start-factory.bash` starts the first session with prompt `pickup new task and start to work`
- role-specific `call-*.bash` scripts start new conversations with the appropriate role prompt
- handoff naming stays aligned with the launcher names so the next role/action is operationally obvious
- recurring IntelliJ IDEA / JetBrains AI acknowledgement buttons (`Keep All`, `Add to Git`, `Continue`) are watched and clicked automatically
- documentation explains supported environment assumptions, watcher lifecycle, failure recovery, and fragility limits

## Proposed solution

### 1. Windows-first launcher architecture

Use the root `.bash` files as thin entrypoints only. Each Bash launcher should delegate to a shared Windows-native helper, for example under a DevOps-owned automation folder such as:

- `devops/automation/jetbrains-agent-orchestrator.ps1`
- optionally `devops/automation/jetbrains-agent-config.json`

This keeps the user-facing entrypoint stable (`call-*.bash`) while allowing reliable PowerShell/.NET desktop automation on Windows.

### 2. Shared orchestration helper

The shared PowerShell helper should support modes such as:

- `start-factory`
- `role-backend-dev`
- `role-devops`
- `role-frontend-dev`
- `role-data-engineer`
- `role-qa`
- `role-po`
- optional `watch-only`

Responsibilities:

1. find or focus the IntelliJ IDEA / JetBrains AI window;
2. insert/send the prompt text for the selected mode;
3. submit the prompt to start a new conversation/session;
4. start or maintain a watcher loop that scans for known button labels and clicks them safely;
5. log actions to a local temp/log file for troubleshooting.

### 3. Prompt model

Prompts should be repository-owned constants in the helper or a small config file.

Minimum required prompts:

- `call-start-factory.bash` -> `pickup new task and start to work`
- each role launcher -> a role-scoped prompt such as `Start a new session as backend-dev and continue the Dark Factory loop for the highest-priority backend-dev task.`

The role prompts should be explicit enough to start the right role lane without changing the Dark Factory single-role-per-session rule.

### 4. IDE acknowledgement watcher

The watcher should use Windows desktop UI automation against button text / automation-name matches.

Initial watched actions:

- `Keep All`
- `Add to Git`
- `Continue`

Safety rules for implementation:

- match only explicit allowed button names, not generic buttons like `OK` or `Yes`
- restrict search to IntelliJ / JetBrains owned windows/dialogs when feasible
- debounce repeated clicks so one dialog is not clicked multiple times in a tight loop
- log every auto-click with timestamp and button text
- allow manual stop/disable if automation misbehaves

### 5. Delivery shape

This remains one `devops` lane task because the work is workstation automation, shell entrypoints, IDE interaction scripting, and operational documentation. No backend/frontend/data lane split is needed unless implementation later proves that editor automation requires a separate repository/tooling package.

## Files/components likely affected

- `call-start-factory.bash`
- `call-dev-backend.bash`
- `call-dev-devops.bash`
- `call-dev-frontend.bash`
- `call-data-engineer.bash`
- `call-qa.bash`
- `call-po.bash`
- new DevOps automation helper(s), likely under `devops/automation/**`
- `README.md` or `docs/` automation guidance
- `df/artifacts/TASK-012/devops/*`
- `df/runtime/board.md`
- `df/runtime/devops-board.md`
- `df/runtime/activity-log.md`
- `df/runtime/decisions.md`

## Data/API contract changes

- No business data model changes.
- No backend or frontend API changes.
- New operational contract only: root launcher scripts become supported workstation automation entrypoints for Dark Factory role sessions.

## Security/privacy considerations

- Do not automate broad or destructive button clicks outside the approved allowlist.
- Do not capture or log secrets from IntelliJ dialogs or agent content.
- Keep any watcher logs local and redact sensitive prompt/body content if logged.
- Prefer explicit window-title/process checks so the watcher only interacts with IntelliJ / JetBrains AI surfaces.

## Test strategy

DevOps should validate using the strongest safe path available:

- syntax validation for Bash and PowerShell scripts
- unit-like validation for prompt/config resolution where feasible
- dry-run or mock mode for the UI watcher if implemented
- manual verification on the target Windows workstation that:
  - `call-start-factory.bash` starts the initial prompt
  - each role launcher sends the intended role prompt
  - the watcher clicks `Keep All`, `Add to Git`, and `Continue` when those dialogs appear
  - false-positive clicks do not occur on unrelated windows
- document exact IntelliJ/JetBrains version and limitations discovered during validation

## Risks and mitigations

- **Risk:** UI selectors are fragile across IDE versions.
  - **Mitigation:** keep selectors narrow, Windows-first, text-based, and documented as environment-specific.
- **Risk:** no stable public JetBrains AI conversation-start API exists.
  - **Mitigation:** centralize the desktop automation in one helper instead of scattering the logic across many scripts.
- **Risk:** automation may click the wrong dialog.
  - **Mitigation:** allowlist only the three requested buttons and scope searches to IntelliJ windows/processes.
- **Risk:** `.bash` entrypoints may be used from Git Bash while automation needs native Windows APIs.
  - **Mitigation:** use Bash only as a thin wrapper that calls PowerShell.

## Rollback plan

- Revert the new launcher/helper files and documentation if the automation proves too brittle.
- Leave the existing manual Dark Factory workflow available as fallback through documented manual prompts.
- If only the watcher is unstable, disable watcher startup while keeping prompt-launch scripts intact.

## Open questions

- None blocking for the initial design. If `devops` discovers that IntelliJ / JetBrains AI cannot be reliably targeted from repository-owned scripts on this workstation, return the task with concrete tooling/selector blocker evidence.

## 2026-05-28 SA blocker review addendum

### Review outcome

After two DevOps rework cycles and two QA cycles, the current Windows + IntelliJ IDEA 2025.3.4 + GitHub Copilot baseline still does not provide a positively verifiable prompt target for the live launcher path.

### Evidence reviewed

- `df/artifacts/TASK-012/qa-report.md`
- `df/artifacts/TASK-012/defects.md`
- `df/artifacts/TASK-012/devops/dev-notes.md`
- `.dark-factory/automation/jetbrains-agent-orchestrator.log`
- local workstation tool check showing no installed `AutoHotkey`, `AutoHotkey64`, or `WinAppDriver` executable on the current baseline

### Architecture decision impact

- Keep the existing launcher and watcher architecture as the best safe partial solution already in the repository.
- Do **not** send the task back to `devops` for another iteration with the same UIAutomation + keyboard-fallback constraints.
- Keep the task `BLOCKED` until either the IDE/provider stack exposes a verifiable prompt control or a stronger desktop-automation stack is explicitly approved and provisioned.

### Unblock options

1. **Preferred:** update/reconfigure the JetBrains/Copilot stack so the chat input becomes machine-verifiable on the documented workstation.
2. **Alternate:** approve and provision a stronger desktop-automation technology plus its repository/runtime support contract, then reroute the task through SA before DevOps resumes.

### Rejected options

- Accepting best-effort prompt submission without verification: rejected because QA already proved it can target IntelliJ Settings instead of the AI chat.
- Continuing blind fallback experiments on the current workstation/tooling surface: rejected because they are now high-effort/low-confidence rework.

