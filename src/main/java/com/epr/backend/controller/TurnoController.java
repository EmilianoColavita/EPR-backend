package com.epr.backend.controller;

import com.epr.backend.dto.request.EstadoTurnoRequest;
import com.epr.backend.dto.request.TurnoRequest;
import com.epr.backend.dto.response.TurnoResponse;
import com.epr.backend.service.TurnoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/turnos")
@RequiredArgsConstructor
public class TurnoController {

    private final TurnoService turnoService;

    @GetMapping
    public ResponseEntity<List<TurnoResponse>> listar(@RequestParam(required = false) LocalDate desde,
                                                         @RequestParam(required = false) LocalDate hasta,
                                                         @RequestParam(required = false) Long alumnoId) {
        return ResponseEntity.ok(turnoService.listar(desde, hasta, alumnoId));
    }

    @PostMapping
    public ResponseEntity<TurnoResponse> crear(@Valid @RequestBody TurnoRequest request,
                                                 Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED).body(turnoService.crear(request, authentication.getName()));
    }

    @PutMapping("/{turnoId}")
    public ResponseEntity<TurnoResponse> actualizar(@PathVariable Long turnoId,
                                                       @Valid @RequestBody TurnoRequest request) {
        return ResponseEntity.ok(turnoService.actualizar(turnoId, request));
    }

    @PatchMapping("/{turnoId}/estado")
    public ResponseEntity<TurnoResponse> cambiarEstado(@PathVariable Long turnoId,
                                                          @Valid @RequestBody EstadoTurnoRequest request) {
        return ResponseEntity.ok(turnoService.cambiarEstado(turnoId, request));
    }

    @GetMapping("/mios")
    public ResponseEntity<List<TurnoResponse>> mios(@RequestParam(required = false) LocalDate desde,
                                                       @RequestParam(required = false) LocalDate hasta,
                                                       Authentication authentication) {
        return ResponseEntity.ok(turnoService.listarMios(authentication.getName(), desde, hasta));
    }
}
