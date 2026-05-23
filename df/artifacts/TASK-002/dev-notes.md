# Dev Notes - TASK-002

## Implementation summary

Implemented a strict repository-level rule that country templates must never change framework code, framework structure, or schemas. Country-specific differences are allowed only as configuration data and values.

The change was applied in:

- universal guidance (`AGENTS.md`)
- architecture guidance (`df/backlog/architecture-direction.md`)
- runtime decision tracking (`df/runtime/decisions.md`)
- the Poland template research artifact (`df/artifacts/SPIKE-001/poland-template-v1.md`)

## Files changed

- `AGENTS.md`: added strict universal rule forbidding country-specific code/structure/schema changes
- `df/backlog/architecture-direction.md`: added architecture invariant that country templates are data-only
- `df/runtime/decisions.md`: recorded the new accepted decision
- `df/artifacts/SPIKE-001/poland-template-v1.md`: added explicit implementation guardrail clarifying Poland is data only
- `df/runtime/board.md`: added TASK-002 and moved it to `READY_FOR_QA`
- `df/runtime/activity-log.md`: added task start/completion and state transitions
- `df/artifacts/TASK-002/task.md`: task definition and acceptance tracking
- `df/artifacts/TASK-002/decision-001-no-country-specific-code.md`: formal decision record
- `df/artifacts/TASK-002/handoffs.md`: dev-to-QA handoff

## Commands run

```text
git --no-pager status --short
Get-Date -Format 'yyyy-MM-dd HH:mm'
```

Result: PASS

## Unit tests

Not applicable. This task changes repository documentation and decision records only.

## Integration tests

Not applicable. No executable system behavior changed.

## Manual checks

- Verified the rule appears in global guidance and architecture guidance, not only in the Poland artifact.
- Verified the Poland artifact now explicitly states that it cannot influence framework code, structure, or schema.
- Verified the decision is documented and linked from runtime tracking.
- Verified the task artifact and runtime board/log reflect `READY_FOR_QA` handoff status.

## Risks and limitations

- This task prevents ambiguity at the documentation level, but future implementation work must still be reviewed against the rule.
- Existing brainstorm/archive notes may still mention Poland as a reference country; those historical notes were not rewritten in this task.

## Rollback notes

Low risk. All changes are Markdown-only and can be reverted by removing the added task and documentation entries.

## Ready for QA?

Yes

## Dev handoff

QA should verify that the new rule is explicit, consistent across the updated documents, and strong enough to prevent country-specific code/schema interpretation in future work.

