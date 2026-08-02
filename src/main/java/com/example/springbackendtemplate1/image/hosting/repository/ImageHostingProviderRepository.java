package com.example.springbackendtemplate1.image.hosting.repository;

import com.example.springbackendtemplate1.image.hosting.model.entity.ImageHostingProviderEntity;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

@SuppressWarnings("unused")
public interface ImageHostingProviderRepository extends
        JpaRepository<@NonNull ImageHostingProviderEntity, @NonNull Long>,
        JpaSpecificationExecutor<@NonNull ImageHostingProviderEntity> {

    Optional<ImageHostingProviderEntity> findByIdAndIsActiveAndIsDeleted(Long id, Boolean isActive, Boolean isDeleted);

    boolean existsByCodeAndIsActiveAndIsDeleted(String code, Boolean isActive, Boolean isDeleted);

}
