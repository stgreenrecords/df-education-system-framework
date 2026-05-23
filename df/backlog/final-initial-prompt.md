# Final Initial Prompt — Education System Framework

We need to use the Dark Factory Framework to define the roadmap and initial backlog for a sovereign, configurable, API-first Education System Framework.

The goal of this prompt is not only to describe the product idea. The main goal is to transform the idea into a structured product roadmap and tracker-ready backlog that can later be synced or imported into Jira, GitHub Issues, Linear, YouTrack, or another issue tracker.

The output must include:

1. Product vision
2. Scope boundaries
3. Domain model
4. Architecture direction
5. MVP definition
6. Product roadmap
7. Initial backlog
8. Epics
9. User stories
10. Acceptance criteria
11. Risks and assumptions
12. Open questions for PO / SA / Dev / QA
13. Jira-ready issue structure

## Product vision

Build a generic Education System Framework for schools, kindergartens, universities, colleges, and other education institutions.

The framework must support different countries, languages, education models, grading systems, academic calendars, institution types, and legal/operational requirements.

The system must be generic from day one, but the first MVP should use Poland as the reference implementation. The Poland configuration should be prepared by researching public/open sources about the Polish education system. Later, the same evidence-based approach should be used to create predefined configurations for other countries.

## Operating model

Each country/ministry has its own license, support line, configuration, localization, data, and infrastructure.

Each country/ministry fully operates its own infrastructure inside its own territory. The framework provider supplies versioned framework releases, security patches, documentation, migration scripts, compatibility tooling, and support/advisory services, but does not directly operate country production infrastructure.

Centralized updates must be supported through release packages rather than direct vendor-controlled deployment.

Preferred operating model:

```text
Framework Vendor
→ builds core framework
→ provides release packages
→ provides documentation
→ provides migration scripts
→ provides compatibility checker
→ provides support/advisory
→ does not directly operate country production infrastructure

Country / Ministry
→ owns infrastructure
→ operates environments
→ controls deployment
→ owns data
→ controls backups
→ controls access
→ controls security operations
→ approves and installs updates
```

## Hierarchy

The platform hierarchy should support:

```text
Tenant / License
→ Country configuration
→ State / Region / Voivodeship
→ City / District / Municipality
→ Institution
→ Faculty / Department / School Unit / Class / Group / Course
→ Teacher / Lecturer / Student / Parent / Guardian
```

The top-level deployment should be understood as a sovereign, self-hosted country deployment rather than a classic centrally hosted SaaS tenant.

## Flexibility requirement

The framework must be fully flexible from a functional configuration point of view.

Use these principles:

```text
Configuration over customization.
Extension over modification.
Inheritance over duplication.
Compatibility over one-off changes.
```

Configuration should be inherited from upper levels. Lower levels cannot break country rules by default, but they can extend allowed schema areas. For example, a school or university may add additional optional subjects, local activities, custom schedules, local programs, or custom dashboards.

If a country explicitly breaks inheritance or customizes core behavior, centralized updates may require compatibility checks, migration support, or manual intervention.

## Framework release and update model

The system must include a Framework Release and Update Manager concept.

It should support:

- Versioned release packages
- Compatibility reports
- Config inheritance validation
- Database migration scripts
- Rollback guidance
- Release notes
- Security patch classification
- Country version tracking
- Manual country approval workflow
- Offline/private deployment support

Target flow:

```text
Central Framework Source
→ Versioned Release Package
→ Country Ministry Receives Release
→ Country Runs Compatibility Check
→ Country Tests in Dev/QA/Stage
→ Country Approves Deployment
→ Country Deploys to Production
```

## Architecture direction

The platform should use a headless, API-first architecture so that web apps, mobile apps, desktop apps, AI services, dashboards, and third-party integrations can use the same backend APIs.

Preferred technical direction:

- Latest stable Java Spring ecosystem
- PostgreSQL database
- API-first approach
- OpenAPI contracts
- Modular monolith first, microservices later if needed
- Event-driven extension points
- Secure integration architecture

Further technical details must be prepared by the Solution Architect.

## Security requirement

The platform must be designed for maximum security, zero-trust access, strong isolation, continuous monitoring, auditability, and country-level data sovereignty.

Do not claim that the system has “zero chance to be hacked.” Instead, design for strongest practical security.

Required security principles:

