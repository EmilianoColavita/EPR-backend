package com.epr.backend.dto.response;

import java.time.LocalDate;

public record CuentaAlumnoResponse(
        PlanCuotaResponse planActual,
        LocalDate fechaVencimiento,
        boolean alDia
) {
}
