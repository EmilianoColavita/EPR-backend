package com.epr.backend.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record HorarioAsignadoRequest(
        @NotEmpty @Valid List<FranjaHorarioRequest> franjas,
        String notas
) {
}
