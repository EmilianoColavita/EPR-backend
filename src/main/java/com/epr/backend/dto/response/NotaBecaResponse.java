package com.epr.backend.dto.response;

import java.time.LocalDate;

public record NotaBecaResponse(
        Long id,
        LocalDate fecha,
        String texto
) {
}
