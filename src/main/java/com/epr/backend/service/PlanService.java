package com.epr.backend.service;

import com.epr.backend.dto.request.PlanCategoriaRequest;
import com.epr.backend.dto.request.PlanRequest;
import com.epr.backend.dto.response.PlanCardResponse;
import com.epr.backend.dto.response.PlanGroupResponse;

import java.util.List;

public interface PlanService {
    List<PlanGroupResponse> listarActivas();

    List<PlanGroupResponse> listarTodas();

    PlanGroupResponse crearCategoria(PlanCategoriaRequest request);

    PlanGroupResponse actualizarCategoria(Long id, PlanCategoriaRequest request);

    void eliminarCategoria(Long id);

    PlanCardResponse crearPlan(Long categoriaId, PlanRequest request);

    PlanCardResponse actualizarPlan(Long id, PlanRequest request);

    void eliminarPlan(Long id);
}
