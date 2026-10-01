package com.epr.backend.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record SolicitudEvaluacionRequest(
        @NotBlank @Size(max = 100, message = "El nombre no puede superar los 100 caracteres") String nombreCompleto,
        @NotBlank @Email @Size(max = 150, message = "El email no puede superar los 150 caracteres") String email,
        @Size(max = 30, message = "El teléfono no puede superar los 30 caracteres") String telefono,
        @Size(max = 2000, message = "El objetivo no puede superar los 2000 caracteres") String objetivo,
        LocalDate fechaPreferida
) {
}
