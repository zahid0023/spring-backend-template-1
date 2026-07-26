package com.example.springbackendtemplate1.address.repository;

import com.example.springbackendtemplate1.address.model.entity.CountryLocaleEntity;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

@SuppressWarnings("unused")
public interface CountryLocaleRepository extends
        JpaRepository<@NonNull CountryLocaleEntity, @NonNull Long>,
        JpaSpecificationExecutor<@NonNull CountryLocaleEntity> {

    Optional<CountryLocaleEntity> findByCountryEntity_IdAndIdAndIsActiveAndIsDeleted(
            Long countryId,
            Long id,
            Boolean isActive,
            Boolean isDeleted
    );
}
