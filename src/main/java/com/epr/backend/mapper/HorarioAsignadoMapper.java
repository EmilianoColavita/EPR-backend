package com.epr.backend.mapper;

import com.epr.backend.dto.request.FranjaHorarioRequest;
import com.epr.backend.dto.response.AlumnoResumenResponse;
import com.epr.backend.dto.response.FranjaHorarioResponse;
import com.epr.backend.dto.response.HorarioAsignadoResponse;
import com.epr.backend.entity.FranjaHorario;
import com.epr.backend.entity.HorarioAsignado;
import com.epr.backend.entity.Usuario;

import java.util.List;

public class HorarioAsignadoMapper {

    private HorarioAsignadoMapper() {
    }

    public static FranjaHorario toEntity(FranjaHorarioRequest request) {
        return FranjaHorario.builder()
                .diaSemana(request.diaSemana())
                .horaInicio(request.horaInicio())
                .horaFin(request.horaFin())
                .actividad(request.actividad())
                .build();
    }

    public static FranjaHorarioResponse toResponse(FranjaHorario franja) {
        return new FranjaHorarioResponse(
                franja.getId(),
                franja.getDiaSemana(),
                franja.getHoraInicio(),
                franja.getHoraFin(),
                franja.getActividad()
        );
    }

    public static HorarioAsignadoResponse toResponse(HorarioAsignado horario) {
        Usuario alumno = horario.getAlumno();
        List<FranjaHorarioResponse> franjas = horario.getFranjas().stream()
                .map(HorarioAsignadoMapper::toResponse)
                .toList();

        return new HorarioAsignadoResponse(
                horario.getId(),
                new AlumnoResumenResponse(alumno.getId(), alumno.getNombre(), alumno.getApellido()),
                franjas,
                horario.getNotas()
        );
    }
}
