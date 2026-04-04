# How to Run Spring Backend Template

This guide covers two ways to run the application:

- **Option A — Local Development** (recommended for development): Run the database in Docker, run the Spring Boot app from your IDE or Maven.
- **Option B — Full Docker** (production-like): Run both the database and the backend inside Docker containers.

---

## Prerequisites

| Tool | Minimum Version | Purpose |
|---|---|---|
| Java JDK | 17 | Compile and run the Spring Boot application |
| Maven | 3.8+ | Build the project |
| Docker Desktop | Any recent | Run PostgreSQL (and optionally the backend) |
| Git | Any | Clone the repository |

> You can verify your installations with:
> ```bash
> java -version
> mvn -version
> docker -version
> ```

---

## Project Overview

| Component | Technology | Notes |
|---|---|---|
| Framework | Spring Boot 4.0.1 | Java 17 |
| Database | PostgreSQL 16 | Managed by Docker |
| Migrations | Flyway | Auto-runs on startup |
| Auth | JWT (JJWT 0.13) | Access + Refresh tokens |
| Email | Spring Mail (SMTP) | Used for OTP password reset |
| API Docs | SpringDoc OpenAPI 3 | Available at `/swagger-ui.html` |

---

## Step 1 — Create the `.env` File

The `.env` file lives in the **same folder as `docker-compose.yml`** (the project root). It supplies environment variables to Docker Compose.

Create a file named `.env` in the project root:

```
spring-backend-template-1/
├── docker-compose.yml
├── .env                  ← create this file here
├── Dockerfile
├── pom.xml
└── src/
```

### `.env` contents

Copy the block below and fill in your values. Defaults are shown in the comments.

```env
# ─── PostgreSQL (Docker) ──────────────────────────────────────────────────────

# Name of the PostgreSQL database to create inside the container
POSTGRES_DB=auth-database-1

# PostgreSQL superuser username
POSTGRES_USER=postgres

# PostgreSQL superuser password
POSTGRES_PASSWORD=1234

# Host port that PostgreSQL will be exposed on (maps to container port 5432)
# Change this if port 5432 is already in use on your machine
POSTGRES_PORT=5432
```

> **Security note:** Never commit `.env` to version control. Add it to `.gitignore` if it isn't already there.

---

## Option A — Local Development (Database in Docker, App runs locally)

This is the recommended setup for day-to-day development. Hot reload and IDE debugging work as normal.

### Step A1 — Start only the database container

```bash
docker compose up auth-database-1 -d
```

This starts a PostgreSQL 16 container named `auth-postgres-container-1` with a named volume `auth_data` for persistence. The container includes a health check — Docker will report it as healthy once PostgreSQL is accepting connections.

Verify it is running:

```bash
docker compose ps
```

You should see `auth-postgres-container-1` with status `healthy`.

### Step A2 — Set application environment variables

The Spring Boot application reads its configuration from environment variables defined in `src/main/resources/application.yaml`. You must provide all required variables before starting the app.

#### Required variables

| Variable | Description | Example |
|---|---|---|
| `DATABASE_URL` | JDBC connection URL to your PostgreSQL instance | `jdbc:postgresql://localhost:5432/auth-database-1` |
| `DATABASE_USERNAME` | PostgreSQL username (matches `POSTGRES_USER` in `.env`) | `postgres` |
| `DATABASE_PASSWORD` | PostgreSQL password (matches `POSTGRES_PASSWORD` in `.env`) | `1234` |
| `JWT_SECRET` | Secret key used to sign JWT tokens. **Must be at least 32 characters.** | `my-super-secret-key-that-is-long-enough` |
| `JWT_ACCESS_EXPIRATION_MINUTES` | How long access tokens are valid (minutes) | `15` |
| `JWT_REFRESH_EXPIRATION_DAYS` | How long refresh tokens are valid (days) | `7` |
| `OTP_EXPIRATION_MINUTES` | How long a password-reset OTP is valid (minutes) | `10` |
| `MAIL_USER_NAME` | SMTP email address used to send OTP emails | `yourapp@gmail.com` |
| `MAIL_PASSWORD` | SMTP password or App Password for that email account | `your-app-password` |
| `MAIL_SENDER_NAME` | Display name shown in OTP emails | `Resort App` |

#### Optional variables (have defaults)

