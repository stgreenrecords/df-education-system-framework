# User Stories — Education System Framework

This file contains the expanded user stories organized by epic. Each story follows the Jira-ready format.

---

## EPIC-01 — Platform Foundation

### STORY-010 — Initialize Spring Boot project with modular structure

Type: Story
Owner role: Dev
Priority: Critical
Phase: 1
Epic: EPIC-01
Status: Draft
Dependencies: None

Description:
Create the initial Spring Boot project with modular architecture, build configuration, and base dependencies.

Acceptance criteria:
- Given a new developer, when they clone the repo and run the build, then the project compiles successfully
- Given the project structure, when inspected, then modules exist for platform-core, identity-access, organization, school-pack, attendance, gradebook, meal-catering, common
- Given the build system, when running tests, then a sample integration test passes

---

### STORY-011 — Implement PostgreSQL database configuration and migration framework

Type: Story
Owner role: Dev
Priority: Critical
Phase: 1
Epic: EPIC-01
Status: Draft
Dependencies: STORY-010

Description:
Set up PostgreSQL connection, connection pooling, and Flyway/Liquibase migration framework.

Acceptance criteria:
- Given the application starts, when connecting to PostgreSQL, then the connection is established successfully
- Given a migration script exists, when the application starts, then the migration is applied automatically
- Given a migration has already been applied, when the application restarts, then it is not re-applied
- Given migration versioning, when a new migration is added, then it runs in order

---

### STORY-012 — Implement OpenAPI contract generation

Type: Story
Owner role: backend-dev
Priority: High
Phase: 1
Epic: EPIC-01
Status: Promoted to runtime - READY_FOR_DEV
Dependencies: STORY-010

Description:
Configure automatic OpenAPI 3.x specification generation from Spring controllers.

Acceptance criteria:
- Given any REST endpoint, when the OpenAPI spec is generated, then the endpoint appears with request/response schemas
- Given the running application, when accessing /api-docs, then a valid OpenAPI JSON is returned
- Given Swagger UI is enabled, when accessing /swagger-ui, then API documentation is browsable

---

### STORY-014 - Initialize website frontend application project

Type: Story
Owner role: frontend-dev
Priority: Critical
Phase: 1
Epic: EPIC-01
Status: Draft
Dependencies: STORY-010, STORY-012

Description:
Create the website frontend project foundation. The website project uses Next.js + React and must remain independent from future Android and iOS mobile projects.

Acceptance criteria:
- Given the frontend structure, when inspected, then `frontend/website` exists as a separate project root
- Given the website project, when inspected, then it is a Next.js + React application
- Given the website build, when run independently, then it does not require Android or iOS project files
- Given project documentation, when read, then it documents website-only validation paths and notes that Android/iOS are last-priority future work
- Given generated API clients or design tokens are introduced, when reviewed, then sharing is explicit and does not create hidden coupling with future mobile projects

---

### STORY-015 - Initialize Android mobile application project

Type: Story
Owner role: frontend-dev
Priority: Low
Phase: Later
Epic: EPIC-01
Status: Draft
Dependencies: STORY-014, STORY-012

Description:
Create the Android mobile application project as an independent frontend project. Mobile applications are last-priority frontend work unless PO/SA explicitly promotes them.

Acceptance criteria:
- Given the frontend structure, when inspected, then `frontend/android` exists as a separate project root
- Given the Android build, when run independently, then it does not require website or iOS project files
- Given project documentation, when read, then it documents Android-only validation paths
- Given generated API clients or design tokens are introduced, when reviewed, then sharing is explicit and does not create hidden direct source coupling with website or iOS

---

### STORY-016 - Initialize iOS mobile application project

Type: Story
Owner role: frontend-dev
Priority: Low
Phase: Later
Epic: EPIC-01
Status: Draft
Dependencies: STORY-014, STORY-012

Description:
Create the iOS mobile application project as an independent frontend project. Mobile applications are last-priority frontend work unless PO/SA explicitly promotes them.

Acceptance criteria:
- Given the frontend structure, when inspected, then `frontend/ios` exists as a separate project root
- Given the iOS build, when run independently, then it does not require website or Android project files
- Given project documentation, when read, then it documents iOS-only validation paths
- Given generated API clients or design tokens are introduced, when reviewed, then sharing is explicit and does not create hidden direct source coupling with website or Android

