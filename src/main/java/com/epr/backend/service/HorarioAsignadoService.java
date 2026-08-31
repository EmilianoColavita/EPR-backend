package com.epr.backend.service;

import com.epr.backend.dto.request.HorarioAsignadoRequest;
import com.epr.backend.dto.response.HorarioAsignadoResponse;

public interface HorarioAsignadoService {

    HorarioAsignadoResponse obtenerActivoPorAlumno(Long alumnoId);

    HorarioAsignadoResponse asignar(Long alumnoId, HorarioAsignadoRequest request, String emailCreador);
}
