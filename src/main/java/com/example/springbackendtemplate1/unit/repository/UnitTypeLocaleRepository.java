package com.example.springbackendtemplate1.unit.repository;

import com.example.springbackendtemplate1.unit.model.entity.UnitTypeLocaleEntity;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UnitTypeLocaleRepository extends JpaRepository<@NonNull UnitTypeLocaleEntity, @NonNull Long> {

    Optional<UnitTypeLocaleEntity> findByUnitTypeEntity_IdAndIdAndIsActiveAndIsDeleted(
            Long unitTypeId, Long id, Boolean isActive, Boolean isDeleted);
}
