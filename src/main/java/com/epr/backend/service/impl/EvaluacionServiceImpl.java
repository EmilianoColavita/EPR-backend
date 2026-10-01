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
import com.epr.backend.service.NotificacionService;
import com.epr.backend.util.ArchivoSubido;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class EvaluacionServiceImpl implements EvaluacionService {

    private static final long TAMANIO_MAXIMO_BYTES = 15L * 1024 * 1024;

    private final EvaluacionRepository evaluacionRepository;
    private final UsuarioRepository usuarioRepository;
    private final NotificacionService notificacionService;

    @Override
    @Transactional
    public EvaluacionResponse subir(Long alumnoId, MultipartFile archivo) {
        Usuario alumno = buscarAlumno(alumnoId);
        ArchivoSubido.Contenido contenido = ArchivoSubido.leerYValidar(
                archivo, Set.of(ArchivoSubido.PDF), TAMANIO_MAXIMO_BYTES, "El archivo debe ser un PDF");

        Evaluacion evaluacion = Evaluacion.builder()
                .alumno(alumno)
                .nombreArchivo(ArchivoSubido.nombreSeguro(archivo.getOriginalFilename(), "evaluacion.pdf"))
                .contentType(contenido.contentType())
                .archivo(contenido.bytes())
                .build();

        EvaluacionResponse response = EvaluacionMapper.toResponse(evaluacionRepository.save(evaluacion));

        notificacionService.crear(alumno, "Nueva evaluación disponible",
                "Tu entrenador subió una nueva evaluación. Ya la podés descargar.", "/panel/alumno/evaluaciones");

        return response;
    }

    @Override
    public List<EvaluacionResponse> listarPorAlumno(Long alumnoId) {
        buscarAlumno(alumnoId);
        return evaluacionRepository.listarResumenPorAlumnoId(alumnoId).stream()
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
        return evaluacionRepository.listarResumenPorAlumnoEmail(emailAlumno).stream()
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

    private Usuario buscarAlumno(Long alumnoId) {
        Usuario alumno = usuarioRepository.findById(alumnoId)
                .orElseThrow(() -> new ResourceNotFoundException("Alumno no encontrado"));

        if (alumno.getRol() != Rol.ALUMNO) {
            throw new BadRequestException("Solo se pueden cargar evaluaciones a un usuario con rol ALUMNO");
        }

        return alumno;
    }
}
