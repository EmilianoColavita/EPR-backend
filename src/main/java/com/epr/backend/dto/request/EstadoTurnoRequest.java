package com.epr.backend.dto.request;

import com.epr.backend.entity.EstadoTurno;
import jakarta.validation.constraints.NotNull;

public record EstadoTurnoRequest(
        @NotNull EstadoTurno estado
) {
}
