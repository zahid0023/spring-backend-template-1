# Countries API

Base URL: `/api/v1/countries`

The Country entity represents a sovereign country or territory, identified by a
unique ISO 2-letter code. Locale-specific data (display names and descriptions)
is stored in child `CountryLocale` records — one per supported locale — allowing
full internationalisation. Locales are managed as a sub-resource under
`/api/v1/countries/{country-id}/locales`. Cities associated with a country can
be browsed via the convenience endpoint `GET /api/v1/countries/{country-id}/cities`.
All records support soft-delete: a deleted country is not removed from the database
but will no longer appear in any response.

---

## Endpoints

| Method   | Path                                                          | Description                      |
|----------|---------------------------------------------------------------|----------------------------------|
| `POST`   | `/api/v1/countries`                                           | Create a country                 |
| `GET`    | `/api/v1/countries`                                           | List / search countries          |
| `GET`    | `/api/v1/countries/{id}`                                      | Get a country                    |
| `PUT`    | `/api/v1/countries/{id}`                                      | Update a country                 |
| `DELETE` | `/api/v1/countries/{id}`                                      | Delete a country                 |
| `GET`    | `/api/v1/countries/{country-id}/cities`                       | Get cities by country            |
| `POST`   | `/api/v1/countries/{country-id}/locales`                      | Create a country locale          |
| `PUT`    | `/api/v1/countries/{country-id}/locales/{id}`                 | Update a country locale          |
| `DELETE` | `/api/v1/countries/{country-id}/locales/{id}`                 | Delete a country locale          |

---

## Data Model

### Country

┌────────────┬──────────┬──────────┬──────────────────────────────────────────────┐
│ Field      │ Type     │ Required │ Constraints                                  │
├────────────┼──────────┼──────────┼──────────────────────────────────────────────┤
│ id         │ Long     │ —        │ read-only, auto-generated                    │
│ code       │ String   │ Yes      │ max 10 chars, unique, not null               │
│ iso3_code  │ String   │ Yes      │ max 10 chars, not null                       │
│ phone_code │ String   │ Yes      │ max 10 chars, pattern ^[A-Za-z]{1,3}$        │
│ sort_order │ Integer  │ Yes      │ not null, default 0                          │
│ locales    │ Array    │ —        │ read-only, CountryLocale child records        │
│ cities     │ Array    │ —        │ read-only, City child records                │
│ currencies │ Array    │ —        │ read-only, Currency child records            │
└────────────┴──────────┴──────────┴──────────────────────────────────────────────┘

> `CountryDto` carries `@JsonInclude(NON_NULL)` at the class level — any field
> that resolves to `null` is omitted from the response.

### CountryLocale

┌─────────────┬─────────┬──────────┬────────────────────────────────────────────────────┐
│ Field       │ Type    │ Required │ Constraints                                        │
├─────────────┼─────────┼──────────┼────────────────────────────────────────────────────┤
│ id          │ Long    │ —        │ read-only, auto-generated                          │
│ locale      │ Object  │ —        │ embedded LocaleDto (id, code, name, sort_order)    │
│ name        │ String  │ Yes      │ max 255 chars, not null                            │
│ description │ String  │ Yes      │ text, not null (empty string allowed)              │
│ sort_order  │ Integer │ Yes      │ not null, default 0                                │
└─────────────┴─────────┴──────────┴────────────────────────────────────────────────────┘

---

## Create Country

`POST /api/v1/countries`

Creates a new country along with one or more locale translations in a single
request. Each locale entry must reference an existing, active Locale record via
`locale_id`. The `code` field is immutable after creation.

### Request Body

```json
{
  "code": "BD",
  "iso3_code": "BGD",
  "phone_code": "BD",
  "sort_order": 1,
  "locales": [
    {
      "locale_id": 1,
      "name": "Bangladesh",
      "description": "A country in South Asia.",
      "sort_order": 1
    },
    {
      "locale_id": 2,
      "name": "বাংলাদেশ",
      "description": "দক্ষিণ এশিয়ার একটি দেশ।",
      "sort_order": 2
    }
  ]
}
```

### Request Fields

