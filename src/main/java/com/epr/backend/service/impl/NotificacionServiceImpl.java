package com.epr.backend.service.impl;

import com.epr.backend.dto.response.NotificacionResponse;
import com.epr.backend.entity.Notificacion;
import com.epr.backend.entity.Usuario;
import com.epr.backend.exception.ResourceNotFoundException;
import com.epr.backend.mapper.NotificacionMapper;
import com.epr.backend.repository.NotificacionRepository;
import com.epr.backend.service.NotificacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificacionServiceImpl implements NotificacionService {

    private final NotificacionRepository notificacionRepository;

    @Override
    public List<NotificacionResponse> listarMias(String email) {
        return notificacionRepository.findByDestinatarioEmailOrderByFechaDesc(email).stream()
                .map(NotificacionMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public NotificacionResponse marcarLeida(String email, Long notificacionId) {
        Notificacion notificacion = notificacionRepository.findByIdAndDestinatarioEmail(notificacionId, email)
                .orElseThrow(() -> new ResourceNotFoundException("Notificación no encontrada"));

        notificacion.setLeida(true);

        return NotificacionMapper.toResponse(notificacionRepository.save(notificacion));
    }

    @Override
    @Transactional
    public void marcarTodasLeidas(String email) {
        List<Notificacion> pendientes = notificacionRepository.findByDestinatarioEmailAndLeidaFalse(email);
        pendientes.forEach(notificacion -> notificacion.setLeida(true));
        notificacionRepository.saveAll(pendientes);
    }

    @Override
    @Transactional
    public void crear(Usuario destinatario, String titulo, String mensaje, String href) {
        Notificacion notificacion = Notificacion.builder()
                .destinatario(destinatario)
                .titulo(titulo)
                .mensaje(mensaje)
                .href(href)
                .build();

        notificacionRepository.save(notificacion);
    }
}
