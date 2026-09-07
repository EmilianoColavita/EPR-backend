package com.epr.backend.controller;

import com.epr.backend.dto.response.ComprobantePagoArchivoResponse;
import com.epr.backend.dto.response.ComprobantePagoResponse;
import com.epr.backend.service.ComprobantePagoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api/v1/alumnos/{alumnoId}/comprobantes-pago")
@RequiredArgsConstructor
public class AlumnoComprobantePagoController {

    private final ComprobantePagoService comprobantePagoService;

    @GetMapping
    public ResponseEntity<List<ComprobantePagoResponse>> listar(@PathVariable Long alumnoId) {
        return ResponseEntity.ok(comprobantePagoService.listarPorAlumno(alumnoId));
    }

    @GetMapping("/{id}/archivo")
    public ResponseEntity<byte[]> descargar(@PathVariable Long alumnoId, @PathVariable Long id) {
        ComprobantePagoArchivoResponse archivo = comprobantePagoService.descargarPorAlumno(alumnoId, id);

        ContentDisposition disposition = ContentDisposition.attachment()
                .filename(archivo.nombreArchivo(), StandardCharsets.UTF_8)
                .build();

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(archivo.contentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, disposition.toString())
                .body(archivo.contenido());
    }
}
