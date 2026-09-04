package com.epr.backend.service.impl;

import com.epr.backend.dto.request.PlanCuotaRequest;
import com.epr.backend.dto.response.PlanCuotaResponse;
import com.epr.backend.entity.PlanCuota;
import com.epr.backend.exception.ResourceNotFoundException;
import com.epr.backend.mapper.PlanCuotaMapper;
import com.epr.backend.repository.PlanCuotaRepository;
import com.epr.backend.service.PlanCuotaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlanCuotaServiceImpl implements PlanCuotaService {

    private final PlanCuotaRepository planCuotaRepository;

    @Override
    @Transactional
    public PlanCuotaResponse crear(PlanCuotaRequest request) {
        PlanCuota planCuota = PlanCuota.builder()
                .nombre(request.nombre())
                .duracionDias(request.duracionDias())
                .precio(request.precio())
                .build();
        return PlanCuotaMapper.toResponse(planCuotaRepository.save(planCuota));
    }

    @Override
    public List<PlanCuotaResponse> listar() {
        return planCuotaRepository.findAll().stream()
                .map(PlanCuotaMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public PlanCuotaResponse actualizar(Long id, PlanCuotaRequest request) {
        PlanCuota planCuota = buscar(id);
        planCuota.setNombre(request.nombre());
        planCuota.setDuracionDias(request.duracionDias());
        planCuota.setPrecio(request.precio());
        return PlanCuotaMapper.toResponse(planCuotaRepository.save(planCuota));
    }

    @Override
    @Transactional
    public PlanCuotaResponse actualizarActivo(Long id, boolean activo) {
        PlanCuota planCuota = buscar(id);
        planCuota.setActivo(activo);
        return PlanCuotaMapper.toResponse(planCuotaRepository.save(planCuota));
    }

    private PlanCuota buscar(Long id) {
        return planCuotaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Plan de cuota no encontrado"));
    }
}
