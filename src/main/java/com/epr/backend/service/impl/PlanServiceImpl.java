package com.epr.backend.service.impl;

import com.epr.backend.dto.request.PlanCategoriaRequest;
import com.epr.backend.dto.request.PlanRequest;
import com.epr.backend.dto.response.PlanCardResponse;
import com.epr.backend.dto.response.PlanGroupResponse;
import com.epr.backend.entity.Plan;
import com.epr.backend.entity.PlanCategoria;
import com.epr.backend.exception.ResourceNotFoundException;
import com.epr.backend.mapper.PlanMapper;
import com.epr.backend.repository.PlanCategoriaRepository;
import com.epr.backend.repository.PlanRepository;
import com.epr.backend.service.PlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PlanServiceImpl implements PlanService {

    private final PlanCategoriaRepository planCategoriaRepository;
    private final PlanRepository planRepository;

    @Override
    public List<PlanGroupResponse> listarActivas() {
        return planCategoriaRepository.findByActivoTrueOrderByOrdenAsc().stream()
                .map(categoria -> PlanMapper.toGroupResponse(categoria, true))
                .toList();
    }

    @Override
    public List<PlanGroupResponse> listarTodas() {
        return planCategoriaRepository.findAllByOrderByOrdenAsc().stream()
                .map(categoria -> PlanMapper.toGroupResponse(categoria, false))
                .toList();
    }

    @Override
    public PlanGroupResponse crearCategoria(PlanCategoriaRequest request) {
        PlanCategoria categoria = PlanCategoria.builder()
                .titulo(request.titulo())
                .orden(request.orden())
                .activo(request.activo() == null || request.activo())
                .notas(request.notas() == null ? new ArrayList<>() : new ArrayList<>(request.notas()))
                .build();
        return PlanMapper.toGroupResponse(planCategoriaRepository.save(categoria), false);
    }

    @Override
    @Transactional
    public PlanGroupResponse actualizarCategoria(Long id, PlanCategoriaRequest request) {
        PlanCategoria categoria = buscarCategoriaPorId(id);
        categoria.setTitulo(request.titulo());
        categoria.setOrden(request.orden());
        categoria.setActivo(request.activo() == null || request.activo());
        categoria.getNotas().clear();
        if (request.notas() != null) {
            categoria.getNotas().addAll(request.notas());
        }
        return PlanMapper.toGroupResponse(categoria, false);
    }

    @Override
    public void eliminarCategoria(Long id) {
        planCategoriaRepository.delete(buscarCategoriaPorId(id));
    }

    @Override
    @Transactional
    public PlanCardResponse crearPlan(Long categoriaId, PlanRequest request) {
        PlanCategoria categoria = buscarCategoriaPorId(categoriaId);
        Plan plan = Plan.builder()
                .titulo(request.titulo())
                .orden(request.orden())
                .activo(request.activo() == null || request.activo())
                .precio(request.precio())
                .items(request.items() == null ? new ArrayList<>() : new ArrayList<>(request.items()))
                .build();
        categoria.addPlan(plan);
        return PlanMapper.toCardResponse(planRepository.save(plan));
    }

    @Override
    @Transactional
    public PlanCardResponse actualizarPlan(Long id, PlanRequest request) {
        Plan plan = buscarPlanPorId(id);
        plan.setTitulo(request.titulo());
        plan.setOrden(request.orden());
        plan.setActivo(request.activo() == null || request.activo());
        plan.setPrecio(request.precio());
        plan.getItems().clear();
        if (request.items() != null) {
            plan.getItems().addAll(request.items());
        }
        return PlanMapper.toCardResponse(plan);
    }

    @Override
    public void eliminarPlan(Long id) {
        planRepository.delete(buscarPlanPorId(id));
    }

    private PlanCategoria buscarCategoriaPorId(Long id) {
        return planCategoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría de planes no encontrada"));
    }

    private Plan buscarPlanPorId(Long id) {
        return planRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Plan no encontrado"));
    }
}
