package com.epr.backend.dto.response;

public record LoginResponse(
        String token,
        UsuarioResponse usuario
) {
}
