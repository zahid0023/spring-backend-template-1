package com.example.resortapplication1.repository;

import com.example.resortapplication1.model.entity.UiBlockCategoryEntity;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UiBlockCategoryRepository extends JpaRepository<@NonNull UiBlockCategoryEntity, @NonNull Long> {
    Optional<UiBlockCategoryEntity> findByIdAndIsActiveAndIsDeleted(@NonNull Long id, boolean isActive, boolean isDeleted);

    Page<@NonNull UiBlockCategoryEntity> findAllByIsActiveAndIsDeleted(boolean isActive, boolean isDeleted, Pageable pageable);
}
