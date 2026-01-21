package com.example.springbackendtemplate1.auth.config;

import com.example.springbackendtemplate1.auth.dto.request.permission.CreatePermissionRequest;
import com.example.springbackendtemplate1.auth.dto.request.role.CreateRoleRequest;
import com.example.springbackendtemplate1.auth.dto.request.RegistrationRequest;
import com.example.springbackendtemplate1.auth.model.enitty.UserEntity;
import com.example.springbackendtemplate1.auth.service.PermissionService;
import com.example.springbackendtemplate1.auth.service.RoleService;
import com.example.springbackendtemplate1.auth.service.UserService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Bootstraps the initial super admin user at an application startup.
 *
 * <p>This runner checks if any user with the ADMIN role exists in the system.
 * If none exists, it automatically creates a "superadmin" user with a randomly
 * generated temporary password and grants it the following permissions:
 * <ul>
 *     <li>CREATE_ADMIN</li>
 *     <li>ACTIVATE_ADMIN</li>
 *     <li>ASSIGN_PERMISSIONS</li>
 * </ul>
 * This ensures that the system has at least one fully privileged admin to manage users and permissions.</p>
 *
 * <p>The output, including the temporary password, is logged once at startup.
 * It is strongly recommended to change this password immediately after the first login.</p>
 *
 * <p>This runner is only active in non-test profiles (controlled by {@link Profile}).</p>
 */
@Component
@Profile("!test")
@Slf4j
public class AdminBootstrapRunner implements ApplicationRunner {

    private final UserService userService;
    private final PermissionService permissionService;
    private final RoleService roleService;
    private final String SUPER_ADMIN = "superadmin";
    Map<String, String> SUPER_ADMIN_PERMISSIONS = Map.of(
            "CREATE_ADMIN", "Create new admin",
            "ACTIVATE_ADMIN", "Activate admin account",
            "ASSIGN_PERMISSIONS", "Assign permissions to admin"
    );

    /**
     * Constructs the AdminBootstrapRunner.
     *
     * @param userService       the user service to manage users
     * @param permissionService the permission service to assign permissions
     */
    public AdminBootstrapRunner(
            UserService userService,
            RoleService roleService,
            PermissionService permissionService
    ) {
        this.userService = userService;
        this.roleService = roleService;
        this.permissionService = permissionService;
    }

    /**
     * Runs the bootstrap process at application startup.
     *
     * <p>If no admin exists, creates a super admin with a temporary password,
     * activates the account, and assigns all required admin permissions.</p>
     *
     * @param args application arguments (ignored)
     */
    @Transactional
    @Override
    public void run(@NonNull ApplicationArguments args) {

        // If SUPER_ADMIN already exists, do nothing
        if (userService.existsSuperAdmin(SUPER_ADMIN)) {
            log.info("SUPER_ADMIN already exists. Skipping bootstrap.");
            return;
        }

        createAdminRole();
        createAdminPermissions();
        createSuperAdmin();
        grantAdminPermissionsToSuperAdmin();
        userService.activateUser(userService.getUserByUsername(SUPER_ADMIN));
    }

    private void createAdminRole() {
        CreateRoleRequest request = new CreateRoleRequest();
        request.setName("ADMIN");
        roleService.createRole(request);
    }

    private void createAdminPermissions() {
        Set<CreatePermissionRequest> permissionRequests = SUPER_ADMIN_PERMISSIONS.entrySet().stream()
                .map(entry -> {
                    CreatePermissionRequest request = new CreatePermissionRequest();
                    request.setName(entry.getKey());
                    request.setDescription(entry.getValue());
                    return request;
                })
                .collect(Collectors.toSet());
        permissionService.createPermissions(permissionRequests);
    }

    private void createSuperAdmin() {
        String rawPassword = generateSecurePassword();

        RegistrationRequest request = new RegistrationRequest();
        request.setUserName(SUPER_ADMIN);
        request.setPassword(rawPassword);
        request.setConfirmPassword(rawPassword);

        userService.registerAdmin(request);

        // Output ONLY ONCE
        log.warn("==============================================");
        log.warn("BOOTSTRAP ADMIN CREATED");
        log.warn("Username: {}", SUPER_ADMIN);
        log.warn("Temporary Password: {}", rawPassword);
        log.warn("CHANGE THIS PASSWORD IMMEDIATELY");
        log.warn("==============================================");

    }

    private void grantAdminPermissionsToSuperAdmin() {
        UserEntity userEntity = userService.getUserByUsername(SUPER_ADMIN);
        Set<String> permissionNames = SUPER_ADMIN_PERMISSIONS.keySet();
        permissionService.grantPermissions(userEntity, permissionNames);
    }

    /**
     * Generates a secure random password for the super admin.
     *
     * @return a randomly generated password string
     */
    private String generateSecurePassword() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
