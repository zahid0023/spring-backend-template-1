package com.example.springbackendtemplate1.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class TestAuthController {
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin-only")
    public ResponseEntity<?> adminOnlyAction() {
        return ResponseEntity.ok("Admin access granted");
    }

    @PostMapping("/admin-only-create")
    @PreAuthorize("hasRole('ADMIN') AND hasAuthority('CREATE_ADMIN')")
    public ResponseEntity<?> adminOnlyActionCreate() {
        return ResponseEntity.ok("Admin access granted");
    }

    @PreAuthorize("hasRole('MERCHANT')")
    @PostMapping("/merchant-only")
    public ResponseEntity<?> merchantOnlyAction() {
        return ResponseEntity.ok("Merchant access granted");
    }
}
