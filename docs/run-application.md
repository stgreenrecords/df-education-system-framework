# Run the application locally

This guide explains how to run the currently implemented application pieces in this repository:

- the Spring Boot backend in `backend/platform-core`
- the website frontend in `frontend/website`

## What is runnable today

### Backend

The runnable backend application entrypoint is the `platform-core` module:

- module: `backend/platform-core`
- main class: `com.darkfactory.education.platform.EducationSystemApplication`
- health endpoint: `GET /platform/status`
- OpenAPI endpoint: `GET /api-docs`
- Swagger UI: `GET /swagger-ui`

### Frontend

The website frontend lives in `frontend/website` and proxies backend auth endpoints through Next.js route handlers.

- home page: `/`
- login page: `/login`
- student dashboard: `/student`
- teacher dashboard: `/teacher`

The website project requires Node.js 20+ and npm.

## Prerequisites

### Required for backend

- Java 25+ (the root Maven build declares `java.version=25`)
- PostgreSQL 17+ or a compatible PostgreSQL instance
- shell access to run the Maven wrapper (`sh ./mvnw ...`)

### Required for website

- Node.js 20+ (`frontend/website/package.json` declares `"node": ">=20"`)
- npm

### Optional but recommended

- Docker Desktop or another Docker-compatible runtime for quick local PostgreSQL startup

## Recommended: single-file cross-platform launcher

The repository now includes one cross-platform launcher file at the repository root:

- `RunLocal.java`

It is intended to work from the same command on Windows, macOS, and Linux:

```zsh
java RunLocal.java
```

What it does:

- starts a local PostgreSQL container through Docker or Podman
- starts the backend from `backend/platform-core`
- waits for backend readiness
- prints the local verification URLs
- optionally tries to start `frontend/website`

Backend-only startup:

```zsh
java RunLocal.java
```

Try backend + website startup:

```zsh
java RunLocal.java --with-website
```

Useful overrides:

```zsh
java RunLocal.java --db-port 55435 --app-port 18088
java RunLocal.java --with-website --web-port 3010
java RunLocal.java --keep-postgres
```

If `node`/`npm` are unavailable, the launcher keeps the backend running and prints a clear message that the website was skipped.

Show launcher help:

```zsh
java RunLocal.java --help
```

## Manual backend quick start with Docker PostgreSQL

From the repository root, start a local PostgreSQL container:

```zsh
docker rm -f df-local-postgres >/dev/null 2>&1 || true
docker run -d \
  --name df-local-postgres \
  -e POSTGRES_DB=education_framework \
  -e POSTGRES_USER=education_framework \
  -e POSTGRES_PASSWORD=education_framework \
  -p 55433:5432 \
  postgres:17-alpine
```

Then export backend configuration in the same terminal:

```zsh
export EDU_DB_URL="jdbc:postgresql://localhost:55433/education_framework"
export EDU_DB_USERNAME="education_framework"
export EDU_DB_PASSWORD="education_framework"
export EDU_AUTH_JWT_SECRET="df-local-jwt-secret-0123456789abcdef"
export EDU_AUTH_MFA_SECRET_ENCRYPTION_KEY="df-local-mfa-secret-0123456789abcdef"
export EDU_AUTH_BOOTSTRAP_ADMIN_USERNAME="bootstrap-admin"
export EDU_AUTH_BOOTSTRAP_ADMIN_PASSWORD="BootstrapPassword!123"
```

### Important secret-format note

`EDU_AUTH_JWT_SECRET` and `EDU_AUTH_MFA_SECRET_ENCRYPTION_KEY` must be strong values.

For local raw-string secrets, prefer values that are at least 32 characters long and do **not** accidentally look like short base64 strings. A safe local example is:

- `df-local-jwt-secret-0123456789abcdef`
- `df-local-mfa-secret-0123456789abcdef`

If you intentionally use base64-encoded secrets, make sure the decoded value is strong enough for the underlying crypto key requirements.

## Start the backend