---

### STORY-013 — Implement audit trail system

Type: Story
Owner role: Dev
Priority: Critical
Phase: 1
Epic: EPIC-01
Status: Draft
Dependencies: STORY-011

Description:
Create an immutable audit trail that records all meaningful state changes with actor, timestamp, action, and before/after values.

Acceptance criteria:
- Given any entity change, when saved, then an audit record is created with actor, timestamp, entity, action, old value, new value
- Given audit records, when queried, then they cannot be modified or deleted through the application
- Given an admin, when viewing audit logs, then they can filter by entity type, actor, and time range
- Given audit data, when exported, then it includes all fields needed for compliance review

---

## EPIC-02 — Sovereign Country Deployment Model

### STORY-020 — Design country-sovereign deployment architecture

Type: Story
Owner role: SA
Priority: Critical
Phase: 1
Epic: EPIC-02
Status: Promoted to runtime - READY_FOR_QA
Dependencies: None

Description:
Document the deployment architecture where each country fully operates its own infrastructure.

Acceptance criteria:
- Given the deployment docs, when read, then they describe country-owned infrastructure, data, backups, and access
- Given the deployment model, when reviewed, then it shows dev/QA/stage/prod environments per country
- Given the release flow, when described, then it shows vendor → package → country receives → country tests → country deploys
- Given isolation requirements, when described, then no cross-country data flow exists

---

### STORY-021 — Implement basic tenant/deployment configuration

Type: Story
Owner role: backend-dev
Priority: Critical
Phase: 1
Epic: EPIC-02
Status: Promoted to runtime - READY_FOR_DEV
Dependencies: STORY-010, STORY-011

Description:
Implement the base tenant model that represents a country deployment with its own configuration.

Acceptance criteria:
- Given a new deployment, when initialized, then a tenant record is created with country code, name, timezone, locale
- Given a tenant, when APIs are called, then all operations are scoped to that tenant
- Given tenant configuration, when loaded, then it provides country-specific settings to all modules

---

### STORY-022 — Implement Podman-compatible OCI container baseline

Type: Story
Owner role: devops
Priority: Critical
Phase: 1
Epic: EPIC-02
Status: Promoted to runtime - READY_FOR_DEV
Dependencies: STORY-010, STORY-011

Description:
Create the first containerization baseline using open OCI images that can be built and run with Podman, without introducing Docker-daemon-specific assumptions.

Acceptance criteria:
- Given the Maven application build, when the container image is built, then an OCI-compatible application image is produced
- Given a developer or country operator uses Podman, when they run the application image with externalized configuration, then the application starts successfully
- Given PostgreSQL is required, when running the local container baseline, then the application connects to a containerized PostgreSQL instance using environment-provided configuration
- Given the container definition is reviewed, then no secrets, country-specific code, or cloud-specific code are embedded in the image
- Given the image is inspected, then it exposes health/readiness behavior suitable for later orchestration

---

### STORY-023 — Define cloud-portable Kubernetes and IaC deployment baseline

Type: Story
Owner role: devops
Priority: Critical
Phase: 1
Epic: EPIC-02
Status: Promoted to runtime - READY_FOR_DEV
Dependencies: STORY-020, STORY-022

Description:
Define a cloud-portable deployment baseline using Kubernetes-compatible manifests and infrastructure-as-code modules so the same application image can run on AWS, Azure, Google Cloud, private cloud, or on-premises infrastructure.

Acceptance criteria:
- Given the deployment baseline, when reviewed, then application code remains unchanged across AWS, Azure, Google Cloud, and self-hosted/on-prem targets
- Given Kubernetes manifests or templates, when reviewed, then they separate provider-neutral application deployment from provider-specific infrastructure concerns
- Given infrastructure as code, when reviewed, then provider-specific modules exist or are planned for AWS, Azure, Google Cloud, and self-hosted/on-prem infrastructure
- Given the IaC strategy, when reviewed, then it supports an open-source OpenTofu-compatible path and can accommodate Terraform if required by a country operator
- Given a country/ministry deployment model, when reviewed, then container registries, secret stores, networking, databases, and observability are configurable per provider without changing application source code

