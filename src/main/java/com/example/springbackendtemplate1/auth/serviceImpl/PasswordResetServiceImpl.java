package com.example.springbackendtemplate1.auth.serviceImpl;

import com.example.springbackendtemplate1.auth.dto.request.ForgotPasswordRequest;
import com.example.springbackendtemplate1.auth.dto.request.ResetPasswordRequest;
import com.example.springbackendtemplate1.auth.dto.response.SuccessResponse;
import com.example.springbackendtemplate1.auth.model.enitty.ResetTokenEntity;
import com.example.springbackendtemplate1.auth.model.enitty.UserEntity;
import com.example.springbackendtemplate1.auth.repository.ResetTokenRepository;
import com.example.springbackendtemplate1.auth.repository.UserRepository;
import com.example.springbackendtemplate1.auth.service.OtpService;
import com.example.springbackendtemplate1.auth.service.PasswordResetService;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@Slf4j
public class PasswordResetServiceImpl implements PasswordResetService {

    private final UserRepository userRepository;
    private final OtpService otpService;
    private final ResetTokenRepository resetTokenRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    @Value("${reset.token.expiry.minutes}")
    private Integer resetTokenExpiryMinutes;

    public PasswordResetServiceImpl(UserRepository userRepository,
                                    OtpService otpService,
                                    ResetTokenRepository resetTokenRepository,
                                    EmailService emailService,
                                    PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.otpService = otpService;
        this.resetTokenRepository = resetTokenRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public SuccessResponse forgotPassword(ForgotPasswordRequest request) {
        UserEntity user = userRepository.findByUsername(request.getUserName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String otp = otpService.generateOtp();
        otpService.storeOtp(user, otp);

        try {
            emailService.sendOtp(user.getUsername(), otp);
        } catch (MessagingException e) {
            log.error("Failed to send OTP email: {}", e.getMessage());
            throw new RuntimeException("Failed to send OTP email");
        }

        return new SuccessResponse(true, 0L);
    }

    @Override
    @Transactional
    public String verifyOtpAndGetResetToken(String email, String otp) {
        UserEntity user = userRepository.findByUsername(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!otpService.validateOtp(user, otp)) {
            throw new RuntimeException("Invalid or expired OTP");
        }

        String rawToken = UUID.randomUUID().toString();
        String hashedToken = passwordEncoder.encode(rawToken);

        ResetTokenEntity tokenEntity = ResetTokenEntity.builder()
                .user(user)
                .token(hashedToken)
                .expiresAt(OffsetDateTime.now().plusMinutes(resetTokenExpiryMinutes))
                .isUsed(false)
                .build();

        resetTokenRepository.save(tokenEntity);

        return rawToken;
    }

    @Override
    @Transactional
    public SuccessResponse resetPassword(ResetPasswordRequest request) {
        String resetToken = request.getResetToken();
        String newPassword = request.getNewPassword();
        String confirmPassword = request.getConfirmPassword();

        if (!newPassword.equals(confirmPassword)) {
            throw new RuntimeException("Passwords do not match");
        }

        ResetTokenEntity tokenEntity = resetTokenRepository.findAll().stream()
                .filter(t -> passwordEncoder.matches(resetToken, t.getToken()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Invalid reset token"));

        if (tokenEntity.getExpiresAt().isBefore(OffsetDateTime.now())) {
            tokenEntity.setIsUsed(true);
            tokenEntity.setIsDeleted(true);
            resetTokenRepository.save(tokenEntity);
            throw new RuntimeException("Reset token expired");
        }

        if (tokenEntity.getIsUsed()) {
            throw new RuntimeException("Reset token already used");
        }

        UserEntity user = tokenEntity.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        tokenEntity.setIsUsed(true);
        tokenEntity.setIsDeleted(true);
        resetTokenRepository.save(tokenEntity);

        return new SuccessResponse(true, 0L);
    }

}

