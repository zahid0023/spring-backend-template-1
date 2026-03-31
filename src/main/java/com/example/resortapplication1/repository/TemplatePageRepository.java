package com.example.resortapplication1.repository;

import com.example.resortapplication1.model.entity.TemplatePageEntity;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TemplatePageRepository extends JpaRepository<@NonNull TemplatePageEntity, @NonNull Long> {
    Optional<TemplatePageEntity> findByIdAndIsActiveAndIsDeleted(@NonNull Long id, boolean isActive, boolean isDeleted);

    Page<@NonNull TemplatePageEntity> findAllByIsActiveAndIsDeleted(boolean isActive, boolean isDeleted, Pageable pageable);
}
