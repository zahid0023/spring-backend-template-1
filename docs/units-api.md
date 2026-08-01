# Units API

Base URL: `/api/v1/units`

Units represent individual units of measure (e.g., "Meter", "Kilogram") belonging to a unit type, each
identified by a unique `code` and linked to a parent unit type via `unit_type_id`. A unit's display name,
plural name, and description are locale-specific and are managed through a companion sub-resource — Unit
Locales — reached via `/api/v1/units/{unit-id}/locales`. Each unit response embeds a summary of its parent
unit type; that embedded unit type's own `units` field is always `[]` (a deliberately minimal embed that
both breaks the recursion — a unit type's `units` list would otherwise include this same unit again, and
so on infinitely — and avoids pulling in unrelated sibling units the caller didn't ask for). Unit type
responses likewise embed a list of their units, each omitting its own `unit_type` field for the same
reason — see the [Unit Types API](unit-types-api.md). All records support soft-delete — deleted records
are hidden from all responses.

**`Accept-Language` is required on every endpoint below, with no exceptions** — a request missing (or with
a blank) `Accept-Language` header is rejected with `400 INVALID_ARGUMENT` before it reaches any endpoint
(see [Error Responses](#error-responses)). What differs per endpoint is whether the header's *value* is
actually used to shape the response:

- **`GET /{id}` (Get Unit)** — the header must be present, but its value is ignored for the unit's own
  `locales` — every translation the unit has comes back, always. It **is** used, however, to pick the
  single locale shown for the embedded parent `unit_type`.
- **`GET` (List/Search Units)** — the header's value selects exactly one locale translation for both the
  unit itself and its embedded `unit_type`: an exact match if one exists, otherwise `en`, otherwise no
  translation at all.
- **`POST`/`PUT`/`DELETE`** — the header must be present but its value has no effect at all.

---

## Endpoints

| Method | Path                                    | Description          |
|--------|------------------------------------------|-----------------------|
| POST   | `/api/v1/units`                          | Create a unit         |
| GET    | `/api/v1/units`                          | List / search units   |
| GET    | `/api/v1/units/{id}`                     | Get a unit            |
| PUT    | `/api/v1/units/{id}`                     | Update a unit         |
| DELETE | `/api/v1/units/{id}`                     | Delete a unit         |
| POST   | `/api/v1/units/{unit-id}/locales`        | Create a unit locale  |
| PUT    | `/api/v1/units/{unit-id}/locales/{id}`   | Update a unit locale  |
| DELETE | `/api/v1/units/{unit-id}/locales/{id}`   | Delete a unit locale  |

---

## Data Model

### Unit

| Field                | Type     | Required | Constraints                                                          | Description                                                                 |
|----------------------|----------|----------|------------------------------------------------------------------------|------------------------------------------------------------------------------|
| `id`                 | Long     | —        | read-only                                                              | Auto-generated identifier                                                    |
| `unit_type`          | UnitType | —        | read-only; embedded parent summary, its own `units` field is always `[]` | The parent unit type this unit belongs to                                    |
| `code`               | String   | Yes      | max 50 chars, unique among active records; set at creation, immutable | Short unit code (e.g., `M`)                                                   |
| `symbol`             | String   | Yes      | max 20 chars, unique among active records                             | Display symbol (e.g., `m`) — **not** immutable, checked for uniqueness on both create and update |
| `is_base_unit`       | Boolean  | Yes      | —                                                                       | Whether this is the base unit for its unit type's conversions                |
| `conversion_factor`  | Decimal  | Yes      | positive                                                               | Multiplier to convert to/from the unit type's base unit                      |
| `sort_order`         | Integer  | Yes      | default 0; **not settable at creation** — see [Create Unit](#create-unit) | Display order                                                                |
| `locales`            | Array    | —        | see UnitLocale below                                                   | Locale-specific translations — **every** translation on `GET /{id}`, exactly **one** (Accept-Language-matched, `en` fallback) everywhere else |

### UnitLocale

| Field          | Type    | Required | Constraints                                      | Description                                                                    |
|----------------|---------|----------|--------------------------------------------------|--------------------------------------------------------------------------------|
| `id`           | Long    | —        | read-only                                        | Auto-generated identifier                                                      |
| `locale`       | Locale  | —        | read-only, resolved from `locale_id` at creation | The locale this translation is written in (`id`, `code`, `name`, `sort_order`) |
| `name`         | String  | Yes      | max 100 chars                                    | Localized unit name (e.g., `Meter`)                                            |
| `plural_name`  | String  | Yes      | max 100 chars                                    | Localized plural form (e.g., `Meters`)                                         |
| `description`  | String  | Yes      | not null (defaults to `""`)                      | Localized description                                                          |
| `sort_order`   | Integer | Yes      | default 0                                        | Display order among locale entries                                             |

---

## Create Unit

`POST /api/v1/units`

Creates a new unit under an existing unit type, together with exactly **one** initial locale translation.
`unit_type_id` must reference an existing, active unit type — an unknown `unit_type_id` returns
`404 ENTITY_NOT_FOUND`. Both `code` and `symbol` must be unique among active, non-deleted units —
attempting to reuse either returns `409 CONFLICT`.

> **Note:** unlike every other entity in this API, `sort_order` is **not** part of the create request at
> all — a newly created unit always starts at the schema's default (`0`) and can only be changed
> afterward via [Update Unit](#update-unit).

**The initial translation is always attached to the `en` locale, resolved by the server — the request
carries no `locale_id` at all.** There is no option to submit multiple locales at creation time.
Additional languages are added afterward via the Unit Locales sub-resource below.

### Request Body

```json
{
  "code": "M",
  "unit_type_id": 1,
  "symbol": "m",
  "is_base_unit": true,
  "conversion_factor": 1,
  "locale": {
    "name": "Meter",
    "plural_name": "Meters",
    "description": "The base SI unit of length",
    "sort_order": 1
  }
}
```

### Request Fields

| Field                | Type    | Required | Validation                                                |
|----------------------|---------|----------|-------------------------------------------------------------|
| `code`               | String  | Yes      | Not blank, max 50 chars, unique among active records       |
| `unit_type_id`       | Long    | Yes      | Not null; must reference an existing, active unit type     |
| `symbol`             | String  | Yes      | Not blank, max 20 chars, unique among active records        |
| `is_base_unit`       | Boolean | Yes      | Not null                                                     |
| `conversion_factor`  | Decimal | Yes      | Not null, must be positive                                   |
| `locale`             | Object  | Yes      | Not null; validated (see below) — no `locale_id` field; always resolved to the `en` locale |

**Locale entry (`locale`):**

| Field         | Type    | Required | Validation                |
|---------------|---------|----------|----------------------------|
| `name`        | String  | Yes      | Not blank, max 100 chars |
| `plural_name` | String  | Yes      | Not blank, max 100 chars |
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

## Get Unit

`GET /api/v1/units/{id}`

Returns a single active unit by its ID, including **every** locale translation associated with it (the
`Accept-Language` value has no effect on the unit's own `locales` — it's used only to pick the single
locale shown for the embedded parent unit type), plus a summary of its parent unit type.

### Path Parameters

| Parameter | Type | Description    |
|-----------|------|-----------------|
| `id`      | Long | ID of the unit |

### Response `200 OK`

```json
{
  "data": {
    "id": 1,
    "unit_type": {
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
    },
    "code": "M",
    "symbol": "m",
    "is_base_unit": true,
    "conversion_factor": 1,
    "sort_order": 0,
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
      },
      {
        "id": 2,
        "locale": {
          "id": 2,
          "code": "bn",
          "name": "Bengali",
          "sort_order": 2
        },
        "name": "মিটার",
        "plural_name": "মিটার",
        "description": "",
        "sort_order": 2
      }
    ]
  }
}
```

> **Note:** the embedded `unit_type` shows exactly **one** locale entry (Accept-Language-matched, `en`
> fallback) and its own `units` is always `[]`, even though the unit itself (above) returns **every**
> translation it has. The unit type's own `GET /{id}` (via the Unit Types API) is what returns every one
> of *its* translations — the embedding here is intentionally minimal.

---

## List / Search Units

`GET /api/v1/units`

Returns a paginated, filterable list of active (non-deleted) units. All filter parameters are optional;
omitting them returns all units. Multiple filters are combined with AND. The `code` filter performs a
case-insensitive partial match; `unitTypeId` and `isBaseUnit` perform an exact match. `Accept-Language`
selects which single locale translation is included per unit — and per its embedded unit type — falling
back to `en`, then to no translation.

### Query Parameters

> **Note:** Query parameters bind directly onto `UnitFilterRequest`'s Java field names, so they are
> **camelCase** — not the snake_case used in JSON request/response bodies.

| Parameter    | Type    | Default | Constraints                                             | Description                                                                               |
|--------------|---------|---------|------------------------------------------------------------|----------------------------------------------------------------------------------------------|
| `code`       | String  | —       | —                                                        | Filter by code (partial, case-insensitive)                                                   |
| `unitTypeId` | Long    | —       | —                                                        | Filter by parent unit type ID (exact match)                                                  |
| `isBaseUnit` | Boolean | —       | —                                                        | Filter by base-unit flag (exact match)                                                       |
| `name`       | String  | —       | —                                                        | Filter by locale-specific name (partial, case-insensitive), scoped to the resolved locale     |
| `page`       | int     | `0`     | >= 0                                                      | Zero-based page index                                                                         |
| `size`       | int     | `10`    | 1 – 50                                                    | Number of items per page                                                                       |
| `sortBy`     | String  | `id`    | `createdAt`, `code`, `unitTypeEntity.id`, `isBaseUnit`, `name` | Field to sort by                                                                          |
| `sortDir`    | String  | `ASC`   | `ASC`, `DESC`                                             | Sort direction                                                                                |

### Response `200 OK`

```json
{
  "data": [
    {
      "id": 1,
      "unit_type": {
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
      },
      "code": "M",
      "symbol": "m",
      "is_base_unit": true,
      "conversion_factor": 1,
      "sort_order": 0,
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
    "unitTypeEntity.id",
    "isBaseUnit",
    "name"
  ],
  "searchable_fields": [
    "code",
    "name"
  ]
}
```

---

## Update Unit

`PUT /api/v1/units/{id}`

Updates `symbol`, `is_base_unit`, `conversion_factor`, and `sort_order`. `code` and `unit_type_id` are set
at creation and cannot be changed. `symbol` is re-checked for uniqueness only if it's actually being
changed. Locale translations are managed separately via the Unit Locales sub-resource endpoints below, not
through this endpoint.

### Path Parameters

| Parameter | Type | Description    |
|-----------|------|-----------------|
| `id`      | Long | ID of the unit |

### Request Body

```json
{
  "symbol": "m",
  "is_base_unit": true,
  "conversion_factor": 1,
  "sort_order": 1
}
```

### Request Fields

| Field                | Type    | Required | Validation                    |
|----------------------|---------|----------|----------------------------------|
| `symbol`             | String  | Yes      | Not blank, max 20 chars, unique among other active records |
| `is_base_unit`       | Boolean | Yes      | Not null                         |
| `conversion_factor`  | Decimal | Yes      | Not null, must be positive       |
| `sort_order`         | Integer | Yes      | Not null                         |

### Response `200 OK`

```json
{
  "success": true,
  "id": 1
}
```

---

## Delete Unit

`DELETE /api/v1/units/{id}`

Soft-deletes the unit. The record is not removed from the database but will no longer appear in any
response.

### Path Parameters

| Parameter | Type | Description    |
|-----------|------|-----------------|
| `id`      | Long | ID of the unit |

### Response `200 OK`

```json
{
  "success": true,
  "id": 1
}
```

---

## Unit Locales

Unit Locale endpoints manage locale-specific name/plural name/description translations for a unit. The
`{unit-id}` path parameter must reference an existing, active unit.

---

### Create Unit Locale

`POST /api/v1/units/{unit-id}/locales`

Adds a new locale translation to an existing unit. `locale_id` must reference an existing, active locale —
an unknown `locale_id` returns `404 ENTITY_NOT_FOUND`. The combination of unit and locale must be unique —
adding a locale the unit already has a translation for returns `409 CONFLICT`, pre-checked at the
application level before any write (backed by a DB-level unique constraint as a last-resort guard).

#### Path Parameters

| Parameter | Type | Description           |
|-----------|------|-------------------------|
| `unit-id` | Long | ID of the parent unit  |

#### Request Body

```json
{
  "locale_id": 2,
  "name": "মিটার",
  "plural_name": "মিটার",
  "description": "দৈর্ঘ্যের মূল একক",
  "sort_order": 2
}
```

#### Request Fields

| Field         | Type    | Required | Validation                                  |
|---------------|---------|----------|-----------------------------------------------|
| `locale_id`   | Long    | Yes      | Not null; must reference an existing locale |
| `name`        | String  | Yes      | Not blank, max 100 chars                    |
| `plural_name` | String  | Yes      | Not blank, max 100 chars                    |
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

### Update Unit Locale

`PUT /api/v1/units/{unit-id}/locales/{id}`

Updates `name`, `plural_name`, `description`, and `sort_order` for an existing unit locale translation.
The associated unit and locale cannot be changed after creation.

#### Path Parameters

| Parameter | Type | Description           |
|-----------|------|-------------------------|
| `unit-id` | Long | ID of the parent unit  |
| `id`      | Long | ID of the unit locale  |

#### Request Body

```json
{
  "name": "মিটার",
  "plural_name": "মিটার",
  "description": "দৈর্ঘ্যের মূল একক",
  "sort_order": 2
}
```

#### Request Fields

| Field         | Type    | Required | Validation               |
|---------------|---------|----------|----------------------------|
| `name`        | String  | Yes      | Not blank, max 100 chars |
| `plural_name` | String  | Yes      | Not blank, max 100 chars |
| `description` | String  | Yes      | Not null                  |
| `sort_order`  | Integer | Yes      | Not null                  |

#### Response `200 OK`

```json
{
  "success": true,
  "id": 2
}
```

---

### Delete Unit Locale

`DELETE /api/v1/units/{unit-id}/locales/{id}`

Soft-deletes a unit locale. The record is not removed from the database but will no longer appear in any
response.

#### Path Parameters

| Parameter | Type | Description           |
|-----------|------|-------------------------|
| `unit-id` | Long | ID of the parent unit  |
| `id`      | Long | ID of the unit locale  |

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
  "message": "Unit not found with id: 99"
}
```

| HTTP Status | Error Code                 | Cause                                                                                                                                                        |
|-------------|----------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------|
| 400         | `INVALID_ARGUMENT`         | Missing or blank `Accept-Language` header (checked globally, before any endpoint runs — see the intro above); missing/invalid required fields; or an unsupported `sortBy` query value |
| 404         | `ENTITY_NOT_FOUND`         | Unit not found, unit locale not found, unit type referenced by `unit_type_id` not found (unit creation), or the locale referenced by `locale_id` not found (locale creation) |
| 409         | `CONFLICT`                 | `code` or `symbol` already in use by another active unit (checked explicitly in `create`/`update`); or the unit already has a translation for the given `locale_id` (`create` unit locale, pre-checked at the application level) |
| 409         | `DATA_INTEGRITY_VIOLATION` | Last-resort DB-level unique constraint on `unit_id` + `locale_id`, should not normally be reachable now that the duplicate is pre-checked at the application level |
