# Decision Record - DECISION-011

## Title

Translation foundation stays in `backend/platform-core` with a default namespace model and a local audit bridge

## Status

Accepted

## Date

2026-05-24

## Context

`STORY-220` was originally designed before the Spring Boot/PostgreSQL/Flyway application substrate existed. That substrate is now accepted via `STORY-010` and `STORY-011`. The story still needs a concrete architecture decision for where the first translation-storage slice lives, how optional namespaces avoid PostgreSQL uniqueness pitfalls, and how audit requirements are met before the platform-wide audit story is implemented.

## Decision

- Implement the first translation-storage slice inside `backend/platform-core` under a dedicated translation package boundary rather than introducing a new Maven module now.
- Model “optional namespace” at the API/service level but persist `namespace` as non-null with a generic default such as `default` to preserve deterministic uniqueness.
- Satisfy the story’s audit requirement with a minimal local `translation_audit` persistence mechanism that records actor, old value, new value, and timestamp, while keeping the structure compatible with later migration or bridging to the broader audit subsystem.
- Expose only the smallest REST surface needed to prove lookup/update behavior and cache invalidation; defer full translation-management API breadth to `STORY-222`.

## Consequences

- Backend delivery can proceed now in the active `backend-dev` lane without waiting for new module extraction or the full audit platform.
- PostgreSQL uniqueness for `(translation_key, language_code, namespace)` remains simple and reliable.
- A future audit story may consolidate or migrate translation audit records, so the local audit contract must stay generic and well documented.
- `STORY-222` can extend the same translation API area without forcing a redesign of the storage and cache foundation.

## Alternatives Considered

- Create a new dedicated Maven module now: rejected because the codebase is still in a minimal foundation stage and a new module would add structure overhead before enough code exists to justify it.
- Keep `namespace` nullable and solve uniqueness with expression or partial indexes: rejected because it complicates application behavior and test expectations for the MVP.
- Defer audit until `STORY-013`: rejected because `STORY-220` explicitly requires an audit record when translations change.

## Related

- Task: `STORY-220`
- Design: `df/artifacts/STORY-220/solution-design.md`
- Related prior decisions: `DECISION-002`, `DECISION-010`

