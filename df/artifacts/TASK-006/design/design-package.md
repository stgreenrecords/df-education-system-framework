# Design Package - TASK-006

## Summary

This package defines low-fidelity block-scheme screens for the first website experience: a public home page, a login page, and role-specific student and teacher dashboards. The package is intentionally structural and grayscale so `frontend-dev` can implement the first UI skeleton without inventing layout, states, or the initial testable sign-in flow.

## Asset structure

All page-specific design assets live under the root project `design/` folder, using dedicated page-slug folders:

- `design/home-page/`
- `design/login-page/`
- `design/student-dashboard/`
- `design/teacher-dashboard/`

Current page-specific wireframe assets:

- `design/home-page/low-fi-wireframe.html`
- `design/login-page/low-fi-wireframe.html`
- `design/student-dashboard/low-fi-wireframe.html`
- `design/teacher-dashboard/low-fi-wireframe.html`

## Target frontend scope

`frontend/website`

## User flow

### 1. Home page
1. User lands on the public website home page.
2. User immediately sees the hero/banner with a value proposition and two primary actions:
   - `Institution selection`
   - `Account login`
3. User can review the supporting blocks below the banner:
   - institution discovery / selection preview
   - platform benefits / feature blocks
   - trust / support / footer information

### 2. Login page
1. User selects `Account login` from the home page hero or top navigation.
2. User lands on the login page and sees a simple sign-in form with `Username` and `Password`.
3. User submits credentials against `POST /api/v1/identity/auth/login`.
4. On success, the website resolves current identity context through `GET /api/v1/identity/me`.
5. The website routes the user to the first supported dashboard for this initial scope:
   - student-capable user -> student dashboard
   - teacher-capable user -> teacher dashboard
6. If credentials are invalid, the login page remains visible and shows an inline error.
7. If authentication succeeds but the role is outside the current four-page scope, the page shows a neutral "role not yet supported" state instead of routing to an undefined screen.

### 3. Student dashboard
1. Authenticated student enters the dashboard landing view.
2. Student sees a summary of today, quick metrics, class schedule, assignments/homework, grade highlights, and announcements.
3. Student uses quick actions to navigate deeper to schedule, homework, or grades.

### 4. Teacher dashboard
1. Authenticated teacher enters the dashboard landing view.
2. Teacher sees today's agenda, assigned classes, attendance queue, homework review queue, key alerts, and quick links.
3. Teacher can jump into teaching workflows such as attendance, assignments, and class views.

## Markup/static structure

### A. Home page block scheme

```text
┌────────────────────────────────────────────────────────────────────┐
│ Top navigation                                                    │
│ Logo | Product name | Language | Institution selection | Login    │
├────────────────────────────────────────────────────────────────────┤
│ Hero / banner                                                     │
│ Eyebrow: Generic education platform                               │
│ H1: Manage learning across institutions with one secure platform  │
│ Supporting text                                                   │
│ [ Institution selection ] [ Account login ]                       │
│ Optional secondary hint: Choose a school or continue to sign in   │
├────────────────────────────────────────────────────────────────────┤
│ Institution selection preview / search block                      │
│ Search/select field | recent institutions | quick categories      │
├────────────────────────────────────────────────────────────────────┤
│ Platform value blocks (3 columns desktop / stacked mobile)        │
│ Learning tools | Communication | Security / control               │
├────────────────────────────────────────────────────────────────────┤
│ Audience pathways                                                 │
│ Student | Teacher | Parent | Institution                          │
├────────────────────────────────────────────────────────────────────┤
│ Trust / support / deployment note                                 │
│ Sovereign deployment | API-first | Modular platform               │
├────────────────────────────────────────────────────────────────────┤
│ Footer                                                            │
│ About | Help | Accessibility | Legal | Contact                    │
└────────────────────────────────────────────────────────────────────┘
```

### B. Student dashboard block scheme

```text
┌────────────────────────────────────────────────────────────────────┐
│ Dashboard header                                                  │
│ Title + greeting | school / class context | user menu             │
├────────────────────────────────────────────────────────────────────┤
│ Quick summary cards                                               │
│ Today's lessons | Pending homework | New grades | Attendance      │
├────────────────────────────────────────────────────────────────────┤
│ Main content grid                                                 │
│ ┌───────────────────────────┬───────────────────────────────────┐ │
│ │ Today's schedule          │ Assignments / homework           │ │
│ │ time, subject, room       │ due soon, overdue, status        │ │
│ ├───────────────────────────┼───────────────────────────────────┤ │
│ │ Grade highlights          │ Announcements / school notices   │ │
│ │ latest marks, trend       │ teacher/school updates           │ │
│ └───────────────────────────┴───────────────────────────────────┘ │
├────────────────────────────────────────────────────────────────────┤
│ Quick actions row                                                 │
│ View schedule | Open homework | Open gradebook | Messages        │
└────────────────────────────────────────────────────────────────────┘
```

