package com.epr.backend.dto.request;

import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record PlanCategoriaRequest(
        @NotBlank String titulo,
        Integer orden,
        Boolean activo,
        List<String> notas
) {
}
