package com.epr.backend.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PagoResponse(
        Long id,
        Long alumnoId,
        PlanCuotaResponse planCuota,
        LocalDate fecha,
        BigDecimal monto
) {
}
