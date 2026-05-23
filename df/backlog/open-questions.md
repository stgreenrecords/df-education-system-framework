# Open Questions — Education System Framework

## Questions for PO (Product Owner)

| ID | Question | Impact | Status |
|---|---|---|---|
| PO-Q01 | Should teachers have full visibility into student AI conversation history, or only usage statistics? | Affects AI module privacy design and parent trust | Open |
| PO-Q02 | What happens when a country wants to use a grading scale not yet supported? Can they define custom scales, or must it be added to the framework? | Affects configuration engine extensibility | Open |
| PO-Q03 | Should parents be able to communicate with teachers through the platform (messaging), or is that out of scope? | Scope decision for MVP and beyond | Open |
| PO-Q04 | What level of offline support is required for the application? Should teachers be able to record attendance/grades offline? | Architecture impact (PWA, sync) | Open |
| PO-Q05 | Should the system support multiple children per parent across different institutions? | Data model impact for parent accounts | Open — Recommendation: Yes, support by default |
| PO-Q06 | Is there a maximum class size or student count per institution for MVP? | Performance and testing scope | Open |
| PO-Q07 | Should homework support file attachments (student uploads) in MVP? | Storage architecture decision | Open |
| PO-Q08 | What is the expected catering billing granularity? Per-day or per-meal? | Billing logic complexity | Open — Recommendation: per-day initially |
| PO-Q09 | Should the system track extracurricular activities (clubs, sports) in MVP? | Scope boundary | Open — Recommendation: No, post-MVP |
| PO-Q10 | What approval workflow is needed for grade corrections? Teacher-only or requires admin approval? | Workflow complexity | Open — Recommendation: teacher + audit trail initially |

## Questions for SA (Solution Architect)

| ID | Question | Impact | Status |
|---|---|---|---|
| SA-Q01 | Schema isolation: schema-per-module or schema-per-tenant or single schema with tenant column? | Database architecture, performance, isolation | Open |
| SA-Q02 | Should the configuration engine use JSON/JSONB in PostgreSQL or a dedicated configuration table structure? | Flexibility vs query performance | Open |
| SA-Q03 | What AI provider should be used? OpenAI API, Azure OpenAI, self-hosted LLM, or pluggable interface? | Cost, latency, data sovereignty | Open |
| SA-Q04 | Should event sourcing be used for audit-critical operations or is a simple audit table sufficient for MVP? | Complexity, storage, rebuild capability | Open — Recommendation: audit table for MVP |
| SA-Q05 | How should file storage be handled (homework attachments, documents)? Local filesystem, S3-compatible, country-operated object store? | Infrastructure dependency | Open |
| SA-Q06 | Should the API gateway be a separate service (Kong, Envoy) or integrated into Spring Boot (Spring Cloud Gateway)? | Deployment complexity vs features | Open — Recommendation: Spring Cloud Gateway for MVP |
| SA-Q07 | What session/token strategy? JWT with refresh tokens, opaque tokens, or server-side sessions? | Security, scalability, revocation | Open — Recommendation: JWT with refresh tokens |
| SA-Q08 | Should the system support multiple database instances per country (e.g., per-region sharding), or single DB per country deployment? | Scalability, operational complexity | Open — Recommendation: single DB per country for MVP |
| SA-Q09 | How should the release compatibility checker compare country config against new release requirements? Schema diffing, manifest comparison, or rule engine? | Release management implementation | Open |
| SA-Q10 | Should background job processing (grade calculation, billing, reports) use Spring Batch, Quartz, or a simpler approach? | Reliability, monitoring, complexity | Open |

## Questions for Dev (Development)

| ID | Question | Impact | Status |
|---|---|---|---|
| DEV-Q01 | What Java version to target? Java 21 (latest LTS) or Java 17 (wider compatibility)? | Build, dependencies, features available | Open — Recommendation: Java 21 |
| DEV-Q02 | Gradle or Maven for the build system? | Developer experience, plugin ecosystem | Open |
| DEV-Q03 | What testing framework beyond JUnit 5? Testcontainers for integration tests? | Test infrastructure, CI requirements | Open — Recommendation: Testcontainers + JUnit 5 |
| DEV-Q04 | Should API versioning use URL path (/v1/), header, or content negotiation? | Client compatibility, routing | Open — Recommendation: URL path /api/v1/ |
| DEV-Q05 | What database migration tool? Flyway or Liquibase? | Migration management, XML vs SQL | Open — Recommendation: Flyway (SQL-based) |
| DEV-Q06 | Should DTOs use Java records, Lombok, or manual POJO? | Code style, boilerplate, IDE support | Open — Recommendation: Java records |
| DEV-Q07 | What code quality tools? SonarQube, Checkstyle, SpotBugs, Error Prone? | CI pipeline, code standards | Open |
| DEV-Q08 | Should the project use Spring Data JPA, Spring Data JDBC, or JOOQ for data access? | Query control, performance, simplicity | Open |
| DEV-Q09 | Mono-repo or multi-repo for modules? | Build speed, versioning, team coordination | Open — Recommendation: mono-repo for MVP |
| DEV-Q10 | What logging/observability stack? SLF4J + Logback + Micrometer + OpenTelemetry? | Debugging, monitoring, tracing | Open — Recommendation: Yes, all of these |

## Questions for QA (Quality Assurance)

| ID | Question | Impact | Status |
|---|---|---|---|
| QA-Q01 | What test environments are needed? Local + CI or also a shared staging? | Infrastructure cost, test reliability | Open |
| QA-Q02 | What is the minimum test coverage target for MVP? | Quality gate for release | Open — Recommendation: 80% line coverage for core modules |
| QA-Q03 | Should security/penetration testing be automated in CI or periodic manual? | Security assurance, cost | Open — Recommendation: automated SAST + periodic manual pentest |
| QA-Q04 | What browser/platform matrix for UI testing (if frontend is built)? | Test scope | Open — Recommendation: API-only testing for MVP |
| QA-Q05 | Should load/performance testing be part of MVP or post-MVP? | Performance assurance | Open — Recommendation: basic load test for MVP |
| QA-Q06 | How should test data be managed? Fixtures, factories, or shared seed data? | Test reproducibility | Open |
| QA-Q07 | Should the QA strategy include contract testing (Pact) for API consumers? | API stability assurance | Open — Recommendation: Yes, once frontend/mobile clients exist |
| QA-Q08 | What is the acceptable defect density for MVP release? | Release quality gate | Open |
| QA-Q09 | Should accessibility testing (WCAG) be included if a reference frontend is built? | Compliance, inclusivity | Open — Recommendation: Yes, WCAG 2.1 AA |
| QA-Q10 | Should data integrity tests verify configuration inheritance correctness across all hierarchy levels? | Configuration engine reliability | Open — Recommendation: Yes, critical path |

