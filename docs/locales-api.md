# Locales API

Base URL: `/api/v1/locales`

Locales represent language and region identifiers used across the platform. Each locale has a unique
code (e.g., `en`, `bn`) and a display name. Locales are referenced by country, city, currency, unit,
and unit type entities to provide their locale-specific translations. All records support soft-delete —
deleted records are hidden from all responses.

---

## Endpoints

| Method | Path                   | Description           |
|--------|------------------------|-----------------------|
| POST   | `/api/v1/locales`      | Create a locale       |
| GET    | `/api/v1/locales`      | List / search locales |
| GET    | `/api/v1/locales/{id}` | Get a locale          |
| PUT    | `/api/v1/locales/{id}` | Update a locale       |
| DELETE | `/api/v1/locales/{id}` | Delete a locale       |

---

## Data Model

### Locale

| Field        | Type    | Required | Constraints          | Description                                                |
|--------------|---------|----------|----------------------|------------------------------------------------------------|
| `id`         | Long    | —        | read-only            | Auto-generated identifier                                  |
| `code`       | String  | Yes      | max 50 chars, unique | Locale code (e.g., `en`, `bn`); set at creation, immutable |
| `name`       | String  | Yes      | max 255 chars        | Display name of the locale (e.g., `English`)               |
| `sort_order` | Integer | Yes      | not null             | Display order                                              |

---

## Create Locale

`POST /api/v1/locales`

Creates a new locale. The `code` field is set at creation and cannot be changed after that. `code`
must be unique among active, non-deleted locales — attempting to reuse an existing code returns a
`409 CONFLICT`.

### Request Body

```json
{
  "code": "en",
  "name": "English",
  "sort_order": 1
}
```

### Request Fields

| Field        | Type    | Required | Validation               |
|--------------|---------|----------|--------------------------|
| `code`       | String  | Yes      | Not blank, max 50 chars  |
| `name`       | String  | Yes      | Not blank, max 255 chars |
| `sort_order` | Integer | Yes      | Not null                 |

### Response `201 Created`

```json
{
  "success": true,
  "id": 1
}
```

---

## Get Locale

`GET /api/v1/locales/{id}`

Returns a single active locale by its ID.

### Path Parameters

| Parameter | Type | Description      |
|-----------|------|------------------|
| `id`      | Long | ID of the locale |

### Response `200 OK`

```json
{
  "data": {
    "id": 1,
    "code": "en",
    "name": "English",
    "sort_order": 1
  }
}
```

---

## List / Search Locales

`GET /api/v1/locales`

Returns a paginated, filterable list of active (non-deleted) locales. All filter parameters are
optional; omitting them returns all locales. Multiple filters are combined with AND. Each filter
performs a case-insensitive partial match.

### Query Parameters

> **Note:** Query parameters are **camelCase** (Java field names via Spring's plain `DataBinder`), not
> the snake_case used in JSON bodies (which goes through Jackson's `@JsonNaming` instead).

| Parameter | Type   | Default | Constraints                                    | Description                                        |
|-----------|--------|---------|-------------------------------------------------|-----------------------------------------------------|
| `code`    | String | —       | —                                               | Filter by locale code (partial, case-insensitive)  |
| `name`    | String | —       | —                                               | Filter by display name (partial, case-insensitive) |
| `page`    | int    | `0`     | >= 0                                            | Zero-based page index                              |
| `size`    | int    | `10`    | 1 – 50                                          | Number of items per page                           |
| `sortBy`  | String | `id`    | `id`, `createdAt`, `sortOrder`, `code`, `name`  | Field to sort by                                   |
| `sortDir` | String | `ASC`   | `ASC`, `DESC`                                   | Sort direction                                     |

### Response `200 OK`

```json
{
  "data": [
    {
      "id": 1,
      "code": "en",
      "name": "English",
      "sort_order": 1
    },
    {
      "id": 2,
      "code": "bn",
      "name": "Bengali",
      "sort_order": 2
    }
  ],
  "current_page": 0,
  "total_pages": 1,
  "total_elements": 2,
  "page_size": 10,
  "has_next": false,
  "has_previous": false,
  "sortable_fields": [
    "id",
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

## Update Locale

`PUT /api/v1/locales/{id}`

Updates `name` and `sort_order`. The `code` field is set at creation and cannot be changed.

### Path Parameters

| Parameter | Type | Description      |
|-----------|------|------------------|
| `id`      | Long | ID of the locale |

### Request Body

```json
{
  "name": "English (US)",
  "sort_order": 1
}
```

### Request Fields

| Field        | Type    | Required | Validation               |
|--------------|---------|----------|--------------------------|
| `name`       | String  | Yes      | Not blank, max 255 chars |
| `sort_order` | Integer | Yes      | Not null                 |

### Response `200 OK`

```json
{
  "success": true,
  "id": 1
}
```

---

## Delete Locale

`DELETE /api/v1/locales/{id}`

Soft-deletes the locale. The record is not removed from the database but will no longer appear in
any response.

### Path Parameters

| Parameter | Type | Description      |
|-----------|------|------------------|
| `id`      | Long | ID of the locale |

### Response `200 OK`

```json
{
  "success": true,
  "id": 1
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
  "message": "Locale not found with id: 99"
}
```

| HTTP Status | Error Code         | Cause                                                                           |
|-------------|--------------------|---------------------------------------------------------------------------------|
| 400         | `INVALID_ARGUMENT` | Missing required fields, or an unsupported `sortBy` query value                  |
| 404         | `ENTITY_NOT_FOUND` | Locale not found, or already deleted                                            |
| 409         | `CONFLICT`         | `code` already in use by another active locale (checked explicitly in `create`) |
