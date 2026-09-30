package com.epr.backend.service;

import com.epr.backend.dto.response.RutinaPdfArchivoResponse;
import com.epr.backend.dto.response.RutinaPdfResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface RutinaPdfService {

    RutinaPdfResponse subir(Long alumnoId, MultipartFile archivo);

    List<RutinaPdfResponse> listarPorAlumno(Long alumnoId);

    RutinaPdfArchivoResponse descargarPorAlumno(Long alumnoId, Long rutinaPdfId);

    void eliminar(Long alumnoId, Long rutinaPdfId);

    List<RutinaPdfResponse> listarMias(String emailAlumno);

    RutinaPdfArchivoResponse descargarMia(String emailAlumno, Long rutinaPdfId);
}
