package com.example.springbackendtemplate1.auth.controller;

import com.example.springbackendtemplate1.auth.config.JwtTokenProvider;
import com.example.springbackendtemplate1.auth.dto.request.ForgotPasswordRequest;
import com.example.springbackendtemplate1.auth.dto.request.LoginRequest;
import com.example.springbackendtemplate1.auth.dto.request.ResetPasswordRequest;
import com.example.springbackendtemplate1.auth.dto.request.VerifyOtpRequest;
import com.example.springbackendtemplate1.auth.dto.response.LoginResponse;
import com.example.springbackendtemplate1.auth.dto.response.VerifyOtpResponse;
import com.example.springbackendtemplate1.auth.model.enitty.UserEntity;
import com.example.springbackendtemplate1.auth.service.PasswordResetService;
import com.example.springbackendtemplate1.auth.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class LoginController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordResetService passwordResetService;
    private final UserService userService;

    public LoginController(AuthenticationManager authenticationManager,
                           JwtTokenProvider jwtTokenProvider, PasswordResetService passwordResetService, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.passwordResetService = passwordResetService;
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUserName(),
                        request.getPassword()
                )
        );

        String accessToken = jwtTokenProvider.generateAccessToken(authentication);
        String refreshToken = jwtTokenProvider.generateRefreshToken(authentication);
        return ResponseEntity.ok(new LoginResponse(accessToken, refreshToken));
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