package com.example.resortapplication1.repository;

import com.example.resortapplication1.model.entity.UiBlockDefinitionEntity;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UiBlockDefinitionRepository extends JpaRepository<@NonNull UiBlockDefinitionEntity, @NonNull Long> {
    Optional<UiBlockDefinitionEntity> findByIdAndIsActiveAndIsDeleted(@NonNull Long id, boolean isActive, boolean isDeleted);

    Page<@NonNull UiBlockDefinitionEntity> findAllByIsActiveAndIsDeleted(boolean isActive, boolean isDeleted, Pageable pageable);
}
