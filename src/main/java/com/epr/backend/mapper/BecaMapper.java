package com.epr.backend.mapper;

import com.epr.backend.dto.response.BecaResponse;
import com.epr.backend.dto.response.NotaBecaResponse;
import com.epr.backend.entity.Beca;
import com.epr.backend.entity.NotaBeca;

import java.util.List;

public class BecaMapper {

    private BecaMapper() {
    }

    public static NotaBecaResponse toResponse(NotaBeca nota) {
        return new NotaBecaResponse(nota.getId(), nota.getFecha(), nota.getTexto());
    }

    public static BecaResponse toResponse(Beca beca) {
        List<NotaBecaResponse> notas = beca.getNotas().stream()
                .map(BecaMapper::toResponse)
                .toList();

        return new BecaResponse(
                beca.getId(),
                beca.getAlumno().getId(),
                beca.getFechaInicio(),
                beca.getFechaFinalizacion(),
                beca.getEstado(),
                notas
        );
    }
}
