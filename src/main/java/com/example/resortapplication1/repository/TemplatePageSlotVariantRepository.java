package com.example.resortapplication1.repository;

import com.example.resortapplication1.model.entity.TemplatePageSlotVariantEntity;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TemplatePageSlotVariantRepository extends JpaRepository<@NonNull TemplatePageSlotVariantEntity, @NonNull Long> {
    Optional<TemplatePageSlotVariantEntity> findByIdAndIsActiveAndIsDeleted(@NonNull Long id, boolean isActive, boolean isDeleted);

    Page<@NonNull TemplatePageSlotVariantEntity> findAllByIsActiveAndIsDeleted(boolean isActive, boolean isDeleted, Pageable pageable);
}
