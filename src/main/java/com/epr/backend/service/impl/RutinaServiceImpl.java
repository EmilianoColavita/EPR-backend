package com.epr.backend.service.impl;

import com.epr.backend.dto.request.EjercicioRequest;
import com.epr.backend.dto.request.RutinaRequest;
import com.epr.backend.dto.response.RutinaResponse;
import com.epr.backend.entity.Ejercicio;
import com.epr.backend.entity.Rol;
import com.epr.backend.entity.Rutina;
import com.epr.backend.entity.Usuario;
import com.epr.backend.exception.BadRequestException;
import com.epr.backend.exception.ResourceNotFoundException;
import com.epr.backend.mapper.RutinaMapper;
import com.epr.backend.repository.RutinaRepository;
import com.epr.backend.repository.UsuarioRepository;
import com.epr.backend.service.RutinaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RutinaServiceImpl implements RutinaService {

    private final RutinaRepository rutinaRepository;
    private final UsuarioRepository usuarioRepository;

    @Override
    public List<RutinaResponse> listarTodas() {
        return rutinaRepository.findAll().stream()
                .map(RutinaMapper::toResponse)
                .toList();
    }

    @Override
    public List<RutinaResponse> listarPorAlumno(Long alumnoId) {
        return rutinaRepository.findByAlumnoId(alumnoId).stream()
                .map(RutinaMapper::toResponse)
                .toList();
    }

    @Override
    public List<RutinaResponse> listarPorEmailAlumno(String email) {
        Usuario alumno = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        return listarPorAlumno(alumno.getId());
    }

    @Override
    public RutinaResponse obtenerPorId(Long id) {
        return RutinaMapper.toResponse(buscarPorId(id));
    }

    @Override
    @Transactional
    public RutinaResponse crear(RutinaRequest request, String emailCreador) {
        Usuario alumno = usuarioRepository.findById(request.alumnoId())
                .orElseThrow(() -> new ResourceNotFoundException("Alumno no encontrado"));

        if (alumno.getRol() != Rol.ALUMNO) {
            throw new BadRequestException("La rutina solo puede asignarse a un usuario con rol ALUMNO");
        }

        Usuario creadoPor = usuarioRepository.findByEmail(emailCreador).orElse(null);

        Rutina rutina = Rutina.builder()
                .nombre(request.nombre())
                .descripcion(request.descripcion())
                .alumno(alumno)
                .creadoPor(creadoPor)
                .activa(true)
                .build();

        agregarEjercicios(rutina, request.ejercicios());

        return RutinaMapper.toResponse(rutinaRepository.save(rutina));
    }

    @Override
    @Transactional
    public RutinaResponse actualizar(Long id, RutinaRequest request) {
        Rutina rutina = buscarPorId(id);

        if (!rutina.getAlumno().getId().equals(request.alumnoId())) {
            Usuario alumno = usuarioRepository.findById(request.alumnoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Alumno no encontrado"));
            rutina.setAlumno(alumno);
        }

        rutina.setNombre(request.nombre());
        rutina.setDescripcion(request.descripcion());
        rutina.getEjercicios().clear();
        agregarEjercicios(rutina, request.ejercicios());

        return RutinaMapper.toResponse(rutinaRepository.save(rutina));
    }

    @Override
    public void eliminar(Long id) {
        rutinaRepository.delete(buscarPorId(id));
    }

    private void agregarEjercicios(Rutina rutina, List<EjercicioRequest> ejerciciosRequest) {
        if (ejerciciosRequest == null) {
            return;
        }
        for (EjercicioRequest ejercicioRequest : ejerciciosRequest) {
            Ejercicio ejercicio = RutinaMapper.toEntity(ejercicioRequest);
            rutina.addEjercicio(ejercicio);
        }
    }

    private Rutina buscarPorId(Long id) {
        return rutinaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rutina no encontrada"));
    }
}
