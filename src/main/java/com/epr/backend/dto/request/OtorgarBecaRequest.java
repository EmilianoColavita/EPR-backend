package com.epr.backend.dto.request;

import java.time.LocalDate;

public record OtorgarBecaRequest(
        LocalDate fechaInicio
) {
}
