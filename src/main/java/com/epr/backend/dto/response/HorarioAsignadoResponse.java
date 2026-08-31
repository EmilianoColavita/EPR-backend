package com.epr.backend.dto.response;

import java.util.List;

public record HorarioAsignadoResponse(
        Long id,
        AlumnoResumenResponse alumno,
        List<FranjaHorarioResponse> franjas,
        String notas
) {
}
