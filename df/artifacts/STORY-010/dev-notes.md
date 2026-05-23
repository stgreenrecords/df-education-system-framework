# Dev Notes - STORY-010

## Implementation summary

Reworked the Maven scaffold after QA/human rejection so the repository now has one root Maven parent with three independent project areas:

- `backend/`: Spring Boot backend parent containing the required modules: `common`, `identity-access`, `organization`, `school-pack`, `attendance`, `gradebook`, `meal-catering`, and `platform-core`.
- `frontend/`: independent Maven project scaffold for future frontend work.
- `devops/`: independent Maven project scaffold for deployment/infrastructure work.

The backend Spring Boot application and sample integration test were preserved under `backend/platform-core`.

## Files changed

- `pom.xml`: root parent/aggregator now unites `backend`, `frontend`, and `devops`.
- `backend/pom.xml`: independent backend Maven parent.
- `backend/common/`, `backend/identity-access/`, `backend/organization/`, `backend/school-pack/`, `backend/attendance/`, `backend/gradebook/`, `backend/meal-catering/`, `backend/platform-core/`: moved existing backend modules under the backend project and updated child parent POMs.
- `frontend/pom.xml`: independent frontend Maven project scaffold.
- `devops/pom.xml`: independent DevOps Maven project scaffold.
- `README.md`: documented backend-only, frontend-only, DevOps-only, and all-project build commands.
- `df/artifacts/STORY-010/defects.md`: recorded root cause and fix evidence.
- `df/artifacts/STORY-010/dev-notes.md`: updated Dev evidence.
- `df/artifacts/STORY-010/handoffs.md`: added Dev-to-QA rework handoff.
- `df/artifacts/STORY-010/task.md`, `df/runtime/board.md`, `df/runtime/activity-log.md`: updated runtime state and handoff evidence.

## Root cause

The first scaffold treated the repository root as a backend-only Maven reactor and placed backend modules directly under the root. That satisfied the original module acceptance criteria but did not satisfy the later human requirement for separate backend, frontend, and DevOps Maven project boundaries under one parent.

## Commands run

```text
git --no-pager status --short
```

Result: PASS. Confirmed existing dirty/untracked repository state before rework.

```text
rg --files -g "pom.xml" -g "*.java" -g "*.properties" -g "*.xml" -g "*.md"
```

Result: PASS. Confirmed current Maven and Java scaffold before moving backend modules.

```text
.\mvnw.cmd -pl backend -am clean verify
```

Result: PASS, but only validated the root and backend aggregator POMs. This was not enough to prove backend module verification, so the stronger `-f backend/pom.xml` check below was run.

```text
.\mvnw.cmd -f backend/pom.xml clean verify
```

Result: FAIL on first run due to the known local Java/Maven trust-store issue: `PKIX path building failed`.

```text
$env:MAVEN_OPTS='-Djavax.net.ssl.trustStore=' + (Join-Path $env:TEMP 'df-maven-truststore.jks') + ' -Djavax.net.ssl.trustStorePassword=changeit'; .\mvnw.cmd -f backend/pom.xml clean verify
```

Result: PASS. Backend reactor succeeded for 9 projects. Integration tests: 1 run, 0 failures, 0 errors, 0 skipped.

```text
$env:MAVEN_OPTS='-Djavax.net.ssl.trustStore=' + (Join-Path $env:TEMP 'df-maven-truststore.jks') + ' -Djavax.net.ssl.trustStorePassword=changeit'; .\mvnw.cmd -f frontend/pom.xml clean verify
```

Result: PASS. Frontend project scaffold verified successfully.

```text
$env:MAVEN_OPTS='-Djavax.net.ssl.trustStore=' + (Join-Path $env:TEMP 'df-maven-truststore.jks') + ' -Djavax.net.ssl.trustStorePassword=changeit'; .\mvnw.cmd -f devops/pom.xml clean verify
```

Result: PASS. DevOps project scaffold verified successfully.

```text
$env:MAVEN_OPTS='-Djavax.net.ssl.trustStore=' + (Join-Path $env:TEMP 'df-maven-truststore.jks') + ' -Djavax.net.ssl.trustStorePassword=changeit'; .\mvnw.cmd clean verify
```

Result: PASS. Full parent reactor succeeded for 12 projects: root, backend parent, 8 backend modules, frontend, and DevOps. Integration tests: 1 run, 0 failures, 0 errors, 0 skipped.

## Unit tests

No module-level unit tests were added because the implementation is structural scaffolding with marker classes only.

## Integration tests

`backend/platform-core/src/test/java/com/darkfactory/education/platform/EducationSystemApplicationIT.java` starts the Spring context through `@SpringBootTest`.

Result: PASS through Maven Failsafe during backend-only and full parent verification.

## Manual checks

- Verified root parent modules are `backend`, `frontend`, and `devops`.
- Verified backend project contains all required backend modules.
- Verified frontend and DevOps can be verified independently from their own POMs.
- Verified the full parent build includes all 12 reactor projects.
- Verified no country-specific or language-specific code was added.
- Verified database setup was not introduced; this remains scoped to `STORY-011`.

## Risks and limitations

- This workstation's Java/Maven trust store cannot validate Maven Central certificates without a temporary validation trust store. The project build itself passes once dependency resolution can use a valid trust path.
- Spring Boot test dependencies emit a Mockito dynamic-agent warning under the current JDK. This is informational for now and does not fail the build.
- `frontend` and `devops` are minimal Maven scaffolds only. Actual frontend tooling and deployment packaging remain future stories.

## Rollback notes

Revert the Maven scaffold reorganization and restore the previous root-level backend modules if QA rejects the separation. No database, migration, infrastructure, or runtime data changes were introduced.

## Ready for QA?

Yes.

## Dev handoff

QA should verify the parent/child Maven structure, the four documented build paths, the preserved backend module list, the Spring Boot integration test, and that no country/language-specific code or database scope leaked into this story.
