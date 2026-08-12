package com.epr.backend.controller;

import com.epr.backend.dto.request.PlanCategoriaRequest;
import com.epr.backend.dto.request.PlanRequest;
import com.epr.backend.dto.response.PlanCardResponse;
import com.epr.backend.dto.response.PlanGroupResponse;
import com.epr.backend.service.PlanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
@RequestMapping("/api/v1/planes")
@RequiredArgsConstructor
public class PlanController {

    private final PlanService planService;

    @GetMapping
    public ResponseEntity<List<PlanGroupResponse>> listar() {
        return ResponseEntity.ok(planService.listarActivas());
    }

    @GetMapping("/todos")
    public ResponseEntity<List<PlanGroupResponse>> listarTodos() {
        return ResponseEntity.ok(planService.listarTodas());
    }

    @PostMapping("/categorias")
    public ResponseEntity<PlanGroupResponse> crearCategoria(@Valid @RequestBody PlanCategoriaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(planService.crearCategoria(request));
    }

    @PutMapping("/categorias/{id}")
    public ResponseEntity<PlanGroupResponse> actualizarCategoria(@PathVariable Long id,
                                                                   @Valid @RequestBody PlanCategoriaRequest request) {
        return ResponseEntity.ok(planService.actualizarCategoria(id, request));
    }

    @DeleteMapping("/categorias/{id}")
    public ResponseEntity<Void> eliminarCategoria(@PathVariable Long id) {
        planService.eliminarCategoria(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/categorias/{categoriaId}/cards")
    public ResponseEntity<PlanCardResponse> crearPlan(@PathVariable Long categoriaId,
                                                        @Valid @RequestBody PlanRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(planService.crearPlan(categoriaId, request));
    }

    @PutMapping("/cards/{id}")
    public ResponseEntity<PlanCardResponse> actualizarPlan(@PathVariable Long id,
                                                             @Valid @RequestBody PlanRequest request) {
        return ResponseEntity.ok(planService.actualizarPlan(id, request));
    }

    @DeleteMapping("/cards/{id}")
    public ResponseEntity<Void> eliminarPlan(@PathVariable Long id) {
        planService.eliminarPlan(id);
        return ResponseEntity.noContent().build();
    }
}
