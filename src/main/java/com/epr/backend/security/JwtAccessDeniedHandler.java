package com.epr.backend.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Se invoca cuando un usuario YA AUTENTICADO no tiene el rol/permiso requerido.
 * Debe devolver siempre 403 — el 401 queda reservado para JwtAuthenticationEntryPoint
 * (no autenticado / token inválido o expirado). Mezclar ambos casos bajo 401 hace que
 * el frontend, que desloguea ante cualquier 401, cierre la sesión de usuarios válidos
 * que simplemente pegaron a un endpoint fuera de su rol.
 */
@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request,
                        HttpServletResponse response,
                        AccessDeniedException accessDeniedException) throws IOException {
        String body = """
                {"timestamp":"%s","status":%d,"error":"%s","message":"%s","path":"%s"}""".formatted(
                LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                HttpStatus.FORBIDDEN.value(),
                HttpStatus.FORBIDDEN.getReasonPhrase(),
                escapeJson("No tenés permisos para realizar esta acción"),
                escapeJson(request.getRequestURI())
        );

        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(body);
    }

    private String escapeJson(String value) {
        return value == null ? "" : value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
