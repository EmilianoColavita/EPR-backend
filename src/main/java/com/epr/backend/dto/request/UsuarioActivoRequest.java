package com.epr.backend.dto.request;

import jakarta.validation.constraints.NotNull;

public record UsuarioActivoRequest(
        @NotNull Boolean activo
) {
}
