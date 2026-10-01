package com.epr.backend.dto.projection;

import com.epr.backend.entity.EstadoComprobante;
import com.epr.backend.entity.Pago;
import com.epr.backend.entity.PlanCuota;
import com.epr.backend.entity.Usuario;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Comprobante sin el contenido del archivo, para los listados (evita traer el BLOB de cada fila).
 */
public record ComprobantePagoResumen(
        Long id,
        Usuario alumno,
        String nombreArchivo,
        String contentType,
        LocalDate fecha,
        PlanCuota planCuota,
        BigDecimal monto,
        EstadoComprobante estado,
        Pago pago,
        String notaRechazo,
        LocalDate fechaSubida
) {
}
