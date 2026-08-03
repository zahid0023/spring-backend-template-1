# Unified Image Hosting Strategy

This document explains the internal engine that lets any entity in this application upload images to a
configured storage backend (Cloudinary, S3, ...) without that entity's own code ever touching a provider
SDK. It's an internal engineering reference, not an API contract — see `docs/image-hosting-providers-api.md`
for the provider/config CRUD API, and `address/controller/CountryImageController.java` for the one live
consumer of this engine today.

---

## 1. The problem this solves

Multiple entities in this application may eventually need to store an uploaded image (a country's flag, a
resort's gallery, a room category's thumbnail, ...). Each of those uploads should be able to go to whichever
storage backend the caller has configured — Cloudinary for one, S3 for another — without:

- hardcoding a specific provider's SDK into that entity's controller/service, or
- writing an `if (provider == S3) ... else if (provider == CLOUDINARY) ...` branch anywhere, or
- coupling the upload engine to one specific "config" entity, since different scopes (a global admin config
  today, potentially a resort-scoped or room-category-scoped config later) may need to supply credentials.

This module is the shared answer to all three: one small, provider-agnostic engine that any entity's own
controller calls into directly.

---

## 2. Package layout

| Location                                                     | Contents                                                                                     |
|----------------------------------------------------------------|-----------------------------------------------------------------------------------------------|
| `commons/imagehosting/ImageHostingConfigSource.java`          | The abstraction the engine depends on — see §3                                                |
| `image/upload/strategy/`                                      | `ImageHostingStrategy`, `ImageHostingStrategyRegistry`, `CloudinaryHostingStrategy`, `S3HostingStrategy` |
| `image/upload/service/`, `image/upload/serviceImpl/`          | `ImageUploadService` / `ImageUploadServiceImpl` — the entry point entity controllers call     |
| `image/upload/dto/response/`                                  | `ImageUploadResponse`, `ImageBatchUploadResponse`                                              |
| `image/hosting/`                                               | Separate module: admin-managed `ImageHostingProvider` / `ImageHostingProviderConfig` CRUD — where credentials actually live. See `docs/image-hosting-providers-api.md` |

`image/upload` and `image/hosting` are deliberately separate modules: `image/hosting` is a CRUD resource
(providers, their config-field schema, and configured credential instances); `image/upload` is a stateless
engine that *consumes* a config, never persists anything itself, and has no REST endpoints of its own.

---

## 3. The core abstraction: `ImageHostingConfigSource`

```java
public interface ImageHostingConfigSource {
    String getProviderCode();
    Map<String, Object> getConfig();
}
```

`ImageUploadService` depends on this interface, not on any concrete JPA entity. `ImageHostingProviderConfigEntity`
(from `image/hosting`) implements it today — `getProviderCode()` delegates to its parent
`ImageHostingProviderEntity.getCode()`, and `getConfig()` is its existing `jsonb` column.

This exists so a future scoped config — e.g. a hypothetical `ResortImageHostingProviderConfigEntity` or
`RoomCategoryImageHostingProviderConfigEntity` tied to a different parent — can implement the same two
methods and be uploaded through the exact same `ImageUploadService`, with **zero changes** to this module.
The engine only ever sees "a provider code + a credentials map," never which table or scope it came from.

---

## 4. Dispatch: `ImageHostingStrategy` + `ImageHostingStrategyRegistry`

```java
public interface ImageHostingStrategy {
    String providerCode();
    ImageUploadResponse upload(MultipartFile file, Map<String, Object> config);
    void delete(String publicId, Map<String, Object> config);
}
```

Each provider gets one `@Component` implementing this. `ImageHostingStrategyRegistry` collects every
`ImageHostingStrategy` bean Spring knows about into a `Map<String, ImageHostingStrategy>` keyed by
`providerCode()`:

```java
public ImageHostingStrategyRegistry(List<ImageHostingStrategy> strategies) {
    this.strategies = strategies.stream()
            .collect(Collectors.toUnmodifiableMap(ImageHostingStrategy::providerCode, s -> s));
}
```

`get(providerCode)` throws `IllegalArgumentException` (→ `400 INVALID_ARGUMENT`, see §10) if no strategy is
registered for that code. There is no `if`/`switch` on provider type anywhere in this codebase — adding a
provider is purely additive (§9).

---

## 5. Current provider implementations

| Strategy                     | `providerCode()` | Required config keys                         | Optional keys |
|-------------------------------|-------------------|-----------------------------------------------|---------------|
| `CloudinaryHostingStrategy`   | `CLOUDINARY`       | `cloudName`, `apiKey`, `apiSecret`             | `folder`      |
| `S3HostingStrategy`           | `AWS_S3`           | `bucket`, `region`, `accessKey`, `secretKey`   | —             |

These keys match what's seeded per-provider in `V3__create_image_hosting_providers_table.sql`, but each
strategy validates them itself at upload time (`requireNonBlank(config, ...)`) — the `config` jsonb column on
`ImageHostingProviderConfigEntity` is untyped at the persistence layer (see §10, known gap).

Both strategies build a fresh SDK client per call (`new Cloudinary(...)` / `S3Client.builder()...build()`)
rather than caching one per config — simplest correct behavior, since credentials can change between calls
and configs are looked up dynamically.

---

## 6. `ImageUploadService` — upload / uploadAll / delete

```java
public interface ImageUploadService {
    ImageUploadResponse upload(MultipartFile file, ImageHostingConfigSource configSource);
    List<ImageUploadResponse> uploadAll(List<MultipartFile> files, ImageHostingConfigSource configSource);
    void delete(String publicId, ImageHostingConfigSource configSource);
}
```

