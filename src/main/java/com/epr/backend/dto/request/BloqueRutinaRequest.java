package com.epr.backend.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

public record BloqueRutinaRequest(
        @NotNull @Positive Integer numero,
        String nombre,
        @Valid List<EjercicioRequest> ejercicios
) {
}
