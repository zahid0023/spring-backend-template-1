package com.example.springbackendtemplate1.address.repository;

import com.example.springbackendtemplate1.address.model.entity.CityLocaleEntity;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CityLocaleRepository extends JpaRepository<@NonNull CityLocaleEntity, @NonNull Long> {
    Optional<CityLocaleEntity> findByCityEntity_CountryEntity_IdAndCityEntity_IdAndIdAndIsActiveAndIsDeleted(
            Long countryId, Long cityId, Long id, Boolean isActive, Boolean isDeleted);

    Optional<CityLocaleEntity> findByCityEntity_IdAndIdAndIsActiveAndIsDeleted(
            Long cityId, Long id, Boolean isActive, Boolean isDeleted);
}
