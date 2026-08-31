package com.epr.backend.dto.response;

import com.epr.backend.entity.DiaSemana;

import java.time.LocalTime;

public record FranjaHorarioResponse(
        Long id,
        DiaSemana diaSemana,
        LocalTime horaInicio,
        LocalTime horaFin,
        String actividad
) {
}
