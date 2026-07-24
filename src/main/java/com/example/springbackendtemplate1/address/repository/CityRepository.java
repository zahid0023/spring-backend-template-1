package com.example.springbackendtemplate1.address.repository;

import com.example.springbackendtemplate1.address.model.entity.CityEntity;
import com.example.springbackendtemplate1.address.model.entity.CountryEntity;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@SuppressWarnings("unused")
public interface CityRepository extends
        JpaRepository<@NonNull CityEntity, @NonNull Long>,
        JpaSpecificationExecutor<@NonNull CityEntity> {

    Optional<CityEntity> findByIdAndIsActiveAndIsDeleted(
            Long id,
            Boolean isActive,
            Boolean isDeleted
    );

    List<CityEntity> findAllByIsActiveAndIsDeleted(
            Boolean isActive,
            Boolean isDeleted
    );

    Page<CityEntity> findAllByIsActiveAndIsDeleted(
            Boolean isActive,
            Boolean isDeleted,
            Pageable pageable
    );

    List<CityEntity> findAllByIdInAndIsActiveAndIsDeleted(
            Set<Long> ids,
            Boolean isActive,
            Boolean isDeleted
    );

    List<CityEntity> findAllByCountryEntityAndIsActiveAndIsDeleted(
            CountryEntity countryEntity,
            Boolean isActive,
            Boolean isDeleted
    );

}
