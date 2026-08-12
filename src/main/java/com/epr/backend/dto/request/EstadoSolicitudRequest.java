package com.epr.backend.dto.request;

import com.epr.backend.entity.EstadoSolicitud;
import jakarta.validation.constraints.NotNull;

public record EstadoSolicitudRequest(
        @NotNull EstadoSolicitud estado
) {
}
