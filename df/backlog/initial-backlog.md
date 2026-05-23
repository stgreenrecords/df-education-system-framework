# Initial Backlog

## STORY-001 — Define final product vision

Type: Story
Owner role: PO
Priority: Critical
Phase: 0
Status: Draft

Description:
Define the final product vision for the sovereign Education System Framework.

Acceptance criteria:

- Product vision explains country/ministry-operated deployments.
- Product vision explains generic-first design with Poland as MVP reference.
- Product vision explains schools, kindergartens, and universities.
- Product vision explains roadmap/backlog as the primary output of the initial prompt.

## SPIKE-001 — Research Polish education system for country template

Type: Research Spike
Owner role: PO / SA
Priority: Critical
Phase: 0
Status: Draft

Description:
Research public/open sources about the Polish education system and prepare Poland Template v1.

Acceptance criteria:

- Sources are listed.
- Education levels are documented.
- Institution types are documented.
- Grade scale is documented.
- Semester/school-year assumptions are documented.
- Common subjects are proposed.
- Unknowns are listed for validation.

## STORY-002 — Define configuration inheritance rules

Type: Story
Owner role: SA
Priority: Critical
Phase: 1
Status: Draft

Description:
Define how configuration flows from country to region, city, institution, academic unit, and users.

Acceptance criteria:

- Locked inherited fields are defined.
- Extendable fields are defined.
- Inheritance-break behavior is defined.
- Compatibility-check behavior is defined.
- Examples are provided for subjects, grading, schedules, and meal rules.

## STORY-003 — Define sovereign deployment model

Type: Story
Owner role: SA
Priority: Critical
Phase: 1
Status: Draft

Description:
Define how each country/ministry fully operates its own infrastructure while still receiving framework releases.

Acceptance criteria:

- Country owns infrastructure, data, backups, monitoring, deployment, and access.
- Vendor provides release packages, documentation, migration scripts, and support.
- Dev/QA/Stage/Prod flow is described.
- Update approval flow is described.

## STORY-004 — Define meal and catering MVP

Type: Story
Owner role: PO
Priority: High
Phase: 2
Status: Draft

Description:
Define Meal and Catering v1 for school/kindergarten level.

Acceptance criteria:

- Catering is configured only at school/kindergarten level.
- Payment is tracked as paid/unpaid only.
- Parent can exclude today before deadline and future days.
- Meal exclusion is not automatically connected to attendance.
- Valid exclusions create credit/balance adjustment at end of month.
- Admin can manually correct payment/exclusion status with audit trail.

## STORY-005 — Define schema-pack architecture

Type: Story
Owner role: SA
Priority: Critical
Phase: 1
Status: Draft

Description:
Define shared core plus school, kindergarten, and university schema packs.

Acceptance criteria:

- Shared institution core is defined.
- School schema pack is drafted.
- Kindergarten schema pack is drafted.
- University schema pack is drafted.
- Extension mechanism is described.

## STORY-006 — Define AI student assistant rules

Type: Story
Owner role: PO / SA
Priority: High
Phase: 4
Status: Draft

Description:
Define student AI assistant boundaries for homework and learning support.

Acceptance criteria:

- AI can explain topics.
- AI can provide similar examples.
- AI can ask guiding questions.
- AI cannot provide direct answers to assigned tasks.
- Visibility of AI conversation history remains an open governance decision.

## STORY-007 — Define Jira-ready backlog format

Type: Story
Owner role: PO
Priority: Medium
Phase: 0
Status: Draft

Description:
Define tracker-neutral Markdown format that can later be imported or synced into Jira.

Acceptance criteria:

- Epic/story/task hierarchy is defined.
- Required fields are defined.
- Mapping to Jira issue types is proposed.
- Labels/components/releases strategy is proposed.
