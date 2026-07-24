package com.example.springbackendtemplate1.unit.repository;

import com.example.springbackendtemplate1.locale.model.entity.LocaleEntity;
import com.example.springbackendtemplate1.unit.model.entity.UnitTypeEntity;
import com.example.springbackendtemplate1.unit.model.entity.UnitTypeLocaleEntity;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

@SuppressWarnings("unused")
public interface UnitTypeLocaleRepository extends
        JpaRepository<@NonNull UnitTypeLocaleEntity, @NonNull Long>,
        JpaSpecificationExecutor<@NonNull UnitTypeLocaleEntity> {

    Optional<UnitTypeLocaleEntity> findByIdAndIsActiveAndIsDeleted(
            Long id,
            Boolean isActive,
            Boolean isDeleted
    );

    List<UnitTypeLocaleEntity> findAllByIsActiveAndIsDeleted(
            Boolean isActive,
            Boolean isDeleted
    );

    Page<UnitTypeLocaleEntity> findAllByIsActiveAndIsDeleted(
            Boolean isActive,
            Boolean isDeleted,
            Pageable pageable
    );

    List<UnitTypeLocaleEntity> findAllByUnitTypeEntityAndIsActiveAndIsDeleted(
            UnitTypeEntity unitTypeEntity,
            Boolean isActive,
            Boolean isDeleted
    );

    List<UnitTypeLocaleEntity> findAllByLocaleEntityAndIsActiveAndIsDeleted(
            LocaleEntity localeEntity,
            Boolean isActive,
            Boolean isDeleted
    );

}
