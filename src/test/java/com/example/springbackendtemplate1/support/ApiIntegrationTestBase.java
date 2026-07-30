package com.example.springbackendtemplate1.support;

import com.example.springbackendtemplate1.auth.dto.request.RegistrationRequest;
import com.example.springbackendtemplate1.auth.dto.request.permission.CreatePermissionRequest;
import com.example.springbackendtemplate1.auth.dto.request.role.CreateRoleRequest;
import com.example.springbackendtemplate1.auth.model.enitty.PermissionEntity;
import com.example.springbackendtemplate1.auth.model.enitty.UserEntity;
import com.example.springbackendtemplate1.auth.service.PermissionService;
import com.example.springbackendtemplate1.auth.service.RoleService;
import com.example.springbackendtemplate1.auth.service.UserService;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Base class for full end-to-end API tests: real HTTP through MockMvc, real JWT
 * authentication (no security-test shortcuts), against an in-memory H2 database.
 *
 * <p>{@code @ActiveProfiles("test")} disables {@code AdminBootstrapRunner}
 * (it's {@code @Profile("!test")}) because that runner generates a random,
 * unrecoverable superadmin password logged only at startup. Instead, this base
 * mirrors exactly what that runner does — create the ADMIN role, its 3 bootstrap
 * permissions, register + activate a "superadmin" user — but with a password the
 * test knows, then also grants every permission that exists in the system to it
 * (not just the 3 bootstrap ones), and logs in for real via
 * {@code POST /api/v1/auth/login} to obtain a genuine JWT.
 *
 * <p>Subclasses authenticate requests with {@code .with(asSuperAdmin())}, which
 * attaches a real {@code Authorization: Bearer <token>} header processed by the
 * app's actual {@code JwtAuthenticationFilter} — not a Spring Security test bypass.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public abstract class ApiIntegrationTestBase {

    protected static final String SUPERADMIN_USERNAME = "superadmin";
    protected static final String SUPERADMIN_PASSWORD = "SuperSecretPass1234!";

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    private RoleService roleService;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private UserService userService;

    protected String superAdminAccessToken;

    @BeforeEach
    void bootstrapSuperAdmin() throws Exception {
        CreateRoleRequest adminRole = new CreateRoleRequest();
        adminRole.setName("ADMIN");
        roleService.createRole(adminRole);

        Set<CreatePermissionRequest> bootstrapPermissions = Set.of(
                permissionRequest("CREATE_ADMIN", "Create new admin"),
                permissionRequest("ACTIVATE_ADMIN", "Activate admin account"),
                permissionRequest("ASSIGN_PERMISSIONS", "Assign permissions to admin")
        );
        permissionService.createPermissions(bootstrapPermissions);

        RegistrationRequest registration = new RegistrationRequest();
        registration.setUserName(SUPERADMIN_USERNAME);
        registration.setPassword(SUPERADMIN_PASSWORD);
        registration.setConfirmPassword(SUPERADMIN_PASSWORD);
        userService.registerAdmin(registration);

        UserEntity superAdmin = userService.getUserByUsername(SUPERADMIN_USERNAME);
        userService.activateUser(superAdmin);

        Set<String> everyPermissionName = permissionService.getAllPermissions().stream()
                .map(PermissionEntity::getName)
                .collect(Collectors.toSet());
        permissionService.grantPermissions(superAdmin, everyPermissionName);

        superAdminAccessToken = login();
    }

    private CreatePermissionRequest permissionRequest(String name, String description) {
        CreatePermissionRequest request = new CreatePermissionRequest();
        request.setName(name);
        request.setDescription(description);
        return request;
    }

    protected String login() throws Exception {
        String body = """
                { "user_name": "%s", "password": "%s" }
                """.formatted(ApiIntegrationTestBase.SUPERADMIN_USERNAME, ApiIntegrationTestBase.SUPERADMIN_PASSWORD);

        MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andReturn();

        return JsonPath.read(result.getResponse().getContentAsString(), "$.access_token");
    }

    /**
     * Attaches a real, filter-chain-validated JWT for the bootstrapped superadmin.
     */
    protected RequestPostProcessor asSuperAdmin() {
        return request -> {
            request.addHeader("Authorization", "Bearer " + superAdminAccessToken);
            return request;
        };
    }
}
