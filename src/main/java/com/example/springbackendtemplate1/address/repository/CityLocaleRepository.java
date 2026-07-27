package com.example.springbackendtemplate1.address.repository;

import com.example.springbackendtemplate1.address.model.entity.CityEntity;
import com.example.springbackendtemplate1.address.model.entity.CityLocaleEntity;
import com.example.springbackendtemplate1.locale.model.entity.LocaleEntity;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

@SuppressWarnings("unused")
public interface CityLocaleRepository extends
        JpaRepository<@NonNull CityLocaleEntity, @NonNull Long>,
        JpaSpecificationExecutor<@NonNull CityLocaleEntity> {

    Optional<CityLocaleEntity> findByIdAndIsActiveAndIsDeleted(
            Long id,
            Boolean isActive,
            Boolean isDeleted
    );

    Optional<CityLocaleEntity> findByCityEntity_IdAndIdAndIsActiveAndIsDeleted(
            Long cityEntityId,
            Long id,
            Boolean isActive,
            Boolean isDeleted
    );

    List<CityLocaleEntity> findAllByIsActiveAndIsDeleted(
            Boolean isActive,
            Boolean isDeleted
    );

    Page<CityLocaleEntity> findAllByIsActiveAndIsDeleted(
            Boolean isActive,
            Boolean isDeleted,
            Pageable pageable
    );

    List<CityLocaleEntity> findAllByCityEntityAndIsActiveAndIsDeleted(
            CityEntity cityEntity,
            Boolean isActive,
            Boolean isDeleted
    );

    List<CityLocaleEntity> findAllByLocaleEntityAndIsActiveAndIsDeleted(
            LocaleEntity localeEntity,
            Boolean isActive,
            Boolean isDeleted
    );
}
