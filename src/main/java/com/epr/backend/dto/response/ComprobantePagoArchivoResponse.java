package com.epr.backend.dto.response;

public record ComprobantePagoArchivoResponse(
        String nombreArchivo,
        String contentType,
        byte[] contenido
) {
}
