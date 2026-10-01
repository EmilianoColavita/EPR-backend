package com.epr.backend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.stream.Collectors;

@Service
public class JwtService {

    private static final int LONGITUD_MINIMA_SECRETO = 32;
    private static final String CLAIM_VERSION = "ver";

    private final Environment environment;

    @Value("${epr.jwt.secret}")
    private String secret;

    @Value("${epr.jwt.expiration-ms}")
    private long expirationMs;

    private SecretKey signingKey;

    public JwtService(Environment environment) {
        this.environment = environment;
    }

    /**
     * Falla al arrancar si el secreto es débil o, en producción, si es el de ejemplo:
     * con ese secreto cualquiera podría firmar tokens válidos (incluso de ADMIN).
     */
    @PostConstruct
    void validarSecreto() {
        if (secret == null || secret.getBytes(StandardCharsets.UTF_8).length < LONGITUD_MINIMA_SECRETO) {
            throw new IllegalStateException(
                    "JWT_SECRET debe tener al menos " + LONGITUD_MINIMA_SECRETO + " caracteres");
        }
        if (environment.acceptsProfiles(Profiles.of("prod")) && secret.contains("cambiar-este-secreto")) {
            throw new IllegalStateException("JWT_SECRET de producción no puede ser el valor de ejemplo");
        }
        signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(UserDetails userDetails) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationMs);
        String authorities = userDetails.getAuthorities().stream()
                .map(Object::toString)
                .collect(Collectors.joining(","));

        var builder = Jwts.builder()
                .subject(userDetails.getUsername())
                .claim("authorities", authorities)
                .issuedAt(now)
                .expiration(expiry);

        if (userDetails instanceof UsuarioPrincipal principal) {
            builder.claim(CLAIM_VERSION, principal.getTokenVersion());
        }

        return builder.signWith(signingKey).compact();
    }

    /**
     * Verifica firma y expiración (jjwt lanza JwtException si alguna falla).
     */
    public Claims parsear(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * El token es válido si corresponde al usuario, la cuenta sigue activa y no hubo un cambio
     * de contraseña posterior a su emisión.
     */
    public boolean isTokenValid(Claims claims, UserDetails userDetails) {
        if (!userDetails.getUsername().equals(claims.getSubject()) || !userDetails.isEnabled()) {
            return false;
        }
        if (userDetails instanceof UsuarioPrincipal principal) {
            Integer version = claims.get(CLAIM_VERSION, Integer.class);
            return version != null && version == principal.getTokenVersion();
        }
        return true;
    }
}
