# Dark Factory Risks and Blockers

| ID | Risk/blocker | Severity | Owner | Status | Mitigation |
|---|---|---|---|---|---|
| RISK-001 | Poland education research may be incomplete | High | PO/SA | Open | Use multiple official sources; validate with domain expert |
| RISK-004 | Scope creep across 20 epics may delay MVP | Critical | PO | Open | Strict phase boundaries; PO acceptance gates |
| RISK-007 | Data sovereignty vs centralized AI services conflict | High | SA | Open | Design AI as pluggable; country chooses provider |
| RISK-010 | DB migrations must be robust for country-operated deployments | High | SA/Dev | Open | Automated Flyway migrations; rollback scripts; compatibility checker |
| RISK-011 | Poland higher-education grading may be institution-specific rather than nationally uniform | Medium | SA/Dev/PO | Open | Model higher-education grading as institution-level configuration; validate with pilot universities before implementation |
| RISK-012 | Poland school-year dates and winter breaks vary by academic year and voivodeship | Medium | PO/QA | Open | Treat school calendar as versioned yearly data and validate against annual ministry notices before coding |

See `df/backlog/risks-and-assumptions.md` for the full risk register.
