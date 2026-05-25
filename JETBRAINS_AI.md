# JetBrains AI Assistant Adapter for Dark Factory

JetBrains AI Assistant must follow `AGENTS.md` and all workflow files in `df/`.

## Start command behavior

When the user asks to start work:

1. Inspect the current project and `df/runtime/board.md`.
2. Inspect design and delivery subdashboards when design, development, or data work is involved.
3. Pick the highest-priority actionable task.
4. Use the role instructions in `df/roles/`.
5. Use IDE capabilities for code navigation, tests, inspections, run configurations, and screenshots when available.
6. Record every step in the runtime documentation.

## IDE-specific expectations

- Prefer project-aware inspections and tests before declaring work complete.
- Use `designer`, `backend-dev`, `frontend-dev`, `devops`, or `data-engineer` for design/delivery work; do not route new tasks to the retired generic `dev` role.
- Do not implement UI as `frontend-dev` unless a designer package exists or SA has documented the work as non-visual.
- Preserve current editor/user changes.
- If a task needs UI verification, PO must capture screenshots or document why screenshots are impossible in this environment.
- If the IDE cannot execute a step, document the limitation and the alternative evidence used.

