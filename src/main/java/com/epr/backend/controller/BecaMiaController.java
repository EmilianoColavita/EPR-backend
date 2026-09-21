package com.epr.backend.controller;

import com.epr.backend.dto.response.BecaResponse;
import com.epr.backend.service.BecaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/becas/mia")
@RequiredArgsConstructor
public class BecaMiaController {

    private final BecaService becaService;

    @GetMapping
    public ResponseEntity<BecaResponse> mia(Authentication authentication) {
        return ResponseEntity.ok(becaService.obtenerMia(authentication.getName()));
    }
}
