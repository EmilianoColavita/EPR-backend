package com.epr.backend.controller;

import com.epr.backend.dto.request.RutinaRequest;
import com.epr.backend.dto.response.RutinaResponse;
import com.epr.backend.service.RutinaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/rutinas")
@RequiredArgsConstructor
public class RutinaController {

    private final RutinaService rutinaService;

    @GetMapping
    public ResponseEntity<List<RutinaResponse>> listar() {
        return ResponseEntity.ok(rutinaService.listarTodas());
    }

    @GetMapping("/mias")
    public ResponseEntity<List<RutinaResponse>> misRutinas(Authentication authentication) {
        return ResponseEntity.ok(rutinaService.listarPorEmailAlumno(authentication.getName()));
    }

    @GetMapping("/alumno/{alumnoId}")
    public ResponseEntity<List<RutinaResponse>> porAlumno(@PathVariable Long alumnoId) {
        return ResponseEntity.ok(rutinaService.listarPorAlumno(alumnoId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RutinaResponse> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(rutinaService.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<RutinaResponse> crear(@Valid @RequestBody RutinaRequest request,
                                                 Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(rutinaService.crear(request, authentication.getName()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RutinaResponse> actualizar(@PathVariable Long id,
                                                       @Valid @RequestBody RutinaRequest request) {
        return ResponseEntity.ok(rutinaService.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        rutinaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