---

## EPIC-03 — Configuration and Inheritance Engine

### STORY-030 — Implement hierarchical configuration with inheritance

Type: Story
Owner role: backend-dev
Priority: Critical
Phase: 1
Epic: EPIC-03
Status: Promoted to runtime - READY_FOR_DEV
Dependencies: STORY-021

Description:
Implement the configuration engine that supports inheritance from country → region → city → institution → unit.

Acceptance criteria:
- Given a country-level setting, when queried at institution level, then the country value is returned if not overridden
- Given an institution-level override, when queried, then the institution value takes precedence
- Given a locked field at country level, when a lower level tries to override, then the override is rejected
- Given an extensible field, when a lower level adds options, then both inherited and local options are available
- Given a configuration change at region level, when queried at institution level within that region, then the new value is inherited

---

### STORY-031 — Implement configuration validation and inheritance-break detection

Type: Story
Owner role: Dev
Priority: High
Phase: 1
Epic: EPIC-03
Status: Draft
Dependencies: STORY-030

Description:
Detect and report when a lower level attempts to break inheritance rules or when an update would affect overridden configurations.

Acceptance criteria:
- Given a locked field override attempt, when submitted, then a validation error is returned
- Given an inheritance break request, when submitted with justification, then it is recorded with audit trail
- Given a country config update, when institutions have overrides, then a compatibility report lists affected institutions

---

## EPIC-04 — Framework Release and Update Manager

### STORY-040 — Design release package format and compatibility checker

Type: Story
Owner role: SA
Priority: High
Phase: 1
Epic: EPIC-04
Status: Draft
Dependencies: STORY-030

Description:
Define the format of versioned release packages and design the compatibility checking tool.

Acceptance criteria:
- Given a release package, when inspected, then it contains version, release notes, migration scripts, compatibility metadata
- Given a country's current config, when the compatibility checker runs against a new release, then it reports conflicts and required actions
- Given a release with breaking changes, when the compatibility report is generated, then it identifies affected configurations and suggests migration steps

---

## EPIC-05 — Country Template System

### STORY-050 — Define country template schema and builder concept

Type: Story
Owner role: SA
Priority: High
Phase: 1
Epic: EPIC-05
Status: Draft
Dependencies: STORY-030

Description:
Define the structure of a country education template including all configurable dimensions.

Acceptance criteria:
- Given a country template, when created, then it includes: education stages, institution types, grade scales, required subjects, academic calendar, semester structure, attendance rules, teacher roles, legal constraints, evidence links, version, approval status
- Given a template, when versioned, then previous versions are preserved
- Given a template, when not approved, then it is marked as draft

---

## EPIC-06 — Poland MVP Template

### SPIKE-001 — Research Polish education system for country template

Type: Research Spike
Owner role: PO / SA
Priority: Critical
Phase: 0
Epic: EPIC-06
Status: Draft
Dependencies: None

Description:
Research public/open sources about the Polish education system and prepare Poland Template v1.

Acceptance criteria:
- Sources are listed with URLs or references
- Education levels are documented (przedszkole, szkoła podstawowa, liceum, technikum, szkoła branżowa, studia)
- Institution types are documented
- Grade scale is documented (1-6 for schools, 2-5 for universities)
- Semester/school-year assumptions are documented (September start, two semesters)
- Common subjects are proposed for primary and secondary education
- Academic calendar rules are documented
- Unknowns are listed for validation

---

### STORY-060 — Implement Poland country template v1

Type: Story
Owner role: Dev
Priority: Critical
Phase: 2
Epic: EPIC-06
Status: Draft
Dependencies: SPIKE-001, STORY-050, STORY-030

Description:
Implement the Poland country template based on research findings.

Acceptance criteria:
- Given the Poland template, when loaded, then all Polish education stages are configured
- Given the Poland template, when a school is created, then Polish grade scale (1-6) is the default
- Given the Poland template, when configuring subjects, then mandatory Polish subjects are pre-loaded
- Given the Poland template, when creating an academic year, then it defaults to September start with two semesters

---

