package com.epr.backend.mapper;

import com.epr.backend.dto.response.SolicitudEvaluacionResponse;
import com.epr.backend.entity.SolicitudEvaluacion;

public class SolicitudEvaluacionMapper {

    private SolicitudEvaluacionMapper() {
    }

    public static SolicitudEvaluacionResponse toResponse(SolicitudEvaluacion solicitud) {
        return new SolicitudEvaluacionResponse(
                solicitud.getId(),
                solicitud.getNombreCompleto(),
                solicitud.getEmail(),
                solicitud.getTelefono(),
                solicitud.getObjetivo(),
                solicitud.getFechaPreferida(),
                solicitud.getEstado(),
                solicitud.getFechaSolicitud()
        );
    }
}
