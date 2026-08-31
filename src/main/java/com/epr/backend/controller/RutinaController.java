package com.epr.backend.controller;

import com.epr.backend.dto.request.AsignarRutinaRequest;
import com.epr.backend.dto.request.RutinaRequest;
import com.epr.backend.dto.request.SeleccionarDiaRequest;
import com.epr.backend.dto.response.RutinaListItemResponse;
import com.epr.backend.dto.response.RutinaMiaResponse;
import com.epr.backend.dto.response.RutinaResponse;
import com.epr.backend.service.RutinaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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
    public ResponseEntity<List<RutinaListItemResponse>> listar() {
        return ResponseEntity.ok(rutinaService.listar());
    }

    @PostMapping
    public ResponseEntity<RutinaResponse> crear(@Valid @RequestBody RutinaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(rutinaService.crear(request));
    }

    @GetMapping("/{rutinaId}")
    public ResponseEntity<RutinaResponse> obtener(@PathVariable Long rutinaId) {
        return ResponseEntity.ok(rutinaService.obtenerPorId(rutinaId));
    }

    @PutMapping("/{rutinaId}")
    public ResponseEntity<RutinaResponse> actualizar(@PathVariable Long rutinaId,
                                                       @Valid @RequestBody RutinaRequest request) {
        return ResponseEntity.ok(rutinaService.actualizar(rutinaId, request));
    }

    @PostMapping("/{rutinaId}/asignar")
    public ResponseEntity<RutinaMiaResponse> asignar(@PathVariable Long rutinaId,
                                                       @Valid @RequestBody AsignarRutinaRequest request) {
        return ResponseEntity.ok(rutinaService.asignar(rutinaId, request));
    }

    @GetMapping("/mia")
    public ResponseEntity<RutinaMiaResponse> mia(Authentication authentication) {
        return ResponseEntity.ok(rutinaService.obtenerMia(authentication.getName()));
    }

    @PostMapping("/mia/seleccionar-dia")
    public ResponseEntity<RutinaMiaResponse> seleccionarDia(@Valid @RequestBody SeleccionarDiaRequest request,
                                                              Authentication authentication) {
        return ResponseEntity.ok(rutinaService.seleccionarDia(authentication.getName(), request));
    }
}
