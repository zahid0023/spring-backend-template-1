package com.example.springbackendtemplate1.unit.controller;

import com.example.springbackendtemplate1.unit.dto.request.unit.UnitFilterRequest;
import com.example.springbackendtemplate1.unit.dto.request.unit.CreateUnitRequest;
import com.example.springbackendtemplate1.unit.dto.request.unit.UpdateUnitRequest;
import com.example.springbackendtemplate1.unit.model.entity.UnitEntity;
import com.example.springbackendtemplate1.locale.model.entity.LocaleEntity;
import com.example.springbackendtemplate1.unit.service.UnitService;
import com.example.springbackendtemplate1.unit.model.entity.UnitTypeEntity;
import com.example.springbackendtemplate1.unit.service.UnitTypeService;
import com.example.springbackendtemplate1.locale.service.LocaleService;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/units")
public class UnitController {

    private final UnitService unitService;
    private final UnitTypeService unitTypeService;
    private final LocaleService localeService;

    public UnitController(UnitService unitService,
                          UnitTypeService unitTypeService,
                          LocaleService localeService) {
        this.unitService = unitService;
        this.unitTypeService = unitTypeService;
        this.localeService = localeService;
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody CreateUnitRequest request) {
        UnitTypeEntity unitTypeEntity = unitTypeService.getEntityById(request.getUnitTypeId());
        LocaleEntity localeEntity = localeService.getEntityByCode("en");
        return ResponseEntity.status(HttpStatus.CREATED).body(unitService.create(request, unitTypeEntity, localeEntity));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return ResponseEntity.ok(unitService.getById(id));
    }

    @GetMapping
    public ResponseEntity<?> getAll(@Valid @ParameterObject UnitFilterRequest request) {
        return ResponseEntity.ok(unitService.getAll(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUnitRequest request) {
        UnitEntity entity = unitService.getEntityById(id);
        return ResponseEntity.ok(unitService.update(entity, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        UnitEntity entity = unitService.getEntityById(id);
        return ResponseEntity.ok(unitService.delete(entity));
    }
}
