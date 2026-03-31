package com.example.resortapplication1.controller;

import com.example.resortapplication1.commons.dto.request.PaginatedRequest;
import com.example.resortapplication1.dto.request.pagetypes.CreatePageTypeRequest;
import com.example.resortapplication1.dto.request.pagetypes.UpdatePageTypeRequest;
import com.example.resortapplication1.service.PageTypeService;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/v1/page-types")
public class PageTypeController {

    private final PageTypeService pageTypeService;

    public PageTypeController(PageTypeService pageTypeService) {
        this.pageTypeService = pageTypeService;
    }

    @PostMapping
    public ResponseEntity<?> createPageType(@RequestBody CreatePageTypeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pageTypeService.createPageType(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPageType(@PathVariable Long id) {
        return ResponseEntity.ok(pageTypeService.getPageType(id));
    }

    @GetMapping
    public ResponseEntity<?> getAllPageTypes(@ParameterObject PaginatedRequest request) {
        Pageable pageable = request.toPageable(Set.of("id", "key", "name"));
        return ResponseEntity.ok(pageTypeService.getAllPageTypes(pageable));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updatePageType(@PathVariable Long id, @RequestBody UpdatePageTypeRequest request) {
        return ResponseEntity.ok(pageTypeService.updatePageType(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePageType(@PathVariable Long id) {
        return ResponseEntity.ok(pageTypeService.deletePageType(id));
    }
}