- Zero-trust security model
- Country-level data isolation
- Tenant/deployment isolation
- Role-based and attribute-based access control
- Strong authentication
- MFA for administrators
- Least-privilege permissions
- Encryption in transit
- Encryption at rest
- Secure API gateway
- Rate limiting
- Secrets management
- Security monitoring
- Immutable audit logs
- Regular penetration testing
- Dependency scanning
- Backup and disaster recovery
- Data residency controls

## Institution schema packs

The framework must include initial schema packs for schools, kindergartens, and universities from the beginning, even if the MVP implementation starts with schools.

Use this model:

```text
Core Education Platform
→ School Schema Pack
→ Kindergarten Schema Pack
→ University Schema Pack
→ Shared Meal & Catering Module
→ AI Assistance Module
→ Dashboard Module
→ Local Extension Packs
```

The schema must support major differences between institution types.

Kindergarten is focused on care, routine, meals, check-in/check-out, guardians, and parent communication.

School is focused on classes, subjects, schedule, attendance, homework, grades, exams, teachers, students, and parents.

University is focused on faculties, departments, degree programs, courses, enrollment, credits/ECTS, lecturers, exams, retakes, transcripts, and thesis workflows.

## School schema ideas

Support:

- Grades/classes
- Subjects
- Teachers
- Class teacher
- Students
- Parents/guardians
- Schedule
- Attendance
- Homework
- Tests/exams
- Gradebook
- Semester/final grade calculation
- Books/materials
- Meals
- Parent payment visibility
- Announcements
- Behavior/notes
- AI student helper
- AI teacher helper
- Dashboards

## Kindergarten schema ideas

Support:

- Child groups
- Caregivers/teachers
- Daily attendance
- Check-in/check-out
- Authorized pickup persons
- Meals
- Allergies
- Nap/rest schedule
- Daily activities
- Developmental notes
- Parent communication
- Incident reports
- Payment info
- Meal exclusions
- Absence notes
- Photo/report sharing later

## University schema ideas

Support:

- Faculties
- Departments
- Degree programs
- Study levels
- Academic years
- Semesters
- Courses
- Lectures
- Seminars
- Labs
- Credits/ECTS
- Lecturers
- Assistants
- Student groups
- Course enrollment
- Prerequisites
- Exams
- Retakes
- Grades
- Transcripts
- Thesis process
- Dean office workflows
- Elective courses
- Attendance rules per course
- University calendar
- Exam session calendar
- Meal/catering as optional extension

## Meal and Catering Management

Meal and catering management must be configured at the individual school or kindergarten level.

Each school or kindergarten can define its own catering provider or internal kitchen, meal types, menus, prices, exclusion deadlines, billing rules, and parent payment tracking.

Catering is not automatically configured operationally at country, state, or city level. Upper levels may define policy constraints if needed, but actual catering setup belongs to the institution.

MVP rules:

- Catering is defined at school/kindergarten level.
- Universities may use meal/catering only as an optional extension.
- Meal payments are tracked only as paid/unpaid at first.
- No online payment gateway in MVP.
- Parents can exclude meals for future days.
- Parents can exclude meals for the current day before the institution-defined deadline.
- Meal exclusion is not automatically connected to attendance absence.
- Attendance and meal cancellation are separate actions.
- Valid exclusions create pending credit/balance adjustments.
- Credits are applied at the end of the month, not immediately.
- Admins can manually correct records with audit trail.

Example billing flow:

```text
Monthly meal fee is generated
→ Parent sees paid/unpaid status
→ Parent excludes today before deadline or future days
→ System records valid exclusions
→ Exclusions create pending credit adjustments
→ End of month billing closes
→ System applies credits to balance
→ Admin can review/correct
→ Audit trail stores all changes
```

## Gradebook and assessment

The system must support configurable grading scales, for example:

- Numeric grades, such as 1–6 or 0–10
- Letter grades, such as A–F
- Percentage grades
- Custom country-specific grading models

Grades for semester/final periods may be calculated automatically, with manual correction possible through a controlled workflow and audit trail.

## Attendance

Attendance should be included in MVP or near-MVP.

Support:

- Present
- Absent
- Excused absence
- Late
- Remote attendance
- Medical absence
- School event absence
- Partial day absence
- Per-lesson attendance
- Daily attendance
- University course attendance where required
- Kindergarten check-in/check-out
- Parent justification
- Teacher approval
- Attendance dashboards

## AI student assistant

Students may request AI assistance while doing homework.

AI can:

- Explain concepts
- Ask guiding questions
- Provide similar examples
- Solve similar cases
- Generate practice questions
- Adapt explanation to age/grade
- Support configured language
- Summarize relevant learning material
- Quiz the student

