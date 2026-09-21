package com.epr.backend.mapper;

import com.epr.backend.dto.response.PlanCardResponse;
import com.epr.backend.dto.response.PlanGroupResponse;
import com.epr.backend.entity.Plan;
import com.epr.backend.entity.PlanCategoria;

import java.util.List;

public class PlanMapper {

    private PlanMapper() {
    }

    // orden y activo solo se exponen en la vista admin (soloActivos = false)
    public static PlanCardResponse toCardResponse(Plan plan) {
        return toCardResponse(plan, false);
    }

    private static PlanCardResponse toCardResponse(Plan plan, boolean soloActivos) {
        return new PlanCardResponse(
                plan.getId(),
                plan.getTitulo(),
                plan.getItems(),
                plan.getPrecio(),
                soloActivos ? null : plan.getOrden(),
                soloActivos ? null : plan.isActivo()
        );
    }

    public static PlanGroupResponse toGroupResponse(PlanCategoria categoria, boolean soloActivos) {
        List<PlanCardResponse> cards = categoria.getPlanes().stream()
                .filter(plan -> !soloActivos || plan.isActivo())
                .map(plan -> toCardResponse(plan, soloActivos))
                .toList();
        List<String> notas = categoria.getNotas();
        return new PlanGroupResponse(
                categoria.getId(),
                categoria.getTitulo(),
                cards,
                (notas == null || notas.isEmpty()) ? null : notas,
                soloActivos ? null : categoria.getOrden(),
                soloActivos ? null : categoria.isActivo()
        );
    }
}
