package com.example.resortapplication1.controller;

import com.example.resortapplication1.commons.dto.request.PaginatedRequest;
import com.example.resortapplication1.dto.request.templatepageslots.CreateTemplatePageSlotRequest;
import com.example.resortapplication1.dto.request.templatepageslots.UpdateTemplatePageSlotRequest;
import com.example.resortapplication1.service.TemplatePageSlotService;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/v1/template-page-slots")
public class TemplatePageSlotController {

    private final TemplatePageSlotService templatePageSlotService;

    public TemplatePageSlotController(TemplatePageSlotService templatePageSlotService) {
        this.templatePageSlotService = templatePageSlotService;
    }

    @PostMapping
    public ResponseEntity<?> createTemplatePageSlot(@RequestBody CreateTemplatePageSlotRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(templatePageSlotService.createTemplatePageSlot(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTemplatePageSlot(@PathVariable Long id) {
        return ResponseEntity.ok(templatePageSlotService.getTemplatePageSlot(id));
    }

    @GetMapping
    public ResponseEntity<?> getAllTemplatePageSlots(@ParameterObject PaginatedRequest request) {
        Pageable pageable = request.toPageable(Set.of("id", "slotName", "slotOrder"));
        return ResponseEntity.ok(templatePageSlotService.getAllTemplatePageSlots(pageable));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTemplatePageSlot(@PathVariable Long id, @RequestBody UpdateTemplatePageSlotRequest request) {
        return ResponseEntity.ok(templatePageSlotService.updateTemplatePageSlot(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTemplatePageSlot(@PathVariable Long id) {
        return ResponseEntity.ok(templatePageSlotService.deleteTemplatePageSlot(id));
    }
}
