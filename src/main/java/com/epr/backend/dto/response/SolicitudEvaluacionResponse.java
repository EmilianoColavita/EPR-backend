package com.epr.backend.dto.response;

import com.epr.backend.entity.EstadoSolicitud;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record SolicitudEvaluacionResponse(
        Long id,
        String nombreCompleto,
        String email,
        String telefono,
        String objetivo,
        LocalDate fechaPreferida,
        EstadoSolicitud estado,
        LocalDateTime fechaSolicitud
) {
}
