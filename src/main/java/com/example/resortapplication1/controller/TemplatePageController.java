package com.example.resortapplication1.controller;

import com.example.resortapplication1.commons.dto.request.PaginatedRequest;
import com.example.resortapplication1.dto.request.templatepages.CreateTemplatePageRequest;
import com.example.resortapplication1.dto.request.templatepages.UpdateTemplatePageRequest;
import com.example.resortapplication1.service.TemplatePageService;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/v1/template-pages")
public class TemplatePageController {

    private final TemplatePageService templatePageService;

    public TemplatePageController(TemplatePageService templatePageService) {
        this.templatePageService = templatePageService;
    }

    @PostMapping
    public ResponseEntity<?> createTemplatePage(@RequestBody CreateTemplatePageRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(templatePageService.createTemplatePage(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTemplatePage(@PathVariable Long id) {
        return ResponseEntity.ok(templatePageService.getTemplatePage(id));
    }

    @GetMapping
    public ResponseEntity<?> getAllTemplatePages(@ParameterObject PaginatedRequest request) {
        Pageable pageable = request.toPageable(Set.of("id", "pageName", "pageSlug", "pageOrder"));
        return ResponseEntity.ok(templatePageService.getAllTemplatePages(pageable));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTemplatePage(@PathVariable Long id, @RequestBody UpdateTemplatePageRequest request) {
        return ResponseEntity.ok(templatePageService.updateTemplatePage(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTemplatePage(@PathVariable Long id) {
        return ResponseEntity.ok(templatePageService.deleteTemplatePage(id));
    }
}
