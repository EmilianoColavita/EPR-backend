package com.epr.backend.controller;

import com.epr.backend.dto.request.PlanCuotaActivoRequest;
import com.epr.backend.dto.request.PlanCuotaRequest;
import com.epr.backend.dto.response.PlanCuotaResponse;
import com.epr.backend.service.PlanCuotaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/planes-cuota")
@RequiredArgsConstructor
public class PlanCuotaController {

    private final PlanCuotaService planCuotaService;

    @PostMapping
    public ResponseEntity<PlanCuotaResponse> crear(@Valid @RequestBody PlanCuotaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(planCuotaService.crear(request));
    }

    @GetMapping
    public ResponseEntity<List<PlanCuotaResponse>> listar() {
        return ResponseEntity.ok(planCuotaService.listar());
    }

    @PutMapping("/{id}")
    public ResponseEntity<PlanCuotaResponse> actualizar(@PathVariable Long id,
                                                          @Valid @RequestBody PlanCuotaRequest request) {
        return ResponseEntity.ok(planCuotaService.actualizar(id, request));
    }

    @PatchMapping("/{id}/activo")
    public ResponseEntity<PlanCuotaResponse> actualizarActivo(@PathVariable Long id,
                                                                @Valid @RequestBody PlanCuotaActivoRequest request) {
        return ResponseEntity.ok(planCuotaService.actualizarActivo(id, request.activo()));
    }
}
