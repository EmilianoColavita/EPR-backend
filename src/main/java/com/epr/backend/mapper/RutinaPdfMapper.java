package com.epr.backend.mapper;

import com.epr.backend.dto.response.AlumnoResumenResponse;
import com.epr.backend.dto.response.RutinaPdfArchivoResponse;
import com.epr.backend.dto.response.RutinaPdfResponse;
import com.epr.backend.entity.RutinaPdf;
import com.epr.backend.entity.Usuario;

public class RutinaPdfMapper {

    private RutinaPdfMapper() {
    }

    public static RutinaPdfResponse toResponse(RutinaPdf rutinaPdf) {
        Usuario alumno = rutinaPdf.getAlumno();
        return new RutinaPdfResponse(
                rutinaPdf.getId(),
                new AlumnoResumenResponse(alumno.getId(), alumno.getNombre(), alumno.getApellido()),
                rutinaPdf.getNombreArchivo(),
                rutinaPdf.getFechaSubida()
        );
    }

    public static RutinaPdfArchivoResponse toArchivoResponse(RutinaPdf rutinaPdf) {
        return new RutinaPdfArchivoResponse(
                rutinaPdf.getNombreArchivo(),
                rutinaPdf.getContentType(),
                rutinaPdf.getArchivo()
        );
    }
}
