package com.example.springbackendtemplate1.auth.service;

import com.example.springbackendtemplate1.auth.dto.request.ResetPasswordRequest;
import com.example.springbackendtemplate1.auth.dto.response.SuccessResponse;
import com.example.springbackendtemplate1.auth.dto.response.VerifyOtpResponse;
import com.example.springbackendtemplate1.auth.model.enitty.UserEntity;

public interface PasswordResetService {
    SuccessResponse forgotPassword(UserEntity userEntity);

    SuccessResponse resetPassword(ResetPasswordRequest request);

    VerifyOtpResponse verifyOtpAndGetResetToken(UserEntity userEntity, String otp);
}

