package com.epr.backend.controller;

import com.epr.backend.dto.request.EstadoSolicitudRequest;
import com.epr.backend.dto.request.SolicitudEvaluacionRequest;
import com.epr.backend.dto.response.SolicitudEvaluacionResponse;
import com.epr.backend.service.SolicitudEvaluacionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/evaluaciones")
@RequiredArgsConstructor
public class SolicitudEvaluacionController {

    private final SolicitudEvaluacionService solicitudEvaluacionService;

    @PostMapping
    public ResponseEntity<SolicitudEvaluacionResponse> crear(@Valid @RequestBody SolicitudEvaluacionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(solicitudEvaluacionService.crear(request));
    }

    @GetMapping
    public ResponseEntity<List<SolicitudEvaluacionResponse>> listar() {
        return ResponseEntity.ok(solicitudEvaluacionService.listar());
    }

    @PutMapping("/{id}/estado")
    public ResponseEntity<SolicitudEvaluacionResponse> actualizarEstado(@PathVariable Long id,
                                                                          @Valid @RequestBody EstadoSolicitudRequest request) {
        return ResponseEntity.ok(solicitudEvaluacionService.actualizarEstado(id, request));
    }
}
