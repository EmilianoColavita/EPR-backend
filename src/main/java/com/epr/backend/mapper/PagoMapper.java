package com.epr.backend.mapper;

import com.epr.backend.dto.response.PagoResponse;
import com.epr.backend.entity.Pago;

public class PagoMapper {

    private PagoMapper() {
    }

    public static PagoResponse toResponse(Pago pago) {
        return new PagoResponse(
                pago.getId(),
                pago.getAlumno().getId(),
                PlanCuotaMapper.toResponse(pago.getPlanCuota()),
                pago.getFecha(),
                pago.getMonto()
        );
    }
}
