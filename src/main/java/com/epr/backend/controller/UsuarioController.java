package com.epr.backend.controller;

import com.epr.backend.dto.request.UsuarioActivoRequest;
import com.epr.backend.dto.request.UsuarioCreateRequest;
import com.epr.backend.dto.request.UsuarioUpdateRequest;
import com.epr.backend.dto.response.UsuarioResponse;
import com.epr.backend.entity.Rol;
import com.epr.backend.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @GetMapping("/me")
    public ResponseEntity<UsuarioResponse> me(Authentication authentication) {
        return ResponseEntity.ok(usuarioService.obtenerPorEmail(authentication.getName()));
    }

    @GetMapping
    public ResponseEntity<List<UsuarioResponse>> listar(@RequestParam(required = false) Rol rol) {
        return ResponseEntity.ok(usuarioService.listar(rol));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponse> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<UsuarioResponse> crear(@Valid @RequestBody UsuarioCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.crear(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponse> actualizar(@PathVariable Long id,
                                                        @Valid @RequestBody UsuarioUpdateRequest request) {
        return ResponseEntity.ok(usuarioService.actualizar(id, request));
    }

    @PatchMapping("/{id}/activo")
    public ResponseEntity<UsuarioResponse> actualizarActivo(@PathVariable Long id,
                                                              @Valid @RequestBody UsuarioActivoRequest request) {
        return ResponseEntity.ok(usuarioService.actualizarActivo(id, request.activo()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        usuarioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
