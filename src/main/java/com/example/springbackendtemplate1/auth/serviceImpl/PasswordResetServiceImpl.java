package com.example.springbackendtemplate1.auth.serviceImpl;

import com.example.springbackendtemplate1.auth.dto.request.ResetPasswordRequest;
import com.example.springbackendtemplate1.auth.dto.response.SuccessResponse;
import com.example.springbackendtemplate1.auth.dto.response.VerifyOtpResponse;
import com.example.springbackendtemplate1.auth.event.PasswordResetOtpEvent;
import com.example.springbackendtemplate1.auth.model.enitty.OtpRateLimitEntity;
import com.example.springbackendtemplate1.auth.model.enitty.PasswordResetOtpEntity;
import com.example.springbackendtemplate1.auth.model.enitty.UserEntity;
import com.example.springbackendtemplate1.auth.model.mapper.OtpMapper;
import com.example.springbackendtemplate1.auth.model.mapper.OtpRateLimitMapper;
import com.example.springbackendtemplate1.auth.repository.OtpRateLimitRepository;
import com.example.springbackendtemplate1.auth.repository.OtpRepository;
import com.example.springbackendtemplate1.auth.repository.UserRepository;
import com.example.springbackendtemplate1.auth.service.PasswordResetService;
import com.example.springbackendtemplate1.auth.util.OtpGenerator;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;

@Service
@Slf4j
public class PasswordResetServiceImpl implements PasswordResetService {
    private static final Integer MAX_REQUESTS = 5;
    private static final Duration OTP_WINDOW = Duration.ofMinutes(60);

    @Value("${jwt.otp.expiration-minutes}")
    private Integer otpExpiryTimeMinutes;

    private final OtpRepository otpRepository;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final OtpRateLimitRepository otpRateLimitRepository;

    public PasswordResetServiceImpl(OtpRepository otpRepository, ApplicationEventPublisher applicationEventPublisher, PasswordEncoder passwordEncoder, UserRepository userRepository, OtpRateLimitRepository otpRateLimitRepository) {
        this.otpRepository = otpRepository;
        this.applicationEventPublisher = applicationEventPublisher;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.otpRateLimitRepository = otpRateLimitRepository;
    }

    @Override
    @Transactional
    public SuccessResponse forgotPassword(UserEntity userEntity) {
        invalidatePreviousOtps(userEntity);
        enforceOtpRateLimit(userEntity);
        String otp = generateOtpEntity(userEntity);
        log.info("OTP generated for user: {}", userEntity.getUsername());
        applicationEventPublisher.publishEvent(new PasswordResetOtpEvent(userEntity.getUsername(), otp));
        return new SuccessResponse(true, 0L);
    }

    private void enforceOtpRateLimit(UserEntity userEntity) {
        OffsetDateTime now = OffsetDateTime.now();
        OffsetDateTime windowStart = now.minus(OTP_WINDOW);

        OtpRateLimitEntity rateLimit = otpRateLimitRepository.findByUser(userEntity)
                .orElseGet(() -> OtpRateLimitMapper.toRateLimitEntity(userEntity, now));

        if (rateLimit.getWindowStart().isBefore(windowStart)) {
            rateLimit.setWindowStart(now);
            rateLimit.setRequestCount(0);
        }

        if (rateLimit.getRequestCount() >= MAX_REQUESTS) {
            throw new RuntimeException("Too many OTP requests. Please try again later.");
        }

        rateLimit.setRequestCount(rateLimit.getRequestCount() + 1);
        otpRateLimitRepository.save(rateLimit);
    }

    private void invalidatePreviousOtps(UserEntity userEntity) {
        List<PasswordResetOtpEntity> previousOtps = otpRepository
                .findByUserEntityAndIsUsedFalseAndIsDeletedFalse(userEntity);

        for (PasswordResetOtpEntity otpEntity : previousOtps) {
            otpEntity.setIsUsed(true);
            otpEntity.setIsDeleted(true);
        }

        otpRepository.saveAll(previousOtps);
    }

    private String generateOtpEntity(UserEntity userEntity) {
        String otp = OtpGenerator.generate6DigitOtp();
        PasswordResetOtpEntity passwordResetOtpEntity = OtpMapper.toOtpEntity(userEntity, otp, otpExpiryTimeMinutes, passwordEncoder);
        otpRepository.save(passwordResetOtpEntity);
        return otp;
    }


    @Override
    @Transactional
    public VerifyOtpResponse verifyOtpAndGetResetToken(UserEntity userEntity, String otp) {
        PasswordResetOtpEntity otpEntity = otpRepository
                .findByUserEntityAndIsUsedFalse(userEntity)
                .orElseThrow(() -> new RuntimeException("Invalid OTP."));

        validateOtp(otp, otpEntity);

        return new VerifyOtpResponse(otpEntity.getResetToken());
    }

    private void validateOtp(String otp, PasswordResetOtpEntity otpEntity) {
        if (otpEntity.getIsUsed()) {
            throw new RuntimeException("OTP has already been used.");
        }

        if (otpEntity.getExpiresAt().isBefore(OffsetDateTime.now())) {
            throw new RuntimeException("OTP has expired.");
        }

        if (!passwordEncoder.matches(otp, otpEntity.getOtp())) {
            throw new RuntimeException("Invalid OTP.");
        }
    }

    @Override
    @Transactional
    public SuccessResponse resetPassword(ResetPasswordRequest request) {
        PasswordResetOtpEntity otpEntity = validateResetRequest(request);

        UserEntity user = otpEntity.getUserEntity();
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        otpEntity.setIsUsed(true);
        otpEntity.setIsDeleted(true);
        otpRepository.save(otpEntity);

        return new SuccessResponse(true, 0L);
    }

    private PasswordResetOtpEntity validateResetRequest(ResetPasswordRequest request) {
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("Passwords do not match.");
        }

        PasswordResetOtpEntity otpEntity = otpRepository
                .findByResetToken(request.getResetToken())
                .orElseThrow(() -> new RuntimeException("Invalid reset token."));

        if (otpEntity.getIsUsed() || otpEntity.getExpiresAt().isBefore(OffsetDateTime.now())) {
            throw new RuntimeException("Reset token is invalid or expired.");
        }

        return otpEntity;
    }

}

