package com.epr.backend.dto.request;

import java.time.LocalDate;

public record FinalizarBecaRequest(
        LocalDate fechaFinalizacion
) {
}
