package com.example.springbackendtemplate1.auth.controller;

import com.example.springbackendtemplate1.auth.dto.request.ForgotPasswordRequest;
import com.example.springbackendtemplate1.auth.dto.request.LoginRequest;
import com.example.springbackendtemplate1.auth.dto.request.ResetPasswordRequest;
import com.example.springbackendtemplate1.auth.dto.request.VerifyOtpRequest;
import com.example.springbackendtemplate1.auth.dto.response.LoginResponse;
import com.example.springbackendtemplate1.auth.dto.response.VerifyOtpResponse;
import com.example.springbackendtemplate1.auth.model.enitty.UserEntity;
import com.example.springbackendtemplate1.auth.service.AuthService;
import com.example.springbackendtemplate1.auth.service.PasswordResetService;
import com.example.springbackendtemplate1.auth.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class LoginController {

    private final AuthService authService;
    private final PasswordResetService passwordResetService;
    private final UserService userService;

    public LoginController(AuthService authService, PasswordResetService passwordResetService, UserService userService) {
        this.authService = authService;
        this.passwordResetService = passwordResetService;
        this.userService = userService;
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }


    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest forgotPasswordRequest) {
        UserEntity userEntity = userService.getUserByUsername(forgotPasswordRequest.getUserName());
        return ResponseEntity.ok(passwordResetService.forgotPassword(userEntity));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody VerifyOtpRequest request) {
        UserEntity userEntity = userService.getUserByUsername(request.getUserName());

        VerifyOtpResponse response = passwordResetService.verifyOtpAndGetResetToken(userEntity, request.getOtp());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        return ResponseEntity.ok(passwordResetService.resetPassword(request));
    }
}