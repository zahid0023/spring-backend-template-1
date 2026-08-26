package com.example.springbackendtemplate1.auth.repository;

import com.example.springbackendtemplate1.auth.model.entity.PermissionEntity;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Collection;
import java.util.Set;

@SuppressWarnings("unused")
public interface PermissionRepository extends
        JpaRepository<@NonNull PermissionEntity, @NonNull Long>,
        JpaSpecificationExecutor<@NonNull PermissionEntity> {

    Set<PermissionEntity> findAllByNameIn(@NonNull Collection<String> names);

    Set<PermissionEntity> findAllByIdIn(@NonNull Collection<Long> ids);

}
