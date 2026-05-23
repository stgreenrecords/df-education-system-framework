# Jira-Ready Issue Structure — Education System Framework

## Issue type mapping

| Markdown type | Jira issue type | Description |
|---|---|---|
| Epic | Epic | Large body of work spanning multiple sprints |
| Feature | Story (with "Feature" label) | Significant user-facing capability |
| User Story | Story | Implementable unit of user value |
| Task | Task | Technical work without direct user story |
| Subtask | Sub-task | Breakdown of a story or task |
| Bug | Bug | Defect in existing functionality |
| Technical Debt | Task (with "tech-debt" label) | Cleanup/refactor without new features |
| Research Spike | Task (with "spike" label) | Time-boxed investigation |

## Field mapping

| Markdown field | Jira field | Notes |
|---|---|---|
| ID | Key | Auto-generated in Jira (PROJECT-123) |
| Title | Summary | Required |
| Type | Issue Type | Map per table above |
| Description | Description | Markdown → Jira wiki format |
| Owner role | Custom field "Owner Role" or Label | PO / SA / Dev / QA |
| Priority | Priority | Critical→Highest, High→High, Medium→Medium, Low→Low |
| Phase | Fix Version | Phase 0 = "0.1-discovery", Phase 1 = "1.0-foundation", etc. |
| Status | Status | Draft→Open, Ready→To Do, In Progress, Blocked, Done |
| Dependencies | Issue Links (blocks/is-blocked-by) | Link related issues |
| Acceptance criteria | Description (AC section) or custom field | Structured in description |
| Open questions | Comments or linked Confluence page | Document in comments |
| Epic | Epic Link | Associates story with epic |

## Project configuration

```text
Project key: EDU
Project name: Education System Framework
Project type: Scrum / Kanban (team choice)

Issue types:
- Epic
- Story
- Task
- Sub-task
- Bug

Custom fields:
- Owner Role (dropdown: PO, SA, Dev, QA)
- Phase (dropdown: 0, 1, 2, 3, 4, 5, 6)
- Country Template (text)
- Schema Pack (dropdown: school, kindergarten, university, shared)
- Security Impact (checkbox)

Labels:
- feature
- tech-debt
- spike
- security
- ai
- meal-catering
- poland-mvp
- config-engine
- api-first
- deployment
```

## Fix version mapping

| Phase | Fix Version | Target |
|---|---|---|
| 0 | 0.1-discovery | Backlog, roadmap, architecture decisions |
| 1 | 1.0-foundation | Platform core, security, config engine |
| 2 | 2.0-poland-school-mvp | School operations with Poland template |
| 3 | 3.0-schema-packs | Kindergarten + university packs |
| 4 | 4.0-ai-assistants | AI student + teacher helpers |
| 5 | 5.0-dashboards | Analytics and reporting |
| 6 | 6.0-tracker-integration | Jira/tracker sync |

## Component mapping

| Component | Scope |
|---|---|
| platform-core | Tenancy, config inheritance, audit, releases |
| identity-access | Auth, users, roles, permissions, MFA |
| organization | Countries, regions, cities, institutions |
| school-pack | School-specific modules |
| kindergarten-pack | Kindergarten-specific modules |
| university-pack | University-specific modules |
| attendance | Cross-institution attendance |
| gradebook | Grading and assessment |
| meal-catering | Meal management and billing |
| ai-student | Student AI assistant |
| ai-teacher | Teacher AI assistant |
| dashboards | Statistics and reporting |
| release-manager | Framework releases and updates |
| security | Security, encryption, audit |
| api-gateway | API routing and protection |

## Epic key mapping

| Markdown ID | Jira Epic Name | Component |
|---|---|---|
| EPIC-01 | Platform Foundation | platform-core |
| EPIC-02 | Sovereign Deployment | platform-core, organization |
| EPIC-03 | Configuration Engine | platform-core |
| EPIC-04 | Release Manager | release-manager |
| EPIC-05 | Country Template System | organization |
| EPIC-06 | Poland MVP Template | organization |
| EPIC-07 | Schema Packs | school-pack, kindergarten-pack, university-pack |
| EPIC-08 | User & Access Management | identity-access |
| EPIC-09 | School Core | school-pack |
| EPIC-10 | Kindergarten Core | kindergarten-pack |
| EPIC-11 | University Core | university-pack |
| EPIC-12 | Meal & Catering | meal-catering |
| EPIC-13 | Attendance | attendance |
| EPIC-14 | Gradebook | gradebook |
| EPIC-15 | Homework | school-pack |
| EPIC-16 | AI Student | ai-student |
| EPIC-17 | AI Teacher | ai-teacher |
| EPIC-18 | Dashboards | dashboards |
| EPIC-19 | Security & Audit | security |
| EPIC-20 | Tracker Integration | platform-core |

## Import format

For bulk import into Jira, the following CSV structure can be used:

```csv
Summary,Issue Type,Priority,Fix Version,Component,Epic Link,Description,Labels
"Initialize Spring Boot project",Story,Highest,1.0-foundation,platform-core,Platform Foundation,"Create initial project...",api-first
"Implement PostgreSQL configuration",Story,Highest,1.0-foundation,platform-core,Platform Foundation,"Set up database...",
"Implement audit trail",Story,Highest,1.0-foundation,security,Security & Audit,"Create immutable audit...",security
```

## Status workflow

```text
Open → In Progress → In Review → Testing → Done
                  ↘ Blocked ↗
                  ↘ Rejected → In Progress
```

## Sprint planning guidance

- Phase 0 items first (discovery, architecture decisions, Poland research)
- Phase 1 items form the first development sprints
- Each sprint should deliver a testable increment
- Critical-priority items before High-priority items
- Dependencies must be respected (see issue links)

