# Application Testing Strategy

This document explains how integration testing is implemented in this project:
what runs, why it's built the way it is, and how to extend it for a new
entity. It's an internal engineering reference, not an API contract — see
`docs/countries-api.md` / `docs/locale-api.md` for the REST API itself.

---

## 1. What kind of tests exist today

All tests in this project are **full-stack integration tests**: real HTTP
requests via `MockMvc`, dispatched through the actual Spring MVC + Spring
Security filter chain, against a real (in-memory) database — never a mocked
`Service`/`Repository` layer, never a hand-poked `SecurityContext`.

| Test class                                    | Covers                                                              |
|------------------------------------------------|-----------------------------------------------------------------------|
| `support/ApiIntegrationTestBase.java`          | Shared base — not a test itself, see §3                               |
| `locale/controller/LocaleControllerTest.java`  | Full CRUD for `/api/v1/locales`                                       |
| `address/controller/CountryControllerTest.java`| Full CRUD for `/api/v1/countries` + its `CountryLocale` sub-resource  |
| `EndToEndFlowTest.java`                        | One continuous realistic journey: superadmin → create Locales → create a Country referencing them → manage its locale sub-resource |
| `SpringBackendTemplate1ApplicationTests.java`  | Default Spring Boot smoke test (`contextLoads`) — unrelated to the above, still targets the real Postgres datasource |

There are currently no pure unit tests (Mockito-mocked service/repository
tests) and no `@DataJpaTest` repository slices — every test exercises the
whole stack through the controller layer. City has no controller yet, so it
has no test coverage (see §9).

---

## 2. Why H2, not the real Postgres database

The app's production datasource is Postgres, managed by Flyway migrations
under `src/main/resources/db/migration`. Those migrations use **Postgres-only
functions** — `pg_get_serial_sequence`, `setval` — to advance sequences past
hand-seeded rows. H2, even in `MODE=PostgreSQL` compatibility mode, doesn't
implement these, so running the real migrations against H2 fails outright.

Rather than requiring a real Postgres instance (via `docker-compose.yml`) to
run the test suite, `src/test/resources/application.yaml` swaps in an
in-memory H2 database and disables Flyway entirely:

```yaml
spring:
  datasource:
    url: jdbc:h2:mem:testdb;MODE=PostgreSQL;DB_CLOSE_DELAY=-1
    username: sa
    password:
    driver-class-name: org.h2.Driver

  jpa:
    hibernate:
      ddl-auto: create-drop
    properties:
      hibernate:
        dialect: org.hibernate.dialect.H2Dialect

  flyway:
    enabled: false
```

With `ddl-auto: create-drop`, Hibernate generates the schema directly from
the JPA entity annotations instead of running any `.sql` migration file. This
file is a **plain, unprofiled** `application.yaml` — Spring Boot prefers
test-classpath resources over `src/main/resources` automatically for any test
run, so no `@ActiveProfiles` is needed for this override to take effect (that
annotation is used for a separate reason — see §3).

The direct consequence: **none of the production seed data exists in the test
database** — no seeded `system` user, no seeded `en` locale, no seeded `BD`
country row. Every test provides its own fixture data (see §3 and §6).

The remaining keys in that file (`jwt.secret`, `spring.mail.*`,
`jwt.otp.expiration-minutes`) are dummy values that satisfy properties the
main `application.yaml` marks as required (`${VAR:?must be set}` or a bare
`${VAR}` with no default) — without them the Spring context would fail to
start at all during a test run. `jwt.secret` specifically must be long enough
for HS384 key derivation (see `JwtTokenProvider`) or `@PostConstruct` throws.

