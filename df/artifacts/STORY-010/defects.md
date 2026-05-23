# Defects - STORY-010

## Defect 1: Project scaffold does not separate backend, frontend, and DevOps Maven projects

- Found by: Human / QA
- Timestamp: 2026-05-23 11:02 local
- Severity: High
- Status: Closed - QA retest passed
- Environment: Local workspace, current `STORY-010` Maven scaffold

### Steps to reproduce

1. Inspect the current repository root.
2. Open the root `pom.xml`.
3. Compare the current structure with the rework requirement: three separate teams must be able to work on backend, frontend, and DevOps as independent Maven projects under one parent.
4. Check whether the current project can build/deploy only backend, only frontend, only DevOps, or all together from the parent.

### Expected result

The repository should have a clear parent Maven project that unites three independent Maven project areas:

- `backend`: backend developers own the Spring Boot backend and its backend modules.
- `frontend`: frontend developers own the frontend project independently.
- `devops`: DevOps owns deployment/infrastructure packaging independently.

The parent should support build/deployment paths for:

- backend only;
- frontend only;
- DevOps only;
- all projects together.

The backend project should preserve the accepted backend modules from `STORY-010`: `platform-core`, `identity-access`, `organization`, `school-pack`, `attendance`, `gradebook`, `meal-catering`, and `common`.

### Actual result

The current implementation places one Maven parent at repository root and all accepted modules directly below it. It is backend-oriented and does not create separate `backend`, `frontend`, and `devops` Maven project boundaries. There is no independent frontend Maven project and no independent DevOps Maven project.

### Evidence

- `pom.xml`
- `common/pom.xml`
- `identity-access/pom.xml`
- `organization/pom.xml`
- `school-pack/pom.xml`
- `attendance/pom.xml`
- `gradebook/pom.xml`
- `meal-catering/pom.xml`
- `platform-core/pom.xml`
- Human rework request on 2026-05-23: "I want to have 3 independent maven project united by one parent project with possibility to deploy 1) only backend 2) only frontent 3)only devops 4)all together."

### Root cause

The first implementation made the repository root a backend-only Maven reactor and placed backend modules directly under the root. It satisfied the initial module list but did not provide explicit independent backend, frontend, and DevOps Maven project boundaries.

### Fix evidence

- Root `pom.xml` now aggregates `backend`, `frontend`, and `devops`.
- `backend/pom.xml` is an independent backend parent containing `common`, `identity-access`, `organization`, `school-pack`, `attendance`, `gradebook`, `meal-catering`, and `platform-core`.
- `frontend/pom.xml` is an independent frontend Maven project scaffold.
- `devops/pom.xml` is an independent DevOps Maven project scaffold.
- `README.md` documents backend-only, frontend-only, DevOps-only, and all-project build commands.
- Dev validation passed for:
  - `.\mvnw.cmd -f backend/pom.xml clean verify`
  - `.\mvnw.cmd -f frontend/pom.xml clean verify`
  - `.\mvnw.cmd -f devops/pom.xml clean verify`
  - `.\mvnw.cmd clean verify`

### Retest evidence

- Retested by: QA
- Timestamp: 2026-05-23 11:51 local
- Result: PASS
- Evidence:
  - Root `pom.xml` aggregates `backend`, `frontend`, and `devops`.
  - `backend/pom.xml` aggregates `common`, `identity-access`, `organization`, `school-pack`, `attendance`, `gradebook`, `meal-catering`, and `platform-core`.
  - `.\mvnw.cmd -f backend/pom.xml clean verify` passed with 9 backend projects successful and 1 integration test run, 0 failures, 0 errors, 0 skipped.
  - `.\mvnw.cmd -f frontend/pom.xml clean verify` passed.
  - `.\mvnw.cmd -f devops/pom.xml clean verify` passed.
  - `.\mvnw.cmd clean verify` passed with all 12 projects successful and 1 integration test run, 0 failures, 0 errors, 0 skipped.
