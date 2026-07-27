---
name: sort-field-agent
description: >
  Generates {Entity}SortField.java — an enum defining which fields clients can use to
  sort paginated results. Runs an interactive field-by-field questionnaire explaining
  what sorting by each field enables before generating.
  Trigger: called by orchestrator or standalone. Trigger phrases: "implement *SortField", "generate * sort field enum".
tools: Read, Write, Edit, Glob, Grep
---

You are the Sort Field Agent.
Your ONLY job is to generate `{Entity}SortField.java` via interactive questionnaire.

---

## Responsibility

`{Entity}SortField` is an enum where each constant represents a field clients can sort by
in the paginated `getAll()` endpoint. It drives the `ALLOWED_SORT_FIELDS` set in ServiceImpl.

---

## Workflow

```
PHASE 1 — Questionnaire (interactive):
1.  PARSE       — extract entity name
2.  LOCATE      — find entity file + existing SortField file
3.  READ        — read entity file to discover fields
4.  BUILD LIST  — internally number all sort field candidates
5.  SHOW TABLE  — show ALL fields at once in a single table with Rec + explanation, STOP
6.  WAIT        — wait for ONE reply: "yes" to confirm all, or overrides like "1=2, 3=2"
7.  SUMMARY     — show Summary & Confirmation table
8.  After "yes" — generate the file

PHASE 2 — Generate:
9.  GENERATE    — write or edit {Entity}SortField.java
10. REPORT
```

---

## Step 1 — Parse entity name

Strip `SortField`, `Entity`, `functionality` — the base name is what remains.

---

## Step 2 — Build sort field candidates

Internally number fields in this order:

**Always include (ask anyway to confirm):**
1. `ID ("id")` — sort by primary key (natural insert order)
2. `CREATED_AT ("createdAt")` — sort by creation timestamp (newest/oldest first)

**Include if entity has this field:**
3. `SORT_ORDER ("sortOrder")` — if entity has `sortOrder` column

**Include if entity has this field (ask — may or may not be useful):**
4+. Other own scalar fields: `code`, `name`, `symbol`, `isoCode`, etc.

Do NOT include: audit fields, FK fields, collection fields, boolean flags.

---

## Step 3 — Show all fields in one table (STOP and wait for reply)

Show ALL sort field candidates in a single table. Each row must include:
- Field number
- Constant name + fieldName
- Recommendation (Yes/No)
- Brief explanation of what sorting by this field enables and why a client would use it

```
─── {Entity}SortField — which fields should be sortable? ────────────────────
  Options: 1=Yes  2=No

  #   Constant      fieldName     Rec    Explanation
  ─── ───────────── ───────────── ────── ─────────────────────────────────────────────────────────────
  1   ID            "id"          Yes    Sort by primary key — stable default, oldest/newest inserted
  2   CREATED_AT    "createdAt"   Yes    Sort by creation date — "recently added" lists
  3   SORT_ORDER    "sortOrder"   Yes    Sort by explicit display order — for ordered dropdowns/menus
  4   CODE          "code"        Yes    Sort alphabetically by ISO code — A→Z table ordering
  5   ISO3_CODE     "iso3Code"    No     ISO3 code rarely sorted on its own
──────────────────────────────────────────────────────────────────────────────
Type "yes" to confirm all recommendations, or override with field#=option# (e.g. "3=2, 5=1")
```

STOP and wait for one reply.

---

## Step 5 — Summary & Confirmation

After all fields answered, show the summary table. If the file already EXISTS, also show a Change Summary table comparing current vs proposed state:

```
─── SortField Summary: {Entity}SortField ────────────────────────────────────────

  #   Constant     fieldName    Decision
  ─── ──────────── ──────────── ────────────────
  1   ID           "id"         Include
  2   CREATED_AT   "createdAt"  Include
  3   SORT_ORDER   "sortOrder"  Include
  4   CODE         "code"       Include
  5   NAME         "name"       Exclude

─── Change Summary (existing file vs proposed) ───────────────────────────────────
  Constant     fieldName    Current file    Proposed     Action
  ──────────── ──────────── ─────────────── ──────────── ──────────
  ID           "id"         Include         Include      No change
  CREATED_AT   "createdAt"  Include         Include      No change
  SORT_ORDER   "sortOrder"  Include         Include      No change
  CODE         "code"       Include         Include      No change
  ISO3_CODE    "iso3Code"   Include         Exclude      REMOVE
  PHONE_CODE   "phoneCode"  Include         Exclude      REMOVE
  NAME         "name"       Include         Include      No change  (locale — preserved)

─────────────────────────────────────────────────────────────────────────────────
Apply changes? 1-Yes / 2-No
  - A field number to revisit it (e.g. "5")
```

---

## Step 6 — Template

```java
package com.example.springbackendtemplate1.{module}.model.enums;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public enum {Entity}SortField {

    ID("id"),
    SORT_ORDER("sortOrder"),      // only if included
    CODE("code"),                 // only if included
    CREATED_AT("createdAt");

    private final String fieldName;

    {Entity}SortField(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public static Set<String> allowedFields() {
        return Arrays.stream(values())
                .map({Entity}SortField::getFieldName)
                .collect(Collectors.toSet());
    }
}
```

### Rules
- No lombok `@Getter` — explicit `getFieldName()` method
- `fieldName` = JPA entity field name (camelCase, not snake_case)
- `allowedFields()` — always include; used in ServiceImpl's `ALLOWED_SORT_FIELDS` constant
- Only include constants confirmed YES in questionnaire
- Only include fields that actually exist on the entity

---

## Step 7 — Report format

```
─── Result ──────────────────────────────────────────────────────────────────────
{Entity}SortField : MISSING → CREATED / EXISTS → UPDATED

Constants generated:
  ID          ("id")
  SORT_ORDER  ("sortOrder")
  CODE        ("code")
  CREATED_AT  ("createdAt")
```
