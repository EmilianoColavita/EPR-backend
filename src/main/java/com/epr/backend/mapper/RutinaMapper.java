package com.epr.backend.mapper;

import com.epr.backend.dto.request.BloqueRutinaRequest;
import com.epr.backend.dto.request.DiaRutinaRequest;
import com.epr.backend.dto.request.EjercicioRequest;
import com.epr.backend.dto.response.BloqueRutinaResponse;
import com.epr.backend.dto.response.DiaRutinaResponse;
import com.epr.backend.dto.response.EjercicioResponse;
import com.epr.backend.dto.response.RutinaListItemResponse;
import com.epr.backend.dto.response.RutinaMiaResponse;
import com.epr.backend.dto.response.RutinaResponse;
import com.epr.backend.entity.AsignacionRutina;
import com.epr.backend.entity.BloqueRutina;
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
                .videoUrl(request.videoUrl())
                .orden(request.orden())
                .build();
    }

    public static BloqueRutina toEntity(BloqueRutinaRequest request) {
        BloqueRutina bloque = BloqueRutina.builder()
                .numero(request.numero())
                .nombre(request.nombre())
                .build();

        if (request.ejercicios() != null) {
            for (EjercicioRequest ejercicioRequest : request.ejercicios()) {
                bloque.addEjercicio(toEntity(ejercicioRequest));
            }
        }

        return bloque;
    }

    public static DiaRutina toEntity(DiaRutinaRequest request) {
        DiaRutina dia = DiaRutina.builder()
                .numero(request.numero())
                .nombre(request.nombre())
                .build();

        if (request.bloques() != null) {
            for (BloqueRutinaRequest bloqueRequest : request.bloques()) {
                dia.addBloque(toEntity(bloqueRequest));
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
                ejercicio.getVideoUrl(),
                ejercicio.getOrden()
        );
    }

    public static BloqueRutinaResponse toResponse(BloqueRutina bloque) {
        List<EjercicioResponse> ejercicios = bloque.getEjercicios().stream()
                .map(RutinaMapper::toResponse)
                .toList();

        return new BloqueRutinaResponse(
                bloque.getId(),
                bloque.getNumero(),
                bloque.getNombre(),
                ejercicios
        );
    }

    public static DiaRutinaResponse toResponse(DiaRutina dia) {
        List<BloqueRutinaResponse> bloques = dia.getBloques().stream()
                .map(RutinaMapper::toResponse)
                .toList();

        return new DiaRutinaResponse(
                dia.getId(),
                dia.getNumero(),
                dia.getNombre(),
                bloques
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
