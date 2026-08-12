package com.epr.backend.mapper;

import com.epr.backend.dto.request.EjercicioRequest;
import com.epr.backend.dto.response.EjercicioResponse;
import com.epr.backend.dto.response.RutinaResponse;
import com.epr.backend.entity.Ejercicio;
import com.epr.backend.entity.Rutina;

import java.util.List;

public class RutinaMapper {

    private RutinaMapper() {
    }

    public static Ejercicio toEntity(EjercicioRequest request) {
        return Ejercicio.builder()
                .nombre(request.nombre())
                .series(request.series())
                .repeticiones(request.repeticiones())
                .pesoSugerido(request.pesoSugerido())
                .descansoSegundos(request.descansoSegundos())
                .notas(request.notas())
                .orden(request.orden())
                .build();
    }

    public static EjercicioResponse toResponse(Ejercicio ejercicio) {
        return new EjercicioResponse(
                ejercicio.getId(),
                ejercicio.getNombre(),
                ejercicio.getSeries(),
                ejercicio.getRepeticiones(),
                ejercicio.getPesoSugerido(),
                ejercicio.getDescansoSegundos(),
                ejercicio.getNotas(),
                ejercicio.getOrden()
        );
    }

    public static RutinaResponse toResponse(Rutina rutina) {
        List<EjercicioResponse> ejercicios = rutina.getEjercicios().stream()
                .map(RutinaMapper::toResponse)
                .toList();

        return new RutinaResponse(
                rutina.getId(),
                rutina.getNombre(),
                rutina.getDescripcion(),
                rutina.getAlumno().getId(),
                rutina.getAlumno().getNombre() + " " + rutina.getAlumno().getApellido(),
                rutina.getCreadoPor() != null ? rutina.getCreadoPor().getId() : null,
                rutina.isActiva(),
                rutina.getFechaCreacion(),
                rutina.getFechaActualizacion(),
                ejercicios
        );
    }
}
