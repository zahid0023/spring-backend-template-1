package com.example.springbackendtemplate1.auth.repository;

import com.example.springbackendtemplate1.auth.model.enitty.PermissionEntity;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.Set;

public interface PermissionRepository extends JpaRepository<@NonNull PermissionEntity, @NonNull Long> {
    Set<PermissionEntity> findAllByNameIn(@NonNull Collection<String> names);

    Set<PermissionEntity> findAllByIdIn(@NonNull Collection<Long> ids);

}
