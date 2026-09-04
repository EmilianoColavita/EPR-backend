package com.epr.backend.controller;

import com.epr.backend.dto.request.PagoRequest;
import com.epr.backend.dto.response.CuentaAlumnoResponse;
import com.epr.backend.dto.response.PagoResponse;
import com.epr.backend.service.CuotaService;
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

import java.util.List;

@RestController
@RequestMapping("/api/v1/alumnos/{alumnoId}")
@RequiredArgsConstructor
public class AlumnoCuentaController {

    private final CuotaService cuotaService;

    @GetMapping("/cuenta")
    public ResponseEntity<CuentaAlumnoResponse> obtenerCuenta(@PathVariable Long alumnoId) {
        return ResponseEntity.ok(cuotaService.obtenerCuenta(alumnoId));
    }

    @GetMapping("/pagos")
    public ResponseEntity<List<PagoResponse>> listarPagos(@PathVariable Long alumnoId) {
        return ResponseEntity.ok(cuotaService.listarPagos(alumnoId));
    }

    @PostMapping("/pagos")
    public ResponseEntity<PagoResponse> registrarPago(@PathVariable Long alumnoId,
                                                        @Valid @RequestBody PagoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cuotaService.registrarPago(alumnoId, request));
    }
}
