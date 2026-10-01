package com.epr.backend.security;

import com.epr.backend.exception.TooManyRequestsException;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Limitador de ventana fija en memoria. Solo es válido con una única instancia de la aplicación:
 * si se escala horizontalmente, cada instancia lleva su propio contador.
 */
@Component
public class RateLimiter {

    private static final int LIMPIEZA_A_PARTIR_DE = 10_000;
    private static final String MENSAJE_LIMITE = "Demasiados intentos, probá de nuevo más tarde";

    private record Ventana(long expiraMs, int cantidad) {
    }

    private final ConcurrentHashMap<String, Ventana> ventanas = new ConcurrentHashMap<>();

    /**
     * Intenta consumir un intento para la clave.
     *
     * @return 0 si el intento está permitido; si no, los segundos que faltan para que se libere la ventana.
     */
    public long intentar(String clave, int maximo, Duration ventana) {
        long ahora = System.currentTimeMillis();
        if (ventanas.size() > LIMPIEZA_A_PARTIR_DE) {
            ventanas.values().removeIf(v -> v.expiraMs() <= ahora);
        }

        AtomicLong segundosRestantes = new AtomicLong(0);
        ventanas.compute(clave, (k, actual) -> {
            if (actual == null || actual.expiraMs() <= ahora) {
                return new Ventana(ahora + ventana.toMillis(), 1);
            }
            if (actual.cantidad() >= maximo) {
                segundosRestantes.set(Math.max(1, (actual.expiraMs() - ahora + 999) / 1000));
                return actual;
            }
            return new Ventana(actual.expiraMs(), actual.cantidad() + 1);
        });
        return segundosRestantes.get();
    }

    /**
     * Como {@link #intentar}, pero si se superó el límite lanza 429 con el header Retry-After.
     */
    public void exigir(String clave, int maximo, Duration ventana) {
        long espera = intentar(clave, maximo, ventana);
        if (espera > 0) {
            throw new TooManyRequestsException(MENSAJE_LIMITE, espera);
        }
    }

    /**
     * Descarta el contador de la clave (ej: login exitoso reinicia los intentos fallidos del email).
     */
    public void reiniciar(String clave) {
        ventanas.remove(clave);
    }
}
