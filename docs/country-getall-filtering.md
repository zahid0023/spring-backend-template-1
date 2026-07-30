# Country `getAll` — Filtering, Sorting & Locale Resolution

This document explains the internal mechanics of `GET /api/v1/countries` —
specifically how filtering, sorting, and locale resolution interact. This
endpoint is more involved than a typical `getAll` because it mixes three
concerns that all touch the same request: **searching**, **sorting**, and
**picking which language a country's name is shown in** — and two of those
three depend on data that doesn't live on the `countries` table itself, but
on its `country_locales` child table.

If you're extending this pattern to a new entity, see the "Extending this
pattern" section at the bottom.

---

## 1. The request, end to end

```
GET /api/v1/countries?name=Bangladesh&sortBy=name&sortDir=asc
Accept-Language: en-US,en;q=0.9
```

```
CountryController.getAll(request, acceptLanguage)
        │
        ├─ 1. localeId = localeService.resolveLocaleId(acceptLanguage)
        │
        └─ 2. countryService.getAll(request, localeId)
                    │
                    ├─ CountrySpecification.filter(request, localeId)
                    │       → SpecificationUtils.build(request, localeId)
                    │             ├─ request.toPredicates(root, query, cb, localeId)   ← WHERE clause
                    │             └─ request.getLocaleSortInfo(localeId)                ← ORDER BY clause (if sorting by a locale field)
                    │
                    ├─ request.toPageable(ALLOWED_SORT_FIELDS, CountrySortField.localeSortFields())
                    │       → decides whether Spring Data or the Specification owns the ORDER BY
                    │
                    ├─ countryRepository.findAll(spec, pageable)
                    │
                    └─ page.map(entity -> CountryMapper.toDto(entity, localeId))        ← shapes the response
```

Two completely different mechanisms read `localeId`:

1. **The Specification** — scopes the SQL `JOIN ... ON` to one locale row, for both searching and sorting by `name`.
2. **The Mapper** — after the rows come back, picks exactly one `CountryLocaleDto` per country for the response (with an
   English fallback).

They are independent. Getting the wiring wrong in either one produces working-looking code that returns *technically
valid but locale-wrong* results (e.g. Bangla names when the browser asked for English) — this is why it's worth
understanding both halves separately.

---

## 2. Where `localeId` comes from

`CountryController.getAll` reads the raw `Accept-Language` header and hands it to
`LocaleService.resolveLocaleId(String acceptLanguageHeader)`:

1. Parse the primary language tag: split on `,` (take the first entry), then `;` (drop the `q=` weight), then `-` (drop
   the region subtag). `"en-US,en;q=0.9"` → `"en"`.
2. Look up `LocaleRepository.findByCodeAndIsActiveAndIsDeleted(code, true, false)`. If found, return its id.
3. If not found (or the header was missing/blank), retry with code `"en"`.
4. If `"en"` doesn't exist either, return `null`.

`localeId` is **never** a client-supplied query parameter. Deliberately —
if it were a plain field on `CountryFilterRequest`, a client could ask for
`en` in the header but pass a different `locale_id` in the query string, and
whichever one the code happened to trust would be a silent, hard-to-explain
inconsistency. Making it a real Java parameter threaded from the Controller
down means there is exactly one source of truth per request.

---

## 3. Search — `CountryFilterRequest.toPredicates`

`CountryFilterRequest` has **two** `toPredicates` overloads, because it implements the `Filterable` interface (
`commons/utils/Filterable.java`), which looks like this:

```java
public interface Filterable {
    List<Predicate> toPredicates(Root<?> root, CriteriaQuery<?> query, CriteriaBuilder cb);

    default List<Predicate> toPredicates(Root<?> root, CriteriaQuery<?> query, CriteriaBuilder cb, Long localeId) {
        return toPredicates(root, query, cb);   // default: ignore localeId
    }
}
```

Most `FilterRequest` classes in this codebase (ones with no locale-child search fields) only ever implement the 3-arg
version and never think about `localeId` at all.

`CountryFilterRequest` is different — it overrides **both**, but flips which one does real work:

