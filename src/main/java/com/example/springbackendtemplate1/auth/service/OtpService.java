package com.example.springbackendtemplate1.auth.service;

import com.example.springbackendtemplate1.auth.model.enitty.UserEntity;

public interface OtpService {
    String generateOtp();
    void storeOtp(UserEntity user, String otp);
    Boolean validateOtp(UserEntity user, String otp);
}
