package com.epr.backend.controller;

import com.epr.backend.dto.response.RutinaPdfArchivoResponse;
import com.epr.backend.dto.response.RutinaPdfResponse;
import com.epr.backend.service.RutinaPdfService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api/v1/alumnos/{alumnoId}/rutinas-pdf")
@RequiredArgsConstructor
public class AlumnoRutinaPdfController {

    private final RutinaPdfService rutinaPdfService;

    @PostMapping
    public ResponseEntity<RutinaPdfResponse> subir(@PathVariable Long alumnoId,
                                                      @RequestParam("archivo") MultipartFile archivo) {
        return ResponseEntity.status(HttpStatus.CREATED).body(rutinaPdfService.subir(alumnoId, archivo));
    }

    @GetMapping
    public ResponseEntity<List<RutinaPdfResponse>> listar(@PathVariable Long alumnoId) {
        return ResponseEntity.ok(rutinaPdfService.listarPorAlumno(alumnoId));
    }

    @GetMapping("/{rutinaPdfId}/archivo")
    public ResponseEntity<byte[]> descargar(@PathVariable Long alumnoId, @PathVariable Long rutinaPdfId) {
        RutinaPdfArchivoResponse archivo = rutinaPdfService.descargarPorAlumno(alumnoId, rutinaPdfId);

        ContentDisposition disposition = ContentDisposition.attachment()
                .filename(archivo.nombreArchivo(), StandardCharsets.UTF_8)
                .build();

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, disposition.toString())
                .body(archivo.contenido());
    }

    @DeleteMapping("/{rutinaPdfId}")
    public ResponseEntity<Void> eliminar(@PathVariable Long alumnoId, @PathVariable Long rutinaPdfId) {
        rutinaPdfService.eliminar(alumnoId, rutinaPdfId);
        return ResponseEntity.noContent().build();
    }
}
