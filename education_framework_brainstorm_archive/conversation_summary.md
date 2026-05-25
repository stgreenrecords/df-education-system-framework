# Brainstorm Conversation Summary

User asked to brainstorm an initial prompt for an Education System Framework.

## Original Scope

The platform should support:
- schools
- kindergartens
- universities
- multiple countries
- multiple languages
- country/state/city/school/university levels
- configurable access levels
- subjects and e-books
- schedules
- gradebook
- semester calculation
- AI assistance
- dashboards and real-time statistics

## Key Decisions Captured

### SaaS / Tenant Model

Initial question: Should it be SaaS multi-tenant?

Decision:
- Each country has its own license, support line, and adjustment scope.
- Later refined: each country/ministry fully operates its own infrastructure.
- Best terminology: multi-tenant-capable framework with sovereign self-hosted country deployments.

### Country Infrastructure

Final decision:
- Country/ministry fully operates infrastructure.
- Framework vendor provides versioned releases, documentation, support/advisory, compatibility tools, migration scripts.
- Country controls production deployment, hosting, data, backups, access, monitoring, and security.

### Configuration Rules

Decision:
- School/university can only extend country scheme, not fully override it.
- Example: add additional subject, local programs, custom dashboards, schedules.
- Inheritance must be preserved where possible.

### MVP Country Strategy

Decision:
- Design generic global framework from day one.
- MVP uses Poland as reference country.
- Polish education system should be researched from public/open sources.
- Country templates can later be researched and predefined for other countries.

### Institution Types

Decision:
- Initial schema packs should exist for schools, kindergartens, and universities.
- Universities, schools, and kindergartens may have major schema differences.
- Core should be shared, but institution schemas should be flexible.

### Attendance

Question: support attendance or only grades/homework/schedule?

Recommendation accepted:
- Include attendance in MVP or near-MVP.
- Attendance should support schools, kindergartens, and universities differently.

### AI Student Help

Decision:
- Student AI should explain topics and similar examples.
- It must not provide direct answers to assigned homework.
- Teacher visibility into AI history remains open for future brainstorming.

### Meal and Catering

Decision:
- Meal/catering is very important.
- Catering is defined at school/kindergarten level.
- Country/city does not manage operational catering.
- School/kindergarten defines provider, meal types, prices, menu, exclusion rules, billing.

Payment decision:
- MVP only tracks paid/unpaid.
- No online payment gateway at first.

Meal exclusion decision:
- Parents can exclude future days.
- Parents can report today's meal absence before institution-defined deadline.
- Meal exclusion is not automatically connected to attendance absence.
- Exclusions create credit/balance adjustment applied at end of month.

### Architecture

User suggested:
- headless architecture
- mobile, desktop, and web support
- latest Java Spring framework
- PostgreSQL database
- API-first approach
- technical details prepared by Solution Architect

Accepted and refined:
- API-first headless backend
- OpenAPI contracts
- Java Spring + PostgreSQL
- modular monolith first, microservices later if needed
- event-driven extension points

### Security

User wanted 110% secure and 0 chance to be hacked.

Refined requirement:
- Do not claim 0 chance to be hacked.
- Instead define maximum security, zero-trust, tenant/country isolation, encryption, auditability, continuous monitoring, and country-level sovereignty.

## Open Questions Remaining

1. Should teacher see full student AI conversation history, summaries only, or only flagged conversations?
2. How detailed should Poland MVP template research be in the first phase?
3. What is the first user interface target: web admin panel, parent mobile app, teacher portal, or student portal?
4. Should gradebook come before attendance, or should both be first-class MVP modules?
5. Should payment tracking integrate with accounting later, or remain internal school status tracking?
6. Should country templates be manually curated, AI-assisted, or both?
7. Should framework support offline/private deployments without internet access?
8. Should local extensions be plugin-based, configuration-based, or custom code packages?
