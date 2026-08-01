package com.example.springbackendtemplate1.unit.repository;

import com.example.springbackendtemplate1.unit.model.entity.UnitLocaleEntity;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

@SuppressWarnings("unused")
public interface UnitLocaleRepository extends
        JpaRepository<@NonNull UnitLocaleEntity, @NonNull Long>,
        JpaSpecificationExecutor<@NonNull UnitLocaleEntity> {

    Optional<UnitLocaleEntity> findByUnitEntity_IdAndIdAndIsActiveAndIsDeleted(
            Long unitId,
            Long id,
            Boolean isActive,
            Boolean isDeleted
    );

    boolean existsByUnitEntity_IdAndLocaleEntity_IdAndIsActiveAndIsDeleted(
            Long unitId,
            Long localeId,
            Boolean isActive,
            Boolean isDeleted
    );
}
