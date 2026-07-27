package com.example.springbackendtemplate1.unit.repository;

import com.example.springbackendtemplate1.unit.model.entity.UnitTypeEntity;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

@SuppressWarnings("unused")
public interface UnitTypeRepository extends
        JpaRepository<@NonNull UnitTypeEntity, @NonNull Long>,
        JpaSpecificationExecutor<@NonNull UnitTypeEntity> {

    Optional<UnitTypeEntity> findByIdAndIsActiveAndIsDeleted(
            Long id,
            Boolean isActive,
            Boolean isDeleted
    );

}
