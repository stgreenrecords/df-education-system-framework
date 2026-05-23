# Architecture Direction — Education System Framework

## Architecture style

**Modular monolith first, microservices later if needed.**

The system starts as a well-structured modular monolith with clear module boundaries, internal APIs, and event-driven extension points. This allows faster initial development, simpler deployment, and easier debugging while preserving the option to extract modules into independent services later.

## Technology stack (preferred direction)

| Layer | Technology | Rationale |
|---|---|---|
| Language | Java (latest LTS) | Enterprise-grade, large talent pool, strong ecosystem |
| Framework | Spring Boot + Spring Security + Spring Data | Mature, well-documented, modular |
| Database | PostgreSQL | Open-source, robust, JSON support for flexible config |
| API style | REST + OpenAPI 3.x contracts | Standard, tooling-rich, API-first design |
| Event system | Spring Application Events (internal), Kafka/RabbitMQ (external, later) | Decoupled extension points |
| Auth | Spring Security + OAuth2/OIDC | Standards-based, supports external IdPs |
| Build | Gradle or Maven | Standard Java build tools |
| Containerization | Docker, OCI images | Portable, sovereign-friendly |
| Docs | OpenAPI (Swagger), Markdown | Machine-readable and human-readable |

## Architecture principles

1. **API-first** — all functionality exposed via documented REST APIs; no UI-coupled logic
2. **Headless** — web/mobile/desktop/AI clients consume the same APIs
3. **Modular** — each bounded context is a module with defined interfaces
4. **Configurable** — behavior driven by configuration, not code branching
5. **Country-agnostic framework** — country templates are data-only; no country may introduce country-specific framework code, module structure, schema, or API forks
6. **Auditable** — all state changes produce immutable audit events
7. **Secure by default** — zero-trust, encryption, least-privilege from day one
8. **Sovereignty-aware** — architecture supports fully isolated country deployments
9. **Extensible** — event-driven hooks allow adding behavior without modifying core
10. **Testable** — modules can be tested independently
11. **Version-managed** — clear release packaging and compatibility contracts

## Module structure (initial)

```text
education-framework/
├── platform-core/           # Tenancy, config inheritance, audit, release management
├── identity-access/         # Users, roles, permissions, auth, MFA
├── organization/            # Countries, regions, cities, institutions, units
├── school-pack/             # School-specific: classes, subjects, schedule, grades, homework
├── kindergarten-pack/       # Kindergarten-specific: groups, check-in, routines, developmental
├── university-pack/         # University-specific: faculties, courses, ECTS, enrollment, thesis
├── attendance/              # Cross-institution attendance with type-specific rules
├── gradebook/               # Configurable grading, calculation, correction workflow
├── meal-catering/           # Institution-level catering, menus, exclusions, billing
├── ai-student/              # Student AI assistant with safety boundaries
├── ai-teacher/              # Teacher AI assistant for lesson/test prep
├── dashboards/              # Configurable dashboards at all hierarchy levels
├── release-manager/         # Release packaging, compatibility, migration
├── api-gateway/             # API routing, rate limiting, security headers
└── common/                  # Shared utilities, DTOs, events, exceptions
```

## Deployment model

```text
┌─────────────────────────────────────────┐
│         Country / Ministry              │
│         (sovereign deployment)          │
│                                         │
│  ┌─────────┐  ┌─────────┐  ┌────────┐  │
│  │   Dev   │  │  Stage  │  │  Prod  │  │
│  └─────────┘  └─────────┘  └────────┘  │
│                                         │
│  ┌──────────────────────────────────┐   │
│  │  PostgreSQL (country data only)  │   │
│  └──────────────────────────────────┘   │
│                                         │
│  ┌──────────────────────────────────┐   │
│  │  Monitoring / Logging / Backup   │   │
│  └──────────────────────────────────┘   │
└─────────────────────────────────────────┘

┌─────────────────────────────────────────┐
│         Framework Vendor                │
│                                         │
│  Release Packages ──→ Country receives  │
│  Migration Scripts ──→ Country applies  │
│  Compatibility Checker ──→ Country runs │
│  Documentation ──→ Country reads        │
│  Support/Advisory ──→ Country requests  │
└─────────────────────────────────────────┘
```

## Security architecture

### Zero-trust model

- No implicit trust between components
- All API calls authenticated and authorized
- Network segmentation between modules where deployed separately
- Service-to-service authentication for internal APIs

### Authentication & authorization

- OAuth2 / OpenID Connect for user authentication
- Support for external Identity Providers (country IdP, SAML, Azure AD, Keycloak)
- Multi-factor authentication required for admin roles
- Role-Based Access Control (RBAC) for standard permissions
- Attribute-Based Access Control (ABAC) for context-aware decisions (institution, class, subject)
- Least-privilege: users get minimum permissions needed

### Data protection

- Encryption at rest (database-level + sensitive field encryption)
- Encryption in transit (TLS 1.3 for all communication)
- Data residency: all data stays within country infrastructure
- Tenant isolation: complete data separation per deployment
- Secrets management: no hardcoded secrets, vault-based storage
- PII handling: masked in logs, encrypted in storage

### Monitoring & audit

- Immutable audit logs for all state changes
- Security event monitoring and alerting
- Rate limiting on all public APIs
- Dependency scanning in CI/CD
- Regular penetration testing schedule
- Backup and disaster recovery plan per deployment

## API design

- OpenAPI 3.x specification for all endpoints
- Versioned APIs (URL path versioning: `/api/v1/...`)
- Standard error response format
- Pagination, filtering, sorting on collection endpoints
- HATEOAS links for discoverability (optional, later)
- Rate limiting headers
- Correlation IDs for request tracing

## Data architecture

- PostgreSQL as primary database
- JSON/JSONB columns for flexible configuration storage
- Country templates store country-specific values and configuration data only; they must not create country-specific database schemas or structural forks
- Schema-per-module or schema-per-tenant (decision needed in Phase 1)
- Database migrations via Flyway or Liquibase
- Read replicas for dashboard/analytics queries (later)
- Event sourcing for audit-critical operations (optional, decision needed)

## Country template guardrail

- Poland is a reference country template, not a framework special case.
- Country templates may vary only by data, configuration values, and versioned template content.
- The framework must not contain country-specific code paths, schema variants, module variants, or API variants for individual countries.
- If a country requirement cannot be expressed through generic configuration/data modeling, it must trigger an architecture review rather than a country-specific implementation.

## Integration architecture

- REST APIs as primary integration surface
- Webhook notifications for external systems (configurable)
- Event-driven internal communication (Spring Events)
- OpenAPI contracts published for third-party developers
- API gateway for external access (rate limiting, auth, routing)
- Bulk import/export APIs for data migration

## Technical decisions deferred to Phase 1

| Decision | Options | Owner |
|---|---|---|
| Schema isolation strategy | Schema-per-module vs schema-per-tenant | SA |
| Event persistence | Outbox pattern vs event sourcing vs simple audit table | SA |
| AI provider integration | OpenAI / Azure OpenAI / local LLM / pluggable | SA + PO |
| Search engine | PostgreSQL full-text vs Elasticsearch | SA |
| Caching strategy | Redis / Spring Cache / none initially | SA |
| Message broker | Kafka / RabbitMQ / Spring Events only | SA |
| File storage | Local / S3-compatible / country-operated object store | SA |
| CI/CD tooling | GitHub Actions / GitLab CI / Jenkins (country choice) | Dev |
| Container orchestration | Kubernetes / Docker Compose / bare metal (country choice) | SA |

