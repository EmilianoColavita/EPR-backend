package com.epr.backend.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record OlvideContrasenaRequest(
        @NotBlank @Email String email
) {
}
