package com.epr.backend.service;

import com.epr.backend.dto.request.LoginRequest;
import com.epr.backend.dto.request.RegisterRequest;
import com.epr.backend.dto.response.LoginResponse;
import com.epr.backend.dto.response.UsuarioResponse;

public interface AuthService {
    UsuarioResponse register(RegisterRequest request, String ipCliente);

    LoginResponse login(LoginRequest request, String ipCliente);
}
