# Risks and Assumptions — Education System Framework

## Risks

| ID | Risk | Probability | Impact | Severity | Mitigation | Owner | Status |
|---|---|---|---|---|---|---|---|
| RISK-001 | Poland education research may be incomplete or outdated, leading to incorrect country template | Medium | High | High | Use multiple official sources (MEN, CKE, Prawo Oświatowe); mark unknowns; validate with domain expert | PO/SA | Open |
| RISK-002 | Configuration inheritance engine may become too complex for MVP timeline | Medium | High | High | Start with 2-3 levels only (country → institution → class); add region/city later | SA/Dev | Open |
| RISK-003 | AI safety boundaries (preventing direct homework answers) may be difficult to enforce reliably | High | Medium | High | Use strict prompt engineering; content filtering; log all interactions for review; iterate | SA/Dev | Open |
| RISK-004 | Scope creep across 20 epics may delay MVP delivery | High | High | Critical | Strict phase boundaries; PO acceptance gates; defer non-essential features | PO | Open |
| RISK-005 | No real end-users during development means UX assumptions are untested | High | Medium | Medium | API-first reduces UI risk; validate flows via automated tests; PO scenario testing | PO/QA | Open |
| RISK-006 | Country sovereign deployment model adds operational complexity | Medium | Medium | Medium | Provide clear deployment documentation; Docker/OCI images; automated setup scripts | SA/Dev | Open |
| RISK-007 | Data sovereignty requirements may conflict with centralized AI services | Medium | High | High | Design AI as pluggable (cloud or self-hosted LLM); country chooses provider | SA | Open |
| RISK-008 | Multi-institution-type support (school/kindergarten/university) from day one increases initial complexity | Medium | Medium | Medium | Schema pack architecture isolates complexity; MVP implements school only; others are design-only | SA/Dev | Open |
| RISK-009 | Security requirements (zero-trust, encryption, MFA, audit) add significant development overhead | Medium | Low | Medium | Implement security incrementally; start with auth+RBAC+audit; add advanced features per phase | Dev | Open |
| RISK-010 | Database migration strategy must be robust for country-operated deployments where vendor has no access | Medium | High | High | Automated migration with Flyway; rollback scripts; compatibility checker; clear documentation | SA/Dev | Open |
| RISK-011 | Teacher/parent adoption depends on UX quality which is hard to validate without frontend | Medium | Medium | Medium | API-first enables multiple frontend approaches; validate through API contracts first | PO | Open |
| RISK-012 | Legal/privacy requirements vary by country and may require framework changes | Low | High | Medium | Design flexible privacy configuration; country template includes legal constraints section | SA/PO | Open |
| RISK-013 | AI provider costs may be significant at scale (thousands of students) | Medium | Medium | Medium | Track usage; implement rate limiting per student; allow country to choose provider | PO/SA | Open |
| RISK-014 | Meal/catering billing edge cases (partial months, mid-month enrollment changes) | Low | Low | Low | Start simple (paid/unpaid); document edge cases; iterate | Dev | Open |

## Assumptions

| ID | Assumption | Confidence | Impact if wrong | Validated? |
|---|---|---|---|---|
| ASM-001 | Poland will be the first and only MVP country template | High | Low — model supports multiple; just defer others | Yes (prompt) |
| ASM-002 | Java Spring Boot + PostgreSQL is the confirmed technology choice | High | High — would require rewrite | Yes (prompt) |
| ASM-003 | Modular monolith is acceptable for MVP; microservices can wait | High | Medium — architecture change later | Yes (prompt) |
| ASM-004 | No online payment gateway in MVP; paid/unpaid tracking only | High | Low — payment is additive | Yes (prompt) |
| ASM-005 | Meal exclusion and attendance are independent actions | High | Medium — workflow change | Yes (prompt) |
| ASM-006 | Credits for meal exclusions are applied at end of month, not immediately | High | Low — timing change only | Yes (prompt) |
| ASM-007 | AI must never provide direct homework answers | High | High — core safety requirement | Yes (prompt) |
| ASM-008 | Each country deployment is fully isolated (no shared database) | High | High — architecture change | Yes (prompt) |
| ASM-009 | Framework vendor does not access country production infrastructure | High | High — trust model change | Yes (prompt) |
| ASM-010 | The system starts API-first without a mandatory frontend | High | Low — frontend can be added independently | Yes (prompt) |
| ASM-011 | Polish grade scale for schools is 1-6 (1=niedostateczny, 6=celujący) | High | Medium — template error | Needs validation |
| ASM-012 | Polish school year starts in September, has two semesters | High | Low — calendar config | Needs validation |
| ASM-013 | MFA is required only for administrator roles, not all users | Medium | Low — can be extended later | Assumed from prompt |
| ASM-014 | Single PostgreSQL database per country deployment is sufficient for MVP | Medium | Medium — scalability concern | Needs validation |
| ASM-015 | Markdown backlog is the primary format; Jira sync is a later feature | High | Low — format is portable | Yes (prompt) |

