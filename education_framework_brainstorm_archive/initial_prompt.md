# Initial Prompt — Sovereign Education Platform Framework

We need to design a generic, configurable, sovereign Education System Framework for schools, kindergartens, universities, colleges, and other education institutions.

The platform must be flexible from a functional configuration point of view and must support different countries, languages, education structures, grading systems, schedules, attendance rules, curriculum models, dashboards, AI assistance, and institution-specific workflows.

The system should be generic from day one, but the first MVP should use Poland as the reference implementation. We should research the Polish education system using public/open sources and prepare a predefined Poland configuration. Later, the same evidence-based approach can be used to create predefined configurations for other countries.

## Operating Model

Each country/ministry fully operates its own infrastructure inside its own territory.

The framework provider supplies:
- versioned framework releases
- security patches
- documentation
- migration scripts
- compatibility tooling
- support/advisory

The country/ministry controls:
- hosting
- environments
- deployment
- production operations
- data
- backups
- access
- monitoring
- security operations
- update approval

Centralized updates must be supported through release packages rather than direct vendor-controlled deployment.

This is a multi-tenant-capable framework with sovereign self-hosted country deployments.

## Hierarchy

The framework should support:

Tenant / License
→ Country education configuration
→ State / Region / Voivodeship
→ City / District / Municipality
→ Institution
→ Faculty / Department / School unit / Class / Group / Course
→ Teacher / Student / Parent / Guardian

Each country/ministry has its own license, support line, country configuration, localization, infrastructure, and adjustment scope.

## Configuration and Inheritance

Configuration must be inherited from upper levels.

Lower levels cannot break country rules, but they can extend the schema where allowed. For example, a school or university can add additional optional subjects, local programs, clubs, dashboards, schedules, or operational settings.

If a country explicitly breaks inheritance or customizes core behavior, centralized updates may require compatibility checks or migration support.

Core principles:
- Configuration over customization
- Extension over modification
- Inheritance over duplication
- Compatibility over one-off changes

## Institution Schema Packs

The framework must include initial schema packs for:

1. School
2. Kindergarten
3. University

These schema packs should share a common platform core but allow major schema-level differences.

### School Schema Pack

Schools need:
- grades/classes
- subjects
- teachers
- class teacher
- students
- parents/guardians
- schedules
- attendance
- homework
- tests/exams
- gradebook
- semester/final grade calculation
- books/materials
- meals
- parent payment visibility
- announcements
- behavior notes
- AI student helper
- AI teacher helper

### Kindergarten Schema Pack

Kindergartens need:
- child groups
- caregivers/teachers
- daily attendance
- check-in/check-out
- authorized pickup persons
- meals
- allergies
- nap/rest schedule
- daily activities
- developmental notes
- parent communication
- incident reports
- payment information
- absence/exclusion days
- photo/report sharing later

Kindergarten is primarily care, routine, safety, meals, parent communication, and development tracking — not gradebook logic.

### University Schema Pack

Universities need:
- faculties
- departments
- degree programs
- study levels
- academic years
- semesters
- courses
- lecturers
- assistants
- student groups
- course enrollment
- credits / ECTS
- exams
- retakes
- grades
- transcripts
- thesis process
- schedules
- attendance per course where required
- payments/fees later
- optional meal access
- dormitory module later

Universities should not be forced into school-style class/grade logic. They need program/course/enrollment logic.

## Meal & Catering Management

Meal and catering management must be configured at the individual school or kindergarten level.

Each school or kindergarten can define:
- catering provider or internal kitchen
- meal types
- menus
- prices
- exclusion deadlines
- billing rules
- parent payment tracking

University catering is optional.

Parents/guardians must be able to:
- view meal plans
- view monthly charges
- view payment status
- exclude selected future days
- exclude the current day before the institution-defined deadline

Meal payment in MVP is tracked only as paid/unpaid. No online payment gateway in MVP.

Meal exclusion is not automatically connected to attendance absence. Attendance and meal cancellation are separate actions.

Valid meal exclusions do not immediately reduce the active monthly charge. They create credit/balance adjustments applied at the end of the month.

Meal & Catering v1 should include:
- school/kindergarten-level catering config
- catering provider or internal kitchen
- meal types
- daily/weekly/monthly menu
- meal subscription per child/student
- monthly meal charge calculation
- parent-visible paid/unpaid status
- manual payment status update by admin
- parent meal exclusion for today before deadline
- parent meal exclusion for future days
- pending credit adjustments
- end-of-month balance calculation
- admin correction
- audit trail

Out of scope for MVP:
- online payments
- payment gateway integration
- automatic attendance-to-meal cancellation
- automatic bank reconciliation
- invoicing

## Attendance

Attendance should be included in MVP or near-MVP.

