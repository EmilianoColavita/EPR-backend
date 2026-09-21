package com.epr.backend.mapper;

import com.epr.backend.dto.response.NotificacionResponse;
import com.epr.backend.entity.Notificacion;

public class NotificacionMapper {

    private NotificacionMapper() {
    }

    public static NotificacionResponse toResponse(Notificacion notificacion) {
        return new NotificacionResponse(
                notificacion.getId(),
                notificacion.getTitulo(),
                notificacion.getMensaje(),
                notificacion.getHref(),
                notificacion.isLeida(),
                notificacion.getFecha()
        );
    }
}
