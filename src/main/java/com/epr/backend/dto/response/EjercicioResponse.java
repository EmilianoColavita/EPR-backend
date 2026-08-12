package com.epr.backend.dto.response;

public record EjercicioResponse(
        Long id,
        String nombre,
        Integer series,
        String repeticiones,
        String pesoSugerido,
        Integer descansoSegundos,
        String notas,
        Integer orden
) {
}