| Variable | Default | Description |
|---|---|---|
| `HIBERNATE_DDL_AUTO` | `validate` | Hibernate DDL mode. Keep `validate` in production; Flyway handles migrations. |
| `JPA_SHOW_SQL` | `false` | Log every SQL query to console. Set to `true` for debugging. |
| `HIBERNATE_FORMAT_SQL` | `false` | Pretty-print SQL logs. Only useful if `JPA_SHOW_SQL=true`. |
| `MAIL_HOST` | `smtp.gmail.com` | SMTP server hostname |
| `MAIL_PORT` | `587` | SMTP server port |
| `MAIL_DEBUG` | `false` | Enable verbose SMTP debug output |

#### How to provide variables

**Option 1 — IntelliJ IDEA Run Configuration**

1. Open `Run > Edit Configurations`
2. Select your Spring Boot run configuration
3. Click `Modify options > Environment variables`
4. Add each variable as `KEY=VALUE`

**Option 2 — Shell export (Linux/macOS/Git Bash)**

```bash
export DATABASE_URL=jdbc:postgresql://localhost:5432/auth-database-1
export DATABASE_USERNAME=postgres
export DATABASE_PASSWORD=1234
export JWT_SECRET=my-super-secret-key-that-is-long-enough-for-hmac
export JWT_ACCESS_EXPIRATION_MINUTES=15
export JWT_REFRESH_EXPIRATION_DAYS=7
export OTP_EXPIRATION_MINUTES=10
export MAIL_USER_NAME=yourapp@gmail.com
export MAIL_PASSWORD=your-app-password
export MAIL_SENDER_NAME="Resort App"
```

Then run:

```bash
mvn spring-boot:run
```

**Option 3 — `.env` file + dotenv plugin (IDE)**

Many IDEs support loading a `.env` file automatically. You can create a separate `app.env` for the application variables (distinct from the Docker `.env`):

```env
DATABASE_URL=jdbc:postgresql://localhost:5432/auth-database-1
DATABASE_USERNAME=postgres
DATABASE_PASSWORD=1234
JWT_SECRET=my-super-secret-key-that-is-long-enough-for-hmac
JWT_ACCESS_EXPIRATION_MINUTES=15
JWT_REFRESH_EXPIRATION_DAYS=7
OTP_EXPIRATION_MINUTES=10
MAIL_USER_NAME=yourapp@gmail.com
MAIL_PASSWORD=your-app-password
MAIL_SENDER_NAME=Resort App
```

### Step A3 — Run the application

**From your IDE:** Run the main class `ResortApplication1Application.java`.

**From the terminal:**

```bash
mvn spring-boot:run
```

### Step A4 — Verify startup

Watch the logs for these lines:

```
Successfully applied N migration(s) to schema "public"   ← Flyway ran migrations
BOOTSTRAP ADMIN CREATED                                  ← First-run only
Username: superadmin
Temporary Password: <random-uuid-string>
CHANGE THIS PASSWORD IMMEDIATELY
```

> **Important:** Copy the temporary password from the logs on first startup. It is only printed once. You will use it to log in as the super admin.

The application is ready when you see:

```
Started ResortApplication1Application in X.XXX seconds
```

---

## Option B — Full Docker (Backend + Database in Docker)

Use this to run everything containerized, similar to a production deployment.

### Step B1 — Build the JAR

The Dockerfile copies from `target/*.jar`, so you must build the project first:

```bash
mvn clean package -DskipTests
```

This creates `target/resort-application-1-0.0.1-SNAPSHOT.jar`.

### Step B2 — Add backend environment variables to `.env`

