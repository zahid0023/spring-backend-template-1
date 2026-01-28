package com.example.springbackendtemplate1.auth.model.mapper;

import com.example.springbackendtemplate1.auth.model.enitty.PasswordResetOtpEntity;
import com.example.springbackendtemplate1.auth.model.enitty.UserEntity;
import lombok.experimental.UtilityClass;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.OffsetDateTime;
import java.util.UUID;

@UtilityClass
public class OtpMapper {
    public static PasswordResetOtpEntity toOtpEntity(UserEntity userEntity, String otp, Integer otpExpiryInMinutes, PasswordEncoder passwordEncoder) {
        PasswordResetOtpEntity passwordResetOtpEntity = new PasswordResetOtpEntity();
        passwordResetOtpEntity.setUserEntity(userEntity);
        passwordResetOtpEntity.setOtp(passwordEncoder.encode(otp));
        passwordResetOtpEntity.setResetToken(passwordEncoder.encode(generateResetToken()));
        passwordResetOtpEntity.setIsUsed(false);
        passwordResetOtpEntity.setExpiresAt(OffsetDateTime.now().plusMinutes(otpExpiryInMinutes));
        return passwordResetOtpEntity;
    }

    private String generateResetToken() {
        return UUID.randomUUID().toString();
    }
}
