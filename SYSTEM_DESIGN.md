# System Design Documentation

## Table of Contents

1. [System Overview](#1-system-overview)
2. [Technology Stack](#2-technology-stack)
3. [Architecture](#3-architecture)
4. [Data Model](#4-data-model)
5. [Security Design](#5-security-design)
6. [API Design](#6-api-design)
7. [Password Reset Flow](#7-password-reset-flow)
8. [Event System](#8-event-system)
9. [Error Handling](#9-error-handling)
10. [Audit Trail](#10-audit-trail)
11. [Infrastructure & Deployment](#11-infrastructure--deployment)

---

## 1. System Overview

This is a **Spring Boot backend template** built around a complete authentication and authorization system. It is designed to serve as a reusable foundation for applications that need:

- Stateless JWT-based authentication
- Role-based access control (RBAC)
- Fine-grained permission management
- Secure password reset via email OTP
- Admin bootstrapping and lifecycle management

The system follows a multi-layered, RESTful architecture and is containerized for deployment.

---

## 2. Technology Stack

| Layer | Technology | Version |
|---|---|---|
| Language | Java | 17 |
| Framework | Spring Boot | 4.0.1 |
| Security | Spring Security | (via Boot) |
| Persistence | Spring Data JPA + Hibernate | (via Boot) |
| Database | PostgreSQL | 16 |
| Migrations | Flyway | (via Boot) |
| JWT | JJWT | 0.13.0 |
| Email | Spring Mail (SMTP) | (via Boot) |
| API Documentation | SpringDoc OpenAPI | 3.0.0 |
| Containerization | Docker + Docker Compose | — |
| Build Tool | Maven | — |
| Utilities | Lombok | — |

---

## 3. Architecture

### 3.1 Layered Architecture

The system follows a strict four-layer architecture where each layer communicates only with the layer directly below it.

```
┌─────────────────────────────────────┐
│           Controller Layer          │  HTTP request/response, input mapping
├─────────────────────────────────────┤
│            Service Layer            │  Business logic, transaction management
├─────────────────────────────────────┤
│          Repository Layer           │  Database queries (Spring Data JPA)
├─────────────────────────────────────┤
│            Database Layer           │  PostgreSQL 16
└─────────────────────────────────────┘
```

**Controller Layer** receives HTTP requests, validates routing, and delegates directly to services. Controllers do not contain business logic.

**Service Layer** contains all business rules. Each service is defined as an interface with a single implementation class, enabling future substitution or testing. Transactions are managed at the service layer using `@Transactional`.

**Repository Layer** is implemented via Spring Data JPA interfaces. Custom queries use derived method names or JPQL where needed.

**Database Layer** is a PostgreSQL 16 instance. Schema is exclusively managed by Flyway migrations — Hibernate is set to `validate` mode in production and never modifies the schema directly.

### 3.2 Package Structure

```
com.example.springbackendtemplate1
├── auth/                      Authentication & authorization domain
│   ├── config/                Spring Security, JWT, CORS, admin bootstrap
│   ├── controller/            REST controllers
│   ├── dto/
│   │   ├── request/           Inbound request DTOs
│   │   └── response/          Outbound response DTOs
│   ├── event/                 Spring application events
│   ├── exception/             Security-specific exception handlers
│   ├── filter/                JWT authentication filter
│   ├── listener/              Application event listeners
│   ├── model/
│   │   ├── dto/               Internal DTOs (UserDetails)
│   │   ├── entity/            JPA entities
│   │   └── mapper/            Entity ↔ DTO mappers
│   ├── repository/            Spring Data JPA repositories
│   ├── service/               Service interfaces
│   ├── serviceImpl/           Service implementations
│   └── util/                  Utilities (OTP generator)
└── commons/                   Shared infrastructure
    ├── config/                JPA auditing, OpenAPI config
    ├── dto/response/          Shared response shapes
    ├── exception/             Global exception handler
    └── model/entity/          AuditableEntity base class
```

### 3.3 Request Lifecycle

Every authenticated API request follows this path:

```
Client
  │
  ▼
JwtAuthenticationFilter          Skipped for /api/v1/auth/** and /actuator/**
  │  1. Extract Bearer token from Authorization header
  │  2. extractUsername() — returns null on any parse error
  │  3. loadUserByUsername() — fetches user + role + active permissions from DB
  │  4. isTokenValid() — verifies signature and expiry
  │  5. Set Authentication in SecurityContextHolder
  │
  ▼
Spring Security Authorization    hasRole() / hasAuthority() / @PreAuthorize
  │
  ▼
Controller
  │
  ▼
Service (@Transactional)
  │
  ▼
Repository (JPA)
  │
  ▼
PostgreSQL
```

If step 2 returns null (malformed token), the request continues unauthenticated and Spring Security returns 401. If authorization fails, it returns 403.

---

## 4. Data Model

### 4.1 Entity Relationship Diagram

```
┌──────────────┐       ┌──────────────┐
│    roles     │◄──────│    users     │
│              │  N:1  │              │
│ id           │       │ id           │
│ name         │       │ username     │
└──────────────┘       │ password     │
                       │ role_id (FK) │
                       │ enabled      │
                       │ locked       │
                       │ expired      │
                       └──────┬───────┘
                              │
          ┌───────────────────┼───────────────────┐
          │                   │                   │
          ▼                   ▼                   ▼
┌──────────────────┐  ┌────────────────┐  ┌──────────────────┐
│ user_permissions │  │ refresh_tokens │  │password_reset_    │
│                  │  │                │  │otps               │
│ id               │  │ id             │  │                   │
│ user_id (FK)     │  │ user_id (FK)   │  │ id                │
│ permission_id    │  │ token          │  │ user_id (FK)      │
│ is_active        │  │ expires_at     │  │ otp (BCrypt hash) │
└────────┬─────────┘  └────────────────┘  │ reset_token       │
         │                                │ expires_at        │
         ▼                                │ is_used           │
┌──────────────┐                          └───────────────────┘
│ permissions  │
│              │         ┌──────────────────┐
│ id           │         │  otp_rate_limit  │
│ name         │         │                  │
│ description  │         │ id               │
└──────────────┘         │ user_id (FK)     │
                         │ window_start     │
                         │ request_count    │
                         └──────────────────┘
```

### 4.2 AuditableEntity — Base for All Tables

Every table in the system extends a shared `AuditableEntity` which provides a consistent set of infrastructure columns. No entity defines its own `id` or audit fields.

| Column | Type | Description |
|---|---|---|
| `id` | `bigserial` PK | Auto-incrementing primary key |
| `created_by` | `bigint` | ID of user who created the record; `0` for system-generated rows |
| `created_at` | `timestamptz` | Timestamp of creation, set automatically |
| `updated_by` | `bigint` | ID of user who last modified the record |
| `updated_at` | `timestamptz` | Timestamp of last modification, set automatically |
| `version` | `bigint` | Optimistic locking version counter |
| `is_active` | `boolean` | Logical active flag (default `true`) |
| `is_deleted` | `boolean` | Soft-delete flag (default `false`) |
| `deleted_by` | `bigint` | ID of user who soft-deleted the record |
| `deleted_at` | `timestamptz` | Timestamp of soft deletion |

The `created_by` / `updated_by` fields are populated automatically by `AuditorAwareImpl`, which reads the currently authenticated user's ID from the `SecurityContextHolder`. For unauthenticated operations (e.g., bootstrap), they default to `0` (system).

Optimistic locking via `@Version` prevents lost updates when concurrent requests modify the same entity.

### 4.3 Entity Descriptions

#### `roles`
Defines the coarse-grained role assigned to each user. A user has exactly one role.

| Column | Notes |
|---|---|
| `name` | Unique, max 50 chars. Built-in values: `USER`, `ADMIN`. `MERCHANT` reserved for future use. |

The `USER` role is seeded at schema creation (`V1__schema.sql`). The `ADMIN` role is created at runtime by `AdminBootstrapRunner` on first startup.

#### `users`
Central identity table. One user has one role and zero or more permissions.

| Column | Notes |
|---|---|
| `username` | Unique login identifier |
| `password` | BCrypt-encoded, never stored in plain text |
| `role_id` | FK to `roles`. DELETE RESTRICT — a role cannot be deleted while users hold it |
| `enabled` | `false` = account inactive. New ADMIN accounts start disabled until explicitly activated |
| `locked` | `true` = account locked. New ADMIN accounts start locked |
| `expired` | `true` = account expired |

Account state is enforced by Spring Security's `UserDetails` contract: all three flags are checked before a token is accepted.

#### `permissions`
Fine-grained capabilities that can be assigned to individual admin users. Permissions are independent of roles — an admin has a role (`ADMIN`) plus zero or more permissions.

| Column | Notes |
|---|---|
| `name` | Unique, max 100 chars. System-defined: `CREATE_ADMIN`, `ACTIVATE_ADMIN`, `ASSIGN_PERMISSIONS` |
| `description` | Human-readable explanation |

#### `user_permissions`
Junction table linking users to permissions. Unique constraint on `(user_id, permission_id)` prevents duplicates. The `is_active` column (inherited from `AuditableEntity`) controls whether the assignment is currently effective — only active assignments are loaded as Spring Security authorities.

#### `password_reset_otps`
Records each OTP issued for password reset. OTPs are stored BCrypt-hashed, never in plain text.

| Column | Notes |
|---|---|
| `otp` | BCrypt hash of the 6-digit code |
| `reset_token` | UUID token issued after OTP verification; used for the final password reset step |
| `expires_at` | Absolute expiry timestamp based on configured `OTP_EXPIRATION_MINUTES` |
| `is_used` | Set to `true` when the OTP is verified or invalidated |
| `is_deleted` | Set to `true` alongside `is_used` to mark the record as consumed |

#### `otp_rate_limit`
One record per user, tracking the sliding-window OTP request rate.

| Column | Notes |
|---|---|
| `window_start` | Start of the current 60-minute window |
| `request_count` | Number of OTP requests in the current window |

Maximum 5 requests per 60-minute window. When the window has expired, it resets automatically on the next request.

#### `refresh_tokens`
Stores issued refresh tokens with their expiry. Currently provisioned in the schema for future use.

---

## 5. Security Design

### 5.1 Authentication Model

The system uses **stateless JWT authentication**. No server-side sessions are maintained (`SessionCreationPolicy.STATELESS`). Every request must carry a valid token.

On login, the server issues a **short-lived access token** (configurable in minutes). The token is signed with an HMAC-SHA key derived from `JWT_SECRET`. The token payload contains the username and expiry — it does not embed roles or permissions, so every validated request triggers a fresh database lookup to obtain current authorities.

### 5.2 JWT Token Lifecycle

```
POST /api/v1/auth/login
  │
  ├── Spring AuthenticationManager verifies username + password
  ├── Checks: enabled, accountNonLocked, accountNonExpired
  ├── Generates access token (signed JWT, expiry = JWT_ACCESS_EXPIRATION_MINUTES)
  └── Returns: { token_type, access_token }

Subsequent requests:
  Authorization: Bearer <access_token>
  │
  ├── JwtAuthenticationFilter.extractUsername() — parses subject claim
  ├── UserDetailsService.loadUserByUsername() — DB lookup with role + permissions
  ├── JwtTokenProvider.isTokenValid() — verifies signature + expiry + subject match
  └── SecurityContextHolder populated with user + authorities
```

Token validation failures (malformed, expired, wrong signature) all result in `extractUsername()` returning `null`, which causes the request to proceed as unauthenticated and Spring Security to return `401 Unauthorized`.

### 5.3 Authorization Model

The system uses a **two-tier authorization model**:

**Tier 1 — Role-based (coarse-grained)**

Every user has exactly one role. Route-level rules in `SecurityConfig` enforce role requirements:

| Path Pattern | Required Role |
|---|---|
| `/api/v1/admins/**` | `ADMIN` |
| `/api/v1/users/**` | `USER` |
| Any other authenticated path | Any valid authentication |
| `/api/v1/auth/**` | Public |
| `/actuator/**` | Public |
| Swagger paths | Public |

**Tier 2 — Permission-based (fine-grained)**

Individual admin accounts are assigned named permissions. These are enforced at the method level using `@PreAuthorize` on controller methods:

| Permission | Grants ability to |
|---|---|
| `CREATE_ADMIN` | Register new admin accounts |
| `ACTIVATE_ADMIN` | Activate a pending admin account |
| `ASSIGN_PERMISSIONS` | Grant permissions to other admins |

Permissions are only effective while their `is_active` flag is `true` in `user_permissions`. A permission grant can be revoked by setting `is_active = false` without deleting the record.

**Permission delegation rule:** An admin can only assign permissions they themselves hold. The `ASSIGN_PERMISSIONS` permission is required to assign any permission at all, and the set of permissions assignable is strictly limited to those the granter currently possesses.

### 5.4 Account States

A user account has three independent boolean state flags. Spring Security enforces all three during authentication:

| Flag | Meaning when `true` | Spring Security exception |
|---|---|---|
| `enabled` | Account is active and usable | `DisabledException` → 403 |
| `locked` | Account is locked (e.g., pending activation) | `LockedException` → 423 |
| `expired` | Account credentials or access has expired | `AccountExpiredException` → 403 |

New `ADMIN` accounts are created with `enabled = false` and `locked = true`. They cannot log in until an admin with `ACTIVATE_ADMIN` permission explicitly activates them. New `USER` accounts are activated immediately at registration.

### 5.5 CORS Policy

CORS is configured globally. Allowed origins:

- `http://localhost:3000` (local frontend development)
- `http://localhost:8080` (local backend development / same-origin testing)

All standard HTTP methods are allowed. All request headers are allowed. Credentials (Authorization header) are permitted. This configuration is applied to all routes (`/**`).

### 5.6 Bootstrap Admin

On first startup, if no `superadmin` user exists, `AdminBootstrapRunner` automatically:

1. Creates the `ADMIN` role
2. Creates the three system permissions
3. Creates a `superadmin` account with a randomly generated UUID-based password
4. Grants all three permissions to `superadmin`
5. Activates the account

The temporary password is printed to the application logs as a `WARN`-level message. It must be changed immediately. On subsequent startups, the runner detects the existing account and does nothing.

This runner is disabled in the `test` Spring profile via `@Profile("!test")`.

---

## 6. API Design

### 6.1 Design Principles

- All paths are versioned under `/api/v1/`
- JSON request and response bodies throughout
- Response field names use `snake_case` (Jackson `SnakeCaseStrategy`)
- Errors include a `request_id` for traceability (sourced from the `X-Request-Id` request header)
- HTTP status codes follow REST conventions

### 6.2 Endpoint Reference

#### Authentication — `/api/v1/auth/**` (public)

| Method | Path | Description | Request Body | Response |
|---|---|---|---|---|
| `POST` | `/login` | Authenticate and receive access token | `{ user_name, password }` | `{ token_type, access_token }` |
| `POST` | `/registration/user` | Register a new user account | `{ user_name, password, confirm_password }` | `{ success, id }` |
| `POST` | `/forgot-password` | Request a password reset OTP via email | `{ user_name }` | `{ success, id }` — always 200 regardless of whether username exists |
| `POST` | `/verify-otp` | Verify OTP and receive a reset token | `{ user_name, otp }` | `{ reset_token }` |
| `POST` | `/reset-password` | Set a new password using the reset token | `{ reset_token, new_password, confirm_password }` | `{ success, id }` |

#### Admin Management — `/api/v1/admins/**` (requires `ROLE_ADMIN`)

| Method | Path | Permission | Description | Request Body | Response |
|---|---|---|---|---|---|
| `POST` | `/admins` | `CREATE_ADMIN` | Register a new admin account (starts disabled) | `{ user_name, password, confirm_password }` | `{ success, id }` |
| `PUT` | `/admins/{admin-id}/activate` | `ACTIVATE_ADMIN` | Activate a pending admin account | — | `{ success, id }` |
| `POST` | `/admins/{admin-id}/permissions` | `ASSIGN_PERMISSIONS` | Grant permissions to an admin | `{ permission_ids: [1, 2] }` | `"Permissions assigned successfully"` |

#### Test Endpoints — `/api/v1/**` (for development/verification)

| Method | Path | Required | Description |
|---|---|---|---|
| `POST` | `/admin-only` | `ROLE_ADMIN` | Verifies admin role |
| `POST` | `/admin-only-create` | `ROLE_ADMIN` + `CREATE_ADMIN` | Verifies combined role + permission |
| `POST` | `/merchant-only` | `ROLE_MERCHANT` | Verifies merchant role |
| `POST` | `/user-only` | `ROLE_USER` | Verifies user role |
| `GET` | `/current-time` | `ROLE_USER` | Returns server time |

#### Documentation

| Path | Description |
|---|---|
| `/swagger-ui.html` | Interactive Swagger UI |
| `/v3/api-docs` | OpenAPI 3 JSON specification |

### 6.3 Response Shapes

**Success response** (operations that create or modify):
```
{
  "success": true,
  "id": 42
}
```

**Error response** (all error conditions):
```
{
  "request_id": "abc-123",
  "status": 400,
  "error": "INVALID_ARGUMENT",
  "message": "Human-readable description"
}
```

---

## 7. Password Reset Flow

The password reset process is a three-step flow requiring the user to prove email ownership before being allowed to set a new password.

```
Step 1 — Request OTP
  Client: POST /forgot-password { user_name }
  Server:
    ├── Silently ignore if username does not exist (prevents enumeration)
    ├── enforceOtpRateLimit() — max 5 requests per 60-minute sliding window
    ├── invalidatePreviousOtps() — marks all existing active OTPs as used+deleted
    ├── OtpGenerator.generate6DigitOtp() — SecureRandom 6-digit code
    ├── BCrypt-encodes the OTP before storing
    ├── Generates a UUID reset_token and stores alongside the OTP
    ├── Publishes PasswordResetOtpEvent (async)
    └── PasswordResetOtpEmailListener sends the OTP to the user's email
  Response: always { success: true } — identical for known and unknown usernames

Step 2 — Verify OTP
  Client: POST /verify-otp { user_name, otp }
  Server:
    ├── Looks up user — on not-found throws same "Invalid OTP" error (prevents enumeration)
    ├── Finds the user's active (unused, undeleted) OTP record
    ├── Checks: not already used, not expired, BCrypt matches the submitted OTP
    └── Returns the reset_token stored on the OTP record
  Response: { reset_token: "uuid..." }

Step 3 — Reset Password
  Client: POST /reset-password { reset_token, new_password, confirm_password }
  Server:
    ├── Validates new_password == confirm_password
    ├── Looks up OTP record by reset_token
    ├── Checks: not already used, not expired
    ├── BCrypt-encodes and saves the new password on the user record
    └── Marks the OTP record as used + deleted
  Response: { success: true }
```

**Security properties of this flow:**

- The raw OTP is never stored — only its BCrypt hash
- Username existence is never revealed to the caller
- Each new OTP request invalidates all previous ones
- Rate limiting prevents both spam and brute force on the 6-digit space
- The reset token is a one-time-use UUID valid only within the OTP expiry window
- Email sending is decoupled via Spring events — the HTTP response is not blocked by SMTP

---

## 8. Event System

The application uses Spring's synchronous `ApplicationEventPublisher` to decouple OTP generation from email delivery.

```
PasswordResetServiceImpl
  └── publishEvent(PasswordResetOtpEvent)
        │
        ▼
  PasswordResetOtpEmailListener
        │
        ▼
  EmailServiceImpl
        └── JavaMailSender → SMTP server → User's inbox
```

**`PasswordResetOtpEvent`** carries the username and the raw (un-hashed) OTP — the only point in the flow where the plain OTP is available.

**`PasswordResetOtpEmailListener`** is annotated with `@EventListener` and receives the event synchronously on the same thread. It delegates to `EmailService` which uses Spring Mail to send an HTML-formatted email containing the OTP code and the current timestamp.

This design separates the concern of email delivery from the core OTP lifecycle logic, making each independently testable.

---

## 9. Error Handling

A `@RestControllerAdvice` global exception handler (`GlobalExceptionHandler`) catches exceptions thrown anywhere in the controller or service layers and maps them to structured HTTP responses.

| Exception | HTTP Status | Error Code |
|---|---|---|
| `BadCredentialsException` | 401 Unauthorized | `INVALID_CREDENTIALS` |
| `DisabledException` | 403 Forbidden | `ACCOUNT_DISABLED` |
| `AccountExpiredException` | 403 Forbidden | `ACCOUNT_EXPIRED` |
| `LockedException` | 423 Locked | `ACCOUNT_LOCKED` |
| `AuthorizationDeniedException` | 403 Forbidden | `UNAUTHORIZED` |
| `EntityNotFoundException` | 404 Not Found | `ENTITY_NOT_FOUND` |
| `IllegalArgumentException` | 400 Bad Request | `INVALID_ARGUMENT` |
| `DataIntegrityViolationException` | 409 Conflict | `DATA_INTEGRITY_VIOLATION` |
| `Exception` (catch-all) | 500 Internal Server Error | `INTERNAL_SERVER_ERROR` |

All error responses include the `X-Request-Id` header value from the incoming request, enabling log correlation between client and server.

Spring Security authentication/authorization errors that occur before reaching the controller are handled by two dedicated components:

- **`CustomAuthenticationEntryPoint`** — returns `401` when no valid token is present
- **`CustomAccessDeniedHandler`** — returns `403` when the token is valid but permissions are insufficient

---

## 10. Audit Trail

Every record in the system carries a complete audit trail inherited from `AuditableEntity`. This is implemented using **Spring Data JPA Auditing**.

`AuditorAwareImpl` resolves the current auditor (user ID) from the `SecurityContextHolder` at the time of each write operation. This ID is automatically written into `created_by` or `updated_by` columns. For system operations that occur without an authenticated user (such as the bootstrap runner), the value defaults to `0`.

The `@Version` field on every entity enables **optimistic locking**: if two concurrent transactions attempt to update the same row, the second one fails with an `OptimisticLockException` rather than silently overwriting data.

Soft deletion is supported via `is_deleted` and `deleted_at` / `deleted_by` columns. Records are never physically removed; they are marked deleted. However, most repository queries do not currently filter on `is_deleted`, so this is available for future use.

---

## 11. Infrastructure & Deployment

### 11.1 Docker Compose Services

The application is composed of two Docker services:

```
┌──────────────────────────────────────────────────────────┐
│                    Docker Network                        │
│                                                          │
│  ┌───────────────────────┐    ┌─────────────────────┐   │
│  │   auth-backend-1      │    │   auth-database-1   │   │
│  │                       │───►│                     │   │
│  │  Spring Boot App      │    │  PostgreSQL 16      │   │
│  │  Port: 8080           │    │  Port: 5432         │   │
│  │  eclipse-temurin:21   │    │  Named volume:      │   │
│  │                       │    │  auth_data          │   │
│  └───────────────────────┘    └─────────────────────┘   │
└──────────────────────────────────────────────────────────┘
```

**`auth-database-1`**
- Image: `postgres:16`
- Persists data in a Docker named volume `auth_data` — data survives container restarts
- Exposes PostgreSQL on a configurable host port (`POSTGRES_PORT`)
- Health check: `pg_isready` runs every 10 seconds, 5 retries before marking unhealthy

**`auth-backend-1`**
- Built from the project `Dockerfile` using `eclipse-temurin:21-jdk-alpine`
- Only starts after the database is healthy (`depends_on: condition: service_healthy`)
- Exposes port `8080`
- Receives all configuration via environment variables

### 11.2 Database Migrations

Schema changes are managed exclusively by **Flyway**. Migrations live in `src/main/resources/db/migration/` and are named with the pattern `V{n}__{description}.sql`.

| Migration | Description |
|---|---|
| `V1` | Core schema: `roles`, `permissions`, `users`, `refresh_tokens`, `user_permissions`, `password_reset_otps`, `reset_tokens`. Seeds the `USER` role. |
| `V2` | Alters `password_reset_otps` (adds `reset_token` column) |
| `V3` | Creates `otp_request_log` table |
| `V4` | Drops `otp_request_log`, creates `otp_rate_limit` |

Flyway runs automatically on application startup. The `HIBERNATE_DDL_AUTO` setting is `validate` in production — Hibernate confirms the entity model matches the schema but never modifies it. This makes Flyway the single source of truth for the database structure.

### 11.3 Configuration

All sensitive and environment-specific values are externalized as environment variables. No secrets are hardcoded. Fail-fast expressions (e.g., `${JWT_SECRET:?JWT_SECRET must be set}`) ensure the application refuses to start if required variables are missing.

| Category | Variables |
|---|---|
| Database | `DATABASE_URL`, `DATABASE_USERNAME`, `DATABASE_PASSWORD` |
| JWT | `JWT_SECRET`, `JWT_ACCESS_EXPIRATION_MINUTES`, `JWT_REFRESH_EXPIRATION_DAYS` |
| OTP | `OTP_EXPIRATION_MINUTES` |
| Email | `MAIL_USER_NAME`, `MAIL_PASSWORD`, `MAIL_SENDER_NAME`, `MAIL_HOST`, `MAIL_PORT` |
| JPA (optional) | `HIBERNATE_DDL_AUTO`, `JPA_SHOW_SQL`, `HIBERNATE_FORMAT_SQL` |