Attendance can support:
- present
- absent
- excused absence
- late
- remote attendance
- medical absence
- school event absence
- partial day absence
- per-lesson attendance
- daily attendance
- university course attendance
- kindergarten check-in/check-out
- parent justification
- teacher approval
- attendance dashboards
- absence alerts

## AI Assistance

### Student AI

Student AI must help learning but must not provide direct answers to homework tasks.

Allowed behavior:
- explain concepts
- ask guiding questions
- provide similar examples
- solve similar but not identical tasks
- generate practice questions
- summarize learning material
- adapt explanation to age/grade
- support configured language

Restricted behavior:
- do not directly solve assigned homework
- do not provide final answer when the student asks for task completion

Teacher visibility into student AI conversations is an open question requiring additional brainstorming.

Possible modes:
- teacher sees only usage statistics
- teacher sees summaries
- teacher sees flagged conversations
- teacher sees full conversations
- parent-visible AI history

Recommended default for future discussion:
Teacher sees summary plus flagged conversations, not all private conversations by default.

### Teacher AI

Teacher AI should support:
- lesson plan generation
- test generation
- homework generation
- rubric generation
- simplifying explanations
- creating examples by difficulty level
- adapting material for special needs
- translation/localization
- class progress summaries
- identifying weak topics
- parent message drafts
- substitute lesson plans
- quizzes from e-book chapters
- exercises based on curriculum

## Country Template Research

The platform should include a Country Education Template Builder.

It should support evidence-based country templates based on public/open sources.

A country template may include:
- official education stages
- grade scales
- school types
- required subjects
- academic calendar rules
- semester structure
- curriculum references
- language list
- official exams
- attendance rules
- teacher roles
- document/report formats
- legal/privacy constraints
- source evidence links

For MVP, create a Poland template:
- Polish language
- voivodeships
- school types
- grade scale
- education stages
- common subjects
- school year structure
- semester model
- exam types

Country templates must be versioned and source-backed.

Example:
Poland config v2026.1
Status: draft / verified / approved
Sources: official/public references

## Architecture Direction

The system should use headless, API-first architecture.

The same backend APIs should support:
- web app
- mobile app
- desktop app
- external integrations
- AI services
- reporting/dashboard services

Preferred backend stack:
- latest stable Java Spring ecosystem
- PostgreSQL database
- API-first approach
- OpenAPI contracts
- modular monolith first, microservices later if needed
- event-driven extension points

Further technical architecture, infrastructure, security, deployment, and integration details must be prepared by the Solution Architect.

## Framework Release & Update Manager

The framework must support centralized update packages even though countries operate their own infrastructure.

The Release & Update Manager should support:
- versioned release packages
- compatibility reports
- config inheritance validation
- database migration scripts
- rollback plans
- release notes
- security patch classification
- country version tracking
- manual approval workflow
- offline/private deployment support

Possible update flow:

Central Framework Source
→ Versioned Release Package
→ Country Ministry Receives Release
→ Country Runs Compatibility Check
→ Country Tests in Dev/QA/Stage
→ Country Approves Deployment
→ Country Deploys to Production

## Security and Sovereignty

The platform must be designed for maximum security, zero-trust access, strong isolation, continuous monitoring, auditability, and country-level data sovereignty.

No system can honestly guarantee zero chance of being hacked, so the framework must instead follow strong security-by-design principles:
- zero-trust security model
- country-level data isolation
- tenant isolation
- role-based and attribute-based access control
- encryption in transit
- encryption at rest
- audit logs
- immutable security events
- secure API gateway
- rate limiting
- strong authentication
- MFA for admins
- least-privilege permissions
- security monitoring
- backup and disaster recovery
- regular penetration testing
- dependency scanning
- secrets management
- data residency controls

## Dashboards and Statistics

Dashboards should exist at country, region, city, school, teacher, parent, and student levels.

Country level:
- total institutions
- total students
- total teachers
- attendance trends
- subject performance by region
- grade distribution
- curriculum coverage
- dropout risk
- teacher workload
- AI usage
- e-book usage
- exam readiness
- regional inequality analytics

School level:
- class performance
- teacher workload
- timetable gaps
- attendance issues
- homework completion
- at-risk students
- parent communication
- AI usage by class

Teacher level:
- class progress
- homework completion
- students needing help
- upcoming lessons
- generated lesson materials
- grading workload

Parent/student level:
- grades
- attendance
- homework
- upcoming tests
- teacher feedback
- AI learning summary
- recommended practice

## MVP Recommendation

MVP should include:
- generic platform core
- Poland country template
- school schema pack
- initial kindergarten schema pack
- initial university schema pack
- basic tenant/license model
- role model
- subjects
- classes/groups/courses
- students/teachers/parents
- schedule
- attendance
- gradebook
- homework
- meal/catering v1
- basic AI student helper
- basic AI teacher helper
- dashboards v1
- release/update manager concept
- security/audit foundation