## EPIC-08 — User, Role, and Access Management

### STORY-080 — Implement user registration and authentication

Type: Story
Owner role: Dev
Priority: Critical
Phase: 1
Epic: EPIC-08
Status: Draft
Dependencies: STORY-010, STORY-011

Description:
Implement user registration, login, and session management with Spring Security.

Acceptance criteria:
- Given valid credentials, when a user logs in, then a JWT/session token is issued
- Given invalid credentials, when login is attempted, then access is denied with appropriate error
- Given an expired token, when an API is called, then a 401 is returned
- Given a new user, when registered by admin, then they can log in with provided credentials

---

### STORY-081 — Implement role-based access control (RBAC)

Type: Story
Owner role: Dev
Priority: Critical
Phase: 1
Epic: EPIC-08
Status: Draft
Dependencies: STORY-080

Description:
Implement RBAC with predefined roles for the education hierarchy.

Acceptance criteria:
- Given roles (country-admin, region-admin, city-admin, institution-admin, teacher, student, parent), when assigned, then the user has only permissions matching the role
- Given a teacher role, when they try to access another school's data, then access is denied
- Given an institution-admin, when they manage their school, then all school operations are permitted
- Given a parent role, when they view data, then only their child's data is visible

---

### STORY-082 — Implement MFA for administrator accounts

Type: Story
Owner role: Dev
Priority: High
Phase: 1
Epic: EPIC-08
Status: Draft
Dependencies: STORY-080

Description:
Require multi-factor authentication for all administrator-level accounts.

Acceptance criteria:
- Given an admin account, when logging in, then MFA is required after password
- Given MFA is configured, when a valid TOTP code is provided, then login succeeds
- Given MFA is configured, when an invalid code is provided, then login is denied
- Given a non-admin account, when logging in, then MFA is optional

---

## EPIC-09 — School Core Module

### STORY-090 — Implement class management

Type: Story
Owner role: Dev
Priority: Critical
Phase: 2
Epic: EPIC-09
Status: Draft
Dependencies: STORY-021, STORY-060

Description:
Implement class (grupa/klasa) CRUD operations with teacher and student assignment.

Acceptance criteria:
- Given an institution admin, when creating a class, then the class is created with name, year, and assigned class teacher
- Given a class, when students are assigned, then they appear in the class roster
- Given a class, when queried, then all assigned students and teachers are returned
- Given a student transfer, when moved to another class, then the change is recorded in audit trail

---

### STORY-091 — Implement subject management

Type: Story
Owner role: Dev
Priority: Critical
Phase: 2
Epic: EPIC-09
Status: Draft
Dependencies: STORY-030, STORY-060

Description:
Implement subject configuration supporting inherited mandatory subjects and optional institution-level additions.

Acceptance criteria:
- Given the Poland template, when a school is created, then mandatory subjects from country config are available
- Given an institution, when adding an optional subject, then it is added alongside mandatory subjects
- Given an institution, when trying to remove a mandatory subject, then the action is rejected
- Given a subject, when assigned to a class with a teacher, then it appears in the curriculum

---

### STORY-092 — Implement weekly schedule/timetable

Type: Story
Owner role: Dev
Priority: Critical
Phase: 2
Epic: EPIC-09
Status: Draft
Dependencies: STORY-090, STORY-091

Description:
Implement weekly timetable management for classes.

Acceptance criteria:
- Given a class, when a schedule is created, then lessons can be assigned to time slots per day
- Given a lesson slot, when assigned, then it links to a subject, teacher, and room
- Given a schedule conflict (same teacher at same time), when saving, then validation error is shown
- Given a student or parent, when viewing schedule, then the full weekly timetable is displayed

---

### STORY-093 — Implement homework assignment and submission

Type: Story
Owner role: Dev
Priority: High
Phase: 2
Epic: EPIC-09
Status: Draft
Dependencies: STORY-091, STORY-090

Description:
Allow teachers to assign homework and students to submit/view assignments.

Acceptance criteria:
- Given a teacher, when creating homework, then it has title, description, subject, class, deadline
- Given a student, when viewing homework, then all active assignments for their classes are listed
- Given a student, when submitting homework, then the submission is recorded with timestamp
- Given a teacher, when reviewing submissions, then they can see all student submissions for an assignment
- Given a deadline passed, when a student has not submitted, then the status shows as overdue

