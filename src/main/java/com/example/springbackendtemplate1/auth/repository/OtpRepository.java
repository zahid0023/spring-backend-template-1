package com.example.springbackendtemplate1.auth.repository;

import com.example.springbackendtemplate1.auth.model.enitty.OtpEntity;
import com.example.springbackendtemplate1.auth.model.enitty.UserEntity;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OtpRepository extends JpaRepository<@NonNull OtpEntity,@NonNull Long> {
    Optional<OtpEntity> findByOtpAndIsUsedFalse(String otp);

    Optional<OtpEntity> findByOtp(String otp);
}
