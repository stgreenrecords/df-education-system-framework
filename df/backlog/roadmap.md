# Education System Framework Roadmap

## Product direction

Build a sovereign, self-hosted, API-first Education System Framework for countries, ministries, cities, schools, kindergartens, universities, and other educational institutions.

The framework must be generic from day one, but the first MVP reference implementation should use Poland as the first country template.

## Phase 0 — Discovery and backlog definition

Goal: transform the product idea into roadmap, backlog, architecture questions, and MVP boundaries.

Deliverables:

- Final initial prompt
- Product vision
- Domain model draft
- Country/institution hierarchy draft
- Schema-pack concept
- Poland template research spike
- Initial roadmap
- Initial backlog
- SA questions
- PO questions

## Phase 1 — Platform foundation

Goal: build the configurable core platform.

Scope:

- API-first backend foundation
- Java Spring architecture baseline
- PostgreSQL baseline
- Podman-compatible OCI container baseline
- Cloud-portable Kubernetes/IaC deployment baseline
- Security baseline
- User/role model
- Tenant/country deployment model
- Configuration and inheritance engine
- Audit trail
- Release/update manager concept

## Phase 2 — Poland school MVP

Goal: validate the generic framework using Poland as the first reference configuration.

Scope:

- Poland country template v1
- School schema pack v1
- Institution hierarchy
- Students, teachers, parents, classes
- Subjects
- Schedule
- Attendance
- Gradebook
- Homework
- Meal/catering v1
- Basic dashboards

## Phase 3 — Kindergarten and university schema packs

Goal: define and implement initial schema packs beyond schools.

Scope:

- Kindergarten schema pack
- University schema pack
- Shared institution core
- Schema-level differences
- Meal/catering for kindergarten
- Optional meal/catering for university

## Phase 4 — AI assistance

Goal: introduce controlled AI support for students and teachers.

Scope:

- Student AI assistant
- Teacher AI assistant
- AI governance rules
- AI usage logs
- Configurable visibility of student AI conversations
- Safety rules preventing direct homework answers

## Phase 5 — Dashboards, analytics, and reporting

Goal: provide real-time insights across country, region, city, school, class, teacher, parent, and student levels.

Scope:

- Dashboards v1
- Attendance analytics
- Grade analytics
- Homework analytics
- Meal/payment analytics
- AI usage analytics
- Country/state/city/school statistics

## Phase 6 — Issue tracker integration

Goal: sync Markdown backlog items into Jira or another issue tracker.

Scope:

- Jira mapping rules
- Epic/story/task templates
- Import format
- Status mapping
- Label/component mapping
- Release/version mapping
