package com.example.springbackendtemplate1.auth.serviceImpl;

import com.example.springbackendtemplate1.auth.dto.request.permission.CreatePermissionRequest;
import com.example.springbackendtemplate1.auth.dto.response.SuccessResponse;
import com.example.springbackendtemplate1.auth.model.enitty.PermissionEntity;
import com.example.springbackendtemplate1.auth.model.enitty.UserEntity;
import com.example.springbackendtemplate1.auth.model.enitty.UserPermissionEntity;
import com.example.springbackendtemplate1.auth.model.mapper.PermissionMapper;
import com.example.springbackendtemplate1.auth.repository.PermissionRepository;
import com.example.springbackendtemplate1.auth.repository.UserPermissionRepository;
import com.example.springbackendtemplate1.auth.service.PermissionService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PermissionServiceImpl implements PermissionService {
    private final PermissionRepository permissionRepository;
    private final UserPermissionRepository userPermissionRepository;

    public PermissionServiceImpl(PermissionRepository permissionRepository,
                                 UserPermissionRepository userPermissionRepository) {
        this.permissionRepository = permissionRepository;
        this.userPermissionRepository = userPermissionRepository;
    }

    @Transactional
    @Override
    public void grantPermissions(UserEntity userEntity, Set<String> permissionNames) throws IllegalArgumentException {
        Set<PermissionEntity> permissionEntities = validateAndFetchPermissionsByName(permissionNames);
        assignPermissionsToUser(userEntity, permissionEntities);
    }

    @Transactional
    @Override
    public void grantPermissions(UserEntity granter, UserEntity grantee, Set<Long> permissionIds) throws IllegalArgumentException {
        Set<PermissionEntity> permissionEntities = validateAndFetchPermissionsById(permissionIds);
        Set<String> unauthorized = getUnauthorizedPermissions(granter, permissionEntities);

        if (!unauthorized.isEmpty()) {
            throw new IllegalArgumentException("You are not allowed to assign these permissions: " + unauthorized);
        }

        assignPermissionsToUser(grantee, permissionEntities);
    }

    /**
     * Validates that all permission names exist in the database.
     * Returns the corresponding PermissionEntity set if valid.
     * Throws IllegalArgumentException if any name is invalid.
     */
    private Set<PermissionEntity> validateAndFetchPermissionsByName(Set<String> permissionNames) {
        Set<PermissionEntity> permissionEntities = permissionRepository.findAllByNameIn(permissionNames);

        // Check for missing permissions
        if (permissionEntities.size() != permissionNames.size()) {
            Set<String> foundNames = permissionEntities.stream()
                    .map(PermissionEntity::getName)
                    .collect(Collectors.toSet());

            Set<String> missingNames = new HashSet<>(permissionNames);
            missingNames.removeAll(foundNames);

            log.error("Invalid permission names: {}", missingNames);
            throw new IllegalArgumentException("Unknown permissions: " + missingNames);
        }

        return permissionEntities;
    }

    /**
     * Validates that all permission ids exist in the database.
     * Returns the corresponding PermissionEntity set if valid.
     * Throws IllegalArgumentException if any id is invalid.
     */
    private Set<PermissionEntity> validateAndFetchPermissionsById(Set<Long> permissionIds) {
        Set<PermissionEntity> permissionEntities = permissionRepository.findAllByIdIn(permissionIds);

        // Check for missing permissions
        if (permissionEntities.size() != permissionIds.size()) {
            Set<Long> foundIds = permissionEntities.stream()
                    .map(PermissionEntity::getId)
                    .collect(Collectors.toSet());

            Set<Long> missingIds = new HashSet<>(permissionIds);
            missingIds.removeAll(foundIds);

            log.error("Invalid permission ids: {}", missingIds);
            throw new IllegalArgumentException("Unknown permissions: " + missingIds);
        }

        return permissionEntities;
    }

    /**
     * Checks if the granter can assign all the requested permissions to another user.
     * Returns the set of permission names that the granter is not allowed to assign.
     * If the returned set is empty, all permissions can be assigned.
     */
    private Set<String> getUnauthorizedPermissions(UserEntity granter, Set<PermissionEntity> permissionsToAssign) {
        // Check if granter has ASSIGN_PERMISSIONS
        boolean hasAssignPermission = granter.getUserPermissions().stream()
                .filter(UserPermissionEntity::getIsActive)
                .anyMatch(up -> "ASSIGN_PERMISSIONS".equals(up.getPermission().getName()));

        if (!hasAssignPermission) {
            // If they don't have ASSIGN_PERMISSIONS at all, all permissions are unauthorized
            return permissionsToAssign.stream()
                    .map(PermissionEntity::getName)
                    .collect(Collectors.toSet());
        }

        // Collect all permissions that the granter actually has
        Set<Long> granterPermissionIds = granter.getUserPermissions().stream()
                .filter(UserPermissionEntity::getIsActive)
                .map(up -> up.getPermission().getId())
                .collect(Collectors.toSet());

        // Find permissions that are not in granter's set
        return permissionsToAssign.stream()
                .filter(p -> !granterPermissionIds.contains(p.getId()))
                .map(PermissionEntity::getName)
                .collect(Collectors.toSet());
    }

    private void assignPermissionsToUser(UserEntity userEntity, Set<PermissionEntity> permissionEntities) {
        for (PermissionEntity permission : permissionEntities) {
            boolean exists =
                    userPermissionRepository
                            .existsByUserAndPermission(userEntity, permission);

            if (!exists) {
                UserPermissionEntity up = new UserPermissionEntity();
                up.setUser(userEntity);
                up.setPermission(permission);
                up.setIsActive(true);

                userPermissionRepository.save(up);
            }
        }
    }

    @Override
    public SuccessResponse createPermissions(Set<CreatePermissionRequest> permissions) {
        List<PermissionEntity> permissionEntityList = permissions.stream()
                .map(PermissionMapper::fromRequest)
                .toList();

        permissionRepository.saveAll(permissionEntityList);
        return new SuccessResponse(true, 1L);
    }

    @Override
    public List<PermissionEntity> getAllPermissions() {
        return permissionRepository.findAll();
    }
}
