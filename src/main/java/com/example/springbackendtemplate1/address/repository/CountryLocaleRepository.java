package com.example.springbackendtemplate1.address.repository;

import com.example.springbackendtemplate1.address.model.entity.CountryLocaleEntity;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    boolean existsByCountryEntity_IdAndLocaleEntity_IdAndIsActiveAndIsDeleted(
            Long countryId,
            Long localeId,
            Boolean isActive,
            Boolean isDeleted
    );

    Page<@NonNull CountryLocaleEntity> findByCountryEntity_IdAndIsActiveAndIsDeleted(
            Long countryId,
            Boolean isActive,
            Boolean isDeleted,
            Pageable pageable
    );

    Page<@NonNull CountryLocaleEntity> findByCountryEntity_IdAndLocaleEntity_CodeContainingIgnoreCaseAndIsActiveAndIsDeleted(
            Long countryId,
            String localeCode,
            Boolean isActive,
            Boolean isDeleted,
            Pageable pageable
    );
}
