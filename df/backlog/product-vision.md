# Product Vision — Education System Framework

## Vision statement

Build a sovereign, configurable, API-first Education System Framework that enables countries and ministries to operate their own fully independent education management infrastructure while benefiting from a shared, versioned, continuously improved core framework.

## Problem

Education systems worldwide lack affordable, flexible, sovereign digital platforms. Most solutions are either proprietary SaaS systems that store data outside the country, monolithic systems that cannot adapt to different education models, or custom-built systems that are expensive to maintain and cannot share improvements across deployments.

## Solution

A generic Education System Framework that:

- Supports schools, kindergartens, universities, colleges, and other institutions from day one
- Allows each country/ministry to fully own and operate its infrastructure, data, and deployment
- Provides configuration inheritance from country → region → city → institution → unit → user
- Ships as versioned release packages with migration scripts, compatibility checks, and rollback guidance
- Uses an API-first, headless architecture enabling web, mobile, desktop, AI, and third-party integrations
- Includes schema packs for different institution types (school, kindergarten, university)
- Provides AI assistance for students and teachers within controlled boundaries
- Supports configurable grading, attendance, scheduling, meals, dashboards, and assessments

## Target users

| User type | Context |
|---|---|
| Ministry / Country admin | Configures country-wide education rules, subjects, calendars, grading scales |
| Regional / City admin | Manages institutions within their area |
| Institution admin | Manages school/kindergarten/university operations |
| Teacher / Lecturer | Manages classes, grades, homework, attendance, AI-assisted lesson prep |
| Student | Views grades, homework, schedule, uses AI learning assistant |
| Parent / Guardian | Views child's progress, manages meal exclusions, communicates with school |

## Key principles

1. **Configuration over customization** — behavior is controlled through settings, not code forks
2. **Extension over modification** — new functionality is added through extension points, not core changes
3. **Inheritance over duplication** — configuration flows down the hierarchy with override capabilities
4. **Compatibility over one-off changes** — centralized updates remain safe across all deployments
5. **Sovereignty over convenience** — each country fully controls its data, infrastructure, and deployment

## MVP reference

Poland is the first reference implementation. The Poland country template will be created by researching public/open sources about the Polish education system, establishing the evidence-based approach that will later be used for other countries.

## Success metrics (future)

- Framework can be deployed independently by a country ministry without vendor access to production
- Configuration inheritance works across all hierarchy levels without breaking updates
- School schema pack supports full academic year operations (schedule, attendance, grades, homework, meals)
- AI assistants operate within safety boundaries (no direct homework answers)
- Centralized updates can be applied without data loss or breaking country customizations

## Scope boundaries

### In scope for the product

- Generic education platform core
- Country/institution configuration engine with inheritance
- Schema packs: school, kindergarten, university
- Sovereign deployment model (country-operated)
- Framework release/update manager
- User/role/access management (RBAC + ABAC)
- Academic operations: subjects, classes, schedule, attendance, gradebook, homework
- Meal and catering management (institution-level)
- AI student assistant (guided learning, no direct answers)
- AI teacher assistant (lesson prep, test generation, analytics)
- Dashboards at all hierarchy levels
- Security, audit, encryption, zero-trust
- Poland as first country template
- Markdown backlog with Jira-ready structure

### Out of scope for MVP

- Online payment gateway integration
- SMS/push notification system
- Parent-teacher video conferencing
- E-book/LMS content delivery
- Student information system (SIS) import/migration tools
- Mobile app implementation (API-first enables it later)
- Multi-country simultaneous deployment
- Real-time collaboration features
- Gamification
- Alumni management

### Out of scope permanently (framework does not do this)

- Direct operation of country production infrastructure
- Storage of country data outside country territory
- Vendor-initiated production deployments without country approval