┌────────────┬─────────┬──────────┬───────────────────────────────────────────────────┐
│ Field      │ Type    │ Required │ Validation                                        │
├────────────┼─────────┼──────────┼───────────────────────────────────────────────────┤
│ code       │ String  │ Yes      │ max 10 chars, not blank                           │
│ iso3_code  │ String  │ Yes      │ max 10 chars, not blank                           │
│ phone_code │ String  │ Yes      │ max 10 chars, not blank, pattern ^[A-Za-z]{1,3}$ │
│ sort_order │ Integer │ Yes      │ not null                                          │
│ locales    │ Array   │ Yes      │ not empty; each entry validated (see below)       │
└────────────┴─────────┴──────────┴───────────────────────────────────────────────────┘

**Locale fields (`locales[]`):**

┌─────────────┬─────────┬──────────┬───────────────────────────────────────────────┐
│ Field       │ Type    │ Required │ Validation                                    │
├─────────────┼─────────┼──────────┼───────────────────────────────────────────────┤
│ locale_id   │ Long    │ Yes      │ not null; must reference an existing locale   │
│ name        │ String  │ Yes      │ max 255 chars, not blank                      │
│ description │ String  │ Yes      │ not null (empty string allowed)               │
│ sort_order  │ Integer │ Yes      │ not null                                      │
└─────────────┴─────────┴──────────┴───────────────────────────────────────────────┘

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

Returns a single country with all its locale translations, associated cities,
and associated currencies. Fields that are `null` are omitted from the response.

### Path Parameters

┌───────────┬──────┬─────────────────────────────┐
│ Parameter │ Type │ Description                 │
├───────────┼──────┼─────────────────────────────┤
│ id        │ Long │ ID of the country to fetch  │
└───────────┴──────┴─────────────────────────────┘

### Response `200 OK`

```json
{
  "country": {
    "id": 1,
    "code": "BD",
    "iso3_code": "BGD",
    "phone_code": "BD",
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
        "description": "A country in South Asia.",
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
        "sort_order": 2
      }
    ],
    "cities": [],
    "currencies": []
  }
}
```

> `description` is omitted from the second locale entry to illustrate that it
> may be absent when not populated.

---

## List / Search Countries

`GET /api/v1/countries`

Returns a paginated, filterable list of countries. All filter parameters are
optional; when multiple filters are supplied they are combined with AND logic.
String filters use partial, case-insensitive matching. The `locale_id` parameter
scopes locale-joined filters (`name`) to the specified locale.

### Query Parameters

┌─────────────┬─────────┬─────────┬───────────────────────────────────────────┬──────────────────────────────────────────────────────┐
│ Parameter   │ Type    │ Default │ Constraints                               │ Description                                          │
├─────────────┼─────────┼─────────┼───────────────────────────────────────────┼──────────────────────────────────────────────────────┤
│ page        │ Integer │ 0       │ >= 0                                      │ Zero-based page index                                │
│ size        │ Integer │ 10      │ 1 – 50                                    │ Items per page                                       │
│ sort_by     │ String  │ id      │ id, createdAt, code, sortOrder, name      │ Field to sort by                                     │
│ sort_dir    │ String  │ ASC     │ ASC, DESC                                 │ Sort direction                                       │
│ code        │ String  │ —       │ optional                                  │ Filter by code (partial, case-insensitive)            │
│ iso3_code   │ String  │ —       │ optional                                  │ Filter by iso3Code (partial, case-insensitive)       │
│ phone_code  │ String  │ —       │ optional                                  │ Filter by phoneCode (partial, case-insensitive)      │
│ name        │ String  │ —       │ optional; requires locale_id for scoping  │ Filter by locale name (partial, case-insensitive)    │
│ locale_id   │ Long    │ —       │ optional                                  │ Scope locale-joined filters to this locale (exact)   │
└─────────────┴─────────┴─────────┴───────────────────────────────────────────┴──────────────────────────────────────────────────────┘

### Response `200 OK`

```json
{
  "data": [
    {
      "id": 1,
      "code": "BD",
      "iso3_code": "BGD",
      "phone_code": "BD",
      "sort_order": 1,
      "locales": [
        {
          "id": 1,
          "locale": { "id": 1, "code": "en", "name": "English", "sort_order": 1 },
          "name": "Bangladesh",
          "description": "A country in South Asia.",
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
      "phone_code": "US",
      "sort_order": 2,
      "locales": [
        {
          "id": 3,
          "locale": { "id": 1, "code": "en", "name": "English", "sort_order": 1 },
          "name": "United States",
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
  "sortable_fields": ["id", "createdAt", "code", "sortOrder", "name"],
  "searchable_fields": ["code", "iso3Code", "phoneCode", "name"]
}
```

