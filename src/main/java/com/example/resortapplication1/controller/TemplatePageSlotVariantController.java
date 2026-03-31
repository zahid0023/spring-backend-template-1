package com.example.resortapplication1.controller;

import com.example.resortapplication1.commons.dto.request.PaginatedRequest;
import com.example.resortapplication1.dto.request.templatepageslotvariants.CreateTemplatePageSlotVariantRequest;
import com.example.resortapplication1.dto.request.templatepageslotvariants.UpdateTemplatePageSlotVariantRequest;
import com.example.resortapplication1.service.TemplatePageSlotVariantService;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/v1/template-page-slot-variants")
public class TemplatePageSlotVariantController {

    private final TemplatePageSlotVariantService templatePageSlotVariantService;

    public TemplatePageSlotVariantController(TemplatePageSlotVariantService templatePageSlotVariantService) {
        this.templatePageSlotVariantService = templatePageSlotVariantService;
    }

    @PostMapping
    public ResponseEntity<?> createTemplatePageSlotVariant(@RequestBody CreateTemplatePageSlotVariantRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(templatePageSlotVariantService.createTemplatePageSlotVariant(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTemplatePageSlotVariant(@PathVariable Long id) {
        return ResponseEntity.ok(templatePageSlotVariantService.getTemplatePageSlotVariant(id));
    }

    @GetMapping
    public ResponseEntity<?> getAllTemplatePageSlotVariants(@ParameterObject PaginatedRequest request) {
        Pageable pageable = request.toPageable(Set.of("id", "displayOrder"));
        return ResponseEntity.ok(templatePageSlotVariantService.getAllTemplatePageSlotVariants(pageable));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTemplatePageSlotVariant(@PathVariable Long id, @RequestBody UpdateTemplatePageSlotVariantRequest request) {
        return ResponseEntity.ok(templatePageSlotVariantService.updateTemplatePageSlotVariant(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTemplatePageSlotVariant(@PathVariable Long id) {
        return ResponseEntity.ok(templatePageSlotVariantService.deleteTemplatePageSlotVariant(id));
    }
}
