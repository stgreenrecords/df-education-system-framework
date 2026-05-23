# Domain Model — Education System Framework

## Core domain concepts

### Hierarchy / Organizational structure

```text
Tenant (License)
├── Country Configuration
│   ├── Education Stages (e.g., primary, secondary, higher)
│   ├── Grade Scales
│   ├── Required Subjects
│   ├── Academic Calendar Rules
│   ├── Legal/Privacy Constraints
│   └── Country Template (versioned, evidence-based)
├── Region / Voivodeship / State
│   └── Regional overrides & extensions
├── City / District / Municipality
│   └── City-level overrides & extensions
├── Institution
│   ├── Institution Type (school / kindergarten / university)
│   ├── Schema Pack (loaded based on type)
│   ├── Local Configuration
│   ├── Catering Setup
│   └── Extension Packs
├── Academic Unit
│   ├── Faculty / Department (university)
│   ├── School Unit / Year Group (school)
│   ├── Class / Group / Course
│   └── Semester / Term / Academic Year
└── Person
    ├── Teacher / Lecturer / Caregiver
    ├── Student / Child
    ├── Parent / Guardian
    └── Admin (institution / city / region / country)
```

### Configuration inheritance

```text
Country Config (locked rules + extensible areas)
  ↓ inherits
Region Config (may extend, cannot break locked rules)
  ↓ inherits
City Config (may extend, cannot break locked rules)
  ↓ inherits
Institution Config (may extend, applies schema pack)
  ↓ inherits
Unit Config (class/group/course-level settings)
```

**Locked fields**: Cannot be overridden by lower levels (e.g., national grade scale, mandatory subjects).
**Extensible fields**: Lower levels can add options (e.g., optional subjects, local activities, custom schedules).
**Inheritance break**: Explicit action requiring compatibility check and documented justification.

---

## Bounded contexts

### 1. Platform Core

| Concept | Description |
|---|---|
| Tenant | License holder (typically a country/ministry) |
| Deployment | Sovereign infrastructure instance |
| Configuration | Hierarchical settings with inheritance |
| Release | Versioned framework package |
| Migration | Database/config migration script |
| Audit Trail | Immutable log of all changes |

### 2. Identity & Access

| Concept | Description |
|---|---|
| User | Authenticated person with identity |
| Role | Named set of permissions (admin, teacher, student, parent...) |
| Permission | Granular action allowed on a resource |
| Attribute | Context for access decisions (institution, class, subject...) |
| Session | Authenticated user context |
| MFA | Multi-factor authentication for admins |

### 3. Organization

| Concept | Description |
|---|---|
| Country | Top-level configuration and data boundary |
| Region | Administrative subdivision |
| City | Municipal unit |
| Institution | School, kindergarten, or university |
| Faculty/Department | University sub-organization |
| Class/Group | Collection of students for instruction |
| Academic Year | Time boundary for academic operations |
| Semester/Term | Sub-period within academic year |

### 4. Academic (School)

| Concept | Description |
|---|---|
| Subject | Taught discipline |
| Schedule | Timetable of lessons |
| Lesson | Single teaching period |
| Attendance | Per-lesson or daily presence record |
| Grade | Assessment score within configured scale |
| Gradebook | Collection of grades per student/subject/period |
| Homework | Assigned work with deadline |
| Exam/Test | Formal assessment event |
| Semester Grade | Calculated or manually set period grade |

### 5. Academic (Kindergarten)

| Concept | Description |
|---|---|
| Child Group | Age-based or mixed group |
| Daily Routine | Structured day activities |
| Check-in/Check-out | Arrival and departure tracking |
| Authorized Pickup | Approved persons for child collection |
| Developmental Note | Observation about child progress |
| Allergy | Food/medical allergy record |
| Incident Report | Safety or behavioral incident |
| Nap/Rest | Scheduled rest periods |

### 6. Academic (University)

| Concept | Description |
|---|---|
| Degree Program | Study path (BSc, MSc, PhD...) |
| Course | Taught unit with credits |
| ECTS/Credits | Credit value of courses |
| Enrollment | Student registration for course |
| Prerequisite | Required prior course |
| Exam Session | Formal exam period |
| Retake | Repeated exam attempt |
| Transcript | Official grade record |
| Thesis | Final project/paper |
| Elective | Optional course choice |

### 7. Meal & Catering

| Concept | Description |
|---|---|
| Catering Provider | External or internal food service |
| Menu | Offered meals for a period |
| Meal Type | Breakfast, lunch, snack... |
| Meal Subscription | Student enrollment for meals |
| Meal Exclusion | Parent removes child from a meal day |
| Exclusion Deadline | Cutoff time for same-day exclusion |
| Payment Status | Paid / Unpaid per period |
| Credit Adjustment | End-of-month balance correction |
| Billing Period | Monthly billing cycle |

### 8. AI Assistance

| Concept | Description |
|---|---|
| Student AI Session | AI help session during homework |
| Teacher AI Session | AI help for lesson/test preparation |
| AI Safety Rules | Boundaries preventing direct answers |
| AI Conversation | Exchange of messages with AI |
| AI Usage Log | Record of AI interactions |
| Visibility Policy | Who can see AI conversations |

### 9. Dashboards & Analytics

| Concept | Description |
|---|---|
| Dashboard | Visual summary at a hierarchy level |
| Metric | Measured value (attendance rate, avg grade...) |
| Report | Generated document with statistics |
| Alert | Threshold-triggered notification |
| Trend | Change over time |

### 10. Framework Release Management

| Concept | Description |
|---|---|
| Release Package | Versioned bundle of updates |
| Compatibility Report | Checks against country config |
| Migration Script | Database/config update procedure |
| Release Notes | Human-readable change description |
| Security Patch | Critical fix with severity classification |
| Country Version | Currently deployed version per country |
| Approval Workflow | Country must approve before deploy |
| Rollback Plan | Steps to revert a failed update |

---

## Entity relationship summary (core)

```text
Country 1──* Region 1──* City 1──* Institution 1──* AcademicUnit
Institution 1──1 InstitutionType
Institution 1──1 SchemaPack
Institution 1──* User (via role assignment)
AcademicUnit 1──* Student (enrollment)
AcademicUnit 1──* Teacher (assignment)
Subject *──* AcademicUnit (curriculum)
Schedule 1──* Lesson
Lesson *──1 Subject
Lesson *──1 Teacher
Lesson *──1 AcademicUnit
Attendance *──1 Student, *──1 Lesson
Grade *──1 Student, *──1 Subject
Homework *──1 Subject, *──1 AcademicUnit
MealSubscription *──1 Student, *──1 CateringProvider
MealExclusion *──1 MealSubscription
```

---

## Aggregates (initial design)

| Aggregate root | Contains |
|---|---|
| Country | Regions, CountryConfig, CountryTemplate |
| Institution | AcademicUnits, InstitutionConfig, CateringSetup |
| AcademicUnit | Enrollments, Schedule, Curriculum |
| Student | Grades, Attendance, MealSubscriptions, AIUsage |
| Teacher | Assignments, Lessons, AIUsage |
| Gradebook | Grades per student/subject/period |
| CateringProvider | Menus, MealTypes, BillingRules |
| Release | Package, Migrations, CompatibilityReport |

