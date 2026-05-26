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

## Recommended: single-file terminal startup

The repository now includes one terminal-first startup file at the repository root:

- `compose.local.yaml`

This is the intended cross-platform command for Windows, macOS, and Linux:

```zsh
docker compose -f compose.local.yaml up
```

What it starts:

- PostgreSQL 17 in a container
- the Spring backend from the real repository module entrypoint `backend/platform-core`
- the website frontend from `frontend/website`

The first startup can take longer because:

- the backend reactor is packaged inside the backend container before the application process starts
- the frontend container installs the website dependencies before starting Next.js

Useful variants:

```zsh
docker compose -f compose.local.yaml up -d
docker compose -f compose.local.yaml logs -f backend
docker compose -f compose.local.yaml logs -f frontend
docker compose -f compose.local.yaml down
docker compose -f compose.local.yaml down -v
```

Use `down` when you want to stop the stack but keep the cached Maven/NPM volumes for faster next startup. Use `down -v` only when you intentionally want a clean reset of the database and dependency caches.

Useful environment-variable overrides:

```zsh
export DF_DB_PORT="55435"
export DF_APP_PORT="18088"
export DF_WEB_PORT="3001"
docker compose -f compose.local.yaml up
```

Supported overrides in the compose file:

- `DF_DB_PORT`
- `DF_APP_PORT`
- `DF_WEB_PORT`
- `DF_DB_NAME`
- `DF_DB_USER`
- `DF_DB_PASSWORD`
- `DF_POSTGRES_IMAGE`
- `DF_POSTGRES_CONTAINER_NAME`
- `DF_BACKEND_CONTAINER_NAME`
- `DF_FRONTEND_CONTAINER_NAME`
- `DF_NODE_IMAGE`
- `DF_AUTH_JWT_SECRET`
- `DF_AUTH_MFA_SECRET_ENCRYPTION_KEY`
- `DF_BOOTSTRAP_ADMIN_USERNAME`
- `DF_BOOTSTRAP_ADMIN_PASSWORD`
- `DF_BOOTSTRAP_ADMIN_DISPLAY_NAME`

### Why this is the recommended path

- one file
- terminal-first
- same `docker compose` command on Windows, macOS, and Linux
- starts the database, Spring backend, and website together
- avoids requiring a custom host-side launcher wrapper

After startup, open:

- website home: `http://127.0.0.1:3000/` (or your overridden `DF_WEB_PORT`)
- website login: `http://127.0.0.1:3000/login`
- student dashboard: `http://127.0.0.1:3000/student`
- teacher dashboard: `http://127.0.0.1:3000/teacher`
- backend health: `http://127.0.0.1:8080/platform/status` (or your overridden `DF_APP_PORT`)
- OpenAPI: `http://127.0.0.1:8080/api-docs`

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

If you do not want to use the compose-based full-stack launcher, you can still start the website manually in a separate terminal.

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

The compose-based launcher does not require host-installed Node.js/npm because the website runs inside a Node.js container.

## Alternative local container workflow

There is also an OCI/container helper workflow under:

- `devops/container/platform-core/README.md`

That path is currently documented with PowerShell-oriented helper scripts for building the image and running a local PostgreSQL + application stack.

## Stop and clean up

If you started the stack with Docker Compose, stop it with:

```zsh
docker compose -f compose.local.yaml down
```

Use this stronger reset only when you want to remove the local PostgreSQL data volume and the dependency caches used by Maven/NPM inside the compose services:

```zsh
docker compose -f compose.local.yaml down -v
```

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

For the single-file Docker Compose path, override the published DB port before startup instead:

```zsh
export DF_DB_PORT="55434"
docker compose -f compose.local.yaml up
```

### Login returns `500` after startup

Possible cause:
- JWT or MFA secret values were provided in a format that is interpreted as weak base64-decoded material

Fix:
- use strong local raw-string secrets like the examples in this guide, or provide strong base64 values whose decoded key material is long enough

### `npm` or `node` is missing on the host

Cause:
- the website project depends on Node.js 20+ and npm

Fix:
- for the single-file launcher, this is not a blocker because the website runs in the `frontend` container
- for the manual website workflow, install Node.js 20+ and rerun:

```zsh
cd frontend/website
npm install
npm run dev
```

This does not block the single-file compose startup path in `compose.local.yaml`, because the launcher now starts the website inside a containerized Node.js runtime.

