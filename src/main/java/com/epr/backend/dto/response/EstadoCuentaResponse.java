package com.epr.backend.dto.response;

import java.time.LocalDate;

public record EstadoCuentaResponse(
        boolean alDia,
        LocalDate proximoVencimiento,
        boolean becado
) {
    public static EstadoCuentaResponse sinCuota(boolean becado) {
        return new EstadoCuentaResponse(false, null, becado);
    }
}
