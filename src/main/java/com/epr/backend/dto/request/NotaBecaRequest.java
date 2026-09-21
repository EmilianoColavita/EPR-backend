package com.epr.backend.dto.request;

import jakarta.validation.constraints.NotBlank;

public record NotaBecaRequest(
        @NotBlank String texto
) {
}
