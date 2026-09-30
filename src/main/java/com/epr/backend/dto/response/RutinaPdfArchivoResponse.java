package com.epr.backend.dto.response;

public record RutinaPdfArchivoResponse(
        String nombreArchivo,
        String contentType,
        byte[] contenido
) {
}
