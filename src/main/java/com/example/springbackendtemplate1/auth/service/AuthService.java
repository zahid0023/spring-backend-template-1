package com.example.springbackendtemplate1.auth.service;

import com.example.springbackendtemplate1.auth.dto.request.LoginRequest;
import com.example.springbackendtemplate1.auth.dto.response.LoginResponse;

public interface AuthService {
    LoginResponse login(LoginRequest request);
}
