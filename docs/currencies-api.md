# Currencies API

Base URL: `/api/v1/currencies`

Currencies represent monetary units belonging to a country, each identified by a unique ISO 4217 alphabetic
`code` (e.g., `BDT`) and a unique ISO 4217 numeric `numeric_code` (e.g., `050`), plus a display `symbol`,
`decimal_places`, and an `is_default` flag. A currency's display name and optional short name are
locale-specific and are managed through a companion sub-resource — Currency Locales — reached via
`/api/v1/currencies/{currency-id}/locales`. Each currency response embeds a summary of its parent country;
that embedded country's own `cities` and `currencies` fields are always `[]` (a deliberately minimal embed
that both breaks the recursion — a country's `currencies` list would otherwise include this same currency
again, and so on infinitely — and avoids pulling in unrelated sibling collections the caller didn't ask
for). Country responses likewise embed a list of their currencies, each omitting its own `country` field
for the same reason — see the [Countries API](countries-api.md). All records support soft-delete — deleted
records are hidden from all responses.

**`Accept-Language` is required on every endpoint below, with no exceptions** — a request missing (or with
a blank) `Accept-Language` header is rejected with `400 INVALID_ARGUMENT` before it reaches any endpoint
(see [Error Responses](#error-responses)). What differs per endpoint is whether the header's *value* is
actually used to shape the response:

- **`GET /{id}` (Get Currency)** — the header must be present, but its value is ignored for the currency's
  own `locales` — every translation the currency has comes back, always. It **is** used, however, to pick
  the single locale shown for the embedded parent `country`.
- **`GET` (List/Search Currencies)** — the header's value selects exactly one locale translation for both
  the currency itself and its embedded `country`: an exact match if one exists, otherwise `en`, otherwise
  no translation at all.
- **`POST`/`PUT`/`DELETE`** — the header must be present but its value has no effect at all.

---

## Endpoints

| Method | Path                                            | Description              |
|--------|-------------------------------------------------|--------------------------|
| POST   | `/api/v1/currencies`                            | Create a currency        |
| GET    | `/api/v1/currencies`                            | List / search currencies |
| GET    | `/api/v1/currencies/{id}`                       | Get a currency           |
| PUT    | `/api/v1/currencies/{id}`                       | Update a currency        |
| DELETE | `/api/v1/currencies/{id}`                       | Delete a currency        |
| POST   | `/api/v1/currencies/{currency-id}/locales`      | Create a currency locale |
| PUT    | `/api/v1/currencies/{currency-id}/locales/{id}` | Update a currency locale |
| DELETE | `/api/v1/currencies/{currency-id}/locales/{id}` | Delete a currency locale |

---

## Data Model

### Currency

| Field            | Type    | Required | Constraints                                                                                   | Description                                                                                                                                   |
|------------------|---------|----------|-----------------------------------------------------------------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------|
| `id`             | Long    | —        | read-only                                                                                     | Auto-generated identifier                                                                                                                     |
| `country`        | Country | —        | read-only; embedded parent summary, its own `cities`/`currencies` fields are always `[]`      | The parent country this currency belongs to                                                                                                   |
| `code`           | String  | Yes      | max 3 chars, must match `^[A-Z]{3}$`, unique among active records; set at creation, immutable | ISO 4217 alphabetic code (e.g., `BDT`)                                                                                                        |
| `numeric_code`   | String  | Yes      | max 3 chars, must match `^[0-9]{3}$`, unique among active records; set at creation, immutable | ISO 4217 numeric code (e.g., `050`)                                                                                                           |
| `symbol`         | String  | Yes      | max 10 chars                                                                                  | Currency symbol (e.g., `৳`)                                                                                                                   |
| `decimal_places` | Integer | Yes      | default 2                                                                                     | Number of decimal places (e.g., 2 for USD, 0 for JPY)                                                                                         |
| `is_default`     | Boolean | Yes      | default false                                                                                 | Whether this is the platform default currency                                                                                                 |
| `sort_order`     | Integer | Yes      | default 0                                                                                     | Display order                                                                                                                                 |
| `locales`        | Array   | —        | see CurrencyLocale below                                                                      | Locale-specific translations — **every** translation on `GET /{id}`, exactly **one** (Accept-Language-matched, `en` fallback) everywhere else |

### CurrencyLocale

| Field        | Type    | Required | Constraints                                      | Description                                                                    |
|--------------|---------|----------|--------------------------------------------------|--------------------------------------------------------------------------------|
| `id`         | Long    | —        | read-only                                        | Auto-generated identifier                                                      |
| `locale`     | Locale  | —        | read-only, resolved from `locale_id` at creation | The locale this translation is written in (`id`, `code`, `name`, `sort_order`) |
| `name`       | String  | Yes      | max 200 chars                                    | Localized currency name (e.g., `Bangladeshi Taka`)                             |
| `short_name` | String  | No       | max 100 chars, optional                          | Optional localized short name (e.g., `Taka`)                                   |
| `sort_order` | Integer | Yes      | default 0                                        | Display order among locale entries                                             |

---

## Create Currency

`POST /api/v1/currencies`

Creates a new currency under an existing country, together with exactly **one** initial locale
translation. `country_id` must reference an existing, active country — an unknown `country_id` returns
`404 ENTITY_NOT_FOUND`. Both `code` and `numeric_code` must be unique among active, non-deleted
currencies — attempting to reuse either returns `409 CONFLICT`.

**The initial translation is always attached to the `en` locale, resolved by the server — the request
carries no `locale_id` at all.** There is no option to submit multiple locales at creation time.
Additional languages are added afterward via the Currency Locales sub-resource below.

### Request Body

```json
{
  "code": "BDT",
  "numeric_code": "050",
  "country_id": 1,
  "symbol": "৳",
  "decimal_places": 2,
  "is_default": true,
  "sort_order": 1,
  "locale": {
    "name": "Bangladeshi Taka",
    "short_name": "Taka",
    "sort_order": 1
  }
}
```

### Request Fields

| Field            | Type    | Required | Validation                                                                                 |
|------------------|---------|----------|--------------------------------------------------------------------------------------------|
| `code`           | String  | Yes      | Not blank, max 3 chars, must match `^[A-Z]{3}$`, unique among active records               |
| `numeric_code`   | String  | Yes      | Not blank, max 3 chars, must match `^[0-9]{3}$`, unique among active records               |
| `country_id`     | Long    | Yes      | Not null; must reference an existing, active country                                       |
| `symbol`         | String  | Yes      | Not blank, max 10 chars                                                                    |
| `decimal_places` | Integer | Yes      | Not null                                                                                   |
| `is_default`     | Boolean | Yes      | Not null                                                                                   |
| `sort_order`     | Integer | Yes      | Not null                                                                                   |
| `locale`         | Object  | Yes      | Not null; validated (see below) — no `locale_id` field; always resolved to the `en` locale |

**Locale entry (`locale`):**

| Field        | Type    | Required | Validation               |
|--------------|---------|----------|--------------------------|
| `name`       | String  | Yes      | Not blank, max 200 chars |
| `short_name` | String  | No       | Max 100 chars            |
| `sort_order` | Integer | Yes      | Not null                 |

### Response `201 Created`

```json
{
  "success": true,
  "id": 1
}
```

---

## Get Currency

`GET /api/v1/currencies/{id}`

Returns a single active currency by its ID, including **every** locale translation associated with it (the
`Accept-Language` value has no effect on the currency's own `locales` — it's used only to pick the single
locale shown for the embedded parent country), plus a summary of its parent country.

### Path Parameters

| Parameter | Type | Description        |
|-----------|------|--------------------|
| `id`      | Long | ID of the currency |

### Response `200 OK`

```json
{
  "data": {
    "id": 1,
    "country": {
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
      },
      {
        "id": 2,
        "locale": {
          "id": 2,
          "code": "bn",
          "name": "Bengali",
          "sort_order": 2
        },
        "name": "বাংলাদেশী টাকা",
        "short_name": "টাকা",
        "sort_order": 2
      }
    ]
  }
}
```

> **Note:** the embedded `country` shows exactly **one** locale entry (Accept-Language-matched, `en`
> fallback) and its own `cities`/`currencies` are always `[]`, even though the currency itself (above)
> returns **every** translation it has. The country's own `GET /{id}` (via the Countries API) is what
> returns every one of *its* translations — the embedding here is intentionally minimal.

---

## List / Search Currencies

`GET /api/v1/currencies`

Returns a paginated, filterable list of active (non-deleted) currencies. All filter parameters are optional;
omitting them returns all currencies. Multiple filters are combined with AND. The `code`/`numericCode` filters
perform a case-insensitive partial match; `countryId` and `isDefault` perform an exact match.
`Accept-Language` selects which single locale translation is included per currency — and per its embedded
country — falling back to `en`, then to no translation.

> **Note:** `id` is not a selectable `sortBy` value — passing `?sortBy=id` throws
> `400 INVALID_ARGUMENT: Invalid sort field: id`. It's used only as the implicit sort when `sortBy` is
> omitted entirely.

### Query Parameters

> **Note:** Query parameters bind directly onto `CurrencyFilterRequest`'s Java field names, so they are
> **camelCase** — not the snake_case used in JSON request/response bodies.

| Parameter     | Type    | Default         | Constraints                                                                   | Description                                                                                     |
|---------------|---------|-----------------|-------------------------------------------------------------------------------|-------------------------------------------------------------------------------------------------|
| `code`        | String  | —               | —                                                                             | Filter by code (partial, case-insensitive)                                                      |
| `numericCode` | String  | —               | —                                                                             | Filter by numeric code (partial, case-insensitive)                                              |
| `countryId`   | Long    | —               | —                                                                             | Filter by parent country ID (exact match)                                                       |
| `isDefault`   | Boolean | —               | —                                                                             | Filter by default-currency flag (exact match)                                                   |
| `name`        | String  | —               | —                                                                             | Filter by locale-specific name (partial, case-insensitive), scoped to the resolved locale       |
| `shortName`   | String  | —               | —                                                                             | Filter by locale-specific short name (partial, case-insensitive), scoped to the resolved locale |
| `page`        | int     | `0`             | >= 0                                                                          | Zero-based page index                                                                           |
| `size`        | int     | `10`            | 1 – 50                                                                        | Number of items per page                                                                        |
| `sortBy`      | String  | `id` (implicit) | `createdAt`, `code`, `numericCode`, `name`, `shortName` (`id` NOT selectable) | Field to sort by                                                                                |
| `sortDir`     | String  | `ASC`           | `ASC`, `DESC`                                                                 | Sort direction                                                                                  |

### Response `200 OK`

```json
{
  "data": [
    {
      "id": 1,
      "country": {
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
  ],
  "current_page": 0,
  "total_pages": 1,
  "total_elements": 1,
  "page_size": 10,
  "has_next": false,
  "has_previous": false,
  "sortable_fields": [
    "createdAt",
    "code",
    "numericCode",
    "name",
    "shortName"
  ],
  "searchable_fields": [
    "code",
    "numericCode",
    "name",
    "shortName"
  ]
}
```

---

## Update Currency

`PUT /api/v1/currencies/{id}`

Updates `symbol`, `decimal_places`, `is_default`, and `sort_order`. `code`, `numeric_code`, and `country_id`
are set at creation and cannot be changed. Locale translations are managed separately via the Currency
Locales sub-resource endpoints below, not through this endpoint.

### Path Parameters

| Parameter | Type | Description        |
|-----------|------|--------------------|
| `id`      | Long | ID of the currency |

### Request Body

```json
{
  "symbol": "৳",
  "decimal_places": 2,
  "is_default": true,
  "sort_order": 2
}
```

### Request Fields

| Field            | Type    | Required | Validation              |
|------------------|---------|----------|-------------------------|
| `symbol`         | String  | Yes      | Not blank, max 10 chars |
| `decimal_places` | Integer | Yes      | Not null                |
| `is_default`     | Boolean | Yes      | Not null                |
| `sort_order`     | Integer | Yes      | Not null                |

### Response `200 OK`

```json
{
  "success": true,
  "id": 1
}
```

---

## Delete Currency

`DELETE /api/v1/currencies/{id}`

Soft-deletes the currency. The record is not removed from the database but will no longer appear in any
response.

### Path Parameters

| Parameter | Type | Description        |
|-----------|------|--------------------|
| `id`      | Long | ID of the currency |

### Response `200 OK`

```json
{
  "success": true,
  "id": 1
}
```

---

## Currency Locales

Currency Locale endpoints manage locale-specific name/short name translations for a currency. The
`{currency-id}` path parameter must reference an existing, active currency.

---

### Create Currency Locale

`POST /api/v1/currencies/{currency-id}/locales`

Adds a new locale translation to an existing currency. `locale_id` must reference an existing, active
locale — an unknown `locale_id` returns `404 ENTITY_NOT_FOUND`. The combination of currency and locale must
be unique — adding a locale the currency already has a translation for returns `409 CONFLICT`, pre-checked
at the application level before any write (backed by a DB-level unique constraint as a last-resort guard).

#### Path Parameters

| Parameter     | Type | Description               |
|---------------|------|---------------------------|
| `currency-id` | Long | ID of the parent currency |

#### Request Body

```json
{
  "locale_id": 2,
  "name": "বাংলাদেশী টাকা",
  "short_name": "টাকা",
  "sort_order": 2
}
```

#### Request Fields

| Field        | Type    | Required | Validation                                  |
|--------------|---------|----------|---------------------------------------------|
| `locale_id`  | Long    | Yes      | Not null; must reference an existing locale |
| `name`       | String  | Yes      | Not blank, max 200 chars                    |
| `short_name` | String  | No       | Max 100 chars                               |
| `sort_order` | Integer | Yes      | Not null                                    |

#### Response `201 Created`

```json
{
  "success": true,
  "id": 2
}
```

---

### Update Currency Locale

`PUT /api/v1/currencies/{currency-id}/locales/{id}`

Updates `name`, `short_name`, and `sort_order` for an existing currency locale translation. The associated
currency and locale cannot be changed after creation.

#### Path Parameters

| Parameter     | Type | Description               |
|---------------|------|---------------------------|
| `currency-id` | Long | ID of the parent currency |
| `id`          | Long | ID of the currency locale |

#### Request Body

```json
{
  "name": "বাংলাদেশী টাকা",
  "short_name": "টাকা",
  "sort_order": 2
}
```

#### Request Fields

| Field        | Type    | Required | Validation               |
|--------------|---------|----------|--------------------------|
| `name`       | String  | Yes      | Not blank, max 200 chars |
| `short_name` | String  | No       | Max 100 chars            |
| `sort_order` | Integer | Yes      | Not null                 |

#### Response `200 OK`

```json
{
  "success": true,
  "id": 2
}
```

---

### Delete Currency Locale

`DELETE /api/v1/currencies/{currency-id}/locales/{id}`

Soft-deletes a currency locale. The record is not removed from the database but will no longer appear in any
response.

#### Path Parameters

| Parameter     | Type | Description               |
|---------------|------|---------------------------|
| `currency-id` | Long | ID of the parent currency |
| `id`          | Long | ID of the currency locale |

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
  "message": "Currency not found with id: 99"
}
```

| HTTP Status | Error Code         | Cause                                                                                                                                                                                                                              |
|-------------|--------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| 400         | `INVALID_ARGUMENT` | Missing or blank `Accept-Language` header (checked globally, before any endpoint runs — see the intro above); missing/invalid required fields; or an unsupported `sortBy` query value                                              |
| 404         | `ENTITY_NOT_FOUND` | Currency not found, currency locale not found, country referenced by `country_id` not found (currency creation), or the locale referenced by `locale_id` not found (locale creation)                                               |
| 409         | `CONFLICT`         | `code` or `numeric_code` already in use by another active currency (checked explicitly in currency `create`), or the currency already has a translation for the given `locale_id` (checked explicitly in currency locale `create`) |
