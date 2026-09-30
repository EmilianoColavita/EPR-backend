package com.epr.backend.dto.response;

import java.time.LocalDate;

public record RutinaPdfResponse(
        Long id,
        AlumnoResumenResponse alumno,
        String nombreArchivo,
        LocalDate fechaSubida
) {
}
