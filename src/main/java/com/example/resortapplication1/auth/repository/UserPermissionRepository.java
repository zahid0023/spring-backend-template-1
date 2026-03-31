package com.example.resortapplication1.auth.repository;

import com.example.resortapplication1.auth.model.enitty.PermissionEntity;
import com.example.resortapplication1.auth.model.enitty.UserPermissionEntity;
import com.example.resortapplication1.auth.model.enitty.UserEntity;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserPermissionRepository extends JpaRepository<@NonNull UserPermissionEntity, @NonNull Long> {
    boolean existsByUserAndPermission(
            UserEntity userEntity,
            PermissionEntity permissionEntity
    );
}