### C. Login page block scheme

```text
┌────────────────────────────────────────────────────────────────────┐
│ Top navigation                                                    │
│ Logo | Product name | Back to home | Help                         │
├────────────────────────────────────────────────────────────────────┤
│ Login shell                                                       │
│ ┌───────────────────────────┬───────────────────────────────────┐ │
│ │ Intro / trust panel       │ Sign-in form                      │ │
│ │ - Page title              │ Username field                    │ │
│ │ - Brief explanation       │ Password field                    │ │
│ │ - Security / access note  │ Remember session (optional later) │ │
│ │ - Support/help text       │ [ Sign in ]                       │ │
│ ├───────────────────────────┴───────────────────────────────────┤ │
│ │ Inline states: loading | invalid credentials | unsupported role│ │
│ └───────────────────────────────────────────────────────────────┘ │
├────────────────────────────────────────────────────────────────────┤
│ Secondary actions / helper links                                  │
│ Return to home | Institution selection | Contact support          │
└────────────────────────────────────────────────────────────────────┘
```

### D. Teacher dashboard block scheme

```text
┌────────────────────────────────────────────────────────────────────┐
│ Dashboard header                                                  │
│ Title + greeting | institution context | user menu                │
├────────────────────────────────────────────────────────────────────┤
│ Quick summary cards                                               │
│ Classes today | Attendance pending | Homework to review | Alerts  │
├────────────────────────────────────────────────────────────────────┤
│ Main content grid                                                 │
│ ┌───────────────────────────┬───────────────────────────────────┐ │
│ │ Today's teaching agenda   │ Class / group list               │ │
│ │ lesson timeline           │ roster shortcut blocks           │ │
│ ├───────────────────────────┼───────────────────────────────────┤ │
│ │ Attendance queue          │ Homework / assessment queue      │ │
│ │ not yet completed         │ drafts, due reviews              │ │
│ ├───────────────────────────┼───────────────────────────────────┤ │
│ │ Announcements / notices   │ Performance / status snapshot    │ │
│ │ school + class alerts     │ completion, quick metrics        │ │
│ └───────────────────────────┴───────────────────────────────────┘ │
├────────────────────────────────────────────────────────────────────┤
│ Quick actions row                                                 │
│ Start attendance | Create homework | Open classes | View notices  │
└────────────────────────────────────────────────────────────────────┘
```

## Components and states

### Shared components
- Top navigation / shell header
- Page title block
- Summary metric card
- Section card with title + action link
- Quick action button group
- Announcement / notice list item
- Empty-state panel
- Error-state panel
- Loading skeleton rows/cards

### Home page specific
- Hero/banner with CTA group
- Institution selection preview block
- Value proposition card
- Audience pathway card
- Footer link group

#### Home page states
- **Default**: both CTAs visible and banner text readable above the fold
- **Loading**: skeleton blocks for institution preview and feature cards
- **Empty institution list**: helper text like `No recent institutions yet`
- **Error institution lookup**: inline warning block + retry button
- **CTA hover/focus**: clear visual distinction and focus ring

### Login page specific
- Intro / trust panel
- Username input
- Password input
- Submit button
- Inline authentication feedback area
- Supported-role redirect note / unsupported-role panel

#### Login page states
- **Default**: page title, intro text, username field, password field, and sign-in button are visible without scrolling on common laptop widths
- **Submitting**: form controls disabled and submit button shows in-progress copy
- **Invalid credentials**: inline error message above or within the form; password field remains available for retry
- **Unauthorized role for current UI scope**: authenticated but unsupported-role panel appears with guidance to return home or contact support
- **Network/service error**: inline error block with retry action and no destructive reset of typed username
- **Logged out return**: optional neutral confirmation message at the top of the form when arriving from a later sign-out flow

### Student dashboard states
- **Default**: summary cards + populated grid
- **No assignments**: empty-state message in the assignments block
- **No grades yet**: neutral placeholder in grade highlights
- **Announcement overflow**: scroll or `View all` link
- **Loading**: skeleton cards and skeleton list rows
- **Error**: card-level inline error with retry action

### Teacher dashboard states
- **Default**: agenda + classes + work queues visible
- **No lessons today**: calm empty-state card in agenda area
- **No homework to review**: empty-state queue block
- **Urgent alert present**: alert card promoted near summary row
- **Loading**: skeleton cards and queue rows
- **Error**: card-level error state with retry action

## Responsive behavior

### Desktop (>= 1280px)
- Home page uses a broad hero/banner with supporting blocks in 2-4 column layouts where appropriate.
- Login page uses a two-column shell with intro content on one side and the sign-in card on the other.
- Student and teacher dashboards use a 2-column or asymmetrical content grid beneath the summary cards.
- Quick actions remain horizontal.

