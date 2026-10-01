package com.epr.backend.service;

import com.epr.backend.dto.request.EstadoSolicitudRequest;
import com.epr.backend.dto.request.SolicitudEvaluacionRequest;
import com.epr.backend.dto.response.SolicitudEvaluacionResponse;

import java.util.List;

public interface SolicitudEvaluacionService {
    SolicitudEvaluacionResponse crear(SolicitudEvaluacionRequest request, String ipCliente);

    List<SolicitudEvaluacionResponse> listar();

    SolicitudEvaluacionResponse actualizarEstado(Long id, EstadoSolicitudRequest request);

    void eliminar(Long id);
}
