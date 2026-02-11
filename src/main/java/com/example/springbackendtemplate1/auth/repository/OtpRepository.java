package com.example.springbackendtemplate1.auth.repository;

import com.example.springbackendtemplate1.auth.model.enitty.PasswordResetOtpEntity;
import com.example.springbackendtemplate1.auth.model.enitty.UserEntity;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OtpRepository extends JpaRepository<@NonNull PasswordResetOtpEntity, @NonNull Long> {
    Optional<PasswordResetOtpEntity> findByUserEntityAndIsUsedFalse(UserEntity userEntity);

    Optional<PasswordResetOtpEntity> findByResetToken(String resetToken);

    List<PasswordResetOtpEntity> findByUserEntityAndIsUsedFalseAndIsDeletedFalse(UserEntity userEntity);
}
