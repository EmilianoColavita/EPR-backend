package com.epr.backend.dto.response;

import java.time.LocalDate;

public record EstadoCuentaResponse(
        boolean alDia,
        LocalDate proximoVencimiento
) {
    public static EstadoCuentaResponse sinCuota() {
        return new EstadoCuentaResponse(false, null);
    }
}
