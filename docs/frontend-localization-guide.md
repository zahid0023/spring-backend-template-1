# Frontend Guide: Implementing Localization

This guide explains how a frontend client should integrate with this platform's opt-in
localization pattern. It's written generically, in terms of an `{Entity}` with a companion
`{Entity}Locale` — apply it to whichever entity you're fetching. (`Country` /
`CountryLocale` is the current reference implementation this pattern was built from; the
same contract holds for every other entity that adopts it.)

Not every entity is localized. Only ROOT entities that expose a `locales` array in their
response — and have a matching `{entity}-locales` sub-resource — participate in this
pattern. If an entity's response has no `locales` field, none of this applies; just fetch
it normally.

---

## 1. Core concepts

- A `Locale` (`GET /api/v1/locales`) represents one language, identified by a short `code`
  (e.g. `en`, `bn`) plus a display `name`. This is shared across every localized entity.
- Each localized `{Entity}` has zero or more child translation rows (`{Entity}Locale`),
  each tied to exactly one `Locale` via `locale_id`. The pair `(entity_id, locale_id)` is
  unique — a given record can have at most one translation per locale.
- The frontend selects which translation it wants via the standard `Accept-Language` HTTP
  header — for **any** localized entity's list endpoint. There is **no** query-string
  equivalent — passing `?locale_id=...` has no effect on any entity. Always set the header,
  never try to pass locale as a query param.

---

## 2. Which endpoints honor `Accept-Language`

This shape repeats identically for every localized entity — substitute `{entities}` with
the entity's own collection path (e.g. `countries`, or whatever a future localized entity
uses):

| Endpoint                                             | Honors `Accept-Language`? | `locales` in response                          |
|--------------------------------------------------------|:--------------------------:|---------------------------------------------------|
| `GET /api/v1/{entities}`                                | **Yes**                    | At most 1 entry — the resolved locale (see §3)     |
| `GET /api/v1/{entities}/{id}`                           | No                          | **Every** translation the record has                |
| `POST /api/v1/{entities}`                               | No                          | n/a (client supplies `locales[]` directly)          |
| `PUT /api/v1/{entities}/{id}`                           | No                          | n/a (doesn't touch translations at all)             |
| `{entities}` locale sub-resource (`/{entities}/{id}/locales`) | No                    | n/a (each call operates on one explicit `locale_id`) |

This split matters for **every** localized entity: a detail page (`getById`) gets **all**
translations back and must pick the right one client-side (§5); a list page (`getAll`)
gets the server to do that picking for you, scoped to one locale per row.

---

## 3. How the server resolves a locale from the header

Identical logic runs for every localized entity's list endpoint. `Accept-Language` is
parsed as follows (only the **primary** language tag is used — region and quality values
are discarded):

```
"en-US,en;q=0.9"  →  "en"
"bn"              →  "bn"
```

Resolution order:

1. Look up an active `Locale` whose `code` matches the parsed tag.
2. If not found (including if the header was missing or blank), fall back to the locale
   with code `"en"`.
3. If even `"en"` doesn't exist in the system, no locale is applied — `locales` in the
   response will be an empty array for every row, for every entity.

**Important:** the server never reports back which locale it actually used. If you request
`fr` and only `en` exists, you'll silently get English data back — for any localized
entity. To know which locale you actually received, read `locales[0].locale.code` from the
response — don't assume it matches what you sent.

---

## 4. Discovering available locales

This is a one-time, entity-independent lookup — do it once for the whole app, not per
entity:

```
GET /api/v1/locales?sort_by=sort_order&sort_dir=asc
```

Use this to drive a language switcher UI, and to validate/normalize whatever language the
browser or user profile reports (e.g. map browser `navigator.language` down to a supported
`code`, or fall back to your app's default). Every localized entity shares this same
locale set — you don't need a per-entity locale list.

---

## 5. Client-side translation picking (for `getById`)

Since `getById` returns **every** translation on **any** localized entity, replicate the
same fallback the backend uses for `getAll`, so detail pages and list pages never show
inconsistent languages for the same record:

```js
function pickTranslation(locales, preferredCode) {
  return (
    locales.find(l => l.locale.code === preferredCode) ??
    locales.find(l => l.locale.code === "en") ??
    locales[0] ??
    null
  );
}
```

Reuse this one helper across every localized entity's detail view — it doesn't need to
know which entity it's picking a translation for. If `locales` is empty (no translations
exist yet for that record), render a placeholder — don't assume there's always at least
one row.

---

## 6. Sending the header

Set `Accept-Language` on every list request, for every localized entity:

```js
fetch(`/api/v1/${entityCollectionPath}?sort_by=name&sort_dir=asc`, {
  headers: { "Accept-Language": userPreferredLocaleCode }
});
```

Use a plain locale code (`"bn"`), or a full browser-style value (`"bn-BD,bn;q=0.9,en;q=0.8"`)
— both parse fine since only the primary tag before the first `-`/`;`/`,` is read. A single
shared `fetch` wrapper that always attaches this header works for every localized entity —
no per-entity special-casing needed.

---

## 7. Creating and updating translations

The same rules apply no matter which localized entity you're working with:

- **Create** (`POST /api/v1/{entities}`): submit the full initial set of translations
  inline via `locales[]`, each with an explicit `locale_id` (fetch this from
  `GET /api/v1/locales` first — don't hardcode IDs). `Accept-Language` is ignored here.
- **Add a translation later**: `POST /api/v1/{entities}/{id}/locales` with an explicit
  `locale_id`. Adding a `locale_id` the record already has a translation for returns
  `409 DATA_INTEGRITY_VIOLATION` — check the existing `locales` array client-side before
  offering "add language" for a locale that's already present.
- **Edit a translation**: `PUT /api/v1/{entities}/{id}/locales/{localeRowId}` — targets one
  specific `{Entity}Locale` row by its own `id`, not by `locale_id`. Non-translation fields
  on the ROOT record are updated separately via `PUT /api/v1/{entities}/{id}`, which is
  unrelated to translations.

---

## 8. Summary checklist

- [ ] Fetch `GET /api/v1/locales` once at startup; use it to populate a language switcher and validate any locale code before use. This list is shared across every localized entity.
- [ ] Store the user's chosen/detected locale `code` (not a raw `Accept-Language` value).
- [ ] Send `Accept-Language: {code}` on every list (`getAll`) request, for every localized entity — a shared fetch wrapper is the simplest way to guarantee this.
- [ ] Never send locale as a query parameter — it's ignored, for every entity.
- [ ] On detail pages (`getById`), pick the displayed translation client-side using the §5 fallback logic — don't assume the server scoped it for you.
- [ ] Don't assume `locales[0]` on a `getAll` row matches the locale you requested — check `locales[0].locale.code` if it matters.
- [ ] Before offering "add translation" for a locale, check it isn't already in the record's `locales` array (avoids a 409).
- [ ] Before applying any of this to a given entity, confirm it's actually localized — check that its response includes a `locales` array in the first place.
