package com.example.resortapplication1.controller;

import com.example.resortapplication1.commons.dto.request.PaginatedRequest;
import com.example.resortapplication1.dto.request.uiblockcategories.CreateUiBlockCategoryRequest;
import com.example.resortapplication1.dto.request.uiblockcategories.UpdateUiBlockCategoryRequest;
import com.example.resortapplication1.service.UiBlockCategoryService;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/v1/ui-block-categories")
public class UiBlockCategoryController {

    private final UiBlockCategoryService uiBlockCategoryService;

    public UiBlockCategoryController(UiBlockCategoryService uiBlockCategoryService) {
        this.uiBlockCategoryService = uiBlockCategoryService;
    }

    @PostMapping
    public ResponseEntity<?> createUiBlockCategory(@RequestBody CreateUiBlockCategoryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(uiBlockCategoryService.createUiBlockCategory(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUiBlockCategory(@PathVariable Long id) {
        return ResponseEntity.ok(uiBlockCategoryService.getUiBlockCategory(id));
    }

    @GetMapping
    public ResponseEntity<?> getAllUiBlockCategories(@ParameterObject PaginatedRequest request) {
        Pageable pageable = request.toPageable(Set.of("id", "key", "name"));
        return ResponseEntity.ok(uiBlockCategoryService.getAllUiBlockCategories(pageable));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUiBlockCategory(@PathVariable Long id, @RequestBody UpdateUiBlockCategoryRequest request) {
        return ResponseEntity.ok(uiBlockCategoryService.updateUiBlockCategory(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUiBlockCategory(@PathVariable Long id) {
        return ResponseEntity.ok(uiBlockCategoryService.deleteUiBlockCategory(id));
    }
}
