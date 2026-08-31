package com.epr.backend.dto.request;

import com.epr.backend.entity.DiaSemana;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalTime;

public record FranjaHorarioRequest(
        @NotNull DiaSemana diaSemana,
        @NotNull LocalTime horaInicio,
        LocalTime horaFin,
        @NotBlank String actividad
) {
}
