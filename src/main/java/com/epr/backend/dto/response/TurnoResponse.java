package com.epr.backend.dto.response;

import com.epr.backend.entity.EstadoTurno;

import java.time.LocalDate;
import java.time.LocalTime;

public record TurnoResponse(
        Long id,
        AlumnoResumenResponse alumno,
        LocalDate fecha,
        LocalTime horaInicio,
        LocalTime horaFin,
        String actividad,
        EstadoTurno estado,
        String notas
) {
}