`upload()` rejects an empty file, resolves `configSource.getProviderCode()` through the registry, and
delegates. `delete()` is the same shape, using the same config's provider.

`uploadAll()` uploads files one at a time and **rolls back everything already uploaded in that same call** if
any later file fails:

```java
try {
    for (MultipartFile file : files) {
        uploaded.add(upload(file, configSource));
    }
    return uploaded;
} catch (Exception ex) {
    uploaded.forEach(response -> delete(response.getPublicId(), configSource)); // best-effort
    throw ex;
}
```

There's no database transaction to roll back here (nothing is persisted by this module) — "rollback" means
deleting the already-uploaded files back out of the remote provider, so a batch call either fully succeeds or
leaves nothing behind remotely.

---

## 7. How an entity plugs in — the Country worked example

`address/controller/CountryImageController.java` is the reference pattern for wiring a new entity into this
engine:

```java
@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
public ResponseEntity<SuccessResponse> upload(
        @PathVariable("country-id") Long countryId,
        @RequestParam("provider_config_id") Long providerConfigId,
        @RequestPart("image") MultipartFile image) {
    CountryEntity countryEntity = countryService.getEntityById(countryId);
    ImageHostingProviderConfigEntity configEntity = imageHostingProviderConfigService.getEntityById(providerConfigId);
    ImageUploadResponse uploadResponse = imageUploadService.upload(image, configEntity);
    return ResponseEntity.ok(countryService.updateFlagImage(countryEntity, uploadResponse.getImageUrl()));
}
```

Four steps, always in this order:

1. Resolve the owning entity (`CountryService.getEntityById`).
2. Resolve the credentials source (`ImageHostingProviderConfigService.getEntityById`) — the caller supplies
   which configured account to use via `provider_config_id`; there is no "default provider per entity type."
3. Call `ImageUploadService.upload(...)`, passing the config entity directly — it satisfies
   `ImageHostingConfigSource` implicitly (§3), so no adapter/wrapper code is needed.
4. Persist the result onto the entity's *own* field (`CountryService.updateFlagImage`), via a small
   entity-specific service method — the upload engine itself never touches `CountryRepository`.

`DELETE /api/v1/countries/{country-id}/images` mirrors this: resolve entity + config, call
`imageUploadService.delete(publicId, configEntity)`, then reset `flagUrl` back to `""` (not `null` —
`countries.flag_url` is `not null default ''`).

---

## 8. History: why there is no shared `/api/v1/images` endpoint

An earlier version of this feature exposed one top-level `ImageUploadController` at `/api/v1/images/upload`
(and a matching `docs/image-upload-api.md`), following a spec that called for a single unified upload API
across all entities. It was deliberately removed: entities differ in what they do with an uploaded image —
Country wants exactly one URL saved onto its own `flagUrl` column, while a future gallery-style entity might
want every uploaded URL appended to a child collection. A single shared controller can't express that
difference cleanly, so each entity now owns its own nested `.../images` endpoint (§7) and calls straight into
the same underlying engine described in this document. **Only the shared controller and its doc were
deleted — `ImageUploadService`, the strategies, the registry, and `ImageHostingConfigSource` are unchanged
and are exactly what every per-entity controller (including Country's) is built on.**

---

## 9. Extending this to a new entity

1. Confirm the entity has somewhere to put the result — a single URL column (like `Country.flagUrl`) or a
   child collection, depending on whether it's a single image or a gallery.
2. Add a narrow, entity-specific service method that persists the result (e.g.
   `CountryService.updateFlagImage(entity, url)`) — don't have the upload engine call the entity's repository
   directly.
3. Add a controller nested under that entity, e.g. `FooImageController` at `/api/v1/foos/{foo-id}/images`,
   with `POST` (multipart `provider_config_id` + `image`/`images`) and `DELETE`, following §7's four steps
   exactly.
4. Inject `ImageHostingProviderConfigService` and `ImageUploadService` — never a Cloudinary/S3 SDK class —
   into the new controller.

---

## 10. Extending this to a new provider

1. Create the provider definition (code, name, its config-field schema) via [Create Provider]
   (image-hosting-providers-api.md#create-provider) — e.g. `CLOUDFLARE_R2` (already seeded as a provider row,
   but see §11 — no strategy exists for it yet).
2. Implement `ImageHostingStrategy` as a new `@Component` (e.g. `CloudflareR2HostingStrategy`), returning the
   matching code from `providerCode()`.
3. Nothing else changes — `ImageHostingStrategyRegistry` auto-discovers the new bean; `ImageUploadService`
   and every existing entity controller (Country's included) require no modification.

---

## 11. Known gaps

- **`uploadAll`/`ImageBatchUploadResponse` have no current caller.** They were built for the since-removed
  shared endpoint's batch-upload requirement (§8) and kept because a future gallery-style entity will likely
  need the same rollback-on-partial-failure semantics — but today, `CountryImageController` only calls the
  single-file `upload()`.
- **Config values are never validated against the provider's declared config-field schema.** A `config` jsonb
  value can be saved via [Create Config](image-hosting-providers-api.md#create-config) missing a required key
  for its provider — nothing checks it against `image_hosting_provider_config_fields` at save time. The
  failure only surfaces later, at upload time, via each strategy's own `requireNonBlank` check (§5).
- **`CLOUDFLARE_R2` is seeded as a provider row with no matching strategy.** Creating a config against it
  works fine; uploading through it fails with `400 INVALID_ARGUMENT` ("No upload strategy registered for
  provider: CLOUDFLARE_R2") until a `CloudflareR2HostingStrategy` is written (§10).
- **Country is the only entity wired up today.** No other module has its own `.../images` endpoint yet.
