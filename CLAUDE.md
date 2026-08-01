# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project overview

Spring Boot 4 (Java 17) backend for a spring backend template system. Package root:
`com.example.springbackendtemplate1`. PostgreSQL via Spring Data JPA, schema managed entirely by Flyway
(`spring.jpa.hibernate.ddl-auto: none` — never rely on Hibernate auto-DDL). JWT-based stateless auth, role-based
authorization, image hosting (Cloudinary/S3), and OpenAPI/Swagger docs.

## Commands

Build tooling is **Maven with a wrapper** — use `./mvnw` (bash) or `mvnw.cmd` (Windows), not a globally
installed `mvn`, and not Gradle.

```bash
./mvnw.cmd -q -DskipTests compile   # compile only, fastest way to check for errors (Windows)
./mvnw compile                       # same, POSIX
./mvnw test                          # run the test suite
./mvnw test -Dtest=ClassName#method  # run a single test
./mvnw spring-boot:run                # run the app locally (needs env vars below + a running Postgres)
```

Docker (app + Postgres):

```bash
docker-compose up
```

`docker-compose.yml` reads `POSTGRES_DB`/`POSTGRES_USER`/`POSTGRES_PASSWORD`/`POSTGRES_PORT` from `.env`.

The app itself requires additional env vars beyond `.env` (not all have defaults — see
`src/main/resources/application.yaml`): `DATABASE_URL`, `DATABASE_USERNAME`, `DATABASE_PASSWORD`, `JWT_SECRET`,
`JWT_ACCESS_EXPIRATION_MINUTES`, `JWT_REFRESH_EXPIRATION_DAYS`, `OTP_EXPIRATION_MINUTES`, `MAIL_USER_NAME`,
`MAIL_PASSWORD`, `MAIL_SENDER_NAME`, plus optional Cloudinary/S3 credentials.

There is no lint/format command configured beyond the IDE-driven `qodana.yaml` static analysis profile.

## Architecture

### Module layout

Code is organized by **domain module** under `src/main/java/.../resortbackendapplication1/`, not by technical
layer: `address`, `auth`, `bedtype`, `contact`, `currency`, `dayofweek`, `facility`, `facilitypricetype`,
`imagehosting`, `locale`, `pagetype`, `price`, `resort`, `resortaccesstype`, `resortbasicinfo`, `resortcontact`,
`resortfacilityprice`, `resortpermissiontype`, `resortroomcategory`, `resortroomcategoryprice`, `roomcategory`,
`uiblocksection`, `unit`, `unittype`. Each module is internally layered; `commons` holds cross-cutting
infrastructure shared by every module.

### Per-entity layered shape (repeated in every module)

Every CRUD entity (e.g. `Country`) follows the same file layout and layer responsibilities:

- `controller/` — resolves related entities (parent, the single `en` locale for create, etc.) via the
  service's `getEntityById`/`LocaleService.getEntityByCode("en")`, then delegates to the service, passing
  entities rather than bare ids. Does **not** handle `Accept-Language` itself — that's global (see
  `commons/filter/LocaleContextFilter.java` below)
- `service/` — interface: `create`, `getById`, `getAll(filter)`, `update`, `delete`, `getEntityById`. `getAll`
  does **not** take a `localeId` parameter from the controller — `ServiceImpl` reads
  `LocaleContext.getLocaleId()` itself
- `serviceImpl/` — `@Service @Slf4j`, constructor injection, `@Transactional` on writes, **soft delete only**
  (`isDeleted=true`, `isActive=false`, then save — never a hard delete)
- `repository/` — `JpaRepository`, plus `JpaSpecificationExecutor` when the entity has a filterable list
  endpoint
- `specification/` — Spring Data `Specification` builders for filter/search
- `model/entity/` — `@Getter @Setter` only (no `@Builder`, no `@Data`), extends
  `commons/model/entity/AuditableEntity.java`
