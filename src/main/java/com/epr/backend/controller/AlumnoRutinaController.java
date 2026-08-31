package com.epr.backend.controller;

import com.epr.backend.dto.response.RutinaMiaResponse;
import com.epr.backend.service.RutinaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/alumnos/{alumnoId}/rutina")
@RequiredArgsConstructor
public class AlumnoRutinaController {

    private final RutinaService rutinaService;

    @GetMapping
    public ResponseEntity<RutinaMiaResponse> obtener(@PathVariable Long alumnoId) {
        return ResponseEntity.ok(rutinaService.obtenerPorAlumno(alumnoId));
    }
}
