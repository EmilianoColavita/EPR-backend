package com.epr.backend.dto.request;

import com.epr.backend.entity.Rol;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UsuarioUpdateRequest(
        @NotBlank String nombre,
        @NotBlank String apellido,
        @NotBlank @Email String email,
        String telefono,
        @NotNull Rol rol,
        @NotNull Boolean activo
) {
}
