package com.epr.backend.mapper;

import com.epr.backend.dto.response.PlanCuotaResponse;
import com.epr.backend.entity.PlanCuota;

public class PlanCuotaMapper {

    private PlanCuotaMapper() {
    }

    public static PlanCuotaResponse toResponse(PlanCuota planCuota) {
        return new PlanCuotaResponse(
                planCuota.getId(),
                planCuota.getNombre(),
                planCuota.getDuracionDias(),
                planCuota.getPrecio(),
                planCuota.isActivo()
        );
    }
}
