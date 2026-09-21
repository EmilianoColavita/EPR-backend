package com.epr.backend.dto.response;

public record FotoPerfilArchivoResponse(
        String contentType,
        byte[] contenido
) {
}
