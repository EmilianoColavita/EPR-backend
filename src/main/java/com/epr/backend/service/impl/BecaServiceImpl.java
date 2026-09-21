package com.epr.backend.service.impl;

import com.epr.backend.dto.request.FinalizarBecaRequest;
import com.epr.backend.dto.request.NotaBecaRequest;
import com.epr.backend.dto.request.OtorgarBecaRequest;
import com.epr.backend.dto.response.BecaResponse;
import com.epr.backend.entity.Beca;
import com.epr.backend.entity.EstadoBeca;
import com.epr.backend.entity.NotaBeca;
import com.epr.backend.entity.Rol;
import com.epr.backend.entity.Usuario;
import com.epr.backend.exception.BadRequestException;
import com.epr.backend.exception.ResourceNotFoundException;
import com.epr.backend.mapper.BecaMapper;
import com.epr.backend.repository.BecaRepository;
import com.epr.backend.repository.UsuarioRepository;
import com.epr.backend.service.BecaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class BecaServiceImpl implements BecaService {

    private final BecaRepository becaRepository;
    private final UsuarioRepository usuarioRepository;

    @Override
    @Transactional
    public BecaResponse otorgar(Long alumnoId, OtorgarBecaRequest request) {
        Usuario alumno = buscarAlumno(alumnoId);

        if (becaRepository.findByAlumnoIdAndEstado(alumnoId, EstadoBeca.ACTIVA).isPresent()) {
            throw new BadRequestException("El alumno ya tiene una beca activa");
        }

        LocalDate fechaInicio = request != null && request.fechaInicio() != null
                ? request.fechaInicio()
                : LocalDate.now();

        Beca beca = Beca.builder()
                .alumno(alumno)
                .fechaInicio(fechaInicio)
                .estado(EstadoBeca.ACTIVA)
                .build();

        return BecaMapper.toResponse(becaRepository.save(beca));
    }

    @Override
    public BecaResponse obtenerUltima(Long alumnoId) {
        buscarAlumno(alumnoId);
        Beca beca = becaRepository.findFirstByAlumnoIdOrderByFechaInicioDescIdDesc(alumnoId)
                .orElseThrow(() -> new ResourceNotFoundException("El alumno nunca tuvo una beca"));
        return BecaMapper.toResponse(beca);
    }

    @Override
    @Transactional
    public BecaResponse agregarNota(Long alumnoId, NotaBecaRequest request) {
        buscarAlumno(alumnoId);
        Beca beca = buscarBecaActiva(alumnoId);

        beca.addNota(NotaBeca.builder()
                .fecha(LocalDate.now())
                .texto(request.texto())
                .build());

        return BecaMapper.toResponse(becaRepository.save(beca));
    }

    @Override
    @Transactional
    public BecaResponse finalizar(Long alumnoId, FinalizarBecaRequest request) {
        buscarAlumno(alumnoId);
        Beca beca = buscarBecaActiva(alumnoId);

        LocalDate fechaFinalizacion = request != null && request.fechaFinalizacion() != null
                ? request.fechaFinalizacion()
                : LocalDate.now();

        beca.setFechaFinalizacion(fechaFinalizacion);
        beca.setEstado(EstadoBeca.FINALIZADA);

        return BecaMapper.toResponse(becaRepository.save(beca));
    }

    @Override
    public BecaResponse obtenerMia(String email) {
        Beca beca = becaRepository.findByAlumnoEmailAndEstado(email, EstadoBeca.ACTIVA)
                .orElseThrow(() -> new ResourceNotFoundException("No tenés una beca activa"));
        return BecaMapper.toResponse(beca);
    }

    private Beca buscarBecaActiva(Long alumnoId) {
        return becaRepository.findByAlumnoIdAndEstado(alumnoId, EstadoBeca.ACTIVA)
                .orElseThrow(() -> new BadRequestException("El alumno no tiene una beca activa"));
    }

    private Usuario buscarAlumno(Long alumnoId) {
        Usuario alumno = usuarioRepository.findById(alumnoId)
                .orElseThrow(() -> new ResourceNotFoundException("Alumno no encontrado"));

        if (alumno.getRol() != Rol.ALUMNO) {
            throw new BadRequestException("El usuario indicado no tiene rol ALUMNO");
        }

        return alumno;
    }
}
