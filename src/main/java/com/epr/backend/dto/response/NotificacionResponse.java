package com.epr.backend.dto.response;

import java.time.LocalDateTime;

public record NotificacionResponse(
        Long id,
        String titulo,
        String mensaje,
        String href,
        boolean leida,
        LocalDateTime fecha
) {
}
