package com.example.springbackendtemplate1.auth.service;

import com.example.springbackendtemplate1.auth.dto.request.ForgotPasswordRequest;
import com.example.springbackendtemplate1.auth.dto.response.ForgotPasswordResponse;

public interface PasswordResetService {
    ForgotPasswordResponse forgotPassword(ForgotPasswordRequest forgotPasswordRequest);
    void resetPassword(String token, String newPassword);
}
