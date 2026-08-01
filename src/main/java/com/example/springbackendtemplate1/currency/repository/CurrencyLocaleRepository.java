package com.example.springbackendtemplate1.currency.repository;

import com.example.springbackendtemplate1.currency.model.entity.CurrencyLocaleEntity;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

@SuppressWarnings("unused")
public interface CurrencyLocaleRepository extends
        JpaRepository<@NonNull CurrencyLocaleEntity, @NonNull Long>,
        JpaSpecificationExecutor<@NonNull CurrencyLocaleEntity> {

    Optional<CurrencyLocaleEntity> findByCurrencyEntity_IdAndIdAndIsActiveAndIsDeleted(
            Long currencyId,
            Long id,
            Boolean isActive,
            Boolean isDeleted
    );

    boolean existsByCurrencyEntity_IdAndLocaleEntity_IdAndIsActiveAndIsDeleted(
            Long currencyId,
            Long localeId,
            Boolean isActive,
            Boolean isDeleted
    );
}
