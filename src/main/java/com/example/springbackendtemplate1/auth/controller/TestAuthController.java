package com.example.springbackendtemplate1.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

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

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/user-only")
    public ResponseEntity<?> userOnlyAction() {
        return ResponseEntity.ok("User access granted");
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/current-time")
    public ResponseEntity<?> getCurrentTime() {
        return ResponseEntity.ok("Current time is " + new Date());
    }
}
