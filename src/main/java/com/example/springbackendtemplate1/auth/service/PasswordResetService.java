package com.example.springbackendtemplate1.auth.service;

import com.example.springbackendtemplate1.auth.dto.request.ForgotPasswordRequest;
import com.example.springbackendtemplate1.auth.dto.response.ForgotPasswordResponse;
import com.example.springbackendtemplate1.auth.dto.response.SuccessResponse;

public interface PasswordResetService {
    ForgotPasswordResponse forgotPassword(ForgotPasswordRequest forgotPasswordRequest);

    SuccessResponse resetPassword(String token, String newPassword);
}
