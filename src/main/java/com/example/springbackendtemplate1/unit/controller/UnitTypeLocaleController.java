package com.example.springbackendtemplate1.unit.controller;

import com.example.springbackendtemplate1.unit.dto.request.unittype.locale.CreateUnitTypeLocaleRequest;
import com.example.springbackendtemplate1.unit.dto.request.unittype.locale.UpdateUnitTypeLocaleRequest;
import com.example.springbackendtemplate1.unit.model.entity.UnitTypeEntity;
import com.example.springbackendtemplate1.unit.model.entity.UnitTypeLocaleEntity;
import com.example.springbackendtemplate1.commons.dto.request.PaginatedRequest;
import com.example.springbackendtemplate1.locale.model.entity.LocaleEntity;
import com.example.springbackendtemplate1.unit.service.UnitTypeLocaleService;
import com.example.springbackendtemplate1.unit.service.UnitTypeService;
import com.example.springbackendtemplate1.locale.service.LocaleService;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/unit-types/{unit-type-id}/locales")
public class UnitTypeLocaleController {

    private final UnitTypeService unitTypeService;
    private final UnitTypeLocaleService unitTypeLocaleService;
    private final LocaleService localeService;

    public UnitTypeLocaleController(UnitTypeService unitTypeService,
                                    UnitTypeLocaleService unitTypeLocaleService,
                                    LocaleService localeService) {
        this.unitTypeService = unitTypeService;
        this.unitTypeLocaleService = unitTypeLocaleService;
        this.localeService = localeService;
    }

    @GetMapping
    public ResponseEntity<?> getAll(
            @PathVariable("unit-type-id") Long unitTypeId,
            @RequestParam(value = "localeCode", required = false) String localeCode,
            @ParameterObject PaginatedRequest paginatedRequest) {
        unitTypeService.getEntityById(unitTypeId);
        return ResponseEntity.ok(unitTypeLocaleService.getAll(unitTypeId, localeCode, paginatedRequest));
    }

    @PostMapping
    public ResponseEntity<?> create(
            @PathVariable("unit-type-id") Long unitTypeId,
            @Valid @RequestBody CreateUnitTypeLocaleRequest request) {
        UnitTypeEntity unitTypeEntity = unitTypeService.getEntityById(unitTypeId);
        LocaleEntity localeEntity = localeService.getEntityById(request.getLocaleId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(unitTypeLocaleService.create(request, unitTypeEntity, localeEntity));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable("unit-type-id") Long unitTypeId,
            @PathVariable Long id,
            @Valid @RequestBody UpdateUnitTypeLocaleRequest request) {
        UnitTypeLocaleEntity entity = unitTypeLocaleService.getEntityById(unitTypeId, id);
        return ResponseEntity.ok(unitTypeLocaleService.update(entity, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(
            @PathVariable("unit-type-id") Long unitTypeId,
            @PathVariable Long id) {
        UnitTypeLocaleEntity entity = unitTypeLocaleService.getEntityById(unitTypeId, id);
        return ResponseEntity.ok(unitTypeLocaleService.delete(entity));
    }
}
