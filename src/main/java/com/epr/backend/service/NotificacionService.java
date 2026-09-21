package com.epr.backend.service;

import com.epr.backend.dto.response.NotificacionResponse;
import com.epr.backend.entity.Usuario;

import java.util.List;

public interface NotificacionService {

    List<NotificacionResponse> listarMias(String email);

    NotificacionResponse marcarLeida(String email, Long notificacionId);

    void marcarTodasLeidas(String email);

    void crear(Usuario destinatario, String titulo, String mensaje, String href);
}
