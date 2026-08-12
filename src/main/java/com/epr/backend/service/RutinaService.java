package com.epr.backend.service;

import com.epr.backend.dto.request.RutinaRequest;
import com.epr.backend.dto.response.RutinaResponse;

import java.util.List;

public interface RutinaService {
    List<RutinaResponse> listarTodas();

    List<RutinaResponse> listarPorAlumno(Long alumnoId);

    List<RutinaResponse> listarPorEmailAlumno(String email);

    RutinaResponse obtenerPorId(Long id);

    RutinaResponse crear(RutinaRequest request, String emailCreador);

    RutinaResponse actualizar(Long id, RutinaRequest request);

    void eliminar(Long id);
}
