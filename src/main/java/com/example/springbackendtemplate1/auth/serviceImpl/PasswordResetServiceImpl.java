package com.example.springbackendtemplate1.auth.serviceImpl;

import com.example.springbackendtemplate1.auth.dto.request.ResetPasswordRequest;
import com.example.springbackendtemplate1.auth.dto.response.SuccessResponse;
import com.example.springbackendtemplate1.auth.dto.response.VerifyOtpResponse;
import com.example.springbackendtemplate1.auth.event.PasswordResetOtpEvent;
import com.example.springbackendtemplate1.auth.model.enitty.PasswordResetOtpEntity;
import com.example.springbackendtemplate1.auth.model.enitty.UserEntity;
import com.example.springbackendtemplate1.auth.model.mapper.OtpMapper;
import com.example.springbackendtemplate1.auth.repository.OtpRepository;
import com.example.springbackendtemplate1.auth.service.PasswordResetService;
import com.example.springbackendtemplate1.auth.util.OtpGenerator;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PasswordResetServiceImpl implements PasswordResetService {
    private final OtpRepository otpRepository;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final PasswordEncoder passwordEncoder;

    @Value("${OTP_EXPIRY_DURATION_MINUTES}")
    private Integer otpExpiryTimeMinutes;

    public PasswordResetServiceImpl(OtpRepository otpRepository,
                                    PasswordEncoder passwordEncoder,
                                    ApplicationEventPublisher applicationEventPublisher) {
        this.otpRepository = otpRepository;
        this.applicationEventPublisher = applicationEventPublisher;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public SuccessResponse forgotPassword(UserEntity userEntity) {
        String otp = generateOtpEntity(userEntity);
        log.info("OTP generated for user: {}", userEntity.getUsername());
        applicationEventPublisher.publishEvent(new PasswordResetOtpEvent(userEntity.getUsername(), otp));
        return new SuccessResponse(true, 0L);
    }

    private String generateOtpEntity(UserEntity userEntity) {
        String plainOtp = OtpGenerator.generate6DigitOtp();
        PasswordResetOtpEntity passwordResetOtpEntity = OtpMapper.toOtpEntity(userEntity, plainOtp, otpExpiryTimeMinutes,passwordEncoder);
        otpRepository.save(passwordResetOtpEntity);
        return plainOtp;
    }

    @Override
    @Transactional
    public VerifyOtpResponse verifyOtpAndGetResetToken(PasswordResetOtpEntity passwordResetOtpEntity) {
        return null;
    }

    @Override
    @Transactional
    public SuccessResponse resetPassword(ResetPasswordRequest request) {
        return null;
    }

}

