package com.example.springbackendtemplate1.image.hosting.repository;

import com.example.springbackendtemplate1.image.hosting.model.entity.ImageHostingProviderConfigFieldEntity;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

@SuppressWarnings("unused")
public interface ImageHostingProviderConfigFieldRepository extends
        JpaRepository<@NonNull ImageHostingProviderConfigFieldEntity, @NonNull Long>,
        JpaSpecificationExecutor<@NonNull ImageHostingProviderConfigFieldEntity> {

    Optional<ImageHostingProviderConfigFieldEntity> findByImageHostingProviderEntity_IdAndIdAndIsActiveAndIsDeleted(
            Long imageHostingProviderId, Long id, Boolean isActive, Boolean isDeleted);

    List<ImageHostingProviderConfigFieldEntity> findByImageHostingProviderEntity_IdAndIsActiveAndIsDeleted(
            Long imageHostingProviderId, Boolean isActive, Boolean isDeleted);

    boolean existsByImageHostingProviderEntity_IdAndKeyAndIsActiveAndIsDeleted(
            Long imageHostingProviderId, String key, Boolean isActive, Boolean isDeleted);
}
