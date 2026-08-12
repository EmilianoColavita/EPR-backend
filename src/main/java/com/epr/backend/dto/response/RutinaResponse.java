package com.epr.backend.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record RutinaResponse(
        Long id,
        String nombre,
        String descripcion,
        Long alumnoId,
        String alumnoNombre,
        Long creadoPorId,
        boolean activa,
        LocalDateTime fechaCreacion,
        LocalDateTime fechaActualizacion,
        List<EjercicioResponse> ejercicios
) {
}
