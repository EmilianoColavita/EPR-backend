package com.epr.backend.dto.request;

import jakarta.validation.constraints.NotNull;

public record AsignarRutinaRequest(
        @NotNull Long alumnoId
) {
}
