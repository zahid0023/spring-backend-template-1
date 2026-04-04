package com.example.springbackendtemplate1.auth.repository;

import com.example.springbackendtemplate1.auth.model.enitty.RefreshTokenEntity;
import com.example.springbackendtemplate1.auth.model.enitty.UserEntity;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<@NonNull RefreshTokenEntity, @NonNull Long> {

    Optional<RefreshTokenEntity> findByTokenAndIsActiveAndIsDeleted(String token, boolean active, boolean deleted);

    @Modifying
    @Query("UPDATE RefreshTokenEntity r SET r.isDeleted = true WHERE r.user = :user AND r.isDeleted = false")
    void revokeAllByUser(@Param("user") UserEntity user);
}
