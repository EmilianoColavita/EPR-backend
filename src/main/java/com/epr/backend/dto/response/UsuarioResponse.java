package com.epr.backend.dto.response;

import com.epr.backend.entity.Rol;

import java.time.LocalDateTime;

public record UsuarioResponse(
        Long id,
        String nombre,
        String apellido,
        String email,
        String telefono,
        Rol rol,
        boolean activo,
        LocalDateTime fechaRegistro
) {
}
