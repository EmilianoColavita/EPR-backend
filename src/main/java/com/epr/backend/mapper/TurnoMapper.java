package com.epr.backend.mapper;

import com.epr.backend.dto.response.AlumnoResumenResponse;
import com.epr.backend.dto.response.TurnoResponse;
import com.epr.backend.entity.Turno;
import com.epr.backend.entity.Usuario;

public class TurnoMapper {

    private TurnoMapper() {
    }

    public static TurnoResponse toResponse(Turno turno) {
        Usuario alumno = turno.getAlumno();

        return new TurnoResponse(
                turno.getId(),
                new AlumnoResumenResponse(alumno.getId(), alumno.getNombre(), alumno.getApellido()),
                turno.getFecha(),
                turno.getHoraInicio(),
                turno.getHoraFin(),
                turno.getActividad(),
                turno.getEstado(),
                turno.getNotas()
        );
    }
}
