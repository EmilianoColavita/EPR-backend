package com.epr.backend.service.impl;

import com.epr.backend.dto.request.OlvideContrasenaRequest;
import com.epr.backend.dto.request.RestablecerContrasenaRequest;
import com.epr.backend.entity.PasswordResetToken;
import com.epr.backend.entity.Usuario;
import com.epr.backend.exception.BadRequestException;
import com.epr.backend.exception.TooManyRequestsException;
import com.epr.backend.repository.PasswordResetTokenRepository;
import com.epr.backend.repository.UsuarioRepository;
import com.epr.backend.security.RateLimiter;
import com.epr.backend.service.EmailService;
import com.epr.backend.service.PasswordResetService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class PasswordResetServiceImpl implements PasswordResetService {

    private static final String TOKEN_INVALIDO = "El link venció o ya fue usado, pedí uno nuevo";
    private static final SecureRandom RANDOM = new SecureRandom();

    private final UsuarioRepository usuarioRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final RateLimiter rateLimiter;

    @Value("${epr.frontend.url}")
    private String frontendUrl;

    @Value("${epr.password-reset.expiration-minutes}")
    private long expirationMinutes;

    @Value("${epr.password-reset.rate-limit.window-minutes}")
    private long rateLimitWindowMinutes;

    @Value("${epr.password-reset.rate-limit.max-per-ip}")
    private int maxPorIp;

    @Value("${epr.password-reset.rate-limit.max-per-email}")
    private int maxPorEmail;

    /**
     * No revela si el email existe: si no existe o la cuenta no está activa, no hace nada y
     * retorna igual que en el caso exitoso. El mail se envía de forma asíncrona para que el
     * tiempo de respuesta tampoco delate si el email está registrado.
     * <p>
     * Rate limiting: por IP se responde 429 (no depende del email, no filtra nada). Por email, el
     * contador se consume exista o no la cuenta y al superarlo se responde igual que siempre (sin
     * enviar el mail), para que el límite tampoco permita saber si un email está registrado.
     */
    @Override
    @Transactional
    public void solicitarRestablecimiento(OlvideContrasenaRequest request, String ipCliente) {
        Duration ventana = Duration.ofMinutes(rateLimitWindowMinutes);

        long esperaIp = rateLimiter.intentar("pwd-reset:ip:" + ipCliente, maxPorIp, ventana);
        if (esperaIp > 0) {
            throw new TooManyRequestsException("Demasiados intentos, probá de nuevo más tarde", esperaIp);
        }

        String email = request.email().trim().toLowerCase(Locale.ROOT);
        if (rateLimiter.intentar("pwd-reset:email:" + email, maxPorEmail, ventana) > 0) {
            return;
        }

        usuarioRepository.findByEmail(request.email())
                .filter(Usuario::isActivo)
                .ifPresent(usuario -> {
                    tokenRepository.invalidarPendientes(usuario);

                    String token = generarToken();
                    tokenRepository.save(PasswordResetToken.builder()
                            .usuario(usuario)
                            .token(token)
                            .fechaExpiracion(LocalDateTime.now().plusMinutes(expirationMinutes))
                            .build());

                    String link = frontendUrl + "/restablecer-contrasena?token=" + token;
                    emailService.enviarRestablecerContrasena(usuario.getEmail(), usuario.getNombre(), link,
                            expirationMinutes);
                });
    }

    @Override
    @Transactional
    public void restablecer(RestablecerContrasenaRequest request) {
        if (tokenRepository.consumirSiVigente(request.token(), LocalDateTime.now()) == 0) {
            throw new BadRequestException(TOKEN_INVALIDO);
        }

        Usuario usuario = tokenRepository.findByToken(request.token())
                .map(PasswordResetToken::getUsuario)
                .orElseThrow(() -> new BadRequestException(TOKEN_INVALIDO));

        // Si la cuenta se desactivó después de pedir el link, la excepción revierte también el consumo del token.
        if (!usuario.isActivo()) {
            throw new BadRequestException(TOKEN_INVALIDO);
        }

        usuario.setPassword(passwordEncoder.encode(request.password()));
        usuarioRepository.save(usuario);
    }

    private String generarToken() {
        byte[] bytes = new byte[32];
        RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
