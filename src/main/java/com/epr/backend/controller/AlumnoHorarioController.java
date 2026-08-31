package com.epr.backend.controller;

import com.epr.backend.dto.request.HorarioAsignadoRequest;
import com.epr.backend.dto.response.HorarioAsignadoResponse;
import com.epr.backend.service.HorarioAsignadoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/alumnos/{alumnoId}/horario")
@RequiredArgsConstructor
public class AlumnoHorarioController {

    private final HorarioAsignadoService horarioAsignadoService;

    @GetMapping
    public ResponseEntity<HorarioAsignadoResponse> obtener(@PathVariable Long alumnoId) {
        return ResponseEntity.ok(horarioAsignadoService.obtenerActivoPorAlumno(alumnoId));
    }

    @PostMapping
    public ResponseEntity<HorarioAsignadoResponse> asignar(@PathVariable Long alumnoId,
                                                              @Valid @RequestBody HorarioAsignadoRequest request,
                                                              Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(horarioAsignadoService.asignar(alumnoId, request, authentication.getName()));
    }
}
