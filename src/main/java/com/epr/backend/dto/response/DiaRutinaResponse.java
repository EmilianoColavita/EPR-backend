package com.epr.backend.dto.response;

import java.util.List;

public record DiaRutinaResponse(
        Long id,
        Integer numero,
        String nombre,
        List<EjercicioResponse> ejercicios
) {
}
