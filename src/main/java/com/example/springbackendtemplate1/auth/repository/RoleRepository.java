package com.example.springbackendtemplate1.auth.repository;

import com.example.springbackendtemplate1.auth.model.enitty.RoleEntity;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

@SuppressWarnings("unused")
public interface RoleRepository extends
        JpaRepository<@NonNull RoleEntity, @NonNull Long>,
        JpaSpecificationExecutor<@NonNull RoleEntity> {
    Optional<RoleEntity> findByName(String name);
}
