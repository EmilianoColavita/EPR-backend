package com.epr.backend.service;

import com.epr.backend.dto.response.EvaluacionArchivoResponse;
import com.epr.backend.dto.response.EvaluacionResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface EvaluacionService {

    EvaluacionResponse subir(Long alumnoId, MultipartFile archivo);

    List<EvaluacionResponse> listarPorAlumno(Long alumnoId);

    EvaluacionArchivoResponse descargarPorAlumno(Long alumnoId, Long evaluacionId);

    void eliminar(Long alumnoId, Long evaluacionId);

    List<EvaluacionResponse> listarMias(String emailAlumno);

    EvaluacionArchivoResponse descargarMia(String emailAlumno, Long evaluacionId);
}
