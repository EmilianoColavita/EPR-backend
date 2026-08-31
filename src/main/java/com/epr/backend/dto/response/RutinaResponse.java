package com.epr.backend.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record RutinaResponse(
        Long id,
        String nombre,
        String descripcion,
        LocalDateTime fechaCreacion,
        LocalDateTime fechaActualizacion,
        List<DiaRutinaResponse> dias
) {
}