---

### STORY-094 — Implement grade/year-based student themes

Type: Story
Owner role: Dev
Priority: Medium
Phase: 3
Epic: EPIC-09
Status: Draft
Dependencies: STORY-014, STORY-030, STORY-090, STORY-180

Description:
Allow the student-facing application to apply different designer-approved visual themes based on the student's school grade/year level or configured grade band, so younger and older students can receive age-appropriate styling without changing permissions, data visibility, or country-specific code.

Acceptance criteria:
- Given a student assigned to grade/year 3, when they log in, then the student UI applies the theme configured for grade/year 3 or its configured grade band
- Given a student assigned to grade/year 4, when they log in, then the student UI applies the theme configured for grade/year 4 or its configured grade band, and it may differ from the grade/year 3 theme
- Given no grade/year-specific theme is configured, when a student logs in, then a default student theme is applied without breaking navigation, readability, or accessibility
- Given a theme mapping is configured, when reviewed, then theme selection is driven by generic configuration/data and reusable design assets rather than country-specific code branches
- Given a student's theme is applied, when their session is used, then the change affects visual presentation only and does not alter role permissions, available features, or student-data visibility rules

---

### STORY-095 — Implement institution-level extracurricular and supplemental lesson offerings

Type: Story
Owner role: Dev
Priority: High
Phase: 3
Epic: EPIC-09
Status: Draft
Dependencies: STORY-090, STORY-091, STORY-092

Description:
Allow each school/institution to manage additional non-core and supplemental lesson offerings attached to that institution, such as karate, martial arts, robotics, English reinforcement, additional math lessons, and other locally available programs. Business value: this lets schools present a fuller educational offer, supports parent/student choice, and creates a reusable framework path for optional activities without country-specific code.

Acceptance criteria:
- Given an institution admin, when they create or update an additional offering, then they can define at least: offering name, area/category, description, target grade/age range, optional schedule details, capacity, active/inactive status, and whether the offering is extracurricular or supplemental academic support
- Given an institution has additional offerings, when parents, students, or staff view the institution profile or relevant catalog/API, then the currently active offerings are listed with their main descriptive details
- Given offerings such as karate, martial arts, robotics, English reinforcement, additional math lessons, or another institution-defined activity, when configured, then they are represented as data/configuration entries rather than hard-coded special-case behavior
- Given an offering is no longer available, when an institution admin deactivates it, then it no longer appears in the active offering list but remains auditable/historically traceable
- Given the framework is reviewed, when this capability is implemented, then it supports arbitrary institution-defined additional lessons/sections without country-specific code branches or schema changes per activity type

---

## EPIC-12 — Meal and Catering Management

### STORY-120 — Implement institution-level catering configuration

Type: Story
Owner role: Dev
Priority: High
Phase: 2
Epic: EPIC-12
Status: Draft
Dependencies: STORY-021

Description:
Allow institution admins to configure catering providers, meal types, menus, and billing rules.

Acceptance criteria:
- Given an institution admin, when configuring catering, then they can set provider name, meal types, and default prices
- Given catering config, when setting exclusion deadlines, then a cutoff time for same-day exclusion is defined
- Given catering config, when setting billing period, then monthly billing is the default

---

### STORY-121 — Implement meal subscription and parent exclusion

Type: Story
Owner role: Dev
Priority: High
Phase: 2
Epic: EPIC-12
Status: Draft
Dependencies: STORY-120

Description:
Allow parents to manage their child's meal subscriptions and exclude meals.

Acceptance criteria:
- Given a parent, when subscribing their child to meals, then the subscription is recorded
- Given a parent, when excluding a future day, then the exclusion is saved and creates a pending credit
- Given a parent, when excluding today before deadline, then the exclusion is accepted
- Given a parent, when excluding today after deadline, then the exclusion is rejected
- Given meal exclusion, when it is not connected to attendance, then excluding a meal does not mark attendance as absent

---

### STORY-122 — Implement meal billing and credit adjustment

Type: Story
Owner role: Dev
Priority: High
Phase: 2
Epic: EPIC-12
Status: Draft
Dependencies: STORY-121

