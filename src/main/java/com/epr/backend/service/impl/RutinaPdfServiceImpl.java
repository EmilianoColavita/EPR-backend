package com.epr.backend.service.impl;

import com.epr.backend.dto.response.RutinaPdfArchivoResponse;
import com.epr.backend.dto.response.RutinaPdfResponse;
import com.epr.backend.entity.Rol;
import com.epr.backend.entity.RutinaPdf;
import com.epr.backend.entity.Usuario;
import com.epr.backend.exception.BadRequestException;
import com.epr.backend.exception.ResourceNotFoundException;
import com.epr.backend.mapper.RutinaPdfMapper;
import com.epr.backend.repository.RutinaPdfRepository;
import com.epr.backend.repository.UsuarioRepository;
import com.epr.backend.service.NotificacionService;
import com.epr.backend.service.RutinaPdfService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RutinaPdfServiceImpl implements RutinaPdfService {

    private static final String CONTENT_TYPE_PDF = "application/pdf";

    private final RutinaPdfRepository rutinaPdfRepository;
    private final UsuarioRepository usuarioRepository;
    private final NotificacionService notificacionService;

    @Override
    @Transactional
    public RutinaPdfResponse subir(Long alumnoId, MultipartFile archivo) {
        Usuario alumno = buscarAlumno(alumnoId);
        validarArchivo(archivo);

        RutinaPdf rutinaPdf = RutinaPdf.builder()
                .alumno(alumno)
                .nombreArchivo(archivo.getOriginalFilename())
                .contentType(archivo.getContentType())
                .archivo(leerBytes(archivo))
                .build();

        RutinaPdfResponse response = RutinaPdfMapper.toResponse(rutinaPdfRepository.save(rutinaPdf));

        notificacionService.crear(alumno, "Nueva rutina disponible",
                "Tu entrenador subió una nueva rutina en PDF. Ya la podés descargar.", "/panel/alumno/rutina");

        return response;
    }

    @Override
    public List<RutinaPdfResponse> listarPorAlumno(Long alumnoId) {
        buscarAlumno(alumnoId);
        return rutinaPdfRepository.findByAlumnoId(alumnoId).stream()
                .map(RutinaPdfMapper::toResponse)
                .toList();
    }

    @Override
    public RutinaPdfArchivoResponse descargarPorAlumno(Long alumnoId, Long rutinaPdfId) {
        RutinaPdf rutinaPdf = rutinaPdfRepository.findByIdAndAlumnoId(rutinaPdfId, alumnoId)
                .orElseThrow(() -> new ResourceNotFoundException("Rutina en PDF no encontrada"));
        return RutinaPdfMapper.toArchivoResponse(rutinaPdf);
    }

    @Override
    @Transactional
    public void eliminar(Long alumnoId, Long rutinaPdfId) {
        RutinaPdf rutinaPdf = rutinaPdfRepository.findByIdAndAlumnoId(rutinaPdfId, alumnoId)
                .orElseThrow(() -> new ResourceNotFoundException("Rutina en PDF no encontrada"));
        rutinaPdfRepository.delete(rutinaPdf);
    }

    @Override
    public List<RutinaPdfResponse> listarMias(String emailAlumno) {
        return rutinaPdfRepository.findByAlumnoEmail(emailAlumno).stream()
                .map(RutinaPdfMapper::toResponse)
                .toList();
    }

    @Override
    public RutinaPdfArchivoResponse descargarMia(String emailAlumno, Long rutinaPdfId) {
        RutinaPdf rutinaPdf = rutinaPdfRepository.findById(rutinaPdfId)
                .orElseThrow(() -> new ResourceNotFoundException("Rutina en PDF no encontrada"));

        if (!rutinaPdf.getAlumno().getEmail().equalsIgnoreCase(emailAlumno)) {
            throw new AccessDeniedException("No tenés permiso para acceder a esta rutina en PDF");
        }

        return RutinaPdfMapper.toArchivoResponse(rutinaPdf);
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
            throw new BadRequestException("Solo se pueden cargar rutinas en PDF a un usuario con rol ALUMNO");
        }

        return alumno;
    }
}
