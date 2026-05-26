# GitHub Copilot Instructions for Dark Factory

Follow the universal Dark Factory instructions in `AGENTS.md` and the detailed workflow in `df/`.

## Required behavior

- Treat `df/00-start-here.md` as the boot sequence.
- Use role files in `df/roles/` for behavior and deliverables.
- Update runtime evidence in `df/runtime/` and task artifacts in `df/artifacts/{task-id}/`.
- Do not finish a task unless `qa` has passed it and `po` has accepted it.
- If work is rejected, return it to the responsible lane with evidence and defects.
- UI-facing frontend implementation requires a designer package first; country data work belongs to `data-engineer` and must keep real place/school/subject names source-backed while people and grade records stay synthetic.
- For designer work, keep design docs in `df/artifacts/{task-id}/design/`, but store design asset files such as HTML, PNG, SVG, PDF, and similar outputs under the root `design/{page-slug}/` structure, with a dedicated folder for each page and a globally unique descriptive page slug.

## Pull request behavior

When creating or reviewing pull requests:

- Link the PR to the task id.
- Include implementation summary, test evidence, risks, and rollback notes.
- Do not merge if tests fail, requirements are unclear, or PO acceptance is missing unless a human explicitly overrides.

