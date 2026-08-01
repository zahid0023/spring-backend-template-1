package com.example.springbackendtemplate1.unit.repository;

import com.example.springbackendtemplate1.unit.model.entity.UnitTypeLocaleEntity;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

@SuppressWarnings("unused")
public interface UnitTypeLocaleRepository extends
        JpaRepository<@NonNull UnitTypeLocaleEntity, @NonNull Long>,
        JpaSpecificationExecutor<@NonNull UnitTypeLocaleEntity> {

    Optional<UnitTypeLocaleEntity> findByUnitTypeEntity_IdAndIdAndIsActiveAndIsDeleted(
            Long unitTypeId,
            Long id,
            Boolean isActive,
            Boolean isDeleted
    );

    boolean existsByUnitTypeEntity_IdAndLocaleEntity_IdAndIsActiveAndIsDeleted(
            Long unitTypeId,
            Long localeId,
            Boolean isActive,
            Boolean isDeleted
    );
}
