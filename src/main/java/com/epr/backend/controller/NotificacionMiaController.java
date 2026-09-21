package com.epr.backend.controller;

import com.epr.backend.dto.response.NotificacionResponse;
import com.epr.backend.service.NotificacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notificaciones/mias")
@RequiredArgsConstructor
public class NotificacionMiaController {

    private final NotificacionService notificacionService;

    @GetMapping
    public ResponseEntity<List<NotificacionResponse>> mias(Authentication authentication) {
        return ResponseEntity.ok(notificacionService.listarMias(authentication.getName()));
    }

    @PostMapping("/{id}/leida")
    public ResponseEntity<NotificacionResponse> marcarLeida(@PathVariable Long id, Authentication authentication) {
        return ResponseEntity.ok(notificacionService.marcarLeida(authentication.getName(), id));
    }

    @PostMapping("/leer-todas")
    public ResponseEntity<Void> marcarTodasLeidas(Authentication authentication) {
        notificacionService.marcarTodasLeidas(authentication.getName());
        return ResponseEntity.ok().build();
    }
}
