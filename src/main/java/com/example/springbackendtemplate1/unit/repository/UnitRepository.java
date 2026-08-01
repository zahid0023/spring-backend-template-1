package com.example.springbackendtemplate1.unit.repository;

import com.example.springbackendtemplate1.unit.model.entity.UnitEntity;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

@SuppressWarnings("unused")
public interface UnitRepository extends
        JpaRepository<@NonNull UnitEntity, @NonNull Long>,
        JpaSpecificationExecutor<@NonNull UnitEntity> {

    Optional<UnitEntity> findByIdAndIsActiveAndIsDeleted(Long id, Boolean isActive, Boolean isDeleted);

    boolean existsByCodeAndIsActiveAndIsDeleted(String code, Boolean isActive, Boolean isDeleted);

    boolean existsBySymbolAndIsActiveAndIsDeleted(String symbol, Boolean isActive, Boolean isDeleted);

    List<UnitEntity> findByUnitTypeEntity_IdAndIsActiveAndIsDeleted(Long unitTypeId, Boolean isActive, Boolean isDeleted);
}
