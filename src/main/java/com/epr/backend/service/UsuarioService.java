package com.epr.backend.service;

import com.epr.backend.dto.request.UsuarioCreateRequest;
import com.epr.backend.dto.request.UsuarioUpdateRequest;
import com.epr.backend.dto.response.UsuarioResponse;
import com.epr.backend.entity.Rol;

import java.util.List;

public interface UsuarioService {
    List<UsuarioResponse> listar(Rol rol);

    UsuarioResponse obtenerPorId(Long id);

    UsuarioResponse obtenerPorEmail(String email);

    UsuarioResponse crear(UsuarioCreateRequest request);

    UsuarioResponse actualizar(Long id, UsuarioUpdateRequest request);

    UsuarioResponse actualizarActivo(Long id, boolean activo);

    void eliminar(Long id);
}
