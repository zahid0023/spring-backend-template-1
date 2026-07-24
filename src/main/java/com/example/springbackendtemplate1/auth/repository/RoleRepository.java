package com.example.springbackendtemplate1.auth.repository;

import com.example.springbackendtemplate1.auth.model.enitty.RoleEntity;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@SuppressWarnings("unused")
public interface RoleRepository extends
        JpaRepository<@NonNull RoleEntity, @NonNull Long>,
        JpaSpecificationExecutor<@NonNull RoleEntity> {

    Optional<RoleEntity> findByIdAndIsActiveAndIsDeleted(
            Long id,
            Boolean isActive,
            Boolean isDeleted
    );

    List<RoleEntity> findAllByIsActiveAndIsDeleted(
            Boolean isActive,
            Boolean isDeleted
    );

    Page<RoleEntity> findAllByIsActiveAndIsDeleted(
            Boolean isActive,
            Boolean isDeleted,
            Pageable pageable
    );

    List<RoleEntity> findAllByIdInAndIsActiveAndIsDeleted(
            Set<Long> ids,
            Boolean isActive,
            Boolean isDeleted
    );
}
