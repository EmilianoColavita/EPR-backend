package com.epr.backend.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public record SolicitudEvaluacionRequest(
        @NotBlank String nombreCompleto,
        @NotBlank @Email String email,
        String telefono,
        String objetivo,
        LocalDate fechaPreferida
) {
}
