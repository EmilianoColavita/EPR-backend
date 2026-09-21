package com.epr.backend.dto.response;

import com.epr.backend.entity.EstadoBeca;

import java.time.LocalDate;
import java.util.List;

public record BecaResponse(
        Long id,
        Long alumnoId,
        LocalDate fechaInicio,
        LocalDate fechaFinalizacion,
        EstadoBeca estado,
        List<NotaBecaResponse> notas
) {
}
