package com.epr.backend.service;

import com.epr.backend.dto.request.LoginRequest;
import com.epr.backend.dto.request.RegisterRequest;
import com.epr.backend.dto.response.LoginResponse;

public interface AuthService {
    LoginResponse register(RegisterRequest request);

    LoginResponse login(LoginRequest request);
}
