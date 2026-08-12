package com.epr.backend.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;
import java.util.List;

public record PlanRequest(
        @NotBlank String titulo,
        Integer orden,
        Boolean activo,
        @DecimalMin(value = "0", inclusive = true) BigDecimal precio,
        List<String> items
) {
}
