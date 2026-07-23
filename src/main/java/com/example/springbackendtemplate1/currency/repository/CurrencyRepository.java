package com.example.springbackendtemplate1.currency.repository;

import com.example.springbackendtemplate1.currency.model.entity.CurrencyEntity;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface CurrencyRepository extends JpaRepository<@NonNull CurrencyEntity, @NonNull Long>,
        JpaSpecificationExecutor<@NonNull CurrencyEntity> {

    Optional<CurrencyEntity> findByIdAndIsActiveAndIsDeleted(Long id, Boolean isActive, Boolean isDeleted);

    List<CurrencyEntity> findAllByIdInAndIsActiveAndIsDeleted(Set<Long> ids, Boolean isActive, Boolean isDeleted);
}
