package com.epr.backend.service;

import com.epr.backend.dto.request.UsuarioCreateRequest;
import com.epr.backend.dto.request.UsuarioUpdateRequest;
import com.epr.backend.dto.response.UsuarioResponse;

import java.util.List;

public interface UsuarioService {
    List<UsuarioResponse> listar();

    UsuarioResponse obtenerPorId(Long id);

    UsuarioResponse obtenerPorEmail(String email);

    UsuarioResponse crear(UsuarioCreateRequest request);

    UsuarioResponse actualizar(Long id, UsuarioUpdateRequest request);

    void eliminar(Long id);
}
