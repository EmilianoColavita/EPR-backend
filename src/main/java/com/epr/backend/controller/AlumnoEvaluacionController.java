package com.epr.backend.controller;

import com.epr.backend.dto.response.EvaluacionArchivoResponse;
import com.epr.backend.dto.response.EvaluacionResponse;
import com.epr.backend.service.EvaluacionService;
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
@RequestMapping("/api/v1/alumnos/{alumnoId}/evaluaciones")
@RequiredArgsConstructor
public class AlumnoEvaluacionController {

    private final EvaluacionService evaluacionService;

    @PostMapping
    public ResponseEntity<EvaluacionResponse> subir(@PathVariable Long alumnoId,
                                                       @RequestParam("archivo") MultipartFile archivo) {
        return ResponseEntity.status(HttpStatus.CREATED).body(evaluacionService.subir(alumnoId, archivo));
    }

    @GetMapping
    public ResponseEntity<List<EvaluacionResponse>> listar(@PathVariable Long alumnoId) {
        return ResponseEntity.ok(evaluacionService.listarPorAlumno(alumnoId));
    }

    @GetMapping("/{evaluacionId}/archivo")
    public ResponseEntity<byte[]> descargar(@PathVariable Long alumnoId, @PathVariable Long evaluacionId) {
        EvaluacionArchivoResponse archivo = evaluacionService.descargarPorAlumno(alumnoId, evaluacionId);

        ContentDisposition disposition = ContentDisposition.attachment()
                .filename(archivo.nombreArchivo(), StandardCharsets.UTF_8)
                .build();

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, disposition.toString())
                .body(archivo.contenido());
    }

    @DeleteMapping("/{evaluacionId}")
    public ResponseEntity<Void> eliminar(@PathVariable Long alumnoId, @PathVariable Long evaluacionId) {
        evaluacionService.eliminar(alumnoId, evaluacionId);
        return ResponseEntity.noContent().build();
    }
}
