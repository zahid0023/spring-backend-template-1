# Cities API

Base URL: `/api/v1/cities`

The Cities API manages city records belonging to countries. Each city has a
machine-readable `code` and locale-specific translations (names and descriptions)
stored as `CityLocale` child records. Locale translations are managed as a
sub-resource under each city. All city and locale records support soft-delete —
deleted records are never physically removed but are excluded from all responses.

---

## Endpoints

| Method | Path                                            | Description                  |
|--------|-------------------------------------------------|------------------------------|
| POST   | `/api/v1/cities`                                | Create a city                |
| GET    | `/api/v1/cities`                                | List / search cities         |
| GET    | `/api/v1/cities/{id}`                           | Get a city                   |
| PUT    | `/api/v1/cities/{id}`                           | Update a city                |
| DELETE | `/api/v1/cities/{id}`                           | Delete a city                |
| POST   | `/api/v1/cities/{city-id}/locales`              | Create a city locale         |
| PUT    | `/api/v1/cities/{city-id}/locales/{id}`         | Update a city locale         |
| DELETE | `/api/v1/cities/{city-id}/locales/{id}`         | Delete a city locale         |

---

## Data Model

### City

| Field        | Type    | Constraints                        | Notes                           |
|--------------|---------|------------------------------------|---------------------------------|
| `id`         | Long    | read-only, auto-generated          | Set by database on create       |
| `country_id` | Long    | required, not null                 | FK → countries                  |
| `code`       | String  | max 50 chars                       | Immutable after create          |
| `sort_order` | Integer | required, not null, default 0      |                                 |

### CityLocale

| Field         | Type    | Constraints              | Notes                              |
|---------------|---------|--------------------------|------------------------------------|
| `id`          | Long    | read-only, auto-generated|                                    |
| `city_id`     | Long    | required, not null       | FK → cities                        |
| `locale_id`   | Long    | required, not null       | FK → locales                       |
| `name`        | String  | required, max 255 chars  |                                    |
| `description` | String  | optional, default ''     | omitted if null                    |
| `sort_order`  | Integer | required, not null, default 0 |                               |

Unique constraint: `(city_id, locale_id)` — a city may have at most one
translation per locale.

---

## Create City

`POST /api/v1/cities`

Creates a new city and its initial locale translations. The `country_id` must
reference an existing active country. Each entry in `locales` must reference a
distinct active locale. All locale translations are cascade-created with the city.

### Request Body

```json
{
  "code": "DHK",
  "country_id": 1,
  "sort_order": 1,
  "locales": [
    {
      "locale_id": 1,
      "name": "Dhaka",
      "description": "Capital city of Bangladesh",
      "sort_order": 1
    },
    {
      "locale_id": 2,
      "name": "ঢাকা",
      "description": "বাংলাদেশের রাজধানী",
      "sort_order": 2
    }
  ]
}
```

### Request Fields

| Field        | Type    | Required | Validation               |
|--------------|---------|----------|--------------------------|
| `code`       | String  | No       | max 50 chars             |
| `country_id` | Long    | Yes      | must reference a country |
| `sort_order` | Integer | Yes      |                          |
| `locales`    | Array   | No       | See locale fields below  |

**Locale fields (`locales[]`):**

| Field         | Type    | Required | Validation         |
|---------------|---------|----------|--------------------|
| `locale_id`   | Long    | Yes      |                    |
| `name`        | String  | Yes      | max 255 chars      |
| `description` | String  | No       |                    |
| `sort_order`  | Integer | Yes      |                    |

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

Returns a single city by its ID, including all active locale translations.

### Path Parameters

| Parameter | Type | Description  |
|-----------|------|--------------|
| `id`      | Long | City ID      |

### Response `200 OK`

```json
{
  "city": {
    "id": 1,
    "country": {
      "id": 1,
      "code": "BD",
      "iso3_code": "BGD",
      "phone_code": "880",
      "sort_order": 1,
      "locales": [],
      "cities": [],
      "currencies": []
    },
    "code": "DHK",
    "sort_order": 1,
    "locales": [
      {
        "id": 1,
        "locale": { "id": 1, "code": "en", "name": "English", "sort_order": 1 },
        "name": "Dhaka",
        "description": "Capital city of Bangladesh",
        "sort_order": 1
      },
      {
        "id": 2,
        "locale": { "id": 2, "code": "bn", "name": "Bengali", "sort_order": 2 },
        "name": "ঢাকা",
        "sort_order": 2
      }
    ]
  }
}
```

---

## List / Search Cities

`GET /api/v1/cities`

Returns a paginated, filterable list of cities. All query parameters are optional.
Filters are combined with AND logic. String filters use partial, case-insensitive
matching. An optional `country_id` query parameter narrows results to cities of a
specific country.

### Query Parameters

