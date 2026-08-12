package com.epr.backend.dto.request;

import jakarta.validation.constraints.NotBlank;

public record EjercicioRequest(
        @NotBlank String nombre,
        Integer series,
        String repeticiones,
        String pesoSugerido,
        Integer descansoSegundos,
        String notas,
        Integer orden
) {
}
