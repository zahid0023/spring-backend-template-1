package com.example.springbackendtemplate1.currency.repository;

import com.example.springbackendtemplate1.address.model.entity.CountryEntity;
import com.example.springbackendtemplate1.currency.model.entity.CurrencyEntity;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@SuppressWarnings("unused")
public interface CurrencyRepository extends
        JpaRepository<@NonNull CurrencyEntity, @NonNull Long>,
        JpaSpecificationExecutor<@NonNull CurrencyEntity> {

    Optional<CurrencyEntity> findByIdAndIsActiveAndIsDeleted(
            Long id,
            Boolean isActive,
            Boolean isDeleted
    );

    List<CurrencyEntity> findAllByIsActiveAndIsDeleted(
            Boolean isActive,
            Boolean isDeleted
    );

    Page<CurrencyEntity> findAllByIsActiveAndIsDeleted(
            Boolean isActive,
            Boolean isDeleted,
            Pageable pageable
    );

    List<CurrencyEntity> findAllByIdInAndIsActiveAndIsDeleted(
            Set<Long> ids,
            Boolean isActive,
            Boolean isDeleted
    );

    Optional<CurrencyEntity> findByCodeAndIsDeleted(
            String code,
            Boolean isDeleted
    );

    Optional<CurrencyEntity> findByNumericCodeAndIsDeleted(
            String numericCode,
            Boolean isDeleted
    );

    List<CurrencyEntity> findAllByCountryEntityAndIsActiveAndIsDeleted(
            CountryEntity countryEntity,
            Boolean isActive,
            Boolean isDeleted
    );

}
