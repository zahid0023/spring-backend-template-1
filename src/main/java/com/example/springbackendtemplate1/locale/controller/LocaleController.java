package com.example.springbackendtemplate1.locale.controller;

import com.example.springbackendtemplate1.locale.dto.request.locale.CreateLocaleRequest;
import com.example.springbackendtemplate1.locale.dto.request.locale.LocaleFilterRequest;
import com.example.springbackendtemplate1.locale.dto.request.locale.UpdateLocaleRequest;
import com.example.springbackendtemplate1.locale.model.entity.LocaleEntity;
import com.example.springbackendtemplate1.locale.service.LocaleService;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/locales")
public class LocaleController {

    private final LocaleService localeService;

    public LocaleController(LocaleService localeService) {
        this.localeService = localeService;
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody CreateLocaleRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(localeService.create(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return ResponseEntity.ok(localeService.getById(id));
    }

    @GetMapping
    public ResponseEntity<?> getAll(@Valid @ParameterObject LocaleFilterRequest request) {
        return ResponseEntity.ok(localeService.getAll(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateLocaleRequest request) {
        LocaleEntity entity = localeService.getEntityById(id);
        return ResponseEntity.ok(localeService.update(entity, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        LocaleEntity entity = localeService.getEntityById(id);
        return ResponseEntity.ok(localeService.delete(entity));
    }
}
