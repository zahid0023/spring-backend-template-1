# Image Hosting Providers API

Base URL: `/api/v1/image-hosting-providers`

Image Hosting Providers represent the storage backends available to the platform (e.g. Amazon S3,
Cloudinary, Cloudflare R2), each identified by a unique `code`. Each provider defines its own
connection-configuration schema — the fields a caller must supply to configure it (bucket name, API keys,
etc.) — through a companion sub-resource, Config Fields, reached via
`/api/v1/image-hosting-providers/{image-hosting-provider-id}/config-fields`. A config field describes a
*field of the schema itself* (its key, label, type, whether it's required), not an actual stored credential
value. Actual configured instances of a provider (e.g. "Cloudinary Marketing", "Cloudinary Food") are
managed through a second sub-resource, Configs, reached via
`/api/v1/image-hosting-providers/{image-hosting-provider-id}/configs` — see [Configs](#configs) below. All
records support soft-delete — deleted records are hidden from all responses.

**None of these modules have a locale/translation concept** — `name`, `label`, `config`, and other text/JSON
fields are stored directly, once, in whatever the caller submits, never per-language. That said,
**`Accept-Language` is still required on every endpoint below, with no exceptions** — it's enforced globally
by `commons/filter/LocaleContextFilter.java`, before any endpoint in the application runs, regardless of
whether that endpoint's module has a locale concept of its own. Its value has **no effect** on the response
shape anywhere in this document — it's checked for presence only.

---

## Endpoints

| Method | Path                                                               | Description             |
|--------|--------------------------------------------------------------------|-------------------------|
| POST   | `/api/v1/image-hosting-providers`                                  | Create a provider       |
| GET    | `/api/v1/image-hosting-providers`                                  | List / search providers |
| GET    | `/api/v1/image-hosting-providers/{id}`                             | Get a provider          |
| PUT    | `/api/v1/image-hosting-providers/{id}`                             | Update a provider       |
| DELETE | `/api/v1/image-hosting-providers/{id}`                             | Delete a provider       |
| POST   | `/api/v1/image-hosting-providers/{provider-id}/config-fields`      | Create a config field   |
| GET    | `/api/v1/image-hosting-providers/{provider-id}/config-fields`      | List config fields      |
| PUT    | `/api/v1/image-hosting-providers/{provider-id}/config-fields/{id}` | Update a config field   |
| DELETE | `/api/v1/image-hosting-providers/{provider-id}/config-fields/{id}` | Delete a config field   |
| POST   | `/api/v1/image-hosting-providers/{provider-id}/configs`            | Create a config         |
| GET    | `/api/v1/image-hosting-providers/{provider-id}/configs`            | List / search configs   |
| PUT    | `/api/v1/image-hosting-providers/{provider-id}/configs/{id}`       | Update a config         |
| DELETE | `/api/v1/image-hosting-providers/{provider-id}/configs/{id}`       | Delete a config         |

---

## Data Model

### ImageHostingProvider

| Field           | Type    | Required | Constraints                                                                      | Description                                                                          |
|-----------------|---------|----------|----------------------------------------------------------------------------------|--------------------------------------------------------------------------------------|
| `id`            | Long    | —        | read-only                                                                        | Auto-generated identifier                                                            |
| `code`          | String  | Yes      | max 50 chars, unique among active records; set at creation, immutable            | Short provider code (e.g., `AWS_S3`, `CLOUDINARY`, `CLOUDFLARE_R2`)                  |
| `name`          | String  | Yes      | max 100 chars                                                                    | Display name (e.g., `Amazon S3`)                                                     |
| `description`   | String  | Yes      | not null (defaults to `""`)                                                      | Free-text description                                                                |
| `sort_order`    | Integer | Yes      | default 0                                                                        | Display order                                                                        |

> **Note:** `ImageHostingProvider` responses (both `GET /{id}` and `GET` list) never include the provider's
> config fields — there is no `config_fields` field on this DTO at all. To read a provider's
> connection-configuration schema, call [List Config Fields](#list-config-fields) separately.

### ImageHostingProviderConfigField

| Field           | Type    | Required | Constraints                                                                                                       | Description                                                     |
|-----------------|---------|----------|-------------------------------------------------------------------------------------------------------------------|-----------------------------------------------------------------|
| `id`            | Long    | —        | read-only                                                                                                         | Auto-generated identifier                                       |
| `key`           | String  | Yes      | max 100 chars, unique among the provider's active fields; set at creation, immutable                              | Machine-readable field name (e.g., `bucket`, `apiKey`)          |
| `label`         | String  | Yes      | max 100 chars                                                                                                     | Human-readable label (e.g., `Bucket Name`)                      |
| `field_type`    | String  | Yes      | max 30 chars, free text (convention: `TEXT`, `PASSWORD`, `NUMBER`, `BOOLEAN`, `URL` — not enforced by validation) | Input type hint for rendering the field                         |
| `placeholder`   | String  | Yes      | not null (defaults to `""`), max 255 chars                                                                        | Placeholder text for the input                                  |
| `default_value` | String  | Yes      | not null (defaults to `""`), max 500 chars                                                                        | Default value pre-filled for the field                          |
| `is_required`   | Boolean | Yes      | default `true`                                                                                                    | Whether a value must be supplied when configuring this provider |
| `sort_order`    | Integer | Yes      | default 0                                                                                                         | Display order among the provider's config fields                |

### ImageHostingProviderConfig

| Field    | Type          | Required | Constraints                                                                      | Description                                                                    |
|----------|---------------|----------|-------------------------------------------------------------------------------------|-------------------------------------------------------------------------------|
| `id`     | Long          | —        | read-only                                                                          | Auto-generated identifier                                                     |
| `name`   | String        | Yes      | max 100 chars; unique among the owning provider's active configs; set at creation | Human-readable label for this configured instance (e.g., `Cloudinary Marketing`) |
| `config` | Object (JSON) | Yes      | not null; stored as `jsonb`, arbitrary shape                                      | The actual configuration/credential values for this instance                  |

> **Note:** the response DTO never includes which provider a config belongs to — that's implicit in the
> `{provider-id}` path segment you called. See [Configs](#configs) below.

---

## Create Provider

`POST /api/v1/image-hosting-providers`

Creates a new image hosting provider together with its config fields — the connection-configuration schema
must be submitted **in this same request**; there is no separate "create empty provider, add fields later"
path. `code` must be unique among active, non-deleted providers — attempting to reuse an existing code
returns `409 CONFLICT`. `config_fields` must contain at least one entry, and no two entries in it may share
the same `key` — a duplicate `key` within the request returns `409 CONFLICT`. There is no locale/translation
step — `name` and `description` are submitted directly in this same request.

### Request Body

```json
{
  "code": "AWS_S3",
  "name": "Amazon S3",
  "description": "",
  "sort_order": 1,
  "config_fields": [
    {
      "key": "bucket",
      "label": "Bucket Name",
      "field_type": "TEXT",
      "placeholder": "",
      "default_value": "",
      "is_required": true,
      "sort_order": 1
    },
    {
      "key": "region",
      "label": "Region",
      "field_type": "TEXT",
      "placeholder": "",
      "default_value": "",
      "is_required": true,
      "sort_order": 2
    }
  ]
}
```

### Request Fields

| Field           | Type    | Required | Validation                                                                      |
|-----------------|---------|----------|---------------------------------------------------------------------------------|
| `code`          | String  | Yes      | Not blank, max 50 chars, unique among active records                            |
| `name`          | String  | Yes      | Not blank, max 100 chars                                                        |
| `description`   | String  | Yes      | Not null                                                                        |
| `sort_order`    | Integer | Yes      | Not null                                                                        |
| `config_fields` | Array   | Yes      | Not empty; no duplicate `key` within the list; each entry validated (see below) |

**Config field entry (`config_fields[]`):**

| Field           | Type    | Required | Validation               |
|-----------------|---------|----------|--------------------------|
| `key`           | String  | Yes      | Not blank, max 100 chars |
| `label`         | String  | Yes      | Not blank, max 100 chars |
| `field_type`    | String  | Yes      | Not blank, max 30 chars  |
| `placeholder`   | String  | Yes      | Not null, max 255 chars  |
| `default_value` | String  | Yes      | Not null, max 500 chars  |
| `is_required`   | Boolean | Yes      | Not null                 |
| `sort_order`    | Integer | Yes      | Not null                 |

### Response `201 Created`

```json
{
  "success": true,
  "id": 1
}
```

---

## Get Provider

`GET /api/v1/image-hosting-providers/{id}`

Returns a single active provider by its ID. Config fields are **not** included in this response — call
[List Config Fields](#list-config-fields) separately to read them.

### Path Parameters

| Parameter | Type | Description        |
|-----------|------|--------------------|
| `id`      | Long | ID of the provider |

### Response `200 OK`

```json
{
  "data": {
    "id": 1,
    "code": "AWS_S3",
    "name": "Amazon S3",
    "description": "",
    "sort_order": 1
  }
}
```

---

## List / Search Providers

`GET /api/v1/image-hosting-providers`

Returns a paginated, filterable list of active (non-deleted) providers. All filter parameters are
optional; omitting them returns all providers. Multiple filters are combined with AND. `code` and `name`
both perform a case-insensitive partial match. As with `GET /{id}`, rows never include config fields — call
[List Config Fields](#list-config-fields) per provider if you need them.

> **Note:** `id` is not a selectable `sortBy` value — passing `?sortBy=id` throws
> `400 INVALID_ARGUMENT: Invalid sort field: id`. It's used only as the implicit sort when `sortBy` is
> omitted entirely. Also note only `code` and `name` are sortable here — there is no `createdAt` or
> `sortOrder` sort option (unlike Countries/Cities).

### Query Parameters

> **Note:** Query parameters bind directly onto `ImageHostingProviderFilterRequest`'s Java field names, so
> they are **camelCase** — not the snake_case used in JSON request/response bodies.

| Parameter | Type   | Default         | Constraints                          | Description                                |
|-----------|--------|-----------------|--------------------------------------|--------------------------------------------|
| `code`    | String | —               | —                                    | Filter by code (partial, case-insensitive) |
| `name`    | String | —               | —                                    | Filter by name (partial, case-insensitive) |
| `page`    | int    | `0`             | >= 0                                 | Zero-based page index                      |
| `size`    | int    | `10`            | 1 – 50                               | Number of items per page                   |
| `sortBy`  | String | `id` (implicit) | `code`, `name` (`id` NOT selectable) | Field to sort by                           |
| `sortDir` | String | `ASC`           | `ASC`, `DESC`                        | Sort direction                             |

### Response `200 OK`

```json
{
  "data": [
    {
      "id": 1,
      "code": "AWS_S3",
      "name": "Amazon S3",
      "description": "",
      "sort_order": 1
    },
    {
      "id": 2,
      "code": "CLOUDINARY",
      "name": "Cloudinary",
      "description": "",
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

## Update Provider

`PUT /api/v1/image-hosting-providers/{id}`

Updates `name`, `description`, and `sort_order`. `code` is set at creation and cannot be changed.

### Path Parameters

| Parameter | Type | Description        |
|-----------|------|--------------------|
| `id`      | Long | ID of the provider |

### Request Body

```json
{
  "name": "Amazon S3",
  "description": "Primary object storage backend",
  "sort_order": 1
}
```

### Request Fields

| Field         | Type    | Required | Validation               |
|---------------|---------|----------|--------------------------|
| `name`        | String  | Yes      | Not blank, max 100 chars |
| `description` | String  | Yes      | Not null                 |
| `sort_order`  | Integer | Yes      | Not null                 |

### Response `200 OK`

```json
{
  "success": true,
  "id": 1
}
```

---

## Delete Provider

`DELETE /api/v1/image-hosting-providers/{id}`

Soft-deletes the provider. The record is not removed from the database but will no longer appear in any
response.

### Path Parameters

| Parameter | Type | Description        |
|-----------|------|--------------------|
| `id`      | Long | ID of the provider |

### Response `200 OK`

```json
{
  "success": true,
  "id": 1
}
```

---

## Config Fields

Config Field endpoints manage the connection-configuration schema for a provider — each entry describes
one field of that schema (e.g. "Bucket Name", "API Key"), not an actual configured credential value. A
provider's initial config fields are submitted as part of [Create Provider](#create-provider) — the
endpoints below are for adding, changing, or removing fields on a provider that already exists. The
`{provider-id}` path parameter must reference an existing, active provider.

---

### Create Config Field

`POST /api/v1/image-hosting-providers/{provider-id}/config-fields`

Adds a new config field to an existing provider. `key` must be unique among the provider's active config
fields — adding a key the provider already has returns `409 CONFLICT`, pre-checked at the application level
before any write (backed by a DB-level unique constraint on `(image_hosting_provider_id, key)` as a
last-resort guard).

#### Path Parameters

| Parameter     | Type | Description               |
|---------------|------|---------------------------|
| `provider-id` | Long | ID of the parent provider |

#### Request Body

```json
{
  "key": "bucket",
  "label": "Bucket Name",
  "field_type": "TEXT",
  "placeholder": "my-bucket",
  "default_value": "",
  "is_required": true,
  "sort_order": 1
}
```

#### Request Fields

| Field           | Type    | Required | Validation               |
|-----------------|---------|----------|--------------------------|
| `key`           | String  | Yes      | Not blank, max 100 chars |
| `label`         | String  | Yes      | Not blank, max 100 chars |
| `field_type`    | String  | Yes      | Not blank, max 30 chars  |
| `placeholder`   | String  | Yes      | Not null, max 255 chars  |
| `default_value` | String  | Yes      | Not null, max 500 chars  |
| `is_required`   | Boolean | Yes      | Not null                 |
| `sort_order`    | Integer | Yes      | Not null                 |

#### Response `201 Created`

```json
{
  "success": true,
  "id": 2
}
```

---

### List Config Fields

`GET /api/v1/image-hosting-providers/{provider-id}/config-fields`

Returns every active config field belonging to the provider.

> **Note:** unlike every other list endpoint in this API set, this one is **not paginated** — the response
> body is a plain JSON array, with no `data` envelope and no `sortable_fields`/`searchable_fields`.

#### Path Parameters

| Parameter     | Type | Description               |
|---------------|------|---------------------------|
| `provider-id` | Long | ID of the parent provider |

#### Response `200 OK`

```json
[
  {
    "id": 1,
    "key": "bucket",
    "label": "Bucket Name",
    "field_type": "TEXT",
    "placeholder": "",
    "default_value": "",
    "is_required": true,
    "sort_order": 1
  },
  {
    "id": 2,
    "key": "region",
    "label": "Region",
    "field_type": "TEXT",
    "placeholder": "",
    "default_value": "",
    "is_required": true,
    "sort_order": 2
  }
]
```

---

### Update Config Field

`PUT /api/v1/image-hosting-providers/{provider-id}/config-fields/{id}`

Updates `label`, `field_type`, `placeholder`, `default_value`, `is_required`, and `sort_order` for an
existing config field. The associated provider and `key` cannot be changed after creation.

#### Path Parameters

| Parameter     | Type | Description               |
|---------------|------|---------------------------|
| `provider-id` | Long | ID of the parent provider |
| `id`          | Long | ID of the config field    |

#### Request Body

```json
{
  "label": "Bucket Name",
  "field_type": "TEXT",
  "placeholder": "my-bucket",
  "default_value": "",
  "is_required": true,
  "sort_order": 1
}
```

#### Request Fields

| Field           | Type    | Required | Validation               |
|-----------------|---------|----------|--------------------------|
| `label`         | String  | Yes      | Not blank, max 100 chars |
| `field_type`    | String  | Yes      | Not blank, max 30 chars  |
| `placeholder`   | String  | Yes      | Not null, max 255 chars  |
| `default_value` | String  | Yes      | Not null, max 500 chars  |
| `is_required`   | Boolean | Yes      | Not null                 |
| `sort_order`    | Integer | Yes      | Not null                 |

#### Response `200 OK`

```json
{
  "success": true,
  "id": 2
}
```

---

### Delete Config Field

`DELETE /api/v1/image-hosting-providers/{provider-id}/config-fields/{id}`

Soft-deletes a config field. The record is not removed from the database but will no longer appear in any
response.

#### Path Parameters

| Parameter     | Type | Description               |
|---------------|------|---------------------------|
| `provider-id` | Long | ID of the parent provider |
| `id`          | Long | ID of the config field    |

#### Response `200 OK`

```json
{
  "success": true,
  "id": 2
}
```

---

## Configs

Config endpoints manage actual configured instances of a provider — for example "Cloudinary Marketing" and
"Cloudinary Food" could be two separate configs against the same `CLOUDINARY` provider, each with its own
credentials/settings. Where a Config Field (above) describes one *field of the schema* (its key, label,
type), a Config stores the actual configured values for that schema as a single JSON payload. Every endpoint
below is nested under, and scoped to, a single provider — there is no top-level, cross-provider way to list
or address a config. The `{provider-id}` path parameter must reference an existing, active provider on every
endpoint below — an unknown value returns `404 ENTITY_NOT_FOUND`.

---

### Create Config

`POST /api/v1/image-hosting-providers/{provider-id}/configs`

Creates a new config under the given provider. `name` must be unique among that provider's active configs —
reusing a name already used by another active config **of the same provider** returns `409 CONFLICT`; the
same `name` is allowed across different providers.

#### Path Parameters

| Parameter     | Type | Description               |
|---------------|------|---------------------------|
| `provider-id` | Long | ID of the parent provider |

#### Request Body

```json
{
  "name": "Cloudinary Marketing",
  "config": {
    "cloud_name": "my-cloud",
    "api_key": "123456789",
    "api_secret": "secret"
  }
}
```

#### Request Fields

| Field    | Type   | Required | Validation                                                            |
|----------|--------|----------|--------------------------------------------------------------------------|
| `name`   | String | Yes      | Not blank, max 100 chars; unique among the provider's active configs    |
| `config` | Object | Yes      | Not null; arbitrary JSON object                                         |

#### Response `201 Created`

```json
{
  "success": true,
  "id": 1
}
```

---

### List Configs

`GET /api/v1/image-hosting-providers/{provider-id}/configs`

Returns a paginated, filterable list of the given provider's active (non-deleted) configs. All filter
parameters are optional; omitting them returns every config for that provider. `name` performs a
case-insensitive partial match.

> **Note:** unlike [List Config Fields](#list-config-fields), this list **is** paginated — the response
> follows the same `data`/`current_page`/`sortable_fields` shape as [List / Search Providers](#list--search-providers)
> above, not a plain array.

> **Note:** `id` is not a selectable `sortBy` value — passing `?sortBy=id` throws
> `400 INVALID_ARGUMENT: Invalid sort field: id`. It's used only as the implicit sort when `sortBy` is
> omitted entirely.

#### Path Parameters

| Parameter     | Type | Description               |
|---------------|------|---------------------------|
| `provider-id` | Long | ID of the parent provider |

#### Query Parameters

> **Note:** Query parameters bind directly onto `ImageHostingProviderConfigFilterRequest`'s Java field names,
> so they are **camelCase** — not the snake_case used in JSON request/response bodies.

| Parameter | Type   | Default         | Constraints                  | Description                                 |
|-----------|--------|-----------------|-------------------------------|----------------------------------------------|
| `name`    | String | —               | —                              | Filter by name (partial, case-insensitive)   |
| `page`    | int    | `0`             | >= 0                           | Zero-based page index                        |
| `size`    | int    | `10`            | 1 – 50                          | Number of items per page                     |
| `sortBy`  | String | `id` (implicit) | `name` (`id` NOT selectable)   | Field to sort by                             |
| `sortDir` | String | `ASC`           | `ASC`, `DESC`                   | Sort direction                               |

#### Response `200 OK`

```json
{
  "data": [
    {
      "id": 1,
      "name": "Cloudinary Marketing",
      "config": {
        "cloud_name": "my-cloud",
        "api_key": "123456789",
        "api_secret": "secret"
      }
    },
    {
      "id": 2,
      "name": "Cloudinary Food",
      "config": {
        "cloud_name": "my-cloud",
        "api_key": "987654321",
        "api_secret": "secret2"
      }
    }
  ],
  "current_page": 0,
  "total_pages": 1,
  "total_elements": 2,
  "page_size": 10,
  "has_next": false,
  "has_previous": false,
  "sortable_fields": [
    "name",
    "imageHostingProviderEntity.id"
  ],
  "searchable_fields": [
    "name"
  ]
}
```

---

### Update Config

`PUT /api/v1/image-hosting-providers/{provider-id}/configs/{id}`

Updates `name` and `config`. The config must belong to the provider named in the path — passing a valid
config `id` that belongs to a *different* provider returns `404 ENTITY_NOT_FOUND`, the same as an unknown
`id`. `name` must remain unique among the provider's active configs (excluding this record) — a collision
returns `409 CONFLICT`.

#### Path Parameters

| Parameter     | Type | Description               |
|---------------|------|---------------------------|
| `provider-id` | Long | ID of the parent provider |
| `id`          | Long | ID of the config          |

#### Request Body

```json
{
  "name": "Cloudinary Marketing",
  "config": {
    "cloud_name": "my-cloud",
    "api_key": "123456789",
    "api_secret": "rotated-secret"
  }
}
```

#### Request Fields

| Field    | Type   | Required | Validation                                                            |
|----------|--------|----------|--------------------------------------------------------------------------|
| `name`   | String | Yes      | Not blank, max 100 chars; unique among the provider's active configs    |
| `config` | Object | Yes      | Not null; arbitrary JSON object                                         |

#### Response `200 OK`

```json
{
  "success": true,
  "id": 1
}
```

---

### Delete Config

`DELETE /api/v1/image-hosting-providers/{provider-id}/configs/{id}`

Soft-deletes the config. The config must belong to the provider named in the path — the same
provider-mismatch rule as Update Config applies. The record is not removed from the database but will no
longer appear in any response.

#### Path Parameters

| Parameter     | Type | Description               |
|---------------|------|---------------------------|
| `provider-id` | Long | ID of the parent provider |
| `id`          | Long | ID of the config          |

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
  "message": "ImageHostingProvider not found with id: 99"
}
```

| HTTP Status | Error Code                 | Cause                                                                                                                                                                                                                                                                       |
|-------------|----------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| 400         | `INVALID_ARGUMENT`         | Missing or blank `Accept-Language` header (checked globally, before any endpoint runs — see the intro above); missing/invalid required fields; or an unsupported `sortBy` query value on `GET /image-hosting-providers` or `GET /configs`                                    |
| 404         | `ENTITY_NOT_FOUND`         | Provider not found; config field not found; or config not found (unknown `id`, or an `id` that belongs to a different provider than the one in the path)                                                                                                                     |
| 409         | `CONFLICT`                 | `code` already in use by another active provider (`create` provider); two entries in `config_fields` share the same `key` (`create` provider); the provider already has a config field for the given `key` (`create` config field, pre-checked at the application level); or `name` already in use by another active config for the same provider (`create`/`update` config) |
| 409         | `DATA_INTEGRITY_VIOLATION` | Last-resort DB-level unique constraint on `(image_hosting_provider_id, key)` for config fields, should not normally be reachable now that the duplicate is pre-checked at the application level. **Configs have no equivalent DB-level constraint** — the migration for `image_hosting_provider_configs` defines no `unique` constraint on `(image_hosting_provider_id, name)`, so their `409 CONFLICT` above is enforced purely at the application level |
