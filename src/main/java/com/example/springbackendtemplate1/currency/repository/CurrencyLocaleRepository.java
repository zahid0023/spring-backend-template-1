package com.example.springbackendtemplate1.currency.repository;

import com.example.springbackendtemplate1.currency.model.entity.CurrencyLocaleEntity;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CurrencyLocaleRepository extends JpaRepository<@NonNull CurrencyLocaleEntity, @NonNull Long> {

    Optional<CurrencyLocaleEntity> findByCurrencyEntity_IdAndIdAndIsActiveAndIsDeleted(
            Long currencyId, Long id, Boolean isActive, Boolean isDeleted);
}