```java

@Override
public List<Predicate> toPredicates(Root<?> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
    throw new UnsupportedOperationException("CountryFilterRequest requires a localeId — use toPredicates(root, query, cb, localeId)");
}

@Override
public List<Predicate> toPredicates(Root<?> root, CriteriaQuery<?> query, CriteriaBuilder cb, Long localeId) {
    // the real filtering logic — see below
}
```

The 3-arg version throwing is intentional: if anything ever calls it directly (bypassing the localeId-aware path),
that's a bug, and it's much easier to debug a thrown exception than to debug "search results are subtly wrong because
they weren't locale-scoped."

Inside the 4-arg version, `CountrySearchField` (an enum) drives a loop:

| Field                           | Kind                                              | Behavior                                                                                                           |
|---------------------------------|---------------------------------------------------|--------------------------------------------------------------------------------------------------------------------|
| `code`, `iso3Code`, `phoneCode` | direct column on `CountryEntity`                  | `SpecificationUtils.addLikeFilter` / `addEqualFilter` — plain WHERE on the root table                              |
| `name`                          | lives on `CountryLocaleEntity` (the locale child) | `SpecificationUtils.addJoinLikeFilter` / `addJoinEqualFilter` — LEFT JOINs `country_locales`, scoped by `localeId` |

The join-aware helpers look like this (simplified):

```java
Join<T, ?> join = root.join("countryLocaleEntities", JoinType.LEFT);
if(localeId !=null){
        join.

on(cb.equal(join.get("localeEntity").

get("id"),localeId));
        }
        predicates.

add(cb.like(cb.lower(join.get("name")),"%"+value.

toLowerCase() +"%"));
```

Because `(country_id, locale_id)` is unique on `country_locales`, scoping the join's `ON` clause to one `localeId`
guarantees **at most one** matching row per country — which matters for the DISTINCT discussion below.

---

## 4. Sort — `CountryFilterRequest.getLocaleSortInfo` + `LocaleSortable`

Sorting by a locale-child field (`name`) has the exact same "value lives on a joined table" problem as searching, but it
surfaces differently: Spring Data JPA normally builds `ORDER BY` clauses straight off `Pageable.getSort()`, resolving
each sort property directly against the *root* entity class. `name` doesn't exist on `CountryEntity`, so Spring Data
throws `PropertyReferenceException: No property 'name' found for type 'CountryEntity'` if you let it try.

Two things work together to avoid that:

**a) `PaginatedRequest.toPageable` — keep Spring Data out of it for locale fields**

```java
public Pageable toPageable(Set<String> allowedSortFields, Set<String> localeSortFields) {
    if (!allowedSortFields.contains(sortBy)) {
        throw new IllegalArgumentException("Invalid sort field: " + sortBy);
    }
    if (localeSortFields.contains(sortBy)) {
        return PageRequest.of(page, size);   // NO Sort — ordering happens in the Specification instead
    }
    return PageRequest.of(page, size, Sort.by(sortDir, sortBy));
}
```

`CountryServiceImpl.getAll` must call this **two-arg** overload with `CountrySortField.localeSortFields()` — the
single-arg overload always builds a `Sort`, which is exactly what breaks for `name`.

**b) `LocaleSortable` — the Specification builds the ORDER BY itself**

`CountryFilterRequest` also implements `LocaleSortable` (same "no-arg throws, localeId-aware does the work" shape as
`Filterable`):

```java

@Override
public LocaleJoinSortInfo getLocaleSortInfo(Long localeId) {
    if (!CountrySortField.localeSortFields().contains(getSortBy())) {
        return null;   // not sorting by a locale field — nothing to do
    }
    return new LocaleJoinSortInfo("countryLocaleEntities", getSortBy(), "localeEntity", localeId, getSortDir());
}
```

`SpecificationUtils.build()` checks `instanceof LocaleSortable` and, if the returned `LocaleJoinSortInfo` isn't null,
calls `addJoinSort` — which joins `country_locales` (scoped by `localeId`, same as the search join) and calls
`query.orderBy(...)` directly via the Criteria API, bypassing Spring Data's Sort mechanism entirely.

---

## 5. The `DISTINCT` + `ORDER BY` trap (PostgreSQL-specific)