- `model/mapper/` — `@UtilityClass` (no `static` keyword — Lombok adds it), methods named `create`, `update`,
  `toDto` (not `fromRequest`); immutable/unique fields are set only in `create()`, never in the shared
  `applyCommonFields()` used by both `create()` and `update()`

  **Single-`toDto`-method mapper strategy** (current live shape as of 2026-08-01 — see
  `address/model/mapper/CountryMapper.java`, `CityMapper.java`, `currency/model/mapper/CurrencyMapper.java`,
  `unit/model/mapper/UnitTypeMapper.java`, `UnitMapper.java`): every entity mapper exposes **exactly one**
  `toDto` method — never overloads, never a `toDtoWithout{X}` family. Signature shape:

  ```java
  public {Entity}Dto toDto({Entity}Entity entity,
                           boolean includeLocales,
                           boolean include{EachForeignKeyOrNestedCollectionField}...)
  ```

  - Basic scalar fields are always present, never gated behind a parameter.
  - `includeLocales` controls the entity's **own** locale collection only:
    - `true` → every active translation (`activeLocales(entity).stream().map({Entity}LocaleMapper::toDto).toList()`).
    - `false` → exactly one translation, via a private `singleLocale(entity)` helper calling
      `matchLocale(entity, LocaleContext.getLocaleId())`. `matchLocale` already encodes the fallback: match
      the id `LocaleContext` resolved from the current request's `Accept-Language` (via
      `commons/filter/LocaleContextFilter.java` → `commons/context/LocaleContext.java`); if no translation
      exists for that locale, fall back to whichever translation has code `"en"`; if neither exists, an
      empty list.
  - Every foreign-key reference (a single nested object, e.g. `City.country`) or nested child collection
    (e.g. `Country.cities`) gets its own `boolean include{X}` parameter — `true` embeds it via the *other*
    mapper's `toDto`, `false` sets it to `null`/`List.of()`.
  - **Nested/embedded calls always pass `includeLocales=false`, never the caller's own value.** Any entity
    embedded inside another (a city inside `Country.cities`, a country inside `City.country`, a unit type
    inside `Unit.unitType`, etc.) always shows exactly the one locale matching the current request's
    `Accept-Language`, regardless of whether the *root* entity being fetched is showing every locale or just
    one — this is what lets `getById` return every translation of the entity you asked for while every
    embedded child/parent still shows only one.
  - Nested calls also always pass `false` for their own further-nested include flags (e.g. `Country` embeds
    `City` via `CityMapper.toDto(cityEntity, false, false)` — `includeCountry=false`) — this is what stops the
    object graph from recursing, since a city embedded inside a country never re-embeds that country, and a
    country embedded inside a city never re-embeds its own cities/currencies.
  - There is **no `Optional<Long> localeId` parameter anywhere** in a mapper signature (an earlier,
    now-abandoned intermediate design). `ServiceImpl.getAll` still reads
    `Long localeId = LocaleContext.getLocaleId()` for the repository `Specification` (DB-level locale-aware
    sort/search), but never threads that value into the mapper — the mapper always resolves its own
    single-locale case from `LocaleContext` directly.

  **Call-site convention:** `getById` → `toDto(entity, true, true, true, ...)` (every own locale, every nested
  field, in full — the "detailed" view). `getAll` → `toDto(entity, false, ...)` (one Accept-Language-matched
  locale); a ROOT entity's list rows typically exclude their own heavy nested collections
  (`includeCities=false`/`includeCurrencies=false` for `Country`, `includeUnits=false` for `UnitType`), while a
  CHILD entity's list rows keep embedding their single parent object (`includeCountry=true` for
  `City`/`Currency`, `includeUnitType=true` for `Unit`).

  **Worked example** (3 seeded locales `en`/`bn`/`fr`; Bangladesh has `en`+`bn`; United States has `en`+`fr`):
  `GET /countries/{bangladesh-id}` with `Accept-Language: en` → `locales` has 2 entries (`en`, `bn` — header
  ignored entirely for `getById`). `GET /countries` with `Accept-Language: bn` → Bangladesh's row shows 1
  locale (`bn`); United States' row shows 1 locale (`en`, since it has no `bn` translation and falls back).
- `model/dto/` — plain DTOs built via builder
- `model/enums/` — `XSortField`, `XSearchField` restrict what's sortable/filterable
- `dto/request/{entity}/` — `{Entity}Request` (base), `Create{Entity}Request`, `Update{Entity}Request`,
  `{Entity}FilterRequest` (+ `locale/` subfolder for translatable sub-resources)
- `dto/response/{entities}/` — wraps the entity DTO in a single response field

Some entities have a **locale sub-resource** (e.g. `CountryLocale`) exposed as a nested resource
(`/api/v1/countries/{country-id}/locales`) for translated fields — it follows the identical
controller/service/serviceImpl/repository/entity/mapper pattern one level down.

