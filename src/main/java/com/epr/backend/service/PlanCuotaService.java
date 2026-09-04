package com.epr.backend.service;

import com.epr.backend.dto.request.PlanCuotaRequest;
import com.epr.backend.dto.response.PlanCuotaResponse;

import java.util.List;

public interface PlanCuotaService {

    PlanCuotaResponse crear(PlanCuotaRequest request);

    List<PlanCuotaResponse> listar();

    PlanCuotaResponse actualizar(Long id, PlanCuotaRequest request);

    PlanCuotaResponse actualizarActivo(Long id, boolean activo);
}
