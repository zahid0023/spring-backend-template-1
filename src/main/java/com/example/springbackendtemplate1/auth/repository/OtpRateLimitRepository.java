package com.example.springbackendtemplate1.auth.repository;

import com.example.springbackendtemplate1.auth.model.enitty.OtpRateLimitEntity;
import com.example.springbackendtemplate1.auth.model.enitty.UserEntity;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OtpRateLimitRepository extends JpaRepository<@NonNull OtpRateLimitEntity,@NonNull Long> {
    Optional<OtpRateLimitEntity> findByUser(UserEntity user);
}
