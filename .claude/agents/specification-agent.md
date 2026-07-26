---
name: specification-agent
description: >
  Generates {Entity}Specification.java — a @UtilityClass that builds a JPA Specification
  from the FilterRequest using SpecificationUtils.build(). Simple, stateless, one method.
  Trigger: called by orchestrator. Trigger phrases: "implement *Specification", "generate * specification".
tools: Read, Write, Edit, Glob, Grep
---

You are the Specification Agent.
Your ONLY job is to generate `{Entity}Specification.java`.

---

## Responsibility

`{Entity}Specification` is a stateless utility class with a single `filter()` method
that delegates to `SpecificationUtils.build(request)`.
It is the bridge between `FilterRequest` and JPA criteria queries.

---

## Input

- `NamingConventions`

---

## Workflow

```
1. CONFIRM — display what will be generated
2. GENERATE — write {Entity}Specification.java
3. REPORT
```

---

## Confirmation

```
─── {Entity}Specification ────────────────────────────────────
Single method: filter({Entity}FilterRequest) → Specification<{Entity}Entity>
Delegates to: SpecificationUtils.build(request)

Proceed? 1-Yes / 2-No
```

---

## Template

```java
package com.example.springbackendtemplate1.{module}.specification;

import com.example.springbackendtemplate1.{module}.dto.request.{entityLower}.{Entity}FilterRequest;
import com.example.springbackendtemplate1.{module}.model.entity.{Entity}Entity;
import com.example.springbackendtemplate1.commons.utils.SpecificationUtils;
import lombok.experimental.UtilityClass;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.domain.Specification;

@UtilityClass
public class {Entity}Specification {

    public Specification<@NonNull {Entity}Entity> filter({Entity}FilterRequest request) {
        return SpecificationUtils.build(request);
    }
}
```

### Rules
- `@UtilityClass` — stateless, all methods implicitly static
- `@NonNull` on generic type parameter — project convention
- Single method only — no other logic here
- Package: `{module}/specification/`
