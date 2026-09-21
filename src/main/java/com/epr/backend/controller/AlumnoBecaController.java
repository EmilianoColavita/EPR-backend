package com.epr.backend.controller;

import com.epr.backend.dto.request.FinalizarBecaRequest;
import com.epr.backend.dto.request.NotaBecaRequest;
import com.epr.backend.dto.request.OtorgarBecaRequest;
import com.epr.backend.dto.response.BecaResponse;
import com.epr.backend.service.BecaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/alumnos/{alumnoId}/beca")
@RequiredArgsConstructor
public class AlumnoBecaController {

    private final BecaService becaService;

    @PostMapping
    public ResponseEntity<BecaResponse> otorgar(@PathVariable Long alumnoId,
                                                 @RequestBody(required = false) OtorgarBecaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(becaService.otorgar(alumnoId, request));
    }

    @GetMapping
    public ResponseEntity<BecaResponse> obtenerUltima(@PathVariable Long alumnoId) {
        return ResponseEntity.ok(becaService.obtenerUltima(alumnoId));
    }

    @PostMapping("/notas")
    public ResponseEntity<BecaResponse> agregarNota(@PathVariable Long alumnoId,
                                                     @Valid @RequestBody NotaBecaRequest request) {
        return ResponseEntity.ok(becaService.agregarNota(alumnoId, request));
    }

    @PostMapping("/finalizar")
    public ResponseEntity<BecaResponse> finalizar(@PathVariable Long alumnoId,
                                                   @RequestBody(required = false) FinalizarBecaRequest request) {
        return ResponseEntity.ok(becaService.finalizar(alumnoId, request));
    }
}
