# Cities API

Base URL: `/api/v1/cities`

Cities represent settlements belonging to a country, each identified by a unique `code` (e.g., `DHK`) and
linked to a parent country via `country_id`. A city's display name and description are locale-specific and
are managed through a companion sub-resource — City Locales — reached via `/api/v1/cities/{city-id}/locales`.
Each city response embeds a summary of its parent country; that embedded country's own `cities` and
`currencies` fields are always `[]` (a deliberately minimal embed that both breaks the recursion — a
country's `cities` list would otherwise include this same city again, and so on infinitely — and avoids
pulling in unrelated sibling collections the caller didn't ask for). Country responses likewise embed a
list of their cities, each omitting its own `country` field for the same reason — see the
[Countries API](countries-api.md). All records support soft-delete — deleted records are hidden from all
responses.

**`Accept-Language` is required on every endpoint below, with no exceptions** — a request missing (or with
a blank) `Accept-Language` header is rejected with `400 INVALID_ARGUMENT` before it reaches any endpoint
(see [Error Responses](#error-responses)). What differs per endpoint is whether the header's *value* is
actually used to shape the response:

- **`GET /{id}` (Get City)** — the header must be present, but its value is ignored for the city's own
  `locales` — every translation the city has comes back, always. It **is** used, however, to pick the
  single locale shown for the embedded parent `country`.
- **`GET` (List/Search Cities)** — the header's value selects exactly one locale translation for both the
  city itself and its embedded `country`: an exact match if one exists, otherwise `en`, otherwise no
  translation at all.
- **`POST`/`PUT`/`DELETE`** — the header must be present but its value has no effect at all.

---

## Endpoints

| Method | Path                                    | Description          |
|--------|-----------------------------------------|----------------------|
| POST   | `/api/v1/cities`                        | Create a city        |
| GET    | `/api/v1/cities`                        | List / search cities |
| GET    | `/api/v1/cities/{id}`                   | Get a city           |
| PUT    | `/api/v1/cities/{id}`                   | Update a city        |
| DELETE | `/api/v1/cities/{id}`                   | Delete a city        |
| POST   | `/api/v1/cities/{city-id}/locales`      | Create a city locale |
| PUT    | `/api/v1/cities/{city-id}/locales/{id}` | Update a city locale |
| DELETE | `/api/v1/cities/{city-id}/locales/{id}` | Delete a city locale |

---

## Data Model

### City

| Field        | Type    | Required | Constraints                                                                                   | Description                                                                                                                                   |
|--------------|---------|----------|-----------------------------------------------------------------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------|
| `id`         | Long    | —        | read-only                                                                                     | Auto-generated identifier                                                                                                                     |
| `country`    | Country | —        | read-only; embedded parent summary, its own `cities`/`currencies` fields are always `[]`      | The parent country this city belongs to                                                                                                       |
| `code`       | String  | Yes      | max 3 chars, must match `^[A-Z]{3}$`, unique among active records; set at creation, immutable | Short city code (e.g., `DHK`)                                                                                                                 |
| `sort_order` | Integer | Yes      | default 0                                                                                     | Display order                                                                                                                                 |
| `locales`    | Array   | —        | see CityLocale below                                                                          | Locale-specific translations — **every** translation on `GET /{id}`, exactly **one** (Accept-Language-matched, `en` fallback) everywhere else |

### CityLocale

| Field         | Type    | Required | Constraints                                      | Description                                                                    |
|---------------|---------|----------|--------------------------------------------------|--------------------------------------------------------------------------------|
| `id`          | Long    | —        | read-only                                        | Auto-generated identifier                                                      |
| `locale`      | Locale  | —        | read-only, resolved from `locale_id` at creation | The locale this translation is written in (`id`, `code`, `name`, `sort_order`) |
| `name`        | String  | Yes      | max 255 chars                                    | Localized city name                                                            |
| `description` | String  | Yes      | not null (defaults to `""`)                      | Localized description                                                          |
| `sort_order`  | Integer | Yes      | default 0                                        | Display order among locale entries                                             |

---

## Create City

`POST /api/v1/cities`

Creates a new city under an existing country, together with exactly **one** initial locale translation.
`country_id` must reference an existing, active country — an unknown `country_id` returns
`404 ENTITY_NOT_FOUND`. `code` must be unique among active, non-deleted cities — attempting to reuse an
existing code returns `409 CONFLICT`.

**The initial translation is always attached to the `en` locale, resolved by the server — the request
carries no `locale_id` at all.** There is no option to submit multiple locales at creation time.
Additional languages are added afterward via the City Locales sub-resource below.

### Request Body

```json
{
  "code": "DHK",
  "country_id": 1,
  "sort_order": 1,
  "locale": {
    "name": "Dhaka",
    "description": "Capital city of Bangladesh",
    "sort_order": 1
  }
}
```

### Request Fields

| Field        | Type    | Required | Validation                                                                                 |
|--------------|---------|----------|--------------------------------------------------------------------------------------------|
| `code`       | String  | Yes      | Not blank, max 3 chars, must match `^[A-Z]{3}$`, unique among active records               |
| `country_id` | Long    | Yes      | Not null; must reference an existing, active country                                       |
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

## Get City

`GET /api/v1/cities/{id}`

Returns a single active city by its ID, including **every** locale translation associated with it (the
`Accept-Language` value has no effect on the city's own `locales` — it's used only to pick the single
locale shown for the embedded parent country), plus a summary of its parent country.

### Path Parameters

| Parameter | Type | Description    |
|-----------|------|----------------|
| `id`      | Long | ID of the city |

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
      },
      {
        "id": 2,
        "locale": {
          "id": 2,
          "code": "bn",
          "name": "Bengali",
          "sort_order": 2
        },
        "name": "ঢাকা",
        "description": "",
        "sort_order": 2
      }
    ]
  }
}
```

> **Note:** the embedded `country` shows exactly **one** locale entry (Accept-Language-matched, `en`
> fallback) and its own `cities`/`currencies` are always `[]`, even though the city itself (above) returns
> **every** translation it has. The country's own `GET /{id}` (via the Countries API) is what returns every
> one of *its* translations — the embedding here is intentionally minimal.

---

## List / Search Cities

`GET /api/v1/cities`

Returns a paginated, filterable list of active (non-deleted) cities. All filter parameters are optional;
omitting them returns all cities. Multiple filters are combined with AND. The `code` filter performs a
case-insensitive partial match; `country_id` performs an exact match. `Accept-Language` selects which
single locale translation is included per city — and per its embedded country — falling back to `en`,
then to no translation.

> **Note:** `id` is not a selectable `sortBy` value — passing `?sortBy=id` throws
> `400 INVALID_ARGUMENT: Invalid sort field: id`. It's used only as the implicit sort when `sortBy` is
> omitted entirely.

### Query Parameters

> **Note:** Query parameters bind directly onto `CityFilterRequest`'s Java field names, so they are
> **camelCase** — not the snake_case used in JSON request/response bodies.

| Parameter   | Type   | Default | Constraints                                    | Description                                                                               |
|-------------|--------|---------|------------------------------------------------|-------------------------------------------------------------------------------------------|
| `code`      | String | —       | —                                              | Filter by code (partial, case-insensitive)                                                |
| `countryId` | Long   | —       | —                                              | Filter by parent country ID (exact match)                                                 |
| `name`      | String | —       | —                                              | Filter by locale-specific name (partial, case-insensitive), scoped to the resolved locale |
| `page`      | int    | `0`     | >= 0                                           | Zero-based page index                                                                     |
| `size`      | int    | `10`    | 1 – 50                                         | Number of items per page                                                                  |
| `sortBy`    | String | `id` (implicit) | `createdAt`, `sortOrder`, `code`, `name` (`id` NOT selectable) | Field to sort by                                                                  |
| `sortDir`   | String | `ASC`   | `ASC`, `DESC`                                  | Sort direction                                                                            |

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
  "current_page": 0,
  "total_pages": 1,
  "total_elements": 1,
  "page_size": 10,
  "has_next": false,
  "has_previous": false,
  "sortable_fields": [
    "createdAt",
    "sortOrder",
    "code",
    "name"
  ],
  "searchable_fields": [
    "code",
    "name"
  ]
}
```