In addition to the PostgreSQL variables from Step 1, add the Spring Boot application variables to your `.env` file. When running in Docker, the backend container uses `SPRING_*` variable names (Spring Boot's relaxed binding) to override `application.yaml`:

```env
# ─── PostgreSQL (Docker) ──────────────────────────────────────────────────────

POSTGRES_DB=auth-database-1
POSTGRES_USER=postgres
POSTGRES_PASSWORD=1234
POSTGRES_PORT=5432

# ─── Spring Boot Backend (Docker) ────────────────────────────────────────────

# Note: SPRING_DATASOURCE_URL uses the Docker service name "auth-database-1"
# as the hostname, not "localhost"
SPRING_DATASOURCE_URL=jdbc:postgresql://auth-database-1:5432/auth-database-1
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=1234

JWT_SECRET=my-super-secret-key-that-is-long-enough-for-hmac
JWT_ACCESS_EXPIRATION_MINUTES=15
JWT_REFRESH_EXPIRATION_DAYS=7
OTP_EXPIRATION_MINUTES=10

MAIL_USER_NAME=yourapp@gmail.com
MAIL_PASSWORD=your-app-password
MAIL_SENDER_NAME=Resort App
```

> **Why `auth-database-1` as hostname?** Inside Docker's network, containers refer to each other by their service name defined in `docker-compose.yml`, not by `localhost`.

### Step B3 — Start all services

```bash
docker compose up -d
```

Docker Compose will:
1. Start `auth-database-1` (PostgreSQL)
2. Wait until the database health check passes
3. Start `auth-backend-1` (Spring Boot backend)

### Step B4 — Verify startup

```bash
# Check container status
docker compose ps

# Watch backend logs (includes Flyway output and bootstrap admin password)
docker compose logs -f auth-backend-1
```

---

## Gmail SMTP Setup (for OTP emails)

The password reset flow sends OTP codes by email. If you are using Gmail:

1. Enable **2-Step Verification** on your Google account.
2. Go to **Google Account > Security > App Passwords**.
3. Generate an App Password for "Mail".
4. Use that App Password as the value of `MAIL_PASSWORD` (not your regular Gmail password).

The default SMTP settings in `application.yaml` already point to Gmail (`smtp.gmail.com:587` with STARTTLS).

---

## What Happens on First Startup

Flyway automatically runs all database migration scripts from `src/main/resources/db/migration/` in version order (`V1__schema.sql`, `V2__...`, etc.). These create all required tables.

After migrations, `AdminBootstrapRunner` checks if a user named `superadmin` exists. If not, it:

1. Creates the `ADMIN` role (the `USER` role is seeded by `V1__schema.sql`)
2. Creates three permissions: `CREATE_ADMIN`, `ACTIVATE_ADMIN`, `ASSIGN_PERMISSIONS`
3. Creates a `superadmin` user with a randomly generated password
4. Grants all three permissions to `superadmin`
5. Activates the account

The temporary password is logged as a warning — **copy it immediately**.

On subsequent startups, `AdminBootstrapRunner` detects `superadmin` already exists and skips all of the above.

---

## API Reference

Once the application is running, the full interactive API documentation is available at:

```
http://localhost:8080/swagger-ui.html
```

### Key endpoints

| Method | Endpoint | Auth Required | Description |
|---|---|---|---|
| `POST` | `/api/v1/auth/login` | No | Login and receive access + refresh tokens |
| `POST` | `/api/v1/auth/registration/user` | No | Register a new user account |
| `POST` | `/api/v1/auth/forgot-password` | No | Request an OTP for password reset |
| `POST` | `/api/v1/auth/verify-otp` | No | Verify OTP and receive a reset token |
| `POST` | `/api/v1/auth/reset-password` | No | Reset password using the reset token |
| `POST` | `/api/v1/admins` | Yes — `CREATE_ADMIN` | Create a new admin account |
| `PUT` | `/api/v1/admins/{id}/activate` | Yes — `ACTIVATE_ADMIN` | Activate an admin account |
| `POST` | `/api/v1/admins/{id}/permissions` | Yes — `ASSIGN_PERMISSIONS` | Assign permissions to an admin |

### Authentication header

All protected endpoints require a Bearer token in the `Authorization` header:

```
Authorization: Bearer <access_token>
```

---

## Stopping the Application

**Stop only the database (Option A):**

```bash
docker compose stop auth-database-1
```

**Stop all Docker services (Option B):**

```bash
docker compose down
```

**Stop and delete all data (including the database volume):**

```bash
docker compose down -v
```

> Warning: `-v` deletes the `auth_data` volume and all stored data. The next startup will re-run all migrations and re-create the bootstrap admin.

---

## Troubleshooting

### "Connection refused" when starting the app locally

The database container is not running or not yet healthy. Run:

```bash
docker compose ps
```

If `auth-postgres-container-1` is not showing `healthy`, wait a few seconds and check again. You can also inspect its logs:

```bash
docker compose logs auth-database-1
```

### "JWT_SECRET must be set" on startup

The `JWT_SECRET` environment variable is missing or not being read. Verify it is exported in your shell or configured in your IDE run configuration.

### "Flyway validation failed" on startup

This happens when `HIBERNATE_DDL_AUTO` is set to something other than `validate` and there is a mismatch, or when you have modified a migration file that was already applied. Flyway tracks applied migrations by checksum. Never edit a migration file after it has been applied. To start fresh (dev only):

```bash
docker compose down -v
docker compose up auth-database-1 -d
```

Then restart the application.

### Port 5432 already in use

Another PostgreSQL instance is running on your machine. Change `POSTGRES_PORT` in your `.env` to a different port, e.g. `5433`.

### OTP emails not being received

- Verify `MAIL_USER_NAME` and `MAIL_PASSWORD` are correct.
- If using Gmail, ensure you are using an **App Password**, not your account password.
- Set `MAIL_DEBUG=true` temporarily and check the console for SMTP negotiation errors.