Test-only dependencies added to `pom.xml` for this: `com.h2database:h2` and
`org.springframework.boot:spring-boot-webmvc-test` (this project's Spring
Boot 4.x version splits `@AutoConfigureMockMvc` out of the classic
`spring-boot-test-autoconfigure` module into this separate artifact —
without it, `AutoConfigureMockMvc` doesn't resolve at all).

---

## 3. Authentication strategy — real JWT, not a security-test shortcut

Every test needs an authenticated request, since `SecurityConfig` requires
`.anyRequest().authenticated()` for anything outside `/api/v1/auth/**`. Two
approaches were tried and rejected before landing on the current one:

**Rejected: manually setting `SecurityContextHolder`.** Spring Security 6's
`SecurityContextHolderFilter` runs at the start of every request and
*reloads* the security context from its configured repository — for this
stateless app, that means it **overwrites** whatever was set outside the
request (e.g. in `@BeforeEach`) with an empty context, before the controller
ever sees it. Every request came back `401`, even though `SecurityContextHolder`
clearly had an authenticated principal set moments earlier on the same thread.

**Rejected: `AdminBootstrapRunner` (the production bootstrap flow).** This
runner already creates a `superadmin` user with admin permissions at
application startup — but it's `@Profile("!test")` and generates a random
password that's only ever logged, never recoverable by a test.

**What's actually used:** `ApiIntegrationTestBase` re-implements the same
three bootstrap steps `AdminBootstrapRunner` does (create the `ADMIN` role,
its `CREATE_ADMIN`/`ACTIVATE_ADMIN`/`ASSIGN_PERMISSIONS` permissions,
register + activate a `superadmin` user) by calling the real
`RoleService`/`PermissionService`/`UserService` directly — but with a
**password the test knows** — then additionally grants every permission that
exists in the system to it (not just the 3 bootstrap ones), and logs in for
real via `POST /api/v1/auth/login` to get a genuine, filter-chain-issued JWT:

```java
@BeforeEach
void bootstrapSuperAdmin() throws Exception {
    // ...create ADMIN role, bootstrap permissions, register + activate "superadmin"...
    permissionService.grantPermissions(superAdmin, everyPermissionName);
    superAdminAccessToken = login();
}
```

Every test subclass attaches this token to a request with
`.with(asSuperAdmin())`, a `RequestPostProcessor` that adds a real
`Authorization: Bearer <token>` header — processed by the app's actual
`JwtAuthenticationFilter`, exactly like a real client would authenticate:

```java
mockMvc.perform(post("/api/v1/countries")
                .with(asSuperAdmin())
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
        .andExpect(status().isCreated());
```

Note that for Locale/Country/City endpoints, the superadmin's specific
permission set doesn't actually matter — `SecurityConfig` only requires
`.anyRequest().authenticated()` for them, not a specific `@PreAuthority`. The
"grant every permission" step exists so `EndToEndFlowTest` can also exercise
genuinely permission-gated endpoints (`/api/v1/admins/**`) with the same
superadmin, not because Country/Locale need it.

---

## 4. `@Transactional` rollback — per test *method*, not per class

`ApiIntegrationTestBase` is `@Transactional` at the class level. Spring's test
transaction manager wraps **every individual `@Test` method** (including its
own `@BeforeEach` calls) in a fresh transaction that rolls back once that
method finishes — not once per class, and not once for the whole suite.

Two things follow from this:

- **Every test method re-bootstraps its own superadmin from scratch.** This
  looks wasteful but is required — the previous test method's `superadmin`
  row was already rolled back by the time the next one starts.
- **Each test method needs its own unique fixture values** (a `code`, an
  `iso3_code`, etc.) — nothing persists between methods, but two methods can
  still collide with each other's chosen literal values if they're not
  distinct, since Bean Validation still runs before anything touches the
  (per-method, empty) database. `CountryControllerTest` uses short unique
  codes per method (`T1`, `T2`, `AAA`, `AAB`, …) for exactly this reason.

A genuinely **multi-step, continuous** scenario (bootstrap → create Locale →
create Country referencing it → manage its sub-resource) therefore can't be
split across separate `@Test` methods sharing state — step 2's method would
never see step 1's data, since each method gets its own transaction. This is
why `EndToEndFlowTest` is one single `@Test` method containing the whole
journey, rather than several `@Order`ed ones.

---

## 5. Request/response conventions used inside tests

- **Request bodies** are raw Java text-block JSON strings — no `ObjectMapper`
  is used to serialize them. Keys are snake_case, matching the DTOs'
  `@JsonNaming(SnakeCaseStrategy.class)` — but see §8 for a real gotcha this
  surfaced.
- **Response values are read with `com.jayway.jsonpath.JsonPath`**, never an
  injected `ObjectMapper`:
  ```java
  return ((Number) JsonPath.read(result.getResponse().getContentAsString(), "$.id")).longValue();
  ```
  This project has **two Jackson major versions** on the classpath at once —
  Jackson 3 (`tools.jackson.databind`, what the app's MVC converter actually
  uses) and Jackson 2 (`com.fasterxml.jackson.databind`, pulled in for
  SpringDoc's internal needs via `SpringDocJackson2Config`). Autowiring
  "the" `ObjectMapper` bean in a test is ambiguous and version-fragile;
  `JsonPath` parses the raw response string and doesn't care which Jackson
  produced it.
- **Query parameters are camelCase** (`iso3Code`, `sortBy`, `sortDir`), **not**
  the snake_case used in JSON bodies. `@ModelAttribute`/`@ParameterObject`
  query-string binding goes through Spring's plain `DataBinder`, which
  matches the exact Java field name — Jackson's `@JsonNaming` only applies to
  `@RequestBody`/`@ResponseBody`, never to query-string binding. (This was
  originally documented wrong in `docs/countries-api.md` and `docs/locale-api.md`
  until corrected — see §9.)
- **Path variables** are kebab-case for multi-word ones (`{country-id}`),
  passed as MockMvc URI template args:
  `get("/api/v1/countries/{id}", countryId)`.
- **Test names are human-readable.** Every `@Test` method has a
  `@DisplayName("Test: {plain-English sentence}")` directly above it, and
  every class has its own class-level `@DisplayName` (e.g. `"Country API"`).
  Surefire and IDE test runners show these instead of the raw camelCase
  method name — see any existing test class for the pattern.

---

## 6. Seeding fixture / dependency data

Since the H2 test database starts empty (§2), any entity another entity
depends on via `@ManyToOne` must be seeded directly, bypassing HTTP:

```java
@BeforeEach
void seedLocales() {
    LocaleEntity en = new LocaleEntity();
    en.setCode("en");
    en.setName("English");
    en.setSortOrder(1);
    enLocaleId = localeRepository.save(en).getId();
    // ...
}
```

`CountryControllerTest` seeds `en`/`bn` `LocaleEntity` rows this way rather
than calling `POST /api/v1/locales` — Locale's own create flow is already
covered end-to-end by `LocaleControllerTest`, so re-proving it here would be
redundant. `EndToEndFlowTest` is the exception: it deliberately creates its
locales through real HTTP too, because proving the *whole chain* end-to-end
through the actual API is its entire point.

---

## 7. Soft-delete verification

Every entity in this codebase supports soft-delete (`isActive`/`isDeleted` on
`AuditableEntity`). Every delete test asserts the full round-trip, not just
that the delete call itself returned `200`:

```java
mockMvc.perform(delete("/api/v1/countries/{id}", countryId).with(asSuperAdmin()))
        .andExpect(status().isOk());

mockMvc.perform(get("/api/v1/countries/{id}", countryId).with(asSuperAdmin()))
        .andExpect(status().isNotFound());          // hidden from getById

mockMvc.perform(get("/api/v1/countries").with(asSuperAdmin())
                .param("iso3Code", "...").param("sortBy", "code"))
        .andExpect(jsonPath("$.data.length()").value(0));   // hidden from getAll
```

For a CHILD/locale entity, the test additionally re-fetches the **parent**
after deleting the child, asserting the parent's collection shrinks by one.
This isn't a hypothetical edge case — `CountryMapper.toDto(entity)` was
originally missing this exact filter (see §9) and this specific assertion is
what caught it.

---

## 8. Running the tests

```
mvn test                                    # everything
mvn test -Dtest=CountryControllerTest       # one class
mvn test -Dtest=CountryControllerTest#create_shouldPersistCountryWithLocales   # one method
```

No `docker-compose up` or environment variables are needed — the H2 test
config (§2) is entirely self-contained.

---

## 9. Real bugs this testing approach has already caught

Building this test suite surfaced three real application bugs, not just test
authoring mistakes — worth keeping in mind as evidence for *why* full-stack
integration tests (over mocks) are the right default here:

1. **Missing `MethodArgumentNotValidException` handler.** `GlobalExceptionHandler`
   had no handler for Bean Validation failures, so they fell through to the
   generic `Exception.class` handler and returned `500 INTERNAL_SERVER_ERROR`
   instead of `400 INVALID_ARGUMENT`. Fixed by adding a dedicated handler.
2. **`CountryMapper.toDto(entity)` didn't filter soft-deleted children.**
   Deleting a `CountryLocale` translation still left it visible in
   `GET /countries/{id}`'s `locales` array. Caught exactly by the parent
   re-fetch assertion described in §7.
3. **18 DTOs imported the wrong Jackson major version's `@JsonNaming`**
   (`com.fasterxml.jackson.databind.annotation.JsonNaming`, Jackson 2,
   instead of `tools.jackson.databind.annotation.JsonNaming`, Jackson 3 — what
   this Boot 4 app's MVC converter actually uses). The annotation was
   silently a no-op, so e.g. the login response serialized as `accessToken`
   instead of the documented `access_token`. Fixed across all 18 files.

Separately (not a code bug, but a docs bug this testing effort exposed):
`docs/countries-api.md`, `docs/locale-api.md`, and `docs/country-getall-filtering.md`
previously documented query parameters as snake_case — direct testing showed
they actually bind as camelCase (§5) — and have since been corrected.

---

## 10. Extending this to a new entity

Don't write a new `{Entity}ControllerTest` by hand — use
`.claude/agents/crudapi-16-test-generation-agent.md` (trigger phrases:
"write tests for {Entity}", "generate integration tests for {Entity}"). It
reads the entity's already-generated source files plus a freshly-read
`GlobalExceptionHandler.java`, and generates a test class following every
convention in this document — including checking each involved DTO's
`@JsonNaming` import before trusting snake_case JSON (§9, bug 3), since that
exact mistake is easy to reintroduce on a new entity. `ApiIntegrationTestBase`
and the H2 test config are bootstrapped automatically on first use if they
don't already exist.

---

## 11. Known gaps

- **City has no controller** — no endpoint exists yet, so no test coverage exists for it.
- **No pure unit tests.** Every test goes through the full HTTP stack; there
  are no Mockito-mocked service tests or `@DataJpaTest` repository slices.
- **No test coverage for the `imagehosting` or `auth` modules' own endpoints**
  beyond what `ApiIntegrationTestBase` exercises incidentally (registration,
  login, permission granting) to bootstrap the superadmin.
