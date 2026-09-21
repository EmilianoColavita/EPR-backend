package com.epr.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RestablecerContrasenaRequest(
        @NotBlank String token,
        @NotBlank @Size(min = 6) String password
) {
}
