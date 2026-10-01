package com.epr.backend.security;

import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RateLimiterTest {

    private final RateLimiter limiter = new RateLimiter();

    @Test
    void permiteHastaElMaximoYLuegoBloqueaConSegundosRestantes() {
        Duration ventana = Duration.ofMinutes(60);

        for (int i = 0; i < 3; i++) {
            assertEquals(0, limiter.intentar("a", 3, ventana));
        }

        long espera = limiter.intentar("a", 3, ventana);
        assertTrue(espera > 0 && espera <= 3600, "espera=" + espera);
    }

    @Test
    void lasClavesSonIndependientes() {
        Duration ventana = Duration.ofMinutes(60);
        limiter.intentar("a", 1, ventana);

        assertTrue(limiter.intentar("a", 1, ventana) > 0);
        assertEquals(0, limiter.intentar("b", 1, ventana));
    }

    @Test
    void laVentanaSeReiniciaAlVencer() throws InterruptedException {
        Duration ventana = Duration.ofMillis(50);
        limiter.intentar("a", 1, ventana);
        assertTrue(limiter.intentar("a", 1, ventana) > 0);

        Thread.sleep(80);

        assertEquals(0, limiter.intentar("a", 1, ventana));
    }

    @Test
    void reiniciarLiberaLaClave() {
        Duration ventana = Duration.ofMinutes(60);
        limiter.intentar("r", 1, ventana);
        assertTrue(limiter.intentar("r", 1, ventana) > 0);

        limiter.reiniciar("r");
        assertEquals(0, limiter.intentar("r", 1, ventana));
    }
}
