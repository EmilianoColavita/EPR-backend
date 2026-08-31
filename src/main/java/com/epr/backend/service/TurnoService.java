package com.epr.backend.service;

import com.epr.backend.dto.request.EstadoTurnoRequest;
import com.epr.backend.dto.request.TurnoRequest;
import com.epr.backend.dto.response.TurnoResponse;

import java.time.LocalDate;
import java.util.List;

public interface TurnoService {

    List<TurnoResponse> listar(LocalDate desde, LocalDate hasta, Long alumnoId);

    TurnoResponse crear(TurnoRequest request, String emailCreador);

    TurnoResponse actualizar(Long turnoId, TurnoRequest request);

    TurnoResponse cambiarEstado(Long turnoId, EstadoTurnoRequest request);

    List<TurnoResponse> listarMios(String emailAlumno, LocalDate desde, LocalDate hasta);
}
