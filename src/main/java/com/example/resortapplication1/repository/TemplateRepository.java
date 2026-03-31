package com.example.resortapplication1.repository;

import com.example.resortapplication1.model.entity.TemplateEntity;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TemplateRepository extends JpaRepository<@NonNull TemplateEntity, @NonNull Long> {
    Optional<TemplateEntity> findByIdAndIsActiveAndIsDeleted(@NonNull Long id, boolean isActive, boolean isDeleted);

    Page<@NonNull TemplateEntity> findAllByIsActiveAndIsDeleted(boolean isActive, boolean isDeleted, Pageable pageable);

    <T> Page<@NonNull T> findAllByIsActiveAndIsDeleted(
            boolean isActive,
            boolean isDeleted,
            Pageable pageable,
            Class<T> type
    );
}
