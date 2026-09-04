package com.epr.backend.dto.response;

import java.math.BigDecimal;

public record PlanCuotaResponse(
        Long id,
        String nombre,
        Integer duracionDias,
        BigDecimal precio,
        boolean activo
) {
}