---

## Update City

`PUT /api/v1/cities/{id}`

Updates `sort_order`. `code` and `country_id` are set at creation and cannot be changed. Locale
translations are managed separately via the City Locales sub-resource endpoints below, not through this
endpoint.

### Path Parameters

| Parameter | Type | Description    |
|-----------|------|----------------|
| `id`      | Long | ID of the city |

### Request Body

```json
{
  "sort_order": 2
}
```

### Request Fields

| Field        | Type    | Required | Validation |
|--------------|---------|----------|------------|
| `sort_order` | Integer | Yes      | Not null   |

### Response `200 OK`

```json
{
  "success": true,
  "id": 1
}
```

---

## Delete City

`DELETE /api/v1/cities/{id}`

Soft-deletes the city. The record is not removed from the database but will no longer appear in any
response.

### Path Parameters

| Parameter | Type | Description    |
|-----------|------|----------------|
| `id`      | Long | ID of the city |

### Response `200 OK`

```json
{
  "success": true,
  "id": 1
}
```

---

## City Locales

City Locale endpoints manage locale-specific name/description translations for a city. The `{city-id}`
path parameter must reference an existing, active city.

---

### Create City Locale

`POST /api/v1/cities/{city-id}/locales`

Adds a new locale translation to an existing city. `locale_id` must reference an existing, active locale —
an unknown `locale_id` returns `404 ENTITY_NOT_FOUND`. The combination of city and locale must be unique —
adding a locale the city already has a translation for returns `409 CONFLICT`, pre-checked at the
application level before any write (backed by a DB-level unique constraint as a last-resort guard).

