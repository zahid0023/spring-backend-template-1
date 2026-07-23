package com.example.springbackendtemplate1.unit.repository;

import com.example.springbackendtemplate1.unit.model.entity.UnitLocaleEntity;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UnitLocaleRepository extends JpaRepository<@NonNull UnitLocaleEntity, @NonNull Long> {

    Optional<UnitLocaleEntity> findByUnitEntity_IdAndIdAndIsActiveAndIsDeleted(
            Long unitId, Long id, Boolean isActive, Boolean isDeleted);
}
