package com.example.springbackendtemplate1.address.repository;

import com.example.springbackendtemplate1.address.model.entity.CountryEntity;
import com.example.springbackendtemplate1.address.model.entity.CountryLocaleEntity;
import com.example.springbackendtemplate1.locale.model.entity.LocaleEntity;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

@SuppressWarnings("unused")
public interface CountryLocaleRepository extends
        JpaRepository<@NonNull CountryLocaleEntity, @NonNull Long>,
        JpaSpecificationExecutor<@NonNull CountryLocaleEntity> {

    Optional<CountryLocaleEntity> findByIdAndIsActiveAndIsDeleted(
            Long id,
            Boolean isActive,
            Boolean isDeleted
    );

    List<CountryLocaleEntity> findAllByIsActiveAndIsDeleted(
            Boolean isActive,
            Boolean isDeleted
    );

    Page<CountryLocaleEntity> findAllByIsActiveAndIsDeleted(
            Boolean isActive,
            Boolean isDeleted,
            Pageable pageable
    );

    List<CountryLocaleEntity> findAllByCountryEntityAndIsActiveAndIsDeleted(
            CountryEntity countryEntity,
            Boolean isActive,
            Boolean isDeleted
    );

    List<CountryLocaleEntity> findAllByLocaleEntityAndIsActiveAndIsDeleted(
            LocaleEntity localeEntity,
            Boolean isActive,
            Boolean isDeleted
    );
}
