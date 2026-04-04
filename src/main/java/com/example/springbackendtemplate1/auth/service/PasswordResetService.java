package com.example.springbackendtemplate1.auth.service;

import com.example.springbackendtemplate1.auth.dto.request.ResetPasswordRequest;
import com.example.springbackendtemplate1.commons.dto.response.SuccessResponse;
import com.example.springbackendtemplate1.auth.dto.response.VerifyOtpResponse;

public interface PasswordResetService {
    SuccessResponse forgotPassword(String username);

    SuccessResponse resetPassword(ResetPasswordRequest request);

    VerifyOtpResponse verifyOtpAndGetResetToken(String username, String otp);
}

