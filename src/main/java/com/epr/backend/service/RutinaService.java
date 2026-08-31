package com.epr.backend.service;

import com.epr.backend.dto.request.AsignarRutinaRequest;
import com.epr.backend.dto.request.RutinaRequest;
import com.epr.backend.dto.request.SeleccionarDiaRequest;
import com.epr.backend.dto.response.RutinaListItemResponse;
import com.epr.backend.dto.response.RutinaMiaResponse;
import com.epr.backend.dto.response.RutinaResponse;

import java.util.List;

public interface RutinaService {

    List<RutinaListItemResponse> listar();

    RutinaResponse crear(RutinaRequest request);

    RutinaResponse obtenerPorId(Long rutinaId);

    RutinaResponse actualizar(Long rutinaId, RutinaRequest request);

    RutinaMiaResponse asignar(Long rutinaId, AsignarRutinaRequest request);

    RutinaMiaResponse obtenerPorAlumno(Long alumnoId);

    RutinaMiaResponse obtenerMia(String emailAlumno);

    RutinaMiaResponse seleccionarDia(String emailAlumno, SeleccionarDiaRequest request);
}
