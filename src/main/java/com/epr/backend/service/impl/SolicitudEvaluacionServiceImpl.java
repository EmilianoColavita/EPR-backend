package com.epr.backend.service.impl;

import com.epr.backend.dto.request.EstadoSolicitudRequest;
import com.epr.backend.dto.request.SolicitudEvaluacionRequest;
import com.epr.backend.dto.response.SolicitudEvaluacionResponse;
import com.epr.backend.entity.SolicitudEvaluacion;
import com.epr.backend.exception.ResourceNotFoundException;
import com.epr.backend.mapper.SolicitudEvaluacionMapper;
import com.epr.backend.repository.SolicitudEvaluacionRepository;
import com.epr.backend.service.SolicitudEvaluacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SolicitudEvaluacionServiceImpl implements SolicitudEvaluacionService {

    private final SolicitudEvaluacionRepository solicitudEvaluacionRepository;

    @Override
    public SolicitudEvaluacionResponse crear(SolicitudEvaluacionRequest request) {
        SolicitudEvaluacion solicitud = SolicitudEvaluacion.builder()
                .nombreCompleto(request.nombreCompleto())
                .email(request.email())
                .telefono(request.telefono())
                .objetivo(request.objetivo())
                .fechaPreferida(request.fechaPreferida())
                .build();

        return SolicitudEvaluacionMapper.toResponse(solicitudEvaluacionRepository.save(solicitud));
    }

    @Override
    public List<SolicitudEvaluacionResponse> listar() {
        return solicitudEvaluacionRepository.findAll().stream()
                .map(SolicitudEvaluacionMapper::toResponse)
                .toList();
    }

    @Override
    public SolicitudEvaluacionResponse actualizarEstado(Long id, EstadoSolicitudRequest request) {
        SolicitudEvaluacion solicitud = solicitudEvaluacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitud no encontrada"));
        solicitud.setEstado(request.estado());
        return SolicitudEvaluacionMapper.toResponse(solicitudEvaluacionRepository.save(solicitud));
    }
}
