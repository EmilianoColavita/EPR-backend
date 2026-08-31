package com.epr.backend.mapper;

import com.epr.backend.dto.request.DiaRutinaRequest;
import com.epr.backend.dto.request.EjercicioRequest;
import com.epr.backend.dto.response.DiaRutinaResponse;
import com.epr.backend.dto.response.EjercicioResponse;
import com.epr.backend.dto.response.RutinaListItemResponse;
import com.epr.backend.dto.response.RutinaMiaResponse;
import com.epr.backend.dto.response.RutinaResponse;
import com.epr.backend.entity.AsignacionRutina;
import com.epr.backend.entity.DiaRutina;
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

    public static DiaRutina toEntity(DiaRutinaRequest request) {
        DiaRutina dia = DiaRutina.builder()
                .numero(request.numero())
                .nombre(request.nombre())
                .build();

        if (request.ejercicios() != null) {
            for (EjercicioRequest ejercicioRequest : request.ejercicios()) {
                dia.addEjercicio(toEntity(ejercicioRequest));
            }
        }

        return dia;
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

    public static DiaRutinaResponse toResponse(DiaRutina dia) {
        List<EjercicioResponse> ejercicios = dia.getEjercicios().stream()
                .map(RutinaMapper::toResponse)
                .toList();

        return new DiaRutinaResponse(
                dia.getId(),
                dia.getNumero(),
                dia.getNombre(),
                ejercicios
        );
    }

    public static RutinaResponse toResponse(Rutina rutina) {
        List<DiaRutinaResponse> dias = rutina.getDias().stream()
                .map(RutinaMapper::toResponse)
                .toList();

        return new RutinaResponse(
                rutina.getId(),
                rutina.getNombre(),
                rutina.getDescripcion(),
                rutina.getFechaCreacion(),
                rutina.getFechaActualizacion(),
                dias
        );
    }

    public static RutinaListItemResponse toListItem(Rutina rutina, long cantidadAlumnosAsignados) {
        return new RutinaListItemResponse(
                rutina.getId(),
                rutina.getNombre(),
                rutina.getDescripcion(),
                rutina.getDias().size(),
                cantidadAlumnosAsignados
        );
    }

    public static RutinaMiaResponse toMiaResponse(AsignacionRutina asignacion, Long diaSugeridoId) {
        Rutina rutina = asignacion.getRutina();
        List<DiaRutinaResponse> dias = rutina.getDias().stream()
                .map(RutinaMapper::toResponse)
                .toList();

        return new RutinaMiaResponse(
                rutina.getId(),
                rutina.getNombre(),
                rutina.getDescripcion(),
                dias,
                diaSugeridoId,
                asignacion.getUltimoDiaEntrenado() != null ? asignacion.getUltimoDiaEntrenado().getId() : null
        );
    }
}
