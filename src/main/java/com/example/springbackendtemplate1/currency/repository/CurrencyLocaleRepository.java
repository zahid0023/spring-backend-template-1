package com.example.springbackendtemplate1.currency.repository;

import com.example.springbackendtemplate1.currency.model.entity.CurrencyEntity;
import com.example.springbackendtemplate1.currency.model.entity.CurrencyLocaleEntity;
import com.example.springbackendtemplate1.locale.model.entity.LocaleEntity;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

@SuppressWarnings("unused")
public interface CurrencyLocaleRepository extends
        JpaRepository<@NonNull CurrencyLocaleEntity, @NonNull Long>,
        JpaSpecificationExecutor<@NonNull CurrencyLocaleEntity> {

    Optional<CurrencyLocaleEntity> findByIdAndIsActiveAndIsDeleted(
            Long id,
            Boolean isActive,
            Boolean isDeleted
    );

    Optional<CurrencyLocaleEntity> findByCurrencyEntity_IdAndIdAndIsActiveAndIsDeleted(
            Long currencyEntityId,
            Long id,
            Boolean isActive,
            Boolean isDeleted
    );

    List<CurrencyLocaleEntity> findAllByIsActiveAndIsDeleted(
            Boolean isActive,
            Boolean isDeleted
    );

    Page<CurrencyLocaleEntity> findAllByIsActiveAndIsDeleted(
            Boolean isActive,
            Boolean isDeleted,
            Pageable pageable
    );

    List<CurrencyLocaleEntity> findAllByCurrencyEntityAndIsActiveAndIsDeleted(
            CurrencyEntity currencyEntity,
            Boolean isActive,
            Boolean isDeleted
    );

    List<CurrencyLocaleEntity> findAllByLocaleEntityAndIsActiveAndIsDeleted(
            LocaleEntity localeEntity,
            Boolean isActive,
            Boolean isDeleted
    );
}
