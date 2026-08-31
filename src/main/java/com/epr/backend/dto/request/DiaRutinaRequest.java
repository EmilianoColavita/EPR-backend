package com.epr.backend.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record DiaRutinaRequest(
        @NotNull Integer numero,
        String nombre,
        @Valid List<EjercicioRequest> ejercicios
) {
}
