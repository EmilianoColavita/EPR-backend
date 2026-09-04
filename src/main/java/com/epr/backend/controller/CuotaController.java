package com.epr.backend.controller;

import com.epr.backend.dto.response.EstadoCuentaResponse;
import com.epr.backend.dto.response.ResumenCuotasResponse;
import com.epr.backend.service.CuotaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/cuotas")
@RequiredArgsConstructor
public class CuotaController {

    private final CuotaService cuotaService;

    @GetMapping("/mi-estado")
    public ResponseEntity<EstadoCuentaResponse> miEstado(Authentication authentication) {
        return ResponseEntity.ok(cuotaService.obtenerMiEstado(authentication.getName()));
    }

    @GetMapping("/resumen")
    public ResponseEntity<ResumenCuotasResponse> resumen() {
        return ResponseEntity.ok(cuotaService.obtenerResumen());
    }
}