Description:
Track paid/unpaid status and apply end-of-month credit adjustments for valid exclusions.

Acceptance criteria:
- Given a billing period, when generated, then each subscribed student has a payment record (paid/unpaid)
- Given valid exclusions during the month, when month closes, then credits are calculated and applied
- Given a parent, when viewing payment status, then they see paid/unpaid and any pending credits
- Given an admin, when manually correcting a record, then the correction is recorded in audit trail

---

## EPIC-13 — Attendance

### STORY-130 — Implement per-lesson attendance for schools

Type: Story
Owner role: Dev
Priority: Critical
Phase: 2
Epic: EPIC-13
Status: Draft
Dependencies: STORY-092

Description:
Allow teachers to record attendance per lesson for each student.

Acceptance criteria:
- Given a teacher in a lesson, when recording attendance, then each student can be marked: present, absent, excused, late
- Given attendance records, when a parent submits an excuse, then the absence can be changed to excused
- Given attendance data, when queried per student, then a summary shows total present/absent/excused/late
- Given attendance data, when queried per class, then statistics are available for dashboards

---

## EPIC-14 — Gradebook and Assessment

### STORY-140 — Implement configurable gradebook

Type: Story
Owner role: Dev
Priority: Critical
Phase: 2
Epic: EPIC-14
Status: Draft
Dependencies: STORY-091, STORY-030

Description:
Implement gradebook with configurable grade scales inherited from country template.

Acceptance criteria:
- Given the Poland grade scale (1-6), when a teacher enters a grade, then only valid values are accepted
- Given a student/subject, when grades are entered, then they appear in chronological order in the gradebook
- Given a grade, when it includes weight/category, then semester calculation can use weighted average
- Given grade entry, when an error is made, then the teacher can correct with audit trail
- Given semester end, when grades exist, then a semester average/final grade can be calculated or manually set

---

## EPIC-16 — AI Student Assistant

### STORY-160 — Implement basic AI student chat with safety boundaries

Type: Story
Owner role: Dev
Priority: High
Phase: 4
Epic: EPIC-16
Status: Draft
Dependencies: STORY-093

Description:
Implement the student AI assistant that helps with learning while refusing direct homework answers.

Acceptance criteria:
- Given a student doing homework, when they ask AI for help, then AI explains the concept
- Given a student, when they ask AI to solve the assigned problem directly, then AI refuses and offers guidance instead
- Given a student, when they ask for a similar example, then AI provides one with explanation
- Given AI interactions, when logged, then usage statistics are recorded
- Given AI responses, when generated, then they are adapted to the student's grade/age level

---

## EPIC-17 — AI Teacher Assistant

### STORY-170 — Implement basic AI teacher assistant for lesson preparation

Type: Story
Owner role: Dev
Priority: High
Phase: 4
Epic: EPIC-17
Status: Draft
Dependencies: STORY-091

Description:
Provide AI assistance for teachers to prepare lessons, tests, and homework.

Acceptance criteria:
- Given a teacher, when requesting AI help for lesson preparation, then AI generates a structured lesson plan
- Given a teacher, when requesting test generation for a subject/topic, then AI generates questions with answer key
- Given a teacher, when requesting homework generation, then AI creates appropriate assignments for the grade level
- Given AI teacher sessions, when logged, then usage is tracked per teacher

---

## EPIC-18 — Dashboards and Statistics

### STORY-180 — Implement basic school-level dashboard

Type: Story
Owner role: Dev
Priority: High
Phase: 2
Epic: EPIC-18
Status: Draft
Dependencies: STORY-130, STORY-140

Description:
Provide basic dashboard metrics at the school, class, teacher, and student level.

Acceptance criteria:
- Given a school admin, when viewing the dashboard, then attendance rates, grade averages, and homework completion rates are shown
- Given a teacher, when viewing their dashboard, then per-class statistics are displayed
- Given a parent, when viewing their child's dashboard, then grades, attendance, and upcoming homework are shown
- Given a student, when viewing their dashboard, then personal grades, schedule, and assignments are displayed

---

## EPIC-19 — Security, Privacy, and Audit

