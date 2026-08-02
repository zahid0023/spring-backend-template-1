package com.example.springbackendtemplate1.image.hosting.repository;

import com.example.springbackendtemplate1.image.hosting.model.entity.ImageHostingConfigEntity;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

@SuppressWarnings("unused")
public interface ImageHostingConfigRepository extends
        JpaRepository<@NonNull ImageHostingConfigEntity, @NonNull Long>,
        JpaSpecificationExecutor<@NonNull ImageHostingConfigEntity> {

    Optional<ImageHostingConfigEntity> findByIdAndIsActiveAndIsDeleted(Long id, Boolean isActive, Boolean isDeleted);

    boolean existsByImageHostingProviderEntity_IdAndNameAndIsActiveAndIsDeleted(
            Long imageHostingProviderId, String name, Boolean isActive, Boolean isDeleted);

    boolean existsByImageHostingProviderEntity_IdAndNameAndIdNotAndIsActiveAndIsDeleted(
            Long imageHostingProviderId, String name, Long id, Boolean isActive, Boolean isDeleted);
}
