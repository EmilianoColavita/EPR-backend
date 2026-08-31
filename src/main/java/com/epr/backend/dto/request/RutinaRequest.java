package com.epr.backend.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record RutinaRequest(
        @NotBlank String nombre,
        String descripcion,
        @Valid List<DiaRutinaRequest> dias
) {
}
