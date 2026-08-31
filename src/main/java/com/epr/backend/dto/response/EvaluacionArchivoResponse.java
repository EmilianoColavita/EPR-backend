package com.epr.backend.dto.response;

public record EvaluacionArchivoResponse(
        String nombreArchivo,
        String contentType,
        byte[] contenido
) {
}
