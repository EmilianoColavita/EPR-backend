package com.epr.backend.dto.response;

import java.time.LocalDate;

public record EvaluacionResponse(
        Long id,
        AlumnoResumenResponse alumno,
        String nombreArchivo,
        LocalDate fechaSubida
) {
}