> `description` is omitted from the second country's locale to illustrate
> optional-field omission.

---

## Update Country

`PUT /api/v1/countries/{id}`

Updates the mutable fields of a country. The `code` field is immutable and cannot
be changed after creation. Supply only the fields defined in the request body;
`locales` are managed separately via the `/locales` sub-resource.

### Path Parameters

┌───────────┬──────┬───────────────────────────────┐
│ Parameter │ Type │ Description                   │
├───────────┼──────┼───────────────────────────────┤
│ id        │ Long │ ID of the country to update   │
└───────────┴──────┴───────────────────────────────┘

### Request Body

```json
{
  "iso3_code": "BGD",
  "phone_code": "BD",
  "sort_order": 1
}
```

### Request Fields

┌────────────┬─────────┬──────────┬───────────────────────────────────────────────────┐
│ Field      │ Type    │ Required │ Validation                                        │
├────────────┼─────────┼──────────┼───────────────────────────────────────────────────┤
│ iso3_code  │ String  │ Yes      │ max 10 chars, not blank                           │
│ phone_code │ String  │ Yes      │ max 10 chars, not blank, pattern ^[A-Za-z]{1,3}$ │
│ sort_order │ Integer │ Yes      │ not null                                          │
└────────────┴─────────┴──────────┴───────────────────────────────────────────────────┘

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

Soft-deletes the country. The record is not removed from the database but will
no longer appear in any response.

### Path Parameters

┌───────────┬──────┬───────────────────────────────┐
│ Parameter │ Type │ Description                   │
├───────────┼──────┼───────────────────────────────┤
│ id        │ Long │ ID of the country to delete   │
└───────────┴──────┴───────────────────────────────┘

### Response `200 OK`

```json
{
  "success": true,
  "id": 1
}
```

---

## Get Cities by Country

`GET /api/v1/countries/{country-id}/cities`

Returns a paginated, filterable list of cities belonging to the specified country.
All filter parameters are optional. String filters use partial, case-insensitive
matching.

### Path Parameters

┌────────────┬──────┬────────────────────────────────────────────┐
│ Parameter  │ Type │ Description                                │
├────────────┼──────┼────────────────────────────────────────────┤
│ country-id │ Long │ ID of the country whose cities to retrieve │
└────────────┴──────┴────────────────────────────────────────────┘

### Query Parameters

┌──────────┬─────────┬─────────┬───────────────────────────────────────────┬────────────────────────────────────────────────┐
│ Parameter│ Type    │ Default │ Constraints                               │ Description                                    │
├──────────┼─────────┼─────────┼───────────────────────────────────────────┼────────────────────────────────────────────────┤
│ page     │ Integer │ 0       │ >= 0                                      │ Zero-based page index                          │
│ size     │ Integer │ 10      │ 1 – 50                                    │ Items per page                                 │
│ sort_by  │ String  │ id      │ id, createdAt, code, sortOrder, name      │ Field to sort by                               │
│ sort_dir │ String  │ ASC     │ ASC, DESC                                 │ Sort direction                                 │
│ code     │ String  │ —       │ optional                                  │ Filter by city code (partial, case-insensitive)│
└──────────┴─────────┴─────────┴───────────────────────────────────────────┴────────────────────────────────────────────────┘

### Response `200 OK`

