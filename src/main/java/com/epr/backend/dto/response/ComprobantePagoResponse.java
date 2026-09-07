package com.epr.backend.dto.response;

import com.epr.backend.entity.EstadoComprobante;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ComprobantePagoResponse(
        Long id,
        AlumnoResumenResponse alumno,
        String nombreArchivo,
        String contentType,
        LocalDate fecha,
        PlanCuotaResponse planCuota,
        BigDecimal monto,
        EstadoComprobante estado,
        PagoResponse pago,
        String notaRechazo,
        LocalDate fechaSubida
) {
}
