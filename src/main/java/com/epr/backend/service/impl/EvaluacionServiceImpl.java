package com.epr.backend.service.impl;

import com.epr.backend.dto.response.EvaluacionArchivoResponse;
import com.epr.backend.dto.response.EvaluacionResponse;
import com.epr.backend.entity.Evaluacion;
import com.epr.backend.entity.Rol;
import com.epr.backend.entity.Usuario;
import com.epr.backend.exception.BadRequestException;
import com.epr.backend.exception.ResourceNotFoundException;
import com.epr.backend.mapper.EvaluacionMapper;
import com.epr.backend.repository.EvaluacionRepository;
import com.epr.backend.repository.UsuarioRepository;
import com.epr.backend.service.EvaluacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EvaluacionServiceImpl implements EvaluacionService {

    private static final String CONTENT_TYPE_PDF = "application/pdf";

    private final EvaluacionRepository evaluacionRepository;
    private final UsuarioRepository usuarioRepository;

    @Override
    @Transactional
    public EvaluacionResponse subir(Long alumnoId, MultipartFile archivo) {
        Usuario alumno = buscarAlumno(alumnoId);
        validarArchivo(archivo);

        Evaluacion evaluacion = Evaluacion.builder()
                .alumno(alumno)
                .nombreArchivo(archivo.getOriginalFilename())
                .contentType(archivo.getContentType())
                .archivo(leerBytes(archivo))
                .build();

        return EvaluacionMapper.toResponse(evaluacionRepository.save(evaluacion));
    }

    @Override
    public List<EvaluacionResponse> listarPorAlumno(Long alumnoId) {
        buscarAlumno(alumnoId);
        return evaluacionRepository.findByAlumnoId(alumnoId).stream()
                .map(EvaluacionMapper::toResponse)
                .toList();
    }

    @Override
    public EvaluacionArchivoResponse descargarPorAlumno(Long alumnoId, Long evaluacionId) {
        Evaluacion evaluacion = evaluacionRepository.findByIdAndAlumnoId(evaluacionId, alumnoId)
                .orElseThrow(() -> new ResourceNotFoundException("Evaluación no encontrada"));
        return EvaluacionMapper.toArchivoResponse(evaluacion);
    }

    @Override
    @Transactional
    public void eliminar(Long alumnoId, Long evaluacionId) {
        Evaluacion evaluacion = evaluacionRepository.findByIdAndAlumnoId(evaluacionId, alumnoId)
                .orElseThrow(() -> new ResourceNotFoundException("Evaluación no encontrada"));
        evaluacionRepository.delete(evaluacion);
    }

    @Override
    public List<EvaluacionResponse> listarMias(String emailAlumno) {
        return evaluacionRepository.findByAlumnoEmail(emailAlumno).stream()
                .map(EvaluacionMapper::toResponse)
                .toList();
    }

    @Override
    public EvaluacionArchivoResponse descargarMia(String emailAlumno, Long evaluacionId) {
        Evaluacion evaluacion = evaluacionRepository.findById(evaluacionId)
                .orElseThrow(() -> new ResourceNotFoundException("Evaluación no encontrada"));

        if (!evaluacion.getAlumno().getEmail().equalsIgnoreCase(emailAlumno)) {
            throw new AccessDeniedException("No tenés permiso para acceder a esta evaluación");
        }

        return EvaluacionMapper.toArchivoResponse(evaluacion);
    }

    private void validarArchivo(MultipartFile archivo) {
        if (archivo == null || archivo.isEmpty()) {
            throw new BadRequestException("El archivo es requerido");
        }
        if (!CONTENT_TYPE_PDF.equalsIgnoreCase(archivo.getContentType())) {
            throw new BadRequestException("El archivo debe ser un PDF");
        }
    }

    private byte[] leerBytes(MultipartFile archivo) {
        try {
            return archivo.getBytes();
        } catch (IOException e) {
            throw new BadRequestException("No se pudo leer el archivo");
        }
    }

    private Usuario buscarAlumno(Long alumnoId) {
        Usuario alumno = usuarioRepository.findById(alumnoId)
                .orElseThrow(() -> new ResourceNotFoundException("Alumno no encontrado"));

        if (alumno.getRol() != Rol.ALUMNO) {
            throw new BadRequestException("Solo se pueden cargar evaluaciones a un usuario con rol ALUMNO");
        }

        return alumno;
    }
}
