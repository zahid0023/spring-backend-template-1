package com.example.springbackendtemplate1.unit.controller;

import com.example.springbackendtemplate1.unit.dto.request.unittype.UnitTypeFilterRequest;
import com.example.springbackendtemplate1.unit.dto.request.unittype.CreateUnitTypeRequest;
import com.example.springbackendtemplate1.unit.dto.request.unittype.UpdateUnitTypeRequest;
import com.example.springbackendtemplate1.unit.model.entity.UnitTypeEntity;
import com.example.springbackendtemplate1.locale.model.entity.LocaleEntity;
import com.example.springbackendtemplate1.unit.service.UnitTypeService;
import com.example.springbackendtemplate1.locale.service.LocaleService;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/unit-types")
public class UnitTypeController {

    private final UnitTypeService unitTypeService;
    private final LocaleService localeService;

    public UnitTypeController(UnitTypeService unitTypeService,
                              LocaleService localeService) {
        this.unitTypeService = unitTypeService;
        this.localeService = localeService;
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody CreateUnitTypeRequest request) {
        LocaleEntity localeEntity = localeService.getEntityByCode("en");
        return ResponseEntity.status(HttpStatus.CREATED).body(unitTypeService.create(request, localeEntity));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return ResponseEntity.ok(unitTypeService.getById(id));
    }

    @GetMapping
    public ResponseEntity<?> getAll(@Valid @ParameterObject UnitTypeFilterRequest request) {
        return ResponseEntity.ok(unitTypeService.getAll(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUnitTypeRequest request) {
        UnitTypeEntity entity = unitTypeService.getEntityById(id);
        return ResponseEntity.ok(unitTypeService.update(entity, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        UnitTypeEntity entity = unitTypeService.getEntityById(id);
        return ResponseEntity.ok(unitTypeService.delete(entity));
    }
}
