package com.epr.backend.dto.response;

public record RutinaListItemResponse(
        Long id,
        String nombre,
        String descripcion,
        int cantidadDias,
        long cantidadAlumnosAsignados
) {
}
