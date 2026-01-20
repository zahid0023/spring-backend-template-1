package com.example.springbackendtemplate1.auth.repository;

import com.example.springbackendtemplate1.auth.model.enitty.ResetTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.OffsetDateTime;
import java.util.Optional;

public interface ResetTokenRepository extends JpaRepository<ResetTokenEntity, Long> {

    Optional<ResetTokenEntity> findByToken(String token);

    void deleteByExpiresAtBefore(OffsetDateTime expiresAt);
}