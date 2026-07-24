package com.example.springbackendtemplate1.auth.repository;

import com.example.springbackendtemplate1.auth.model.enitty.PermissionEntity;
import com.example.springbackendtemplate1.auth.model.enitty.UserEntity;
import com.example.springbackendtemplate1.auth.model.enitty.UserPermissionEntity;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

@SuppressWarnings("unused")
public interface UserPermissionRepository extends
        JpaRepository<@NonNull UserPermissionEntity, @NonNull Long>,
        JpaSpecificationExecutor<@NonNull UserPermissionEntity> {

    Optional<UserPermissionEntity> findByIdAndIsActiveAndIsDeleted(
            Long id,
            Boolean isActive,
            Boolean isDeleted
    );

    List<UserPermissionEntity> findAllByIsActiveAndIsDeleted(
            Boolean isActive,
            Boolean isDeleted
    );

    Page<UserPermissionEntity> findAllByIsActiveAndIsDeleted(
            Boolean isActive,
            Boolean isDeleted,
            Pageable pageable
    );

    List<UserPermissionEntity> findAllByUserEntityAndIsActiveAndIsDeleted(
            UserEntity userEntity,
            Boolean isActive,
            Boolean isDeleted
    );

    List<UserPermissionEntity> findAllByPermissionEntityAndIsActiveAndIsDeleted(
            PermissionEntity permissionEntity,
            Boolean isActive,
            Boolean isDeleted
    );

}
