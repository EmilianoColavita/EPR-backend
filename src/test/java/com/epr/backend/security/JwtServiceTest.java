package com.epr.backend.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtServiceTest {

    private static final String SECRETO = "un-secreto-de-prueba-suficientemente-largo-para-hs512-0123456789-abcdef";

    private JwtService service;

    @BeforeEach
    void setUp() {
        service = crear(SECRETO, new MockEnvironment());
    }

    @Test
    void tokenValidoParaUsuarioActivoConLaMismaVersion() {
        var token = service.generateToken(usuario(true, 0));
        assertTrue(service.isTokenValid(service.parsear(token), usuario(true, 0)));
    }

    @Test
    void tokenInvalidoSiLaCuentaSeDesactivo() {
        var token = service.generateToken(usuario(true, 0));
        assertFalse(service.isTokenValid(service.parsear(token), usuario(false, 0)));
    }

    @Test
    void tokenInvalidoSiCambioLaContrasena() {
        var token = service.generateToken(usuario(true, 0));
        assertFalse(service.isTokenValid(service.parsear(token), usuario(true, 1)));
    }

    @Test
    void noArrancaConSecretoCorto() {
        assertThrows(IllegalStateException.class, () -> crear("corto", new MockEnvironment()));
    }

    @Test
    void noArrancaEnProdConElSecretoDeEjemplo() {
        var env = new MockEnvironment();
        env.setActiveProfiles("prod");
        assertThrows(IllegalStateException.class,
                () -> crear("cambiar-este-secreto-en-produccion-por-uno-largo-y-aleatorio", env));
    }

    private static JwtService crear(String secreto, MockEnvironment env) {
        var jwt = new JwtService(env);
        ReflectionTestUtils.setField(jwt, "secret", secreto);
        ReflectionTestUtils.setField(jwt, "expirationMs", 60_000L);
        jwt.validarSecreto();
        return jwt;
    }

    private static UsuarioPrincipal usuario(boolean activo, int version) {
        return new UsuarioPrincipal("ana@epr.com", "hash", activo, version,
                List.of(new SimpleGrantedAuthority("ROLE_ALUMNO")));
    }
}
