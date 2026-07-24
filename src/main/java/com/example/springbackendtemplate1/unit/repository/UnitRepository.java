package com.example.springbackendtemplate1.unit.repository;

import com.example.springbackendtemplate1.unit.model.entity.UnitEntity;
import com.example.springbackendtemplate1.unit.model.entity.UnitTypeEntity;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@SuppressWarnings("unused")
public interface UnitRepository extends
        JpaRepository<@NonNull UnitEntity, @NonNull Long>,
        JpaSpecificationExecutor<@NonNull UnitEntity> {

    Optional<UnitEntity> findByIdAndIsActiveAndIsDeleted(
            Long id,
            Boolean isActive,
            Boolean isDeleted
    );

    List<UnitEntity> findAllByIsActiveAndIsDeleted(
            Boolean isActive,
            Boolean isDeleted
    );

    Page<UnitEntity> findAllByIsActiveAndIsDeleted(
            Boolean isActive,
            Boolean isDeleted,
            Pageable pageable
    );

    List<UnitEntity> findAllByIdInAndIsActiveAndIsDeleted(
            Set<Long> ids,
            Boolean isActive,
            Boolean isDeleted
    );

    Optional<UnitEntity> findByCodeAndIsDeleted(
            String code,
            Boolean isDeleted
    );

    Optional<UnitEntity> findBySymbolAndIsDeleted(
            String symbol,
            Boolean isDeleted
    );

    List<UnitEntity> findAllByUnitTypeEntityAndIsActiveAndIsDeleted(
            UnitTypeEntity unitTypeEntity,
            Boolean isActive,
            Boolean isDeleted
    );

}
