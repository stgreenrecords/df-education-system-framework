# Release Package Format and Compatibility Checker

- Task: `STORY-040`
- Status: Draft architecture baseline pending QA/PO review
- Owner role: `sa`
- Timestamp: 2026-05-25 12:39 local

## 1. Purpose

The framework vendor must deliver updates as **release packages**, not direct country-production deployments. Countries/ministry operators need a standard artifact they can inspect, validate, test, and approve locally. This document defines the minimum generic package contract and compatibility-checker concept needed for future release tooling.

## 2. Design principles

1. **Sovereign delivery** — the package supports country-controlled review and deployment.
2. **Provider-neutral** — package contents do not assume one cloud or infrastructure vendor.
3. **Country-neutral** — package logic does not fork by country; country differences are handled through configuration/template compatibility metadata.
4. **Versioned and immutable** — each release package represents one immutable framework release.
5. **Explainable compatibility** — update approval should rely on readable conflict/action reports, not opaque pass/fail behavior.

## 3. Canonical release package structure

A future release package should contain these logical sections:

```text
release-package/
├── manifest
├── release-notes
├── migrations/
│   ├── database
│   ├── configuration
│   └── operational
├── compatibility/
│   ├── metadata
│   └── rules
├── rollback-guidance
└── integrity/
    ├── checksums
    └── signatures-or-verification-data
```

The exact archive/container format can be chosen later, but the logical sections above are required for interoperability and country-side validation.

## 4. Required manifest fields

| Field | Required | Purpose |
|---|---|---|
| `frameworkVersion` | Yes | Target framework release version |
| `packageSchemaVersion` | Yes | Version of the release-package contract |
| `releaseDate` | Yes | Release publication date |
| `releaseChannel` | Yes | Publication stage such as `draft`, `candidate`, `approved-for-distribution` |
| `supportedUpgradeFrom` | Yes | Supported source version range/list |
| `migrationSetIds` | Yes | References to included migration bundles |
| `compatibilityMetadataRef` | Yes | Pointer to compatibility metadata/rules |
| `releaseNotesRef` | Yes | Pointer to release notes |
| `rollbackGuidanceRef` | Yes | Pointer to rollback/forward-only guidance |
| `integrityRef` | Recommended | Pointer to checksum/signature verification data |

## 5. Required package content

### 5.1 Release notes

Must include:

- release summary
- included features/fixes
- breaking changes
- operator-visible risks
- required pre-upgrade checks

### 5.2 Migration scripts

Must include or reference:

- database migrations
- configuration/data migrations where applicable
- operational migration steps that cannot be fully automated
- whether each migration is reversible, forward-only, or manual-recovery-required

### 5.3 Compatibility metadata

Must include:

- minimum/maximum supported current framework version
- required configuration schema/version markers
- required country-template schema/version markers
- affected domains/components
- breaking-change markers
- required manual actions
- automated migration availability
- rollback limitations

### 5.4 Rollback guidance

Must include:

- when rollback is supported
- when rollback is not safe
- required backup/restore preconditions
- manual recovery notes for irreversible changes

## 6. Compatibility checker concept

The compatibility checker is a generic rule-evaluation workflow.

### Inputs

- target release manifest
- target compatibility metadata/rules
- current deployed framework version
- current configuration schema/version markers
- current country-template version/schema markers
- later optional environment capability markers (database/runtime/infrastructure prerequisites)

### Outputs

A structured report containing:

- overall result: `PASS`, `WARN`, or `FAIL`
- detected conflicts
- required actions
- breaking-change summary
- affected configuration/template areas
- suggested migration steps
- blockers vs warnings

## 7. Compatibility rule categories

The first checker design should support these categories:

1. **Version path rules** — is the current release eligible to upgrade directly?
2. **Migration availability rules** — do all required migration bundles exist?
3. **Configuration compatibility rules** — will existing configuration entries conflict with changed framework rules?
4. **Country-template compatibility rules** — does the active country-template or template schema require upgrade/review first?
5. **Breaking-change rules** — does the release remove or alter behavior requiring country/operator action?
6. **Operational precondition rules** — are required backups, maintenance windows, or manual steps acknowledged?

## 8. Phase 1 checker approach

For Phase 1, prefer a **manifest + compatibility-rule model** rather than raw schema diffing alone.

Why:

- it is easier for country operators to review;
- it can cover configuration, template, and operational prerequisites together;
- it remains compatible with sovereign/offline review workflows;
- it avoids coupling the first design to one persistence strategy.

## 9. Example report shape

```text
Compatibility result: FAIL

Conflicts:
- Active framework version 1.2.0 is below the minimum supported upgrade path 1.3.x
- Country template schema 1 is incompatible with required schema 2

Required actions:
- Upgrade first to intermediary release 1.3.x
- Apply country-template migration pack CT-2 before retrying

Affected areas:
- configuration/inheritance rules
- country-template manifest version

Suggested migration steps:
1. Back up database and configuration export
2. Install intermediary package 1.3.x
3. Run CT-2 template migration
4. Re-run compatibility checker against target release
```

## 10. Governance flow

1. Vendor builds and publishes the release package.
2. Country receives the package.
3. Country verifies integrity/checksums.
4. Country runs the compatibility checker locally.
5. Country reviews the report and required actions.
6. Country tests in its own non-production environments.
7. Country approves and deploys, or rejects/postpones.

## 11. Guardrails

- No release package may require vendor control of country production infrastructure.
- No country-specific package variant is allowed.
- No secrets or country production data may be embedded in the package.
- If a compatibility condition cannot be expressed generically, it must trigger architecture review instead of country-specific tooling logic.

