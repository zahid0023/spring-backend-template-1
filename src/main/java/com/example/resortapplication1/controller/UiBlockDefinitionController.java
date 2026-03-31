package com.example.resortapplication1.controller;

import com.example.resortapplication1.commons.dto.request.PaginatedRequest;
import com.example.resortapplication1.dto.request.uiblockdefinitions.CreateUiBlockDefinitionRequest;
import com.example.resortapplication1.dto.request.uiblockdefinitions.UpdateUiBlockDefinitionRequest;
import com.example.resortapplication1.service.UiBlockDefinitionService;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/v1/ui-block-definitions")
public class UiBlockDefinitionController {

    private final UiBlockDefinitionService uiBlockDefinitionService;

    public UiBlockDefinitionController(UiBlockDefinitionService uiBlockDefinitionService) {
        this.uiBlockDefinitionService = uiBlockDefinitionService;
    }

    @PostMapping
    public ResponseEntity<?> createUiBlockDefinition(@RequestBody CreateUiBlockDefinitionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(uiBlockDefinitionService.createUiBlockDefinition(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUiBlockDefinition(@PathVariable Long id) {
        return ResponseEntity.ok(uiBlockDefinitionService.getUiBlockDefinition(id));
    }

    @GetMapping
    public ResponseEntity<?> getAllUiBlockDefinitions(@ParameterObject PaginatedRequest request) {
        Pageable pageable = request.toPageable(Set.of("id", "uiBlockKey", "name"));
        return ResponseEntity.ok(uiBlockDefinitionService.getAllUiBlockDefinitions(pageable));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUiBlockDefinition(@PathVariable Long id, @RequestBody UpdateUiBlockDefinitionRequest request) {
        return ResponseEntity.ok(uiBlockDefinitionService.updateUiBlockDefinition(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUiBlockDefinition(@PathVariable Long id) {
        return ResponseEntity.ok(uiBlockDefinitionService.deleteUiBlockDefinition(id));
    }
}
