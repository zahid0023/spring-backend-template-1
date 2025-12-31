package com.example.springbackendtemplate1.auth.service;

import com.example.springbackendtemplate1.auth.dto.request.permission.CreatePermissionRequest;
import com.example.springbackendtemplate1.auth.dto.response.SuccessResponse;
import com.example.springbackendtemplate1.auth.model.enitty.PermissionEntity;
import com.example.springbackendtemplate1.auth.model.enitty.UserEntity;

import java.util.List;
import java.util.Set;

/**
 * Service interface for managing user permissions.
 *
 * <p>This service provides methods to grant permissions to users,
 * either directly using permission names or using permission IDs,
 * while enforcing rules about which permissions can be assigned
 * based on the granter's current permissions.</p>
 */
public interface PermissionService {
    /**
     * Grants the specified permissions to a user.
     *
     * <p>This method assigns a set of permissions (by name) to the given user.
     * It does not involve a granter; typically used for system-level operations
     * such as bootstrapping a super admin.</p>
     *
     * @param userEntity      the user to whom the permissions will be assigned
     * @param permissionNames the set of permission names to assign
     * @throws IllegalArgumentException if any of the permission names do not exist
     */
    void grantPermissions(UserEntity userEntity, Set<String> permissionNames) throws IllegalArgumentException;


    /**
     * Grants the specified permissions to another user.
     *
     * <p>The granter user must have the "ASSIGN_PERMISSIONS" permission and
     * must already possess all permissions they are attempting to assign.
     * If the granter does not meet these criteria, an exception is thrown.</p>
     *
     * @param granter       the user who is granting permissions
     * @param grantee       the user receiving the permissions
     * @param permissionIds the set of permission IDs to assign
     * @throws IllegalArgumentException if the granter lacks "ASSIGN_PERMISSIONS"
     *                                  or tries to assign permissions they do not possess
     */
    void grantPermissions(UserEntity granter, UserEntity grantee, Set<Long> permissionIds) throws IllegalArgumentException;

    SuccessResponse createPermissions(Set<CreatePermissionRequest> permissions);

    List<PermissionEntity> getAllPermissions();
}