#### Path Parameters

| Parameter | Type | Description           |
|-----------|------|-----------------------|
| `city-id` | Long | ID of the parent city |

#### Request Body

```json
{
  "locale_id": 2,
  "name": "ঢাকা",
  "description": "বাংলাদেশের রাজধানী",
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

### Update City Locale

`PUT /api/v1/cities/{city-id}/locales/{id}`

Updates `name`, `description`, and `sort_order` for an existing city locale translation. The associated
city and locale cannot be changed after creation.

#### Path Parameters

| Parameter | Type | Description           |
|-----------|------|-----------------------|
| `city-id` | Long | ID of the parent city |
| `id`      | Long | ID of the city locale |

#### Request Body

```json
{
  "name": "ঢাকা",
  "description": "বাংলাদেশের রাজধানী",
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

### Delete City Locale

`DELETE /api/v1/cities/{city-id}/locales/{id}`

Soft-deletes a city locale. The record is not removed from the database but will no longer appear in any
response.

#### Path Parameters

| Parameter | Type | Description           |
|-----------|------|-----------------------|
| `city-id` | Long | ID of the parent city |
| `id`      | Long | ID of the city locale |

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
  "message": "City not found with id: 99"
}
```

| HTTP Status | Error Code         | Cause                                                                                                                                                                                            |
|-------------|--------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| 400         | `INVALID_ARGUMENT` | Missing or blank `Accept-Language` header (checked globally, before any endpoint runs — see the intro above); missing/invalid required fields; or an unsupported `sortBy` query value            |
| 404         | `ENTITY_NOT_FOUND` | City not found, city locale not found, country referenced by `country_id` not found (city creation), or the locale referenced by `locale_id` not found (locale creation)                         |
| 409         | `CONFLICT`         | `code` already in use by another active city (checked explicitly in city `create`), or the city already has a translation for the given `locale_id` (checked explicitly in city locale `create`) |
