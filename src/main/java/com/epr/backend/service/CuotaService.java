package com.epr.backend.service;

import com.epr.backend.dto.response.EstadoCuentaResponse;

public interface CuotaService {
    EstadoCuentaResponse obtenerMiEstado(String email);
}
