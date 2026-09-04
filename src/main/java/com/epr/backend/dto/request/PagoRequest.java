package com.epr.backend.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PagoRequest(
        @NotNull Long planCuotaId,
        LocalDate fecha,
        @DecimalMin(value = "0", inclusive = true) BigDecimal monto
) {
}
