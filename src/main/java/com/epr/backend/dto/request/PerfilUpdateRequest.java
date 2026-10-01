package com.epr.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PerfilUpdateRequest(
        @NotBlank @Size(max = 100, message = "El nombre no puede superar los 100 caracteres") String nombre,
        @NotBlank @Size(max = 100, message = "El apellido no puede superar los 100 caracteres") String apellido,
        @Size(max = 30, message = "El teléfono no puede superar los 30 caracteres") String telefono
) {
}
