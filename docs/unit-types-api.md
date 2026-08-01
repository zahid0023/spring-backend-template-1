# Unit Types API

Base URL: `/api/v1/unit-types`

Unit types group related units of measure (e.g., "Length", "Weight"), each identified by a unique `code`.
A unit type's display name and description are locale-specific and are managed through a companion
sub-resource — Unit Type Locales — reached via `/api/v1/unit-types/{unit-type-id}/locales`. Unit types
also track their associated units, embedded as the `units` field on the response — each entry omits its
own `unit_type` field (to avoid infinite recursion; a unit belongs to exactly one unit type, and this API
does not currently publish a separate Units document). All records support soft-delete — deleted records
are hidden from all responses.

**`Accept-Language` is required on every endpoint below, with no exceptions** — a request missing (or with
a blank) `Accept-Language` header is rejected with `400 INVALID_ARGUMENT` before it reaches any endpoint
(see [Error Responses](#error-responses)). What differs per endpoint is whether the header's *value* is
actually used to shape the response:

- **`GET /{id}` (Get Unit Type)** — the header must be present, but its value is ignored for the unit
  type's own `locales` — every translation it has comes back, always. It **is** used, however, to pick the
  single locale shown for each embedded unit.
- **`GET` (List/Search Unit Types)**, and every embedded unit anywhere in this API — the header's value
  selects exactly one locale translation: an exact match if the unit type/unit has one, otherwise `en`,
  otherwise no translation at all.
- **`POST`/`PUT`/`DELETE`** — the header must be present but its value has no effect at all.

---

## Endpoints

| Method | Path                                            | Description                |
|--------|--------------------------------------------------|-----------------------------|
| POST   | `/api/v1/unit-types`                              | Create a unit type          |
| GET    | `/api/v1/unit-types`                              | List / search unit types    |
| GET    | `/api/v1/unit-types/{id}`                         | Get a unit type             |
| PUT    | `/api/v1/unit-types/{id}`                         | Update a unit type          |
| DELETE | `/api/v1/unit-types/{id}`                         | Delete a unit type          |
| POST   | `/api/v1/unit-types/{unit-type-id}/locales`       | Create a unit type locale   |
| PUT    | `/api/v1/unit-types/{unit-type-id}/locales/{id}`  | Update a unit type locale   |
| DELETE | `/api/v1/unit-types/{unit-type-id}/locales/{id}`  | Delete a unit type locale   |

---

## Data Model

### UnitType

| Field        | Type    | Required | Constraints                                                           | Description                                                              |
|--------------|---------|----------|-----------------------------------------------------------------------|---------------------------------------------------------------------------|
| `id`         | Long    | —        | read-only                                                             | Auto-generated identifier                                                |
| `code`       | String  | Yes      | max 50 chars, unique among active records; set at creation, immutable | Short unit type code (e.g., `LENGTH`)                                    |
| `sort_order` | Integer | Yes      | default 0                                                             | Display order                                                            |
| `locales`    | Array   | —        | see UnitTypeLocale below                                              | Locale-specific translations — **every** translation on `GET /{id}`, exactly **one** (Accept-Language-matched, `en` fallback) everywhere else |
| `units`      | Array   | —        | read-only; `[]` except on `GET /{id}`; each entry omits its own `unit_type` field | Units belonging to this unit type |

### UnitTypeLocale

| Field         | Type    | Required | Constraints                                      | Description                                                                    |
|---------------|---------|----------|--------------------------------------------------|--------------------------------------------------------------------------------|
| `id`          | Long    | —        | read-only                                        | Auto-generated identifier                                                      |
| `locale`      | Locale  | —        | read-only, resolved from `locale_id` at creation | The locale this translation is written in (`id`, `code`, `name`, `sort_order`) |
| `name`        | String  | Yes      | max 100 chars                                    | Localized unit type name                                                       |
| `description` | String  | Yes      | not null (defaults to `""`)                      | Localized description                                                          |
| `sort_order`  | Integer | Yes      | default 0                                        | Display order among locale entries                                             |

---

## Create Unit Type

`POST /api/v1/unit-types`

Creates a new unit type together with exactly **one** initial locale translation. `code` must be unique
among active, non-deleted unit types — attempting to reuse an existing code returns `409 CONFLICT`.

**The initial translation is always attached to the `en` locale, resolved by the server — the request
carries no `locale_id` at all.** There is no option to submit multiple locales at creation time.
Additional languages are added afterward via the Unit Type Locales sub-resource below.

### Request Body

```json
{
  "code": "LENGTH",
  "sort_order": 1,
  "locale": {
    "name": "Length",
    "description": "Units measuring distance or length",
    "sort_order": 1
  }
}
```

### Request Fields

| Field        | Type    | Required | Validation                                           |
|--------------|---------|----------|--------------------------------------------------------|
| `code`       | String  | Yes      | Not blank, max 50 chars, unique among active records |
| `sort_order` | Integer | Yes      | Not null                                             |
| `locale`     | Object  | Yes      | Not null; validated (see below) — no `locale_id` field; always resolved to the `en` locale |

**Locale entry (`locale`):**

| Field         | Type    | Required | Validation                |
|---------------|---------|----------|----------------------------|
| `name`        | String  | Yes      | Not blank, max 100 chars |
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

## Get Unit Type

`GET /api/v1/unit-types/{id}`

Returns a single active unit type by its ID, including **every** locale translation associated with it
(the `Accept-Language` value has no effect on the unit type's own `locales` — it's used only to pick the
single locale shown for each embedded unit). See List/Search below for the locale-scoped equivalent of the
unit type's own translations.

### Path Parameters

| Parameter | Type | Description          |
|-----------|------|------------------------|
| `id`      | Long | ID of the unit type   |

### Response `200 OK`

```json
{
  "data": {
    "id": 1,
    "code": "LENGTH",
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
        "name": "Length",
        "description": "Units measuring distance or length",
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
        "name": "দৈর্ঘ্য",
        "description": "",
        "sort_order": 2
      }
    ],
    "units": [
      {
        "id": 1,
        "code": "M",
        "symbol": "m",
        "is_base_unit": true,
        "conversion_factor": 1,
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
            "name": "Meter",
            "plural_name": "Meters",
            "description": "The base SI unit of length",
            "sort_order": 1
          }
        ]
      }
    ]
  }
}
```

Each embedded unit shows exactly **one** locale entry — an exact match for the request's
`Accept-Language`, or `en` if the unit has no translation in that locale — regardless of how many locales
the unit type itself returns above.

---

## List / Search Unit Types

`GET /api/v1/unit-types`

Returns a paginated, filterable list of active (non-deleted) unit types. All filter parameters are
optional; omitting them returns all unit types. Multiple filters are combined with AND. Each `LIKE`-type
filter performs a case-insensitive partial match. `Accept-Language` selects which single locale
translation is included per unit type (falls back to `en`, then to no translation). **List rows always
show `units` as an empty array (`[]`)** — it's only ever populated with entries by `GET /{id}`.

### Query Parameters

> **Note:** Query parameters bind directly onto `UnitTypeFilterRequest`'s Java field names, so they are
> **camelCase** — not the snake_case used in JSON request/response bodies.

| Parameter | Type   | Default | Constraints                        | Description                                                                               |
|-----------|--------|---------|--------------------------------------|--------------------------------------------------------------------------------------------|
| `code`    | String | —       | —                                    | Filter by code (partial, case-insensitive)                                                |
| `name`    | String | —       | —                                    | Filter by locale-specific name (partial, case-insensitive), scoped to the resolved locale |
| `page`    | int    | `0`     | >= 0                                  | Zero-based page index                                                                      |
| `size`    | int    | `10`    | 1 – 50                                | Number of items per page                                                                    |
| `sortBy`  | String | `id`    | `createdAt`, `code`, `name`           | Field to sort by                                                                            |
| `sortDir` | String | `ASC`   | `ASC`, `DESC`                        | Sort direction                                                                              |

### Response `200 OK`

```json
{
  "data": [
    {
      "id": 1,
      "code": "LENGTH",
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
          "name": "Length",
          "description": "Units measuring distance or length",
          "sort_order": 1
        }
      ],
      "units": []
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
    "name"
  ],
  "searchable_fields": [
    "code",
    "name"
  ]
}
```

---

## Update Unit Type

`PUT /api/v1/unit-types/{id}`

Updates `sort_order`. `code` is set at creation and cannot be changed. Locale translations are managed
separately via the Unit Type Locales sub-resource endpoints below, not through this endpoint.

### Path Parameters

| Parameter | Type | Description          |
|-----------|------|------------------------|
| `id`      | Long | ID of the unit type   |

### Request Body

```json
{
  "sort_order": 2
}
```

### Request Fields

| Field        | Type    | Required | Validation |
|--------------|---------|----------|--------------|
| `sort_order` | Integer | Yes      | Not null     |

### Response `200 OK`

```json
{
  "success": true,
  "id": 1
}
```

---

## Delete Unit Type

`DELETE /api/v1/unit-types/{id}`

Soft-deletes the unit type. The record is not removed from the database but will no longer appear in any
response.

### Path Parameters

| Parameter | Type | Description          |
|-----------|------|------------------------|
| `id`      | Long | ID of the unit type   |

### Response `200 OK`

```json
{
  "success": true,
  "id": 1
}
```

---

## Unit Type Locales

Unit Type Locale endpoints manage locale-specific name/description translations for a unit type. The
`{unit-type-id}` path parameter must reference an existing, active unit type.

---

### Create Unit Type Locale

`POST /api/v1/unit-types/{unit-type-id}/locales`

Adds a new locale translation to an existing unit type. `locale_id` must reference an existing, active
locale — an unknown `locale_id` returns `404 ENTITY_NOT_FOUND`. The combination of unit type and locale
must be unique — adding a locale the unit type already has a translation for returns `409 CONFLICT`,
pre-checked at the application level before any write (backed by a DB-level unique constraint as a
last-resort guard).

#### Path Parameters

| Parameter      | Type | Description                 |
|----------------|------|-------------------------------|
| `unit-type-id` | Long | ID of the parent unit type   |

#### Request Body

```json
{
  "locale_id": 2,
  "name": "দৈর্ঘ্য",
  "description": "দূরত্ব পরিমাপের একক",
  "sort_order": 2
}
```

#### Request Fields

| Field         | Type    | Required | Validation                                  |
|---------------|---------|----------|-----------------------------------------------|
| `locale_id`   | Long    | Yes      | Not null; must reference an existing locale |
| `name`        | String  | Yes      | Not blank, max 100 chars                    |
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

### Update Unit Type Locale

`PUT /api/v1/unit-types/{unit-type-id}/locales/{id}`

Updates `name`, `description`, and `sort_order` for an existing unit type locale translation. The
associated unit type and locale cannot be changed after creation.

#### Path Parameters

| Parameter      | Type | Description                 |
|----------------|------|-------------------------------|
| `unit-type-id` | Long | ID of the parent unit type   |
| `id`           | Long | ID of the unit type locale   |

#### Request Body

```json
{
  "name": "দৈর্ঘ্য",
  "description": "দূরত্ব পরিমাপের একক",
  "sort_order": 2
}
```

#### Request Fields

| Field         | Type    | Required | Validation               |
|---------------|---------|----------|--------------------------|
| `name`        | String  | Yes      | Not blank, max 100 chars |
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

### Delete Unit Type Locale

`DELETE /api/v1/unit-types/{unit-type-id}/locales/{id}`

Soft-deletes a unit type locale. The record is not removed from the database but will no longer appear in
any response.

#### Path Parameters

| Parameter      | Type | Description                 |
|----------------|------|-------------------------------|
| `unit-type-id` | Long | ID of the parent unit type   |
| `id`           | Long | ID of the unit type locale   |

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
  "message": "UnitType not found with id: 99"
}
```

| HTTP Status | Error Code                 | Cause                                                                                                                                                        |
|-------------|----------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------|
| 400         | `INVALID_ARGUMENT`         | Missing or blank `Accept-Language` header (checked globally, before any endpoint runs — see the intro above); missing/invalid required fields; or an unsupported `sortBy` query value |
| 404         | `ENTITY_NOT_FOUND`         | Unit type not found, unit type locale not found, or the locale referenced by `locale_id` not found (locale creation)                                        |
| 409         | `CONFLICT`                 | `code` already in use by another active unit type (`create`); or the unit type already has a translation for the given `locale_id` (`create` unit type locale, pre-checked at the application level) |
| 409         | `DATA_INTEGRITY_VIOLATION` | Last-resort DB-level unique constraint on `unit_type_id` + `locale_id`, should not normally be reachable now that the duplicate is pre-checked at the application level |
