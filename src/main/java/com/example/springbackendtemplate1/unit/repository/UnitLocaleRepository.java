package com.example.springbackendtemplate1.unit.repository;

import com.example.springbackendtemplate1.locale.model.entity.LocaleEntity;
import com.example.springbackendtemplate1.unit.model.entity.UnitEntity;
import com.example.springbackendtemplate1.unit.model.entity.UnitLocaleEntity;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

@SuppressWarnings("unused")
public interface UnitLocaleRepository extends
        JpaRepository<@NonNull UnitLocaleEntity, @NonNull Long>,
        JpaSpecificationExecutor<@NonNull UnitLocaleEntity> {

    Optional<UnitLocaleEntity> findByIdAndIsActiveAndIsDeleted(
            Long id,
            Boolean isActive,
            Boolean isDeleted
    );

    List<UnitLocaleEntity> findAllByIsActiveAndIsDeleted(
            Boolean isActive,
            Boolean isDeleted
    );

    Page<UnitLocaleEntity> findAllByIsActiveAndIsDeleted(
            Boolean isActive,
            Boolean isDeleted,
            Pageable pageable
    );

    List<UnitLocaleEntity> findAllByUnitEntityAndIsActiveAndIsDeleted(
            UnitEntity unitEntity,
            Boolean isActive,
            Boolean isDeleted
    );

    List<UnitLocaleEntity> findAllByLocaleEntityAndIsActiveAndIsDeleted(
            LocaleEntity localeEntity,
            Boolean isActive,
            Boolean isDeleted
    );
}
