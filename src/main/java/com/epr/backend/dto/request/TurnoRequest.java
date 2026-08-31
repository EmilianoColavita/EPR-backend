package com.epr.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public record TurnoRequest(
        @NotNull Long alumnoId,
        @NotNull LocalDate fecha,
        @NotNull LocalTime horaInicio,
        LocalTime horaFin,
        @NotBlank String actividad,
        String notas
) {
}
