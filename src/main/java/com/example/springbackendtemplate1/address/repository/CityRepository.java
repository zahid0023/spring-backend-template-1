package com.example.springbackendtemplate1.address.repository;

import com.example.springbackendtemplate1.address.model.entity.CityEntity;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface CityRepository extends JpaRepository<@NonNull CityEntity, @NonNull Long>,
        JpaSpecificationExecutor<@NonNull CityEntity> {
    Optional<CityEntity> findByCountryEntity_IdAndIdAndIsActiveAndIsDeleted(Long countryId, Long id, Boolean isActive, Boolean isDeleted);

    Optional<CityEntity> findByIdAndIsActiveAndIsDeleted(Long id, Boolean isActive, Boolean isDeleted);
}
