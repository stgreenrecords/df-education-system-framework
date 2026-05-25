# MVP Definition — Education System Framework

## MVP goal

Validate the generic Education System Framework using Poland as the first reference country, starting with the school schema pack as the primary implementation target.

## MVP success criteria

1. A country ministry can deploy the framework on its own infrastructure without vendor access to production
2. Poland country template provides working configuration for schools
3. A school admin can manage classes, subjects, teachers, students, and parents
4. A teacher can manage schedule, attendance, grades, and homework
5. A parent can view child's grades, homework, attendance, and manage meal exclusions
6. A student can view grades, schedule, homework, and use basic AI assistance
7. Configuration inheritance works from country → institution → class level
8. Audit trail captures all meaningful changes
9. Security fundamentals are in place (auth, RBAC, encryption, audit)
10. All APIs are documented via OpenAPI

## MVP scope

### Platform core (Phase 1)

- [ ] API-first backend foundation (Java Spring Boot)
- [ ] PostgreSQL database setup
- [ ] Security baseline (authentication, authorization, encryption)
- [ ] User and role management (RBAC)
- [ ] Tenant/country deployment model
- [ ] Configuration and inheritance engine
- [ ] Audit trail system
- [ ] Release/update manager concept (design + basic tooling)
- [ ] OpenAPI contract generation

### Poland school MVP (Phase 2)

- [ ] Poland country template v1 (education stages, grade scale, subjects, calendar)
- [ ] School schema pack v1
- [ ] Institution hierarchy (country → region → city → school → class)
- [ ] Students, teachers, parents/guardians management
- [ ] Subject management (mandatory from country config + optional per school)
- [ ] Class management
- [ ] Schedule (weekly timetable)
- [ ] Attendance (per-lesson, daily, with excuse workflow)
- [ ] Gradebook (configurable grade scale, semester/final calculation)
- [ ] Homework (assign, submit, review)
- [ ] Meal and catering v1 (institution-level config, exclusions, paid/unpaid tracking)
- [ ] Basic dashboards (school, class, teacher, student, parent views)

### Near-MVP (Phase 3-4, initial design only)

- [ ] Kindergarten schema pack design (groups, check-in/out, routines, developmental notes)
- [ ] University schema pack design (faculties, courses, ECTS, enrollment)
- [ ] AI student assistant v1 (explain, guide, no direct answers)
- [ ] AI teacher assistant v1 (lesson prep, test generation)

## MVP exclusions (explicit)

| Excluded | Reason | Phase planned |
|---|---|---|
| Online payment gateway | Cost/complexity; track paid/unpaid only | Post-MVP |
| Mobile app | API-first enables it; focus on API quality first | Post-MVP |
| SMS/push notifications | Integration dependency; email only in MVP | Post-MVP |
| Multi-country simultaneous | Poland first; validate model then expand | Phase 6+ |
| Video conferencing | External tool integration; not core value | Post-MVP |
| E-book/LMS content | Separate content domain; API hooks later | Post-MVP |
| Advanced analytics/ML | Basic dashboards first | Phase 5 |
| Student data import/migration | Manual entry first; import tools later | Post-MVP |

## MVP timeline (indicative)

| Phase | Duration (estimate) | Content |
|---|---|---|
| Phase 0 | 2-4 weeks | Discovery, backlog, architecture questions, Poland research |
| Phase 1 | 8-12 weeks | Platform foundation, security, config engine |
| Phase 2 | 12-16 weeks | Poland school MVP (full academic operations) |
| Phase 3 | 6-8 weeks | Kindergarten + university schema design & initial impl |
| Phase 4 | 6-8 weeks | AI assistants v1 |

## MVP validation approach

1. **Functional testing**: All acceptance criteria verified through automated + manual tests
2. **Security testing**: Authentication, authorization, injection, audit trail verification
3. **Deployment testing**: Country can deploy independently using release package
4. **Configuration testing**: Inheritance works correctly across hierarchy levels
5. **User acceptance**: PO validates end-to-end flows for each user role
6. **Poland template validation**: Cross-reference with public Polish education sources

## Risks to MVP

| Risk | Impact | Mitigation |
|---|---|---|
| Poland education research incomplete | Incorrect template | Use multiple public sources; mark unknowns; validate with domain expert |
| Scope creep in school module | Delayed MVP | Strict phase boundaries; PO acceptance gates |
| Configuration engine complexity | Delayed foundation | Start simple (2-level inheritance); extend later |
| AI safety boundaries unclear | Feature blocked | Start with strict rules; iterate governance |
| No real users during development | Unvalidated UX | API-first; validate via automated tests + PO scenarios |