### Tablet (768px - 1279px)
- Home page hero remains full-width; value blocks collapse to 2 columns.
- Login page may keep the intro panel above the form or in a narrower two-column shell depending on available width.
- Dashboards collapse to a single main column with stacked cards or a 2-column card rhythm when space allows.
- Quick actions may wrap to 2 rows.

### Mobile (< 768px)
- Navigation compresses to logo + utility actions.
- Home page banner content stacks vertically; both CTAs remain high-visibility and full-width.
- Login page stacks intro content above the sign-in form; username and password fields become full-width; submit button remains easy to tap.
- Student and teacher dashboard summary cards stack into a vertical list.
- Content sections become one-column cards ordered by urgency:
  - Student: summary → assignments → schedule → grades → announcements
  - Teacher: summary → agenda → attendance queue → homework queue → classes → notices

## Accessibility

- Use semantic landmarks: `header`, `nav`, `main`, `section`, `footer`.
- Each dashboard section requires a visible heading hierarchy with one `h1` per page and `h2` for major content blocks.
- CTA buttons and quick actions must be keyboard reachable in logical order.
- The login form must use explicit `label` elements for `Username` and `Password` and preserve native form submission with Enter.
- Provide visible focus states for all links, buttons, and selection controls.
- Summary cards should not rely on color alone to communicate status.
- Empty and error states need clear explanatory text, not icon-only treatments.
- If institution selection becomes a searchable combobox later, it must support keyboard navigation and screen-reader announcements.
- Login error messages should be announced clearly and associated with the form region.

## Copy and labels

### Home page
- Hero heading: `Manage learning across institutions with one secure platform`
- Hero supporting text: `Choose your institution or sign in to continue with your learning workspace.`
- Primary CTA 1: `Institution selection`
- Primary CTA 2: `Account login`
- Value block headings:
  - `Teaching and learning`
  - `Institution coordination`
  - `Secure operations`

### Student dashboard
- Page title: `Student dashboard`
- Greeting example: `Good morning, Alex`
- Summary cards:
  - `Today's lessons`
  - `Pending homework`
  - `New grades`
  - `Attendance`

### Teacher dashboard
- Page title: `Teacher dashboard`
- Greeting example: `Good morning, Ms. Novak`
- Summary cards:
  - `Classes today`
  - `Attendance pending`
  - `Homework to review`
  - `Alerts`

### Login page
- Page title: `Account login`
- Supporting text: `Sign in with your account credentials to continue to your learning workspace.`
- Username label: `Username`
- Password label: `Password`
- Primary action: `Sign in`
- Secondary links:
  - `Back to home`
  - `Institution selection`
  - `Contact support`
- Invalid-credentials message: `The username or password is incorrect.`
- Unsupported-role message: `Your account is authenticated, but this preview currently supports only student and teacher dashboards.`

## Assets/icons/tokens

- No branded assets are required for this low-fidelity package.
- Use neutral placeholder icons if frontend chooses to stub icons later:
  - school/building
  - calendar
  - book/homework
  - chart/grade
  - bell/notice
- Recommended low-fi token intent only:
  - one neutral page background
  - white or light-surface cards
  - one primary action color reserved for CTAs
  - one warning color for alerts/errors
  - consistent spacing scale for sections and cards

## Assumptions

- These wireframes are structural starting points and may evolve once branding and exact data contracts are approved.
- Home page serves both discovery and authenticated-entry intent at a high level.
- The initial testable website login flow should align with the accepted backend auth contract rather than inventing a parallel frontend-only credential shape.
- Student and teacher dashboards represent landing screens, not full workflow pages.

## Handoff notes for frontend-dev

- Implement these screens as low-complexity layout-first pages/components in `frontend/website`.
- Preserve the CTA labels on the home page hero exactly as requested unless PO/human later changes the copy.
- Implement four initial website routes/screens for this scope: home page, login page, student dashboard, and teacher dashboard.
- Preferred login-flow contract for the first implementation pass:
  - submit credentials to `POST /api/v1/identity/auth/login` using `username` and `password`
  - resolve current user context through `GET /api/v1/identity/me`
  - route supported users to `/student` or `/teacher` based on returned authority/roles
  - keep unsupported authenticated roles on the login page with a scoped explanatory message rather than navigating to an undefined page
- Build card shells and empty/loading/error states from the start so future data integration does not require layout redesign.
- Reference the page-specific static artifacts:
  - `design/home-page/low-fi-wireframe.html`
  - `design/login-page/low-fi-wireframe.html`
  - `design/student-dashboard/low-fi-wireframe.html`
  - `design/teacher-dashboard/low-fi-wireframe.html`

