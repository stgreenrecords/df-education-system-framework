# Dark Factory Risks and Blockers

| ID | Risk/blocker | Severity | Owner | Status | Mitigation |
|---|---|---|---|---|---|
| RISK-001 | Poland education research may be incomplete | High | PO/SA | Open | Use multiple official sources; validate with domain expert |
| RISK-004 | Scope creep across 20 epics may delay MVP | Critical | PO | Open | Strict phase boundaries; PO acceptance gates |
| RISK-007 | Data sovereignty vs centralized AI services conflict | High | SA | Open | Design AI as pluggable; country chooses provider |
| RISK-010 | DB migrations must be robust for country-operated deployments | High | SA/Dev | Open | Automated Flyway migrations; rollback scripts; compatibility checker |
| RISK-011 | Poland higher-education grading may be institution-specific rather than nationally uniform | Medium | SA/Dev/PO | Open | Model higher-education grading as institution-level configuration; validate with pilot universities before implementation |
| RISK-012 | Poland school-year dates and winter breaks vary by academic year and voivodeship | Medium | PO/QA | Open | Treat school calendar as versioned yearly data and validate against annual ministry notices before coding |
| RISK-013 | Translation cache invalidation may need a distributed provider in multi-node deployments | Medium | SA/Dev | Open | Implement behind Spring Cache-compatible abstraction; start local for MVP and allow Redis/provider swap through configuration |
| BLOCKER-014 | STORY-220 cannot be implemented because database migration framework and PostgreSQL configuration are not yet available in the repository | Critical | human/factory | Blocked | Complete/promote `STORY-011`, or provide the existing PostgreSQL/migration substrate before resuming STORY-220 Dev work |
| RISK-015 | Deferring containerization until after major feature work would create deployment rework in configuration, secrets, health checks, filesystem assumptions, and cloud portability | High | SA/devops | Open | Treat containerization as Phase 1: keep STORY-010/011 container-ready, then implement STORY-022 Podman-compatible OCI baseline and STORY-023 Kubernetes/IaC baseline before deep feature work |
| RISK-016 | Local Java/Maven trust store may fail Maven Central TLS validation on some developer machines | Medium | delivery lanes/QA | Open | QA should rerun `.\mvnw.cmd clean verify` in a normally trusted environment; affected machines need their Java trust store fixed or a documented local trust-store workaround |
| RISK-017 | Frontend and DevOps Maven projects are currently structural scaffolds only | Low | SA/frontend-dev/devops | Open | Future frontend and deployment stories must add actual tooling, packaging, and deployment behavior inside the existing independent project boundaries |
| RISK-018 | Existing active tasks may still refer to the retired generic `dev` owner after the lane split | Medium | SA/QA | Open | New work must use `designer`, `backend-dev`, `frontend-dev`, `devops`, or `data-engineer`; existing active tasks should be completed or explicitly migrated when resumed |
| RISK-019 | Parallel lane tasks may still conflict on root build, CI, runtime, or shared acceptance-criteria files | High | SA/delivery lanes | Open | SA must split scopes, document affected files, and sequence work when shared files or environments are involved |
| RISK-020 | Website, Android, and iOS frontend projects may drift or create hidden source coupling | Medium | SA/frontend-dev/QA | Open | Keep projects independently buildable; share only through APIs, generated clients, design tokens, or explicit shared packages approved by SA |
| RISK-021 | Mobile frontend work may distract from the website-first frontend foundation if promoted too early | Medium | PO/SA/frontend-dev | Open | Keep Android/iOS as last-priority stories unless PO/SA documents a promotion reason |
| RISK-022 | Springdoc OpenAPI dependency compatibility with the current Spring Boot 4 version must be proven locally | Medium | backend-dev/QA | Open | Use Springdoc 3.x Spring Boot 4 documentation as implementation reference and require integration tests for `/api-docs` and `/swagger-ui` |
| RISK-023 | UI-facing frontend work may be attempted without designer input | Medium | designer/frontend-dev/QA | Open | Frontend-dev must block missing design packages; QA must verify design evidence before passing UI work |
| RISK-024 | Country seed/test datasets may mix true public facts with real personal records or unsourced values | High | data-engineer/QA | Open | Require source maps for real city/district/school/subject names and synthetic teacher/student/grade records only |

See `df/backlog/risks-and-assumptions.md` for the full risk register.