**This pattern has changed multiple times historically** (directory renames, mapper method renames, response
DTO field renames, locale-aware search/sort added, an entity's locale sub-resource removed entirely). When
implementing a new entity or touching an existing one, treat the *current* `address/` package's `Country`
files as the live reference — re-read them fresh rather than trusting a remembered/documented snapshot of the
pattern. `docs/filterable-specification-pattern.md` and `docs/localization-architecture.md` are known to
describe an older shape than what's currently on disk.

### CRUD API generation flow

When asked to implement, scaffold, or generate CRUD API functionality for an entity (e.g. "implement Region
CRUD", "scaffold the Amenity API"), **run the flow yourself, inline, in the main conversation — do not
dispatch it via the Agent tool / `crud-api-generation` subagent.** That subagent definition
(`.claude/agents/crud-api-generation.md`) exists as a **playbook to read and follow directly**, not as
something to delegate to in the background: it interleaves multiple rounds of clarifying questions with the
user (schema confirmation, per-field Create/Update/Filter/Sort classification, bidirectional-relationship
choice) between generation steps, and those questions must reach the user in the foreground via
`AskUserQuestion` as part of this same turn/conversation — not get asked by a backgrounded agent whose prompts
the user never sees until it's already finished or stuck.

Concretely: open `.claude/agents/crud-api-generation.md` and execute its Step 0 through Step 5 yourself —
Step 0 (verify the schema/migration exists), Step 1 (the clarifying questionnaire, asked via `AskUserQuestion`,
including the mandatory per-field classification for the entity and any locale sub-resource), Step 2 (re-read
the live `Country`/`CountryLocale` reference files and the target entity's own files, then generate every
layer), Step 3 (locale sub-table migration if applicable), Step 4 (compile check), Step 5 (optional API docs).
Follow its "Ordering rule" and "Ground truth rule" exactly as written — they encode lessons from this being
gotten wrong before (reading implementation files or reporting disk state before Step 1's questions were
confirmed).

### Shared infrastructure (`commons/`)

- `model/entity/AuditableEntity.java` — base entity: id, createdBy/At, updatedBy/At, version, isActive,
  isDeleted, deletedBy/At. Every entity extends this.
- `model/entity/EntityRelationshipHelper.java` — helper for bidirectional parent/child collection wiring
  (`addChild`/`removeChild` style) used by some entities instead of plain `@OneToMany(mappedBy=...)`.
- `dto/request/PaginatedRequest.java` / `dto/response/PaginatedResponse.java` — pagination contract; list
  endpoints take a `PaginatedRequest` and `utils/Pagination.java`'s `buildPaginatedResponse(page)` wraps the
  resulting Spring `Page`.
- `dto/response/SuccessResponse.java` — standard write response shape `{ success, id }`.
- `dto/response/ApiErrorResponse.java` — standard error shape, produced by `exception/GlobalExceptionHandler.java`.
- `utils/EntityValidator.java` — `validateAllFound(ids, entities, getId, "EntityName")` for bulk FK validation.
- `context/LocaleContext.java` — `ThreadLocal<Long>` holder for the current request's resolved locale id;
  `filter/LocaleContextFilter.java` (registered in `auth/config/SecurityConfig.java`, runs for every request)
  resolves `Accept-Language` via `LocaleService.resolveLocaleId(...)`, stores it via
  `LocaleContext.setLocaleId(...)`, and rejects the request with `400 INVALID_ARGUMENT` if the header is
  missing/blank — `Accept-Language` is mandatory platform-wide, not just on list endpoints. Mappers read
  `LocaleContext.getLocaleId()` directly (see the mapper strategy above) rather than having it passed in.
- Root entities with a locale sub-resource take exactly one locale at `create()` time (a single
  `{Entity}LocaleRequest`, no `locale_id`), always attached server-side to whichever `Locale` has code `"en"`
  via `LocaleService.getEntityByCode("en")` — the controller resolves it directly, no map/list resolution
  helper needed. Additional translations are added afterward via the entity's own
  `POST /{entities}/{id}/locales` sub-resource, which still takes an explicit `locale_id`.
- `utils/Filterable.java`, `utils/SpecificationUtils.java`, `utils/SearchType.java`, `utils/LocaleSortable.java`,
  `utils/LocaleJoinSortInfo.java` — the filterable-specification / locale-aware sort-search framework used by
  every module's `specification/` classes. Read the live signatures before using — this is the most
  actively-evolving part of the shared layer.

### Cross-cutting error handling

`commons/exception/GlobalExceptionHandler.java` is a single `@RestControllerAdvice` covering the whole app —
validation errors, `DataIntegrityViolationException` (with a few hand-mapped unique-constraint messages),
`EntityNotFoundException`, auth/authorization exceptions, and a catch-all. New constraint-specific user-facing
messages get added to `resolveConstraintMessage(...)` there, not scattered per-module.

### Auth (`auth/`)

Stateless JWT auth: `auth/filter/JwtAuthenticationFilter.java` runs before
`UsernamePasswordAuthenticationFilter`; `auth/config/SecurityConfig.java` defines the single
`SecurityFilterChain` for the whole app (CSRF disabled, sessions stateless). Route authorization is
centralized there — public: `/api/v1/auth/**`, `/api/v1/locales/**`, Swagger/actuator; role-gated:
`/api/v1/admins/**` (`ROLE_ADMIN`), `/api/v1/users/**` (`ROLE_USER`); everything else requires authentication.
`@EnableMethodSecurity` is also on, so method-level `@PreAuthorize` is available in addition to the URL rules.
Per the `crud-api-generation` agent's convention, new entity CRUD modules should **not** need `SecurityConfig`
changes — Spring component scanning picks up new controllers automatically, and they fall under the
`anyRequest().authenticated()` default unless a route pattern is added deliberately.

### Migrations

Flyway migrations live in `src/main/resources/db/migration/`, versioned `V{n}__description.sql`,
`out-of-order: true` is enabled. The schema in these files is the **source of truth** for entity fields — when
adding/changing an entity, read the migration first rather than inventing columns.

### API documentation

`docs/*.md` — one file per resource (e.g. `docs/countries-api.md`), each following a fixed structure: endpoints
table, per-entity data model table(s), one section per endpoint (path/query/request params + JSON
request/response examples), and an error-responses table. `docs/countries-api.md` is the canonical structural
reference. Note: `docs/filterable-specification-pattern.md` and `docs/localization-architecture.md` describe
patterns that have since evolved — don't treat them as current without cross-checking live code.