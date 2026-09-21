package com.epr.backend.service;

import com.epr.backend.dto.request.OlvideContrasenaRequest;
import com.epr.backend.dto.request.RestablecerContrasenaRequest;

public interface PasswordResetService {
    void solicitarRestablecimiento(OlvideContrasenaRequest request, String ipCliente);

    void restablecer(RestablecerContrasenaRequest request);
}
