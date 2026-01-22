package com.example.springbackendtemplate1.auth.repository;

import com.example.springbackendtemplate1.auth.model.enitty.ResetTokenEntity;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ResetTokenRepository extends JpaRepository<@NonNull ResetTokenEntity,@NonNull Long> {
    Optional<ResetTokenEntity> findByTokenAndIsUsedFalse(String token);
}
