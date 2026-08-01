# Countries API

Base URL: `/api/v1/countries`

Countries represent nations recognized by the platform, each identified by a unique `code` (e.g., `BD`)
plus a 3-letter ISO code and an international calling code. A country's display name and description are
locale-specific and are managed through a companion sub-resource — Country Locales — reached via
`/api/v1/countries/{country-id}/locales`. Countries also track associated cities and currencies, embedded
as the `cities` and `currencies` fields on the response — see the [Cities API](cities-api.md) and
[Currencies API](currencies-api.md) for their own dedicated endpoints. All records support soft-delete —
deleted records are hidden from all responses.

**`Accept-Language` is required on every endpoint below, with no exceptions** — a request missing (or with
a blank) `Accept-Language` header is rejected with `400 INVALID_ARGUMENT` before it reaches any endpoint
(see [Error Responses](#error-responses)). What differs per endpoint is whether the header's *value* is
actually used to shape the response:

- **`GET /{id}` (Get Country)** — the header must be present, but its value is ignored for the country's
  own `locales` — every translation the country has comes back, always. It **is** used, however, to pick
  the single locale shown for each embedded city/currency (see below).
- **`GET` (List/Search Countries)**, and every embedded city/currency anywhere in this API — the header's
  value selects exactly one locale translation: an exact match if the country/city/currency has one,
  otherwise `en`, otherwise no translation at all.
- **`POST`/`PUT`/`DELETE`** — the header must be present but its value has no effect at all.

---

## Endpoints

| Method | Path                                          | Description             |
|--------|-----------------------------------------------|-------------------------|
| POST   | `/api/v1/countries`                           | Create a country        |
| GET    | `/api/v1/countries`                           | List / search countries |
| GET    | `/api/v1/countries/{id}`                      | Get a country           |
| PUT    | `/api/v1/countries/{id}`                      | Update a country        |
| DELETE | `/api/v1/countries/{id}`                      | Delete a country        |
| POST   | `/api/v1/countries/{country-id}/locales`      | Create a country locale |
| PUT    | `/api/v1/countries/{country-id}/locales/{id}` | Update a country locale |
| DELETE | `/api/v1/countries/{country-id}/locales/{id}` | Delete a country locale |

---

## Data Model

### Country

| Field        | Type    | Required | Constraints                                                                     | Description                                                                                                                                   |
|--------------|---------|----------|---------------------------------------------------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------|
| `id`         | Long    | —        | read-only                                                                       | Auto-generated identifier                                                                                                                     |
| `code`       | String  | Yes      | max 10 chars, unique among active records; set at creation, immutable           | Short country code (e.g., `BD`)                                                                                                               |
| `iso3_code`  | String  | Yes      | max 3 chars, must match `^[A-Z]{3}$`                                            | 3-letter ISO country code (e.g., `BGD`)                                                                                                       |
| `phone_code` | String  | Yes      | max 3 chars, must match `^[0-9]{1,3}$`                                          | International calling code (e.g., `880`)                                                                                                      |
| `sort_order` | Integer | Yes      | default 0                                                                       | Display order                                                                                                                                 |
| `locales`    | Array   | —        | see CountryLocale below                                                         | Locale-specific translations — **every** translation on `GET /{id}`, exactly **one** (Accept-Language-matched, `en` fallback) everywhere else |
| `cities`     | Array   | —        | read-only; `[]` except on `GET /{id}`; each entry omits its own `country` field | Cities belonging to this country — see [Cities API](cities-api.md)                                                                            |
| `currencies` | Array   | —        | read-only; `[]` except on `GET /{id}`; each entry omits its own `country` field | Currencies belonging to this country — see [Currencies API](currencies-api.md)                                                                |

### CountryLocale

| Field         | Type    | Required | Constraints                                      | Description                                                                    |
|---------------|---------|----------|--------------------------------------------------|--------------------------------------------------------------------------------|
| `id`          | Long    | —        | read-only                                        | Auto-generated identifier                                                      |
| `locale`      | Locale  | —        | read-only, resolved from `locale_id` at creation | The locale this translation is written in (`id`, `code`, `name`, `sort_order`) |
| `name`        | String  | Yes      | max 255 chars                                    | Localized country name                                                         |
| `description` | String  | Yes      | not null (defaults to `""`)                      | Localized description                                                          |
| `sort_order`  | Integer | Yes      | default 0                                        | Display order among locale entries                                             |

---

## Create Country

`POST /api/v1/countries`

Creates a new country together with exactly **one** initial locale translation. `code` must be unique
among active, non-deleted countries — attempting to reuse an existing code returns `409 CONFLICT`.

**The initial translation is always attached to the `en` locale, resolved by the server — the request
carries no `locale_id` at all.** There is no option to submit multiple locales at creation time.
Additional languages are added afterward via the Country Locales sub-resource below.

### Request Body

```json
{
  "code": "BD",
  "iso3_code": "BGD",
  "phone_code": "880",
  "sort_order": 1,
  "locale": {
    "name": "Bangladesh",
    "description": "People's Republic of Bangladesh",
    "sort_order": 1
  }
}
```

### Request Fields

| Field        | Type    | Required | Validation                                                                                 |
|--------------|---------|----------|--------------------------------------------------------------------------------------------|
| `code`       | String  | Yes      | Not blank, max 10 chars, unique among active records                                       |
| `iso3_code`  | String  | Yes      | Not blank, max 3 chars, must match `^[A-Z]{3}$`                                            |
| `phone_code` | String  | Yes      | Not blank, max 3 chars, must match `^[0-9]{1,3}$`                                          |
| `sort_order` | Integer | Yes      | Not null                                                                                   |
| `locale`     | Object  | Yes      | Not null; validated (see below) — no `locale_id` field; always resolved to the `en` locale |

**Locale entry (`locale`):**

| Field         | Type    | Required | Validation               |
|---------------|---------|----------|--------------------------|
| `name`        | String  | Yes      | Not blank, max 255 chars |
| `description` | String  | Yes      | Not null                 |
| `sort_order`  | Integer | Yes      | Not null                 |

### Response `201 Created`

```json
{
  "success": true,
  "id": 1
}
```

---

## Get Country

`GET /api/v1/countries/{id}`

Returns a single active country by its ID, including **every** locale translation associated with it
(the `Accept-Language` value has no effect on the country's own `locales` — it's used only to pick the
single locale shown for each embedded city/currency). See List/Search below for the locale-scoped
equivalent of the country's own translations.

### Path Parameters

| Parameter | Type | Description       |
|-----------|------|-------------------|
| `id`      | Long | ID of the country |

### Response `200 OK`

```json
{
  "data": {
    "id": 1,
    "code": "BD",
    "iso3_code": "BGD",
    "phone_code": "880",
    "sort_order": 1,
    "locales": [
      {
        "id": 1,
        "locale": {
          "id": 1,
          "code": "en",
          "name": "English",
          "sort_order": 1
        },
        "name": "Bangladesh",
        "description": "People's Republic of Bangladesh",
        "sort_order": 1
      },
      {
        "id": 2,
        "locale": {
          "id": 2,
          "code": "bn",
          "name": "Bengali",
          "sort_order": 2
        },
        "name": "বাংলাদেশ",
        "description": "",
        "sort_order": 2
      }
    ],
    "cities": [
      {
        "id": 1,
        "code": "DHK",
        "sort_order": 1,
        "locales": [
          {
            "id": 1,
            "locale": {
              "id": 1,
              "code": "en",
              "name": "English",
              "sort_order": 1
            },
            "name": "Dhaka",
            "description": "Capital city of Bangladesh",
            "sort_order": 1
          }
        ]
      }
    ],
    "currencies": [
      {
        "id": 1,
        "code": "BDT",
        "numeric_code": "050",
        "symbol": "৳",
        "decimal_places": 2,
        "is_default": true,
        "sort_order": 1,
        "locales": [
          {
            "id": 1,
            "locale": {
              "id": 1,
              "code": "en",
              "name": "English",
              "sort_order": 1
            },
            "name": "Bangladeshi Taka",
            "short_name": "Taka",
            "sort_order": 1
          }
        ]
      }
    ]
  }
}
```

Each embedded city/currency shows exactly **one** locale entry — an exact match for the request's
`Accept-Language`, or `en` if the city/currency has no translation in that locale — regardless of how many
locales the country itself returns above.

---

## List / Search Countries

`GET /api/v1/countries`

Returns a paginated, filterable list of active (non-deleted) countries. All filter parameters are
optional; omitting them returns all countries. Multiple filters are combined with AND. Each `LIKE`-type
filter performs a case-insensitive partial match. `Accept-Language` selects which single locale
translation is included per country (falls back to `en`, then to no translation). **List rows always show
`cities` and `currencies` as empty arrays (`[]`)** — those are only ever populated with entries by
`GET /{id}`.

> **Note:** `id` is not a selectable `sortBy` value — passing `?sortBy=id` throws
> `400 INVALID_ARGUMENT: Invalid sort field: id`. It's used only as the implicit sort when `sortBy` is
> omitted entirely.

### Query Parameters

> **Note:** Query parameters bind directly onto `CountryFilterRequest`'s Java field names, so they are
> **camelCase** — not the snake_case used in JSON request/response bodies. Jackson's `@JsonNaming`
> (which produces snake_case) only applies to `@RequestBody`/`@ResponseBody`; `@ModelAttribute` /
> `@ParameterObject` query-string binding goes through Spring's plain `DataBinder` instead, which
> matches the exact property name.

| Parameter   | Type   | Default         | Constraints                                                                | Description                                                                               |
|-------------|--------|-----------------|----------------------------------------------------------------------------|-------------------------------------------------------------------------------------------|
| `code`      | String | —               | —                                                                          | Accepted but **not currently applied** as a filter (see note below)                       |
| `iso3Code`  | String | —               | —                                                                          | Filter by ISO3 code (partial, case-insensitive)                                           |
| `phoneCode` | String | —               | —                                                                          | Filter by phone code (partial, case-insensitive)                                          |
| `name`      | String | —               | —                                                                          | Filter by locale-specific name (partial, case-insensitive), scoped to the resolved locale |
| `page`      | int    | `0`             | >= 0                                                                       | Zero-based page index                                                                     |
| `size`      | int    | `10`            | 1 – 50                                                                     | Number of items per page                                                                  |
| `sortBy`    | String | `id` (implicit) | `createdAt`, `code`, `iso3Code`, `phoneCode`, `name` (`id` NOT selectable) | Field to sort by                                                                          |
| `sortDir`   | String | `ASC`           | `ASC`, `DESC`                                                              | Sort direction                                                                            |

> **Note:** `code` is bound onto the filter request but is not wired into the search predicates in the
> current implementation (`CountrySearchField` has no entry for it) — passing `?code=...` has no
> filtering effect.

### Response `200 OK`

```json
{
  "data": [
    {
      "id": 1,
      "code": "BD",
      "iso3_code": "BGD",
      "phone_code": "880",
      "sort_order": 1,
      "locales": [
        {
          "id": 1,
          "locale": {
            "id": 1,
            "code": "en",
            "name": "English",
            "sort_order": 1
          },
          "name": "Bangladesh",
          "description": "People's Republic of Bangladesh",
          "sort_order": 1
        }
      ],
      "cities": [],
      "currencies": []
    },
    {
      "id": 2,
      "code": "US",
      "iso3_code": "USA",
      "phone_code": "1",
      "sort_order": 2,
      "locales": [
        {
          "id": 3,
          "locale": {
            "id": 1,
            "code": "en",
            "name": "English",
            "sort_order": 1
          },
          "name": "United States",
          "description": "",
          "sort_order": 1
        }
      ],
      "cities": [],
      "currencies": []
    }
  ],
  "current_page": 0,
  "total_pages": 1,
  "total_elements": 2,
  "page_size": 10,
  "has_next": false,
  "has_previous": false,
  "sortable_fields": [
    "createdAt",
    "code",
    "iso3Code",
    "phoneCode",
    "name"
  ],
  "searchable_fields": [
    "iso3Code",
    "phoneCode",
    "name"
  ]
}
```

---

## Update Country

`PUT /api/v1/countries/{id}`

Updates `iso3_code`, `phone_code`, and `sort_order`. `code` is set at creation and cannot be changed.
Locale translations are managed separately via the Country Locales sub-resource endpoints below, not
through this endpoint.

### Path Parameters

| Parameter | Type | Description       |
|-----------|------|-------------------|
| `id`      | Long | ID of the country |

### Request Body

```json
{
  "iso3_code": "BGD",
  "phone_code": "880",
  "sort_order": 2
}
```

### Request Fields

| Field        | Type    | Required | Validation                                        |
|--------------|---------|----------|---------------------------------------------------|
| `iso3_code`  | String  | Yes      | Not blank, max 3 chars, must match `^[A-Z]{3}$`   |
| `phone_code` | String  | Yes      | Not blank, max 3 chars, must match `^[0-9]{1,3}$` |
| `sort_order` | Integer | Yes      | Not null                                          |

### Response `200 OK`

```json
{
  "success": true,
  "id": 1
}
```

---

## Delete Country

`DELETE /api/v1/countries/{id}`

Soft-deletes the country. The record is not removed from the database but will no longer appear in any
response.

### Path Parameters

| Parameter | Type | Description       |
|-----------|------|-------------------|
| `id`      | Long | ID of the country |

### Response `200 OK`

```json
{
  "success": true,
  "id": 1
}
```

---

## Country Locales

Country Locale endpoints manage locale-specific name/description translations for a country. The
`{country-id}` path parameter must reference an existing, active country.

---

### Create Country Locale

`POST /api/v1/countries/{country-id}/locales`

Adds a new locale translation to an existing country. `locale_id` must reference an existing, active
locale — an unknown `locale_id` returns `404 ENTITY_NOT_FOUND`. The combination of country and locale
must be unique — adding a locale the country already has a translation for returns `409 CONFLICT`,
pre-checked at the application level before any write (backed by a DB-level unique constraint on
`(country_id, locale_id)` as a last-resort guard).

#### Path Parameters

| Parameter    | Type | Description              |
|--------------|------|--------------------------|
| `country-id` | Long | ID of the parent country |

#### Request Body

```json
{
  "locale_id": 2,
  "name": "বাংলাদেশ",
  "description": "গণপ্রজাতন্ত্রী বাংলাদেশ",
  "sort_order": 2
}
```

#### Request Fields

| Field         | Type    | Required | Validation                                  |
|---------------|---------|----------|---------------------------------------------|
| `locale_id`   | Long    | Yes      | Not null; must reference an existing locale |
| `name`        | String  | Yes      | Not blank, max 255 chars                    |
| `description` | String  | Yes      | Not null                                    |
| `sort_order`  | Integer | Yes      | Not null                                    |

#### Response `201 Created`

```json
{
  "success": true,
  "id": 2
}
```

---

### Update Country Locale

`PUT /api/v1/countries/{country-id}/locales/{id}`

Updates `name`, `description`, and `sort_order` for an existing country locale translation. The
associated country and locale cannot be changed after creation.

#### Path Parameters

| Parameter    | Type | Description              |
|--------------|------|--------------------------|
| `country-id` | Long | ID of the parent country |
| `id`         | Long | ID of the country locale |

#### Request Body

```json
{
  "name": "বাংলাদেশ",
  "description": "গণপ্রজাতন্ত্রী বাংলাদেশ",
  "sort_order": 2
}
```

#### Request Fields

| Field         | Type    | Required | Validation               |
|---------------|---------|----------|--------------------------|
| `name`        | String  | Yes      | Not blank, max 255 chars |
| `description` | String  | Yes      | Not null                 |
| `sort_order`  | Integer | Yes      | Not null                 |

#### Response `200 OK`

```json
{
  "success": true,
  "id": 2
}
```

---

### Delete Country Locale

`DELETE /api/v1/countries/{country-id}/locales/{id}`

Soft-deletes a country locale. The record is not removed from the database but will no longer appear in
any response.

#### Path Parameters

| Parameter    | Type | Description              |
|--------------|------|--------------------------|
| `country-id` | Long | ID of the parent country |
| `id`         | Long | ID of the country locale |

#### Response `200 OK`

```json
{
  "success": true,
  "id": 2
}
```

---

## Error Responses

All errors follow a common structure:

```json
{
  "request_id": "abc-123",
  "status": 404,
  "error": "ENTITY_NOT_FOUND",
  "message": "Country not found with id: 99"
}
```

| HTTP Status | Error Code                 | Cause                                                                                                                                                                                          |
|-------------|----------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| 400         | `INVALID_ARGUMENT`         | Missing or blank `Accept-Language` header (checked globally, before any endpoint runs — see the intro above); missing/invalid required fields; or an unsupported `sortBy` query value          |
| 404         | `ENTITY_NOT_FOUND`         | Country not found, country locale not found, or the locale referenced by `locale_id` not found (locale creation)                                                                               |
| 409         | `CONFLICT`                 | `code` already in use by another active country (`create`); or the country already has a translation for the given `locale_id` (`create` country locale, pre-checked at the application level) |
| 409         | `DATA_INTEGRITY_VIOLATION` | Last-resort DB-level unique constraint on `country_id` + `locale_id`, should not normally be reachable now that the duplicate is pre-checked at the application level                          |
