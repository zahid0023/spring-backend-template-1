package com.example.springbackendtemplate1.auth.service;

import com.example.springbackendtemplate1.auth.dto.request.LoginRequest;
import com.example.springbackendtemplate1.auth.dto.request.RefreshTokenRequest;
import com.example.springbackendtemplate1.auth.dto.response.LoginResponse;
import com.example.springbackendtemplate1.auth.dto.response.RefreshTokenResponse;

public interface AuthService {
    LoginResponse login(LoginRequest request);
    RefreshTokenResponse refreshAccessToken(RefreshTokenRequest request);
}
