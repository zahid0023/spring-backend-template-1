package com.example.springbackendtemplate1.unit.repository;

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
public interface UnitTypeRepository extends
        JpaRepository<@NonNull UnitTypeEntity, @NonNull Long>,
        JpaSpecificationExecutor<@NonNull UnitTypeEntity> {

    Optional<UnitTypeEntity> findByIdAndIsActiveAndIsDeleted(
            Long id,
            Boolean isActive,
            Boolean isDeleted
    );

    List<UnitTypeEntity> findAllByIsActiveAndIsDeleted(
            Boolean isActive,
            Boolean isDeleted
    );

    Page<UnitTypeEntity> findAllByIsActiveAndIsDeleted(
            Boolean isActive,
            Boolean isDeleted,
            Pageable pageable
    );

    List<UnitTypeEntity> findAllByIdInAndIsActiveAndIsDeleted(
            Set<Long> ids,
            Boolean isActive,
            Boolean isDeleted
    );

    Optional<UnitTypeEntity> findByCodeAndIsDeleted(
            String code,
            Boolean isDeleted
    );

}