| Parameter    | Type   | Default | Constraints                                    | Description                                 |
|--------------|--------|---------|------------------------------------------------|---------------------------------------------|
| `page`       | int    | `0`     | >= 0                                           | Zero-based page index                       |
| `size`       | int    | `10`    | 1 – 50                                         | Items per page                              |
| `sort_by`    | String | `id`    | `id`, `createdAt`, `code`, `sortOrder`, `name` | Field to sort by                            |
| `sort_dir`   | String | `ASC`   | `ASC`, `DESC`                                  | Sort direction                              |
| `code`       | String | —       |                                                | Filter by code (partial, case-insensitive)  |
| `country_id` | Long   | —       |                                                | Filter by country (exact match)             |

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
        "locales": [],
        "cities": [],
        "currencies": []
      },
      "code": "DHK",
      "sort_order": 1,
      "locales": [
        {
          "id": 1,
          "locale": { "id": 1, "code": "en", "name": "English", "sort_order": 1 },
          "name": "Dhaka",
          "description": "Capital city of Bangladesh",
          "sort_order": 1
        }
      ]
    }
  ],
  "current_page": 0,
  "total_pages": 1,
  "total_elements": 2,
  "page_size": 10,
  "has_next": false,
  "has_previous": false,
  "sortable_fields": ["id", "createdAt", "code", "sortOrder", "name"],
  "searchable_fields": ["code"]
}
```

---

## Update City

`PUT /api/v1/cities/{id}`

Updates an existing city's `sort_order`. The `code` and `country_id` fields are
immutable after creation and cannot be changed via this endpoint.

### Path Parameters

| Parameter | Type | Description |
|-----------|------|-------------|
| `id`      | Long | City ID     |

### Request Body

```json
{
  "sort_order": 2
}
```

### Request Fields

| Field        | Type    | Required | Validation |
|--------------|---------|----------|------------|
| `sort_order` | Integer | Yes      |            |

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

Soft-deletes a city. The record is not removed from the database but will no
longer appear in any response.

### Path Parameters

| Parameter | Type | Description |
|-----------|------|-------------|
| `id`      | Long | City ID     |

### Response `200 OK`

```json
{
  "success": true,
  "id": 1
}
```

---

## City Locales

City Locale endpoints manage locale-specific translations for a city. The
`{city-id}` path parameter must reference an existing, active city.

---

### Create City Locale

`POST /api/v1/cities/{city-id}/locales`

Adds a new locale translation to an existing city. The `locale_id` must reference
an active locale. Each city may have at most one translation per locale.

#### Path Parameters

| Parameter | Type | Description         |
|-----------|------|---------------------|
| `city-id` | Long | Parent city ID      |

#### Request Body

```json
{
  "locale_id": 3,
  "name": "داکا",
  "description": "پایتخت بنگلادش",
  "sort_order": 3
}
```

#### Request Fields

| Field         | Type    | Required | Validation       |
|---------------|---------|----------|------------------|
| `locale_id`   | Long    | Yes      |                  |
| `name`        | String  | Yes      | max 255 chars    |
| `description` | String  | No       |                  |
| `sort_order`  | Integer | Yes      |                  |

#### Response `201 Created`

```json
{
  "success": true,
  "id": 5
}
```

---

### Update City Locale

`PUT /api/v1/cities/{city-id}/locales/{id}`

Updates the `name`, `description`, and `sort_order` of an existing city locale
translation. The `locale_id` is immutable.

#### Path Parameters

| Parameter | Type | Description            |
|-----------|------|------------------------|
| `city-id` | Long | Parent city ID         |
| `id`      | Long | City locale ID         |

#### Request Body

```json
{
  "name": "Dhaka City",
  "description": "The vibrant capital of Bangladesh",
  "sort_order": 1
}
```

#### Request Fields

| Field         | Type    | Required | Validation       |
|---------------|---------|----------|------------------|
| `name`        | String  | Yes      | max 255 chars    |
| `description` | String  | No       |                  |
| `sort_order`  | Integer | Yes      |                  |

#### Response `200 OK`

```json
{
  "success": true,
  "id": 1
}
```

---

### Delete City Locale

`DELETE /api/v1/cities/{city-id}/locales/{id}`

Soft-deletes a city locale translation.

#### Path Parameters

| Parameter | Type | Description            |
|-----------|------|------------------------|
| `city-id` | Long | Parent city ID         |
| `id`      | Long | City locale ID         |

#### Response `200 OK`

```json
{
  "success": true,
  "id": 1
}
```

---

## Error Responses

```json
{
  "request_id": "abc-123",
  "status": 404,
  "error": "ENTITY_NOT_FOUND",
  "message": "City not found with id: 99"
}
```

| HTTP Status | Error Code                   | Cause                                                            |
|-------------|------------------------------|------------------------------------------------------------------|
| 400         | `INVALID_ARGUMENT`           | Missing required fields or invalid sort field                    |
| 404         | `ENTITY_NOT_FOUND`           | City, city locale, country, or locale not found or deleted       |
| 409         | `DATA_INTEGRITY_VIOLATION`   | Duplicate (city_id, locale_id) pair                              |
