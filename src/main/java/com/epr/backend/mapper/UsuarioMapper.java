package com.epr.backend.mapper;

import com.epr.backend.dto.response.UsuarioResponse;
import com.epr.backend.entity.Usuario;

import java.time.LocalDate;

public class UsuarioMapper {

    private UsuarioMapper() {
    }

    public static UsuarioResponse toResponse(Usuario usuario) {
        return toResponse(usuario, false, false, null);
    }

    public static UsuarioResponse toResponse(Usuario usuario, boolean becado, boolean alDia, LocalDate fechaVencimiento) {
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getApellido(),
                usuario.getEmail(),
                usuario.getTelefono(),
                usuario.getRol(),
                usuario.isActivo(),
                usuario.getFechaRegistro(),
                becado,
                alDia,
                fechaVencimiento
        );
    }
}