Run the Spring Boot app from the executable module:

```zsh
sh ./mvnw -f backend/platform-core/pom.xml spring-boot:run
```

### Why this command

Use the module POM, not the backend parent POM.

This works:

```zsh
sh ./mvnw -f backend/platform-core/pom.xml spring-boot:run
```

This is **not** the recommended startup command because the backend parent POM does not define a runnable main class:

```zsh
sh ./mvnw -f backend/pom.xml -pl platform-core -am spring-boot:run
```

## Verify the backend is running

In another terminal:

```zsh
curl http://127.0.0.1:8080/platform/status
curl http://127.0.0.1:8080/api-docs
```

Expected health response:

```json
{"service":"education-system-framework","status":"UP"}
```

## Optional: verify bootstrap-admin login

If you exported `EDU_AUTH_BOOTSTRAP_ADMIN_USERNAME` and `EDU_AUTH_BOOTSTRAP_ADMIN_PASSWORD`, you can test login directly:

```zsh
curl -X POST http://127.0.0.1:8080/api/v1/identity/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"username":"bootstrap-admin","password":"BootstrapPassword!123"}'
```

Possible responses:

- direct bearer-token response when the account is not currently forced into MFA enrollment/verification in that runtime state
- MFA challenge response for administrator accounts, depending on the configured account state

## Website quick start

If Node.js 20+ and npm are available, start the website in a separate terminal.

```zsh
cd frontend/website
npm install
export EDUCATION_API_BASE_URL="http://127.0.0.1:8080"
npm run dev
```

Then open:

- `http://localhost:3000/`
- `http://localhost:3000/login`
- `http://localhost:3000/student`
- `http://localhost:3000/teacher`

### Website auth proxy behavior

The website proxies these backend calls through frontend-owned Next.js routes:

- `POST /api/auth/login` -> backend `POST /api/v1/identity/auth/login`
- `GET /api/auth/me` -> backend `GET /api/v1/identity/me`
- `POST /api/auth/logout` -> clears the website auth cookie

If Node.js/npm are not installed yet, you can still run and validate the backend independently.

## Alternative local container workflow

There is also an OCI/container helper workflow under:

- `devops/container/platform-core/README.md`

That path is currently documented with PowerShell-oriented helper scripts for building the image and running a local PostgreSQL + application stack.

## Stop and clean up

If you started the stack with `RunLocal.java`, stop it with `Ctrl+C` in the launcher terminal.

By default, the launcher also removes its PostgreSQL container on exit unless you started it with `--keep-postgres`.

Stop the backend process with `Ctrl+C` in the terminal where it is running.

Remove the local PostgreSQL container when you are finished:

```zsh
docker rm -f df-local-postgres
```

## Troubleshooting

### `spring-boot:run` fails with “Unable to find a suitable main class”

Cause:
- the command was executed from the backend parent POM instead of `backend/platform-core/pom.xml`

Fix:

```zsh
sh ./mvnw -f backend/platform-core/pom.xml spring-boot:run
```

### Port `55433` is already in use

Cause:
- another local PostgreSQL container or service is already bound to that host port

Fix:
- choose another free host port and update `EDU_DB_URL` to match it

Example:

```zsh
docker run -d --name df-local-postgres -e POSTGRES_DB=education_framework -e POSTGRES_USER=education_framework -e POSTGRES_PASSWORD=education_framework -p 55434:5432 postgres:17-alpine
export EDU_DB_URL="jdbc:postgresql://localhost:55434/education_framework"
```

### Login returns `500` after startup

Possible cause:
- JWT or MFA secret values were provided in a format that is interpreted as weak base64-decoded material

Fix:
- use strong local raw-string secrets like the examples in this guide, or provide strong base64 values whose decoded key material is long enough

### `npm` or `node` is missing

Cause:
- the website project depends on Node.js 20+ and npm

Fix:
- install Node.js 20+ and rerun:

```zsh
cd frontend/website
npm install
npm run dev
```