```json
{
  "data": [
    {
      "id": 1,
      "code": "DHK",
      "sort_order": 1,
      "locales": [
        {
          "id": 1,
          "locale": { "id": 1, "code": "en", "name": "English", "sort_order": 1 },
          "name": "Dhaka",
          "description": "Capital of Bangladesh.",
          "sort_order": 1
        }
      ]
    },
    {
      "id": 2,
      "code": "CTG",
      "sort_order": 2,
      "locales": [
        {
          "id": 3,
          "locale": { "id": 1, "code": "en", "name": "English", "sort_order": 1 },
          "name": "Chittagong",
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

## Country Locales

CountryLocale endpoints manage locale-specific translations for a country (display
name and description). The `country-id` path parameter must reference an existing,
active country.

---

### Create Country Locale

`POST /api/v1/countries/{country-id}/locales`

Creates a new locale translation for a country. The `locale_id` must reference an
existing, active Locale. Each country–locale combination must be unique.

#### Path Parameters

┌────────────┬──────┬─────────────────────────────────────────────────┐
│ Parameter  │ Type │ Description                                     │
├────────────┼──────┼─────────────────────────────────────────────────┤
│ country-id │ Long │ ID of the country to add a locale entry for     │
└────────────┴──────┴─────────────────────────────────────────────────┘

#### Request Body

```json
{
  "locale_id": 3,
  "name": "বাংলাদেশ",
  "description": "দক্ষিণ এশিয়ার একটি দেশ।",
  "sort_order": 3
}
```

#### Request Fields

┌─────────────┬─────────┬──────────┬───────────────────────────────────────────────┐
│ Field       │ Type    │ Required │ Validation                                    │
├─────────────┼─────────┼──────────┼───────────────────────────────────────────────┤
│ locale_id   │ Long    │ Yes      │ not null; must reference an existing locale   │
│ name        │ String  │ Yes      │ max 255 chars, not blank                      │
│ description │ String  │ Yes      │ not null (empty string allowed)               │
│ sort_order  │ Integer │ Yes      │ not null                                      │
└─────────────┴─────────┴──────────┴───────────────────────────────────────────────┘

#### Response `201 Created`

```json
{
  "success": true,
  "id": 3
}
```

---

### Update Country Locale

`PUT /api/v1/countries/{country-id}/locales/{id}`

Updates the name, description, and sort order of an existing country locale.
The `locale_id` (which locale the entry belongs to) is immutable.

#### Path Parameters

┌────────────┬──────┬────────────────────────────────────────────────────┐
│ Parameter  │ Type │ Description                                        │
├────────────┼──────┼────────────────────────────────────────────────────┤
│ country-id │ Long │ ID of the owning country                           │
│ id         │ Long │ ID of the country locale record to update          │
└────────────┴──────┴────────────────────────────────────────────────────┘

#### Request Body

```json
{
  "name": "Bangladesh",
  "description": "A sovereign country in South Asia.",
  "sort_order": 1
}
```

#### Request Fields

┌─────────────┬─────────┬──────────┬───────────────────────────────────┐
│ Field       │ Type    │ Required │ Validation                        │
├─────────────┼─────────┼──────────┼───────────────────────────────────┤
│ name        │ String  │ Yes      │ max 255 chars, not blank          │
│ description │ String  │ Yes      │ not null (empty string allowed)   │
│ sort_order  │ Integer │ Yes      │ not null                          │
└─────────────┴─────────┴──────────┴───────────────────────────────────┘

#### Response `200 OK`

```json
{
  "success": true,
  "id": 1
}
```

---

### Delete Country Locale

`DELETE /api/v1/countries/{country-id}/locales/{id}`

Soft-deletes a country locale. The record is not removed from the database but
will no longer appear in any response.

#### Path Parameters

┌────────────┬──────┬────────────────────────────────────────────────────┐
│ Parameter  │ Type │ Description                                        │
├────────────┼──────┼────────────────────────────────────────────────────┤
│ country-id │ Long │ ID of the owning country                           │
│ id         │ Long │ ID of the country locale record to delete          │
└────────────┴──────┴────────────────────────────────────────────────────┘

#### Response `200 OK`

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
  "message": "Country not found with id: 99"
}
```

┌─────────────┬──────────────────────────┬─────────────────────────────────────────────────────────────────────────┐
│ HTTP Status │ Error Code               │ Cause                                                                   │
├─────────────┼──────────────────────────┼─────────────────────────────────────────────────────────────────────────┤
│ 400         │ INVALID_ARGUMENT         │ Missing required fields or invalid sort field                           │
│ 404         │ ENTITY_NOT_FOUND         │ Country, country locale, locale, or city not found, or already deleted  │
│ 409         │ DATA_INTEGRITY_VIOLATION │ Constraint violation (e.g. duplicate code or duplicate country–locale)  │
└─────────────┴──────────────────────────┴─────────────────────────────────────────────────────────────────────────┘
