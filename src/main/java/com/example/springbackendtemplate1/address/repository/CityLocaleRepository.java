package com.example.springbackendtemplate1.address.repository;

import com.example.springbackendtemplate1.address.model.entity.CityLocaleEntity;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

@SuppressWarnings("unused")
public interface CityLocaleRepository extends
        JpaRepository<@NonNull CityLocaleEntity, @NonNull Long>,
        JpaSpecificationExecutor<@NonNull CityLocaleEntity> {

    Optional<CityLocaleEntity> findByCityEntity_IdAndIdAndIsActiveAndIsDeleted(
            Long cityId,
            Long id,
            Boolean isActive,
            Boolean isDeleted
    );

    boolean existsByCityEntity_IdAndLocaleEntity_IdAndIsActiveAndIsDeleted(
            Long cityId,
            Long localeId,
            Boolean isActive,
            Boolean isDeleted
    );
}