Every join helper above originally called `query.distinct(true)` unconditionally, "just in case" the LEFT JOIN produced
duplicate parent rows. This is necessary when the join is **unscoped** (`localeId == null`) — a country with 3 locale
translations would otherwise appear 3 times. But when `localeId` is non-null, the join's `ON` clause already guarantees
at most one row per country, so DISTINCT was pure dead weight in the common case — and PostgreSQL is strict about the
combination:

> `ERROR: for SELECT DISTINCT, ORDER BY expressions must appear in select list`

`country_locales.name` isn't in the top-level `SELECT` (only `CountryEntity`'s own columns are), so
`SELECT DISTINCT ... ORDER BY cle.name` is flatly rejected by Postgres (other databases are more lenient about this,
which is why it can look fine in dev against, say, H2, and only blow up against Postgres).

The fix, applied to all three join helpers (`addJoinSort`, `addJoinLikeFilter`, `addJoinEqualFilter`): only call
`query.distinct(true)` when `localeId == null`. When it's scoped, skip DISTINCT entirely — there's nothing to
deduplicate.

```java
if(localeId !=null){
        join.

on(cb.equal(join.get(localeEntityField).

get("id"),localeId));
        }else{
        query.

distinct(true);
}
```

**Takeaway:** in this codebase, an unscoped locale join (`localeId == null`) needs DISTINCT; a scoped one (the normal
case, since `resolveLocaleId` almost always returns a real id) must NOT use DISTINCT if you're also sorting by an
unselected joined column.

---

## 6. Shaping the response — `CountryMapper.toDto(entity, localeId)`

Everything above only affects *which rows come back and in what order* — it says nothing about what each row's JSON
looks like. That's a separate, second use of `localeId`, in `CountryMapper`:

- `toDto(entity)` (no `localeId`) — used by `getById` — returns **every** locale translation in `.locales`.
- `toDto(entity, localeId)` — used by `getAll` — returns **at most one** translation:
    1. The one whose `localeEntity.id` equals `localeId`, if it exists.
    2. Otherwise, the one whose `localeEntity.code` is `"en"`.
    3. Otherwise, `.locales` is an empty list (never null).

```java
CountryLocaleEntity matched = entity.getCountryLocaleEntities().stream()
        .filter(c -> c.getLocaleEntity().getId().equals(localeId))
        .findFirst()
        .orElseGet(() -> entity.getCountryLocaleEntities().stream()
                .filter(c -> "en".equals(c.getLocaleEntity().getCode()))
                .findFirst()
                .orElse(null));
```

This is why `getAll` and `getById` genuinely behave differently for the same country — that's by design, not an
inconsistency to fix.

---

## 7. Quick reference — what reads/writes `localeId`

| Layer             | File                                                     | Role                                                                |
|-------------------|----------------------------------------------------------|---------------------------------------------------------------------|
| Controller        | `CountryController.getAll`                               | Reads `Accept-Language` header, resolves `localeId`                 |
| Locale resolution | `LocaleServiceImpl.resolveLocaleId`                      | Header → language code → `LocaleEntity` id, fallback to `"en"`      |
| Service           | `CountryServiceImpl.getAll`                              | Threads `localeId` into both the Specification and the Mapper       |
| Search            | `CountryFilterRequest.toPredicates(..., localeId)`       | Scopes the `name` search join                                       |
| Sort              | `CountryFilterRequest.getLocaleSortInfo(localeId)`       | Scopes the `name` sort join                                         |
| Pagination        | `PaginatedRequest.toPageable(allowed, localeSortFields)` | Keeps Spring Data from building an invalid `Sort` for locale fields |
| Response shape    | `CountryMapper.toDto(entity, localeId)`                  | Picks one locale translation, with English fallback                 |

---

## Extending this pattern to a new entity

This entire mechanism is opt-in and documented inside the `crudapi-*` agents
themselves (`requestdto`, `specification`, `mapper`, `service-interface`,
`service-implementation`, `controller` — each has a "Localization pattern"
section referencing `Country` as the worked example). It only makes sense
for a ROOT entity with a locale companion where searching/sorting by a
locale field AND per-request locale-scoped responses are both real
requirements — not every `*Locale` pair needs it.
