package com.epr.backend.controller;

import com.epr.backend.dto.response.EvaluacionArchivoResponse;
import com.epr.backend.dto.response.EvaluacionResponse;
import com.epr.backend.service.EvaluacionService;
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
@RequestMapping("/api/v1/evaluaciones/mias")
@RequiredArgsConstructor
public class EvaluacionMiaController {

    private final EvaluacionService evaluacionService;

    @GetMapping
    public ResponseEntity<List<EvaluacionResponse>> mias(Authentication authentication) {
        return ResponseEntity.ok(evaluacionService.listarMias(authentication.getName()));
    }

    @GetMapping("/{evaluacionId}/archivo")
    public ResponseEntity<byte[]> descargar(@PathVariable Long evaluacionId, Authentication authentication) {
        EvaluacionArchivoResponse archivo = evaluacionService.descargarMia(authentication.getName(), evaluacionId);

        ContentDisposition disposition = ContentDisposition.attachment()
                .filename(archivo.nombreArchivo(), StandardCharsets.UTF_8)
                .build();

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, disposition.toString())
                .body(archivo.contenido());
    }
}