### STORY-190 — Implement encryption at rest and in transit

Type: Story
Owner role: Dev
Priority: Critical
Phase: 1
Epic: EPIC-19
Status: Draft
Dependencies: STORY-010, STORY-011

Description:
Ensure all data is encrypted at rest in the database and in transit via TLS.

Acceptance criteria:
- Given database storage, when inspected at disk level, then data is encrypted
- Given any API communication, when intercepted, then TLS 1.2+ encryption is active
- Given sensitive fields (passwords, PII), when stored, then additional application-level encryption is applied
- Given encryption keys, when managed, then they are stored in a secrets vault, not in code

---

### STORY-191 — Implement API rate limiting and security headers

Type: Story
Owner role: Dev
Priority: High
Phase: 1
Epic: EPIC-19
Status: Draft
Dependencies: STORY-012

Description:
Protect APIs with rate limiting and standard security headers.

Acceptance criteria:
- Given an API endpoint, when called excessively by one client, then rate limiting blocks further requests with 429 status
- Given any API response, when headers are inspected, then security headers are present (X-Content-Type-Options, X-Frame-Options, Strict-Transport-Security, etc.)
- Given rate limit configuration, when set per endpoint, then different limits apply to different endpoints

---

## EPIC-07 — Institution Schema Packs

## EPIC-22 — Internationalisation (i18n)

### STORY-220 — Design and implement database-backed translation storage

Type: Story
Owner role: SA / Dev
Priority: Critical
Phase: 1
Epic: EPIC-22
Status: Draft
Dependencies: STORY-010, STORY-011

Description:
Design and implement the persistence model for UI translations. Every user-visible label, message, or text string must be stored as a translation entry in the database, keyed by a translation key, a language code, and an optional namespace/context. No translatable strings may be hard-coded in application source code or template files.

Acceptance criteria:
- Given the schema, when inspected, then a `translation` table exists with columns: id, translation_key, language_code (BCP 47), namespace, value, version, created_at, updated_at
- Given a translation key, when queried for a language that has no entry, then a fallback chain (country default → English) is followed and the result is returned without error
- Given all translation keys, when queried, then no duplicate (key + language_code + namespace) combinations exist
- Given translations, when the application starts, then they are loaded and cached with a configurable TTL
- Given a translation value, when updated via API, then the cache is invalidated and the new value is served within the configured TTL
- Given audit requirements, when a translation is changed, then an audit record is created with actor, old value, new value, and timestamp
- Given the data model, when reviewed, then no language or locale logic resides in application Java/Kotlin code

---

### STORY-221 — Implement global language catalogue and country-level language configuration

Type: Story
Owner role: Dev
Priority: Critical
Phase: 1
Epic: EPIC-22
Status: Draft
Dependencies: STORY-220, STORY-030

Description:
Seed the database with the full global default language catalogue (200 + languages, BCP 47 codes). Allow each country configuration to declare an active subset. Languages outside the active subset are dormant but never deleted from the catalogue. No language-specific conditional code is permitted.

Acceptance criteria:
- Given the database is initialised, when the language catalogue is queried, then all languages from the default list are present with their BCP 47 code, English name, native name, text direction (LTR/RTL/TTB), and active flag
- Given a country configuration, when `active_languages` is set to a subset, then only those language codes are returned by the public language list API for that deployment
- Given a language not in the country's active list, when a user requests it, then the API returns 404 / "not available in this deployment"
- Given the framework code, when inspected, then no `if language == "ar"` or equivalent conditional branches exist; all differences are driven by data attributes (e.g., `text_direction`)
- Given the catalogue, when a new language is added by an administrator, then it becomes available system-wide without a code deployment
- Given the country config, when `active_languages` is absent or null, then all 200 + catalogue languages are active by default

---

### STORY-222 — Implement translation management API

Type: Story
Owner role: Dev
Priority: Critical
Phase: 1
Epic: EPIC-22
Status: Draft
Dependencies: STORY-220, STORY-081

Description:
Expose a REST API for CRUD operations on translation entries, bulk import/export, and missing-translation reporting. Access is restricted by role.

