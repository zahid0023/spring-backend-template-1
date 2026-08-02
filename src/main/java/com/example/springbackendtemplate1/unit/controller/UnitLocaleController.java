package com.example.springbackendtemplate1.unit.controller;

import com.example.springbackendtemplate1.unit.dto.request.unit.locale.CreateUnitLocaleRequest;
import com.example.springbackendtemplate1.unit.dto.request.unit.locale.UpdateUnitLocaleRequest;
import com.example.springbackendtemplate1.unit.model.entity.UnitEntity;
import com.example.springbackendtemplate1.unit.model.entity.UnitLocaleEntity;
import com.example.springbackendtemplate1.commons.dto.request.PaginatedRequest;
import com.example.springbackendtemplate1.locale.model.entity.LocaleEntity;
import com.example.springbackendtemplate1.unit.service.UnitLocaleService;
import com.example.springbackendtemplate1.unit.service.UnitService;
import com.example.springbackendtemplate1.locale.service.LocaleService;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/units/{unit-id}/locales")
public class UnitLocaleController {

    private final UnitService unitService;
    private final UnitLocaleService unitLocaleService;
    private final LocaleService localeService;

    public UnitLocaleController(UnitService unitService,
                                UnitLocaleService unitLocaleService,
                                LocaleService localeService) {
        this.unitService = unitService;
        this.unitLocaleService = unitLocaleService;
        this.localeService = localeService;
    }

    @GetMapping
    public ResponseEntity<?> getAll(
            @PathVariable("unit-id") Long unitId,
            @RequestParam(value = "localeCode", required = false) String localeCode,
            @ParameterObject PaginatedRequest paginatedRequest) {
        unitService.getEntityById(unitId);
        return ResponseEntity.ok(unitLocaleService.getAll(unitId, localeCode, paginatedRequest));
    }

    @PostMapping
    public ResponseEntity<?> create(
            @PathVariable("unit-id") Long unitId,
            @Valid @RequestBody CreateUnitLocaleRequest request) {
        UnitEntity unitEntity = unitService.getEntityById(unitId);
        LocaleEntity localeEntity = localeService.getEntityById(request.getLocaleId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(unitLocaleService.create(request, unitEntity, localeEntity));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable("unit-id") Long unitId,
            @PathVariable Long id,
            @Valid @RequestBody UpdateUnitLocaleRequest request) {
        UnitLocaleEntity entity = unitLocaleService.getEntityById(unitId, id);
        return ResponseEntity.ok(unitLocaleService.update(entity, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(
            @PathVariable("unit-id") Long unitId,
            @PathVariable Long id) {
        UnitLocaleEntity entity = unitLocaleService.getEntityById(unitId, id);
        return ResponseEntity.ok(unitLocaleService.delete(entity));
    }
}
