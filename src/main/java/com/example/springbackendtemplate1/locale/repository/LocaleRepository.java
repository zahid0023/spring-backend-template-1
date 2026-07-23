package com.example.springbackendtemplate1.locale.repository;

import com.example.springbackendtemplate1.locale.model.entity.LocaleEntity;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface LocaleRepository extends JpaRepository<@NonNull LocaleEntity, @NonNull Long>,
        JpaSpecificationExecutor<@NonNull LocaleEntity> {

    Optional<LocaleEntity> findByIdAndIsActiveAndIsDeleted(Long id, Boolean isActive, Boolean isDeleted);

    List<LocaleEntity> findAllByIdInAndIsActiveAndIsDeleted(Set<Long> ids, Boolean isActive, Boolean isDeleted);
}