Acceptance criteria:
- Given a country-admin or global-admin, when calling `POST /api/translations`, then a new translation entry is created
- Given a country-admin or global-admin, when calling `PUT /api/translations/{id}`, then the entry is updated and the audit trail records the change
- Given any authenticated user, when calling `GET /api/translations?lang={code}&ns={namespace}`, then all active translations for that language and namespace are returned
- Given a `GET /api/translations/missing?lang={code}`, when called by an admin, then all keys that have no entry for the requested language are listed
- Given a bulk import endpoint `POST /api/translations/import`, when a valid JSON/CSV payload is provided, then all entries are upserted atomically and a report of created/updated/skipped counts is returned
- Given a bulk export endpoint `GET /api/translations/export?lang={code}`, when called, then a downloadable JSON or CSV file is returned containing all translations for that language
- Given an unauthenticated request to any write endpoint, when received, then 401 is returned

---

### STORY-223 — Implement front-end label resolution and RTL/LTR rendering support

Type: Story
Owner role: Dev
Priority: Critical
Phase: 1
Epic: EPIC-22
Status: Draft
Dependencies: STORY-221, STORY-222

Description:
Integrate the translation system into the front-end layer so that every rendered label is resolved from the database-backed translation store. Support bi-directional text layout (LTR/RTL) driven entirely by the language's `text_direction` data attribute — no hard-coded layout branches per language.

Acceptance criteria:
- Given a page load, when the user's preferred language is set, then all visible labels are rendered in that language
- Given a missing translation for the user's language, when a page is rendered, then the fallback translation (country default, then English) is shown and a warning is logged
- Given a language with `text_direction = RTL`, when the page renders, then the layout direction is applied automatically via the `text_direction` value from the catalogue — no per-language conditional code exists in the front-end
- Given a language switch by the user, when the preference is saved, then subsequent page loads reflect the new language without a full re-login
- Given any UI component, when its label string is inspected in source code, then no hard-coded human-readable text is present — only translation keys are used

---

### STORY-224 — Implement translation administration UI

Type: Story
Owner role: Dev
Priority: High
Phase: 2
Epic: EPIC-22
Status: Draft
Dependencies: STORY-222, STORY-223

Description:
Provide an administration interface for managing translations, reviewing missing-translation coverage, and bulk importing/exporting translation files.

Acceptance criteria:
- Given a country-admin, when accessing the translation management screen, then they can browse all translation keys and their values per active language
- Given a country-admin, when editing a translation value, then the change is saved via the API and the updated text appears immediately
- Given a country-admin, when viewing the coverage report, then a percentage and list of untranslated keys are shown per language
- Given a country-admin, when uploading a translation file (JSON or CSV), then all valid entries are imported and a summary is displayed
- Given a country-admin, when downloading a translation file for a language, then an export file is generated

---

### STORY-225 — Implement per-user language preference

Type: Story
Owner role: Dev
Priority: High
Phase: 2
Epic: EPIC-22
Status: Draft
Dependencies: STORY-080, STORY-221, STORY-223

Description:
Allow every user to select their preferred display language from the set of languages active in their country deployment. The preference is stored on the user profile and applied on every session.

Acceptance criteria:
- Given a user, when they select a language from the active language list, then the preference is saved to their profile
- Given a saved language preference, when the user logs in on any device, then the UI is presented in their preferred language
- Given the user's preferred language having incomplete translations, when rendering, then the fallback chain applies and no untranslated keys are rendered as raw keys
- Given a country deployment that activates a subset of languages, when a user tries to set a language outside that subset, then the selection is rejected with a clear error message

---

### STORY-070 — Design and implement schema pack loading mechanism

Type: Story
Owner role: Dev
Priority: Critical
Phase: 1
Epic: EPIC-07
Status: Draft
Dependencies: STORY-010, STORY-030

Description:
Implement the mechanism that loads institution-type-specific schema packs (school, kindergarten, university).

Acceptance criteria:
- Given an institution of type "school", when created, then the school schema pack modules are activated
- Given an institution of type "kindergarten", when created, then the kindergarten schema pack modules are activated
- Given a schema pack, when loaded, then it provides type-specific entities, APIs, and configuration options
- Given a shared module (e.g., meals), when used by different packs, then it adapts to the institution type context

