package com.epr.backend.controller;

import com.epr.backend.dto.response.RutinaPdfArchivoResponse;
import com.epr.backend.dto.response.RutinaPdfResponse;
import com.epr.backend.service.RutinaPdfService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api/v1/rutinas-pdf/mias")
@RequiredArgsConstructor
public class RutinaPdfMiaController {

    private final RutinaPdfService rutinaPdfService;

    @GetMapping
    public ResponseEntity<List<RutinaPdfResponse>> mias(Authentication authentication) {
        return ResponseEntity.ok(rutinaPdfService.listarMias(authentication.getName()));
    }

    @GetMapping("/{rutinaPdfId}/archivo")
    public ResponseEntity<byte[]> descargar(@PathVariable Long rutinaPdfId, Authentication authentication) {
        RutinaPdfArchivoResponse archivo = rutinaPdfService.descargarMia(authentication.getName(), rutinaPdfId);

        ContentDisposition disposition = ContentDisposition.attachment()
                .filename(archivo.nombreArchivo(), StandardCharsets.UTF_8)
                .build();

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, disposition.toString())
                .body(archivo.contenido());
    }
}
