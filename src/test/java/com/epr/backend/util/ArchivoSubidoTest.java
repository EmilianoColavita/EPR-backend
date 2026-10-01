package com.epr.backend.util;

import com.epr.backend.exception.BadRequestException;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ArchivoSubidoTest {

    private static final byte[] PDF = "%PDF-1.7\n...".getBytes();
    private static final byte[] PNG = {(byte) 0x89, 'P', 'N', 'G', 0x0D, 0x0A, 0x1A, 0x0A, 0, 0};
    private static final Set<String> SOLO_PDF = Set.of(ArchivoSubido.PDF);

    @Test
    void aceptaPdfRealYDevuelveElTipoDetectado() {
        var archivo = new MockMultipartFile("archivo", "a.pdf", "application/octet-stream", PDF);
        var contenido = ArchivoSubido.leerYValidar(archivo, SOLO_PDF, 1024, "x");
        assertEquals(ArchivoSubido.PDF, contenido.contentType());
    }

    @Test
    void rechazaArchivoQueDeclaraPdfPeroNoLoEs() {
        var archivo = new MockMultipartFile("archivo", "a.pdf", "application/pdf", "<html></html>".getBytes());
        assertThrows(BadRequestException.class, () -> ArchivoSubido.leerYValidar(archivo, SOLO_PDF, 1024, "x"));
    }

    @Test
    void rechazaTipoRealNoPermitido() {
        var archivo = new MockMultipartFile("archivo", "a.pdf", "application/pdf", PNG);
        assertThrows(BadRequestException.class, () -> ArchivoSubido.leerYValidar(archivo, SOLO_PDF, 1024, "x"));
    }

    @Test
    void rechazaArchivoMasGrandeQueElMaximo() {
        var archivo = new MockMultipartFile("archivo", "a.pdf", "application/pdf", PDF);
        assertThrows(BadRequestException.class, () -> ArchivoSubido.leerYValidar(archivo, SOLO_PDF, 3, "x"));
    }

    @Test
    void nombreSeguroSacaRutasCaracteresDeControlYRecorta() {
        assertEquals("informe.pdf", ArchivoSubido.nombreSeguro("C:\\fake\\path\\informe.pdf", "def"));
        assertEquals("informe.pdf", ArchivoSubido.nombreSeguro("../../informe.pdf", "def"));
        assertEquals("ab.pdf", ArchivoSubido.nombreSeguro("a\r\nb.pdf", "def"));
        assertEquals("def", ArchivoSubido.nombreSeguro("  ", "def"));

        String largo = ArchivoSubido.nombreSeguro("x".repeat(500) + ".pdf", "def");
        assertEquals(200, largo.length());
        assertEquals(true, largo.endsWith(".pdf"));
    }
}
