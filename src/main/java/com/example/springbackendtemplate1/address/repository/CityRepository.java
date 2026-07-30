package com.example.springbackendtemplate1.address.repository;

import com.example.springbackendtemplate1.address.model.entity.CityEntity;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

@SuppressWarnings("unused")
public interface CityRepository extends
        JpaRepository<@NonNull CityEntity, @NonNull Long>,
        JpaSpecificationExecutor<@NonNull CityEntity> {

    Optional<CityEntity> findByIdAndIsActiveAndIsDeleted(Long id, Boolean isActive, Boolean isDeleted);

    boolean existsByCodeAndIsActiveAndIsDeleted(String code, Boolean isActive, Boolean isDeleted);
}
