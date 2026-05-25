# Claude Code Adapter for Dark Factory

Claude Code must use `AGENTS.md` as the universal entrypoint and follow all files under `df/`.

## Startup behavior

When the user says any equivalent of `start work`, immediately:

1. Read `AGENTS.md`.
2. Read `df/00-start-here.md`.
3. Inspect `df/runtime/board.md`.
4. Inspect design and delivery subdashboards when design, development, or data work is involved.
5. Select the highest-priority actionable task.
6. Act as the responsible role from `df/roles/`.
7. Continue the Dark Factory loop until no actionable task remains or a blocker is documented.

## Claude-specific notes

- Do not replace the Dark Factory workflow with ad-hoc coding.
- **One session = one role. Do NOT switch roles within a session. When the current role's work is done, document state and stop.**
- Use `designer`, `backend-dev`, `frontend-dev`, `devops`, or `data-engineer` for design/delivery work; do not route new tasks to the retired generic `dev` role.
- Do not implement UI as `frontend-dev` unless a designer package exists or SA has documented the work as non-visual.
- Keep a written handoff before ending the session.
- Use available tools for file edits, commands, tests, and screenshots if provided by the environment.
- If tool access is missing, document the limitation in `df/runtime/activity-log.md` and continue with the strongest available evidence.

