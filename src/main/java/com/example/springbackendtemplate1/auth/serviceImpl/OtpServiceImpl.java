package com.example.springbackendtemplate1.auth.serviceImpl;

import com.example.springbackendtemplate1.auth.model.enitty.OtpEntity;
import com.example.springbackendtemplate1.auth.model.enitty.UserEntity;
import com.example.springbackendtemplate1.auth.repository.OtpRepository;
import com.example.springbackendtemplate1.auth.service.OtpService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.OffsetDateTime;

@Service
@Slf4j
public class OtpServiceImpl implements OtpService {

    private final OtpRepository otpRepository;
    private final SecureRandom secureRandom;

    @Value("${otp.expiry.duration.minutes}")
    private Integer otpExpiryDuration;

    public OtpServiceImpl(OtpRepository otpRepository) {
        this.otpRepository = otpRepository;
        this.secureRandom = new SecureRandom();
    }

    @Override
    public String generateOtp() {
        int otp = secureRandom.nextInt(999999);
        return String.format("%06d", otp);
    }

    @Override
    @Transactional
    public void storeOtp(UserEntity user, String otp) {
        OffsetDateTime otpExpiry = OffsetDateTime.now().plusMinutes(otpExpiryDuration);
        OtpEntity otpEntity = new OtpEntity();
        otpEntity.setUser(user);
        otpEntity.setOtp(otp);
        otpEntity.setExpiresAt(otpExpiry);

        otpRepository.save(otpEntity);
    }

    @Override
    @Transactional
    public Boolean validateOtp(UserEntity user, String otp) {
        OtpEntity otpEntity = otpRepository.findByOtpAndIsUsedFalse(otp)
                .orElseThrow(() -> new RuntimeException("Invalid or used OTP"));

        if (otpEntity.getExpiresAt().isBefore(OffsetDateTime.now())) {
            otpEntity.setIsDeleted(true);
            otpRepository.save(otpEntity);
            return false;
        }

        otpEntity.setIsUsed(true);
        otpEntity.setIsDeleted(true);
        otpRepository.save(otpEntity);

        return true;
    }
}

