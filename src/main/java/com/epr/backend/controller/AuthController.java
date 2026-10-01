package com.epr.backend.controller;

import com.epr.backend.dto.request.LoginRequest;
import com.epr.backend.dto.request.OlvideContrasenaRequest;
import com.epr.backend.dto.request.RegisterRequest;
import com.epr.backend.dto.request.RestablecerContrasenaRequest;
import com.epr.backend.dto.response.LoginResponse;
import com.epr.backend.dto.response.UsuarioResponse;
import com.epr.backend.service.AuthService;
import com.epr.backend.service.PasswordResetService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final PasswordResetService passwordResetService;

    @PostMapping("/register")
    public ResponseEntity<UsuarioResponse> register(@Valid @RequestBody RegisterRequest request,
                                                    HttpServletRequest httpRequest) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(authService.register(request, httpRequest.getRemoteAddr()));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request,
                                               HttpServletRequest httpRequest) {
        return ResponseEntity.ok(authService.login(request, httpRequest.getRemoteAddr()));
    }

    @PostMapping("/olvide-contrasena")
    public ResponseEntity<Void> olvideContrasena(@Valid @RequestBody OlvideContrasenaRequest request,
                                                 HttpServletRequest httpRequest) {
        passwordResetService.solicitarRestablecimiento(request, httpRequest.getRemoteAddr());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/restablecer-contrasena")
    public ResponseEntity<Void> restablecerContrasena(@Valid @RequestBody RestablecerContrasenaRequest request) {
        passwordResetService.restablecer(request);
        return ResponseEntity.noContent().build();
    }
}
