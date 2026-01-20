package com.example.springbackendtemplate1.auth.serviceImpl;

import com.example.springbackendtemplate1.auth.dto.request.ForgotPasswordRequest;
import com.example.springbackendtemplate1.auth.dto.response.ForgotPasswordResponse;
import com.example.springbackendtemplate1.auth.model.enitty.ResetTokenEntity;
import com.example.springbackendtemplate1.auth.model.enitty.UserEntity;
import com.example.springbackendtemplate1.auth.repository.ResetTokenRepository;
import com.example.springbackendtemplate1.auth.repository.UserRepository;
import com.example.springbackendtemplate1.auth.service.PasswordResetService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.UUID;


@Service
@Slf4j
public class PasswordResetServiceImpl implements PasswordResetService {

    private final UserRepository userRepository;
    private final ResetTokenRepository resetTokenRepository;
    private final PasswordEncoder passwordEncoder;

    public PasswordResetServiceImpl(UserRepository userRepository,
                                    ResetTokenRepository resetTokenRepository,
                                    PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.resetTokenRepository = resetTokenRepository;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    public ForgotPasswordResponse forgotPassword(ForgotPasswordRequest forgotPasswordRequest) {
        UserEntity user = userRepository.findByUsername(forgotPasswordRequest.getUserName())
                .orElseThrow(() -> new RuntimeException("User not found"));


        String resetToken = UUID.randomUUID().toString();
        OffsetDateTime tokenExpiry = OffsetDateTime.now().plusMinutes(30);

        ResetTokenEntity resetTokenEntity = new ResetTokenEntity();
        resetTokenEntity.setUser(user);
        resetTokenEntity.setToken(resetToken);
        resetTokenEntity.setExpiresAt(tokenExpiry);
        resetTokenEntity.setCreatedAt(OffsetDateTime.now().toInstant());

        resetTokenRepository.save(resetTokenEntity);

        return new ForgotPasswordResponse(resetToken);
    }

    @Override
    public void resetPassword(String token, String newPassword) {
        ResetTokenEntity resetTokenEntity = resetTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid or expired token"));

        if (resetTokenEntity.getExpiresAt().isBefore(OffsetDateTime.now())) {
            throw new RuntimeException("Token has expired");
        }

        UserEntity user = resetTokenEntity.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        resetTokenRepository.delete(resetTokenEntity);
    }
}

