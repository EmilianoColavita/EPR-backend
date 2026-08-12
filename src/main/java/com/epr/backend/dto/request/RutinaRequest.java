package com.epr.backend.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record RutinaRequest(
        @NotBlank String nombre,
        String descripcion,
        @NotNull Long alumnoId,
        @Valid List<EjercicioRequest> ejercicios
) {
}
