package com.epr.backend.mapper;

import com.epr.backend.dto.response.PlanCardResponse;
import com.epr.backend.dto.response.PlanGroupResponse;
import com.epr.backend.entity.Plan;
import com.epr.backend.entity.PlanCategoria;

import java.util.List;

public class PlanMapper {

    private PlanMapper() {
    }

    public static PlanCardResponse toCardResponse(Plan plan) {
        return new PlanCardResponse(
                plan.getId(),
                plan.getTitulo(),
                plan.getItems(),
                plan.getPrecio()
        );
    }

    public static PlanGroupResponse toGroupResponse(PlanCategoria categoria, boolean soloActivos) {
        List<PlanCardResponse> cards = categoria.getPlanes().stream()
                .filter(plan -> !soloActivos || plan.isActivo())
                .map(PlanMapper::toCardResponse)
                .toList();
        List<String> notas = categoria.getNotas();
        return new PlanGroupResponse(
                categoria.getId(),
                categoria.getTitulo(),
                cards,
                (notas == null || notas.isEmpty()) ? null : notas
        );
    }
}
