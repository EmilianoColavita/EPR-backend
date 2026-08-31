package com.epr.backend.dto.response;

import java.util.List;

public record RutinaMiaResponse(
        Long id,
        String nombre,
        String descripcion,
        List<DiaRutinaResponse> dias,
        Long diaSugeridoId,
        Long ultimoDiaEntrenadoId
) {
}
