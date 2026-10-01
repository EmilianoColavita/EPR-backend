package com.epr.backend.service.impl;

import com.epr.backend.dto.request.EstadoSolicitudRequest;
import com.epr.backend.dto.request.SolicitudEvaluacionRequest;
import com.epr.backend.dto.response.SolicitudEvaluacionResponse;
import com.epr.backend.entity.SolicitudEvaluacion;
import com.epr.backend.exception.ResourceNotFoundException;
import com.epr.backend.mapper.SolicitudEvaluacionMapper;
import com.epr.backend.repository.SolicitudEvaluacionRepository;
import com.epr.backend.security.RateLimiter;
import com.epr.backend.service.EmailService;
import com.epr.backend.service.SolicitudEvaluacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class SolicitudEvaluacionServiceImpl implements SolicitudEvaluacionService {

    private final SolicitudEvaluacionRepository solicitudEvaluacionRepository;
    private final EmailService emailService;
    private final RateLimiter rateLimiter;

    @Value("${epr.rate-limit.solicitud-evaluacion.window-minutes}")
    private long ventanaMinutos;

    @Value("${epr.rate-limit.solicitud-evaluacion.max-per-ip}")
    private int maxPorIp;

    @Value("${epr.rate-limit.solicitud-evaluacion.max-per-email}")
    private int maxPorEmail;

    @Override
    public SolicitudEvaluacionResponse crear(SolicitudEvaluacionRequest request, String ipCliente) {
        // Endpoint público que dispara un mail a la dirección recibida: sin límite se podría usar
        // para mandar spam desde nuestra cuenta SMTP.
        Duration ventana = Duration.ofMinutes(ventanaMinutos);
        rateLimiter.exigir("solicitud-evaluacion:ip:" + ipCliente, maxPorIp, ventana);
        rateLimiter.exigir("solicitud-evaluacion:email:" + request.email().trim().toLowerCase(Locale.ROOT),
                maxPorEmail, ventana);

        SolicitudEvaluacion solicitud = SolicitudEvaluacion.builder()
                .nombreCompleto(request.nombreCompleto())
                .email(request.email())
                .telefono(request.telefono())
                .objetivo(request.objetivo())
                .fechaPreferida(request.fechaPreferida())
                .build();

        SolicitudEvaluacionResponse response = SolicitudEvaluacionMapper.toResponse(solicitudEvaluacionRepository.save(solicitud));
        emailService.enviarConfirmacionSolicitudEvaluacion(response.email(), response.nombreCompleto());
        return response;
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

    @Override
    public void eliminar(Long id) {
        SolicitudEvaluacion solicitud = solicitudEvaluacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitud no encontrada"));
        solicitudEvaluacionRepository.delete(solicitud);
    }
}
