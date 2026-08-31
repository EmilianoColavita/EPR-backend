package com.epr.backend.service.impl;

import com.epr.backend.dto.request.FranjaHorarioRequest;
import com.epr.backend.dto.request.HorarioAsignadoRequest;
import com.epr.backend.dto.response.HorarioAsignadoResponse;
import com.epr.backend.entity.EstadoTurno;
import com.epr.backend.entity.HorarioAsignado;
import com.epr.backend.entity.Rol;
import com.epr.backend.entity.Turno;
import com.epr.backend.entity.Usuario;
import com.epr.backend.exception.BadRequestException;
import com.epr.backend.exception.ResourceNotFoundException;
import com.epr.backend.mapper.HorarioAsignadoMapper;
import com.epr.backend.repository.HorarioAsignadoRepository;
import com.epr.backend.repository.TurnoRepository;
import com.epr.backend.repository.UsuarioRepository;
import com.epr.backend.service.HorarioAsignadoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HorarioAsignadoServiceImpl implements HorarioAsignadoService {

    private final HorarioAsignadoRepository horarioAsignadoRepository;
    private final TurnoRepository turnoRepository;
    private final UsuarioRepository usuarioRepository;

    @Override
    public HorarioAsignadoResponse obtenerActivoPorAlumno(Long alumnoId) {
        return HorarioAsignadoMapper.toResponse(buscarActivoPorAlumno(alumnoId));
    }

    @Override
    @Transactional
    public HorarioAsignadoResponse asignar(Long alumnoId, HorarioAsignadoRequest request, String emailCreador) {
        Usuario alumno = buscarAlumno(alumnoId);
        validarFranjas(request.franjas());

        horarioAsignadoRepository.findByAlumnoIdAndActivoTrue(alumnoId)
                .ifPresent(this::desactivarYCancelarTurnosFuturos);

        HorarioAsignado horario = HorarioAsignado.builder()
                .alumno(alumno)
                .notas(request.notas())
                .creadoPor(buscarUsuarioPorEmail(emailCreador))
                .build();

        for (FranjaHorarioRequest franjaRequest : request.franjas()) {
            horario.addFranja(HorarioAsignadoMapper.toEntity(franjaRequest));
        }

        return HorarioAsignadoMapper.toResponse(horarioAsignadoRepository.save(horario));
    }

    private void desactivarYCancelarTurnosFuturos(HorarioAsignado anterior) {
        anterior.setActivo(false);
        horarioAsignadoRepository.save(anterior);

        List<Turno> futurosConfirmados = turnoRepository.findByHorarioAsignadoIdAndFechaGreaterThanEqualAndEstado(
                anterior.getId(), LocalDate.now(), EstadoTurno.CONFIRMADO);
        futurosConfirmados.forEach(turno -> turno.setEstado(EstadoTurno.CANCELADO));
        turnoRepository.saveAll(futurosConfirmados);
    }

    private void validarFranjas(List<FranjaHorarioRequest> franjas) {
        for (FranjaHorarioRequest franja : franjas) {
            if (franja.horaFin() != null && !franja.horaFin().isAfter(franja.horaInicio())) {
                throw new BadRequestException("La hora de fin debe ser posterior a la hora de inicio");
            }
        }
    }

    private Usuario buscarAlumno(Long alumnoId) {
        Usuario alumno = usuarioRepository.findById(alumnoId)
                .orElseThrow(() -> new ResourceNotFoundException("Alumno no encontrado"));

        if (alumno.getRol() != Rol.ALUMNO) {
            throw new BadRequestException("El horario solo puede asignarse a un usuario con rol ALUMNO");
        }

        return alumno;
    }

    private Usuario buscarUsuarioPorEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
    }

    private HorarioAsignado buscarActivoPorAlumno(Long alumnoId) {
        return horarioAsignadoRepository.findByAlumnoIdAndActivoTrue(alumnoId)
                .orElseThrow(() -> new ResourceNotFoundException("El alumno no tiene un horario asignado"));
    }
}
