package com.epr.backend.service;

import com.epr.backend.dto.request.FinalizarBecaRequest;
import com.epr.backend.dto.request.NotaBecaRequest;
import com.epr.backend.dto.request.OtorgarBecaRequest;
import com.epr.backend.dto.response.BecaResponse;

public interface BecaService {

    BecaResponse otorgar(Long alumnoId, OtorgarBecaRequest request);

    BecaResponse obtenerUltima(Long alumnoId);

    BecaResponse agregarNota(Long alumnoId, NotaBecaRequest request);

    BecaResponse finalizar(Long alumnoId, FinalizarBecaRequest request);

    BecaResponse obtenerMia(String email);
}
