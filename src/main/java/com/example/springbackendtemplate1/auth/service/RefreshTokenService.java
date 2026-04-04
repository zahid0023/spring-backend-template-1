package com.example.springbackendtemplate1.auth.service;

import com.example.springbackendtemplate1.auth.dto.response.LoginResponse;
import com.example.springbackendtemplate1.auth.model.enitty.UserEntity;
import org.springframework.security.core.Authentication;

public interface RefreshTokenService {
    String createRefreshToken(Authentication authentication, UserEntity user);
    LoginResponse rotateRefreshToken(String refreshToken);
    void revokeAllUserTokens(UserEntity user);
}
