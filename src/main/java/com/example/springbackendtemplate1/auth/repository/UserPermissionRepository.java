package com.example.springbackendtemplate1.auth.repository;

import com.example.springbackendtemplate1.auth.model.enitty.PermissionEntity;
import com.example.springbackendtemplate1.auth.model.enitty.UserPermissionEntity;
import com.example.springbackendtemplate1.auth.model.enitty.UserEntity;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserPermissionRepository extends JpaRepository<@NonNull UserPermissionEntity, @NonNull Long> {
    boolean existsByUserAndPermission(
            UserEntity userEntity,
            PermissionEntity permissionEntity
    );
}
