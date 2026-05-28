# Decision Record - DECISION-025

- Date: 2026-05-26
- Status: Accepted
- Owner role: SA
- Related task: TASK-012

## Context

The repository already contains root-level Dark Factory launcher placeholders (`call-start-factory.bash`, `call-dev-backend.bash`, `call-dev-devops.bash`, `call-dev-frontend.bash`, `call-data-engineer.bash`, `call-qa.bash`, and `call-po.bash`), but they do not yet automate JetBrains AI session startup or recurring IDE acknowledgement actions.

The user explicitly requested that running `call-start-factory.bash` should start the factory with the prompt `pickup new task and start to work`, that role-specific scripts should start the next role session with appropriate prompts, and that the workstation should automatically click recurring IntelliJ/JetBrains buttons such as `Keep All`, `Add to Git`, and `Continue`.

## Decision

For `TASK-012`, the first automation path will be a Windows-first launcher architecture in which the root `.bash` files act as thin wrappers around a shared DevOps-owned PowerShell desktop-automation helper.

The automation will:

1. start the first Dark Factory session from `call-start-factory.bash` with the prompt `pickup new task and start to work`;
2. provide role-specific prompt launchers through the existing root `call-*.bash` files;
3. preserve the Dark Factory one-role-per-session rule by keeping role handoff explicit through separate scripts rather than combining roles;
4. watch IntelliJ IDEA / JetBrains AI dialogs for the explicit allowlist buttons `Keep All`, `Add to Git`, and `Continue`; and
5. treat the first implementation as environment-specific workstation automation rather than a guaranteed cross-platform IDE integration.

## Consequences

- `devops` owns the implementation because the scope is operational scripting, workstation automation, and repository tooling.
- The root launcher files become stable operator entrypoints while the heavy automation logic can live in PowerShell helpers better suited to Windows UI automation.
- Documentation must clearly state the first supported environment baseline and the fragility/recovery limits of IDE UI automation.
- If IntelliJ/JetBrains targeting proves too brittle, the watcher may need to be disabled independently from the prompt-launch scripts.

## Alternatives considered

- Keep the process fully manual: rejected because the user explicitly requested repository-owned automation for prompts and recurring UI acknowledgements.
- Implement everything as pure Bash: rejected because the current operator environment is Windows and reliable IntelliJ desktop automation is better served by PowerShell/.NET than Bash alone.
- Route the work to another lane: rejected because no backend/frontend/data behavior changes are requested; this is workstation tooling and automation.
- Automatically chain roles in one session without role-specific scripts: rejected because it would violate the Dark Factory single-role-per-session rule.

## Evidence

- `df/artifacts/TASK-012/task.md`
- `df/artifacts/TASK-012/solution-design.md`
- `call-start-factory.bash`
- `call-dev-backend.bash`
- `call-dev-devops.bash`
- `call-dev-frontend.bash`
- `call-data-engineer.bash`
- `call-qa.bash`
- `call-po.bash`
- `JETBRAINS_AI.md`

## Follow-up actions

- Route `TASK-012` to `devops` in `READY_FOR_DEV`.
- Keep the user-facing workflow centered on `call-start-factory.bash` plus role-specific `call-*.bash` launchers.
- Restrict automatic clicks to the explicit allowlist and document how to disable/recover the watcher if needed.
- Reassess broader portability only after the Windows-first baseline is working.

