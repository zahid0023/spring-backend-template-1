package com.example.springbackendtemplate1.auth.service;

import com.example.springbackendtemplate1.auth.dto.request.ForgotPasswordRequest;
import com.example.springbackendtemplate1.auth.dto.request.ResetPasswordRequest;
import com.example.springbackendtemplate1.auth.dto.response.SuccessResponse;

public interface PasswordResetService {
    SuccessResponse forgotPassword(ForgotPasswordRequest request);
    String verifyOtpAndGetResetToken(String email, String otp);
    SuccessResponse resetPassword(ResetPasswordRequest request);
}