AI must not:

- Provide the direct answer to the assigned task
- Complete homework for the student
- Bypass teacher restrictions

The question of whether teachers can see full AI conversation history remains open and requires additional brainstorming.

Possible future modes:

- Teacher sees only usage statistics
- Teacher sees summaries
- Teacher sees flagged conversations
- Teacher sees full conversations
- Parent can see child AI learning history

## AI teacher assistant

Teachers should have AI assistance for:

- Lesson preparation
- Test generation
- Homework generation
- Rubric generation
- Explanation simplification
- Substitute lesson plans
- Translation/localization
- Class progress summaries
- Weak-topic identification
- Parent message drafts
- Quiz generation from e-books or materials

## Dashboards and statistics

The system should support dashboards and real-time statistics at multiple levels.

Country level:

- Total institutions
- Total students
- Total teachers
- Attendance trends
- Subject performance by region
- Grade distribution
- Curriculum coverage
- Dropout risk
- Teacher workload
- AI usage statistics
- E-book usage
- Exam readiness
- Regional inequality analytics

Region/city level:

- Institution performance
- Attendance by district
- Teacher shortage
- Subject-level weak areas
- Local program statistics
- Capacity planning

School level:

- Class performance
- Teacher workload
- Attendance issues
- Homework completion
- At-risk students
- Parent communication
- AI usage by class

Teacher/parent/student level:

- Grades
- Attendance
- Homework
- Upcoming tests
- Teacher feedback
- AI learning summary
- Recommended practice

## Country template system

Create a Country Education Template Builder.

It should support:

- Official education stages
- Institution types
- Grade scales
- Required subjects
- Academic calendar rules
- Semester/term structure
- Curriculum references
- Language configuration
- Official exams
- Attendance rules
- Teacher roles
- Report/document formats
- Legal/privacy constraints
- Evidence/source links
- Versioning and approval status

Country templates should be evidence-based and versioned.

Example:

```text
Poland Template v2026.1
Status: draft / verified / approved
Sources: official/public references
```

## Backlog and issue tracker integration

Create a `df/backlog/` folder with Markdown files first. Later, backlog items may be synced or imported into Jira or another issue tracker.

The framework should remain tracker-agnostic but Jira-ready.

Backlog hierarchy:

```text
Roadmap
→ Epic
→ Feature
→ User Story
→ Task
→ Subtask
→ Bug
→ Technical Debt
→ Research Spike
```

Each backlog item should include:

- Type
- ID
- Title
- Description
- Owner role: PO / SA / Dev / QA
- Priority
- Phase
- Dependencies
- Acceptance criteria
- Open questions
- Status

Initial epics should include:

- Platform Foundation
- Sovereign Country Deployment Model
- Configuration and Inheritance Engine
- Framework Release and Update Manager
- Country Template System
- Poland MVP Template
- Institution Schema Packs
- User, Role, and Access Management
- School Core Module
- Kindergarten Core Module
- University Core Module
- Meal and Catering Management
- Attendance
- Gradebook and Assessment
- Homework and Assignments
- AI Student Assistant
- AI Teacher Assistant
- Dashboards and Statistics
- Security, Privacy, and Audit
- Backlog and Issue Tracker Integration

## MVP recommendation

The first MVP should include:

- Generic platform core
- Poland country template
- School schema pack v1
- Initial kindergarten schema design
- Initial university schema design
- Basic tenant/license model
- Country self-hosted deployment model
- User and role management
- Subjects
- Classes
- Students/teachers/parents
- Schedule
- Attendance
- Gradebook
- Homework
- Meal and Catering v1
- Basic AI student helper
- Basic AI teacher helper
- Basic dashboards
- Security and audit foundation
- Markdown backlog in `df/backlog/`

## Required role outputs

PO should produce:

- Product vision
- MVP scope
- Roadmap
- User stories
- Acceptance criteria
- Open business questions

SA should produce:

- Architecture direction
- Domain model
- Deployment model
- Security model
- Integration model
- Data model draft
- Technical risks

Dev should produce:

- Implementation tasks
- Technical assumptions
- API/module breakdown
- Development estimates if requested

QA should produce:

- Test strategy
- Acceptance test cases
- Risk-based test focus
- Security and permission test ideas
- Regression test scope

## Final instruction

Using the Dark Factory Framework, convert this prompt into a structured roadmap and initial backlog. Keep all outputs tracker-ready and suitable for later Jira import.
