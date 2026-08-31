package com.epr.backend.mapper;

import com.epr.backend.dto.response.AlumnoResumenResponse;
import com.epr.backend.dto.response.EvaluacionArchivoResponse;
import com.epr.backend.dto.response.EvaluacionResponse;
import com.epr.backend.entity.Evaluacion;
import com.epr.backend.entity.Usuario;

public class EvaluacionMapper {

    private EvaluacionMapper() {
    }

    public static EvaluacionResponse toResponse(Evaluacion evaluacion) {
        Usuario alumno = evaluacion.getAlumno();
        return new EvaluacionResponse(
                evaluacion.getId(),
                new AlumnoResumenResponse(alumno.getId(), alumno.getNombre(), alumno.getApellido()),
                evaluacion.getNombreArchivo(),
                evaluacion.getFechaSubida()
        );
    }

    public static EvaluacionArchivoResponse toArchivoResponse(Evaluacion evaluacion) {
        return new EvaluacionArchivoResponse(
                evaluacion.getNombreArchivo(),
                evaluacion.getContentType(),
                evaluacion.getArchivo()
        );
    }
}
