package com.example.springbackendtemplate1.auth.controller;

import com.example.springbackendtemplate1.auth.dto.request.permission.AssignPermissionRequest;
import com.example.springbackendtemplate1.auth.dto.request.RegistrationRequest;
import com.example.springbackendtemplate1.auth.model.enitty.UserEntity;
import com.example.springbackendtemplate1.auth.service.PermissionService;
import com.example.springbackendtemplate1.auth.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admins")
public class AdminController {
    private final UserService userService;
    private final PermissionService permissionService;

    public AdminController(UserService userService,
                           PermissionService permissionService) {
        this.userService = userService;
        this.permissionService = permissionService;
    }

    @PostMapping()
    @PreAuthorize("hasAuthority('CREATE_ADMIN')")
    public ResponseEntity<?> createAdmin(@RequestBody RegistrationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.registerAdmin(request));
    }

    @PreAuthorize("hasAuthority('ACTIVATE_ADMIN')")
    @PutMapping("/{admin-id}/activate")
    public ResponseEntity<?> activateAdmin(@PathVariable("admin-id") Long adminId) {
        UserEntity userEntity = userService.getUserById(adminId);
        return ResponseEntity.ok(userService.activateAdmin(userEntity));
    }

    @PreAuthorize("hasAuthority('ASSIGN_PERMISSIONS')")
    @PostMapping("/{admin-id}/permissions")
    public ResponseEntity<?> assignPermissions(
            @PathVariable("admin-id") Long adminId,
            @RequestBody AssignPermissionRequest request) {

        UserEntity granter = userService.getAuthenticatedUserEntity(); // Assume method to get currently authenticated user
        UserEntity grantee = userService.getUserById(adminId);

        permissionService.grantPermissions(granter, grantee, request.getPermissionIds());

        return ResponseEntity.ok("Permissions assigned successfully");
    }
}
