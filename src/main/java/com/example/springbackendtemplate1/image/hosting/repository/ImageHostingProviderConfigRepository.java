package com.example.springbackendtemplate1.image.hosting.repository;

import com.example.springbackendtemplate1.image.hosting.model.entity.ImageHostingProviderConfigEntity;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

@SuppressWarnings("unused")
public interface ImageHostingProviderConfigRepository extends
        JpaRepository<@NonNull ImageHostingProviderConfigEntity, @NonNull Long>,
        JpaSpecificationExecutor<@NonNull ImageHostingProviderConfigEntity> {

    Optional<ImageHostingProviderConfigEntity> findByImageHostingProviderEntity_IdAndIdAndIsActiveAndIsDeleted(
            Long imageHostingProviderId, Long id, Boolean isActive, Boolean isDeleted);

    boolean existsByImageHostingProviderEntity_IdAndNameAndIsActiveAndIsDeleted(
            Long imageHostingProviderId, String name, Boolean isActive, Boolean isDeleted);

    boolean existsByImageHostingProviderEntity_IdAndNameAndIdNotAndIsActiveAndIsDeleted(
            Long imageHostingProviderId, String name, Long id, Boolean isActive, Boolean isDeleted);
}
