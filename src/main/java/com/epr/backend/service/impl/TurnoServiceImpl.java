package com.epr.backend.service.impl;

import com.epr.backend.dto.request.EstadoTurnoRequest;
import com.epr.backend.dto.request.TurnoRequest;
import com.epr.backend.dto.response.TurnoResponse;
import com.epr.backend.entity.DiaSemana;
import com.epr.backend.entity.FranjaHorario;
import com.epr.backend.entity.HorarioAsignado;
import com.epr.backend.entity.Rol;
import com.epr.backend.entity.Turno;
import com.epr.backend.entity.Usuario;
import com.epr.backend.exception.BadRequestException;
import com.epr.backend.exception.ResourceNotFoundException;
import com.epr.backend.mapper.TurnoMapper;
import com.epr.backend.repository.HorarioAsignadoRepository;
import com.epr.backend.repository.TurnoRepository;
import com.epr.backend.repository.UsuarioRepository;
import com.epr.backend.service.TurnoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TurnoServiceImpl implements TurnoService {

    private static final int DIAS_A_GENERAR = 30;

    private final TurnoRepository turnoRepository;
    private final UsuarioRepository usuarioRepository;
    private final HorarioAsignadoRepository horarioAsignadoRepository;

    @Override
    @Transactional
    public List<TurnoResponse> listar(LocalDate desde, LocalDate hasta, Long alumnoId) {
        generarTurnosPendientes();
        return turnoRepository.buscar(alumnoId, desde, hasta).stream()
                .map(TurnoMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public TurnoResponse crear(TurnoRequest request, String emailCreador) {
        validarHorario(request);

        Turno turno = Turno.builder()
                .alumno(buscarAlumno(request.alumnoId()))
                .fecha(request.fecha())
                .horaInicio(request.horaInicio())
                .horaFin(request.horaFin())
                .actividad(request.actividad())
                .notas(request.notas())
                .creadoPor(buscarUsuarioPorEmail(emailCreador))
                .build();

        return TurnoMapper.toResponse(turnoRepository.save(turno));
    }

    @Override
    @Transactional
    public TurnoResponse actualizar(Long turnoId, TurnoRequest request) {
        validarHorario(request);

        Turno turno = buscarPorId(turnoId);
        turno.setAlumno(buscarAlumno(request.alumnoId()));
        turno.setFecha(request.fecha());
        turno.setHoraInicio(request.horaInicio());
        turno.setHoraFin(request.horaFin());
        turno.setActividad(request.actividad());
        turno.setNotas(request.notas());

        return TurnoMapper.toResponse(turnoRepository.save(turno));
    }

    @Override
    @Transactional
    public TurnoResponse cambiarEstado(Long turnoId, EstadoTurnoRequest request) {
        Turno turno = buscarPorId(turnoId);
        turno.setEstado(request.estado());

        return TurnoMapper.toResponse(turnoRepository.save(turno));
    }

    @Override
    @Transactional
    public List<TurnoResponse> listarMios(String emailAlumno, LocalDate desde, LocalDate hasta) {
        generarTurnosPendientes();
        return turnoRepository.buscarPorAlumnoEmail(emailAlumno, desde, hasta).stream()
                .map(TurnoMapper::toResponse)
                .toList();
    }

    private void generarTurnosPendientes() {
        LocalDate hoy = LocalDate.now();
        LocalDate limite = hoy.plusDays(DIAS_A_GENERAR);

        for (HorarioAsignado horario : horarioAsignadoRepository.buscarActivosDeAlumnosActivos()) {
            generarTurnosParaHorario(horario, hoy, limite);
        }
    }

    private void generarTurnosParaHorario(HorarioAsignado horario, LocalDate desde, LocalDate hasta) {
        Set<String> clavesExistentes = turnoRepository.findByHorarioAsignadoIdAndFechaBetween(horario.getId(), desde, hasta).stream()
                .map(turno -> claveTurno(turno.getFecha(), turno.getHoraInicio()))
                .collect(Collectors.toSet());

        for (LocalDate fecha = desde; !fecha.isAfter(hasta); fecha = fecha.plusDays(1)) {
            DiaSemana diaSemana = DiaSemana.values()[fecha.getDayOfWeek().getValue() - 1];

            for (FranjaHorario franja : horario.getFranjas()) {
                if (franja.getDiaSemana() != diaSemana) {
                    continue;
                }

                String clave = claveTurno(fecha, franja.getHoraInicio());
                if (!clavesExistentes.add(clave)) {
                    continue;
                }

                turnoRepository.insertarSiNoExiste(
                        horario.getAlumno().getId(),
                        fecha,
                        franja.getHoraInicio(),
                        franja.getHoraFin(),
                        franja.getActividad(),
                        horario.getNotas(),
                        horario.getCreadoPor().getId(),
                        horario.getId());
            }
        }
    }

    private String claveTurno(LocalDate fecha, LocalTime horaInicio) {
        return fecha + "@" + horaInicio;
    }

    private void validarHorario(TurnoRequest request) {
        if (request.horaFin() != null && !request.horaFin().isAfter(request.horaInicio())) {
            throw new BadRequestException("La hora de fin debe ser posterior a la hora de inicio");
        }
    }

    private Usuario buscarAlumno(Long alumnoId) {
        Usuario alumno = usuarioRepository.findById(alumnoId)
                .orElseThrow(() -> new ResourceNotFoundException("Alumno no encontrado"));

        if (alumno.getRol() != Rol.ALUMNO) {
            throw new BadRequestException("El turno solo puede asignarse a un usuario con rol ALUMNO");
        }

        return alumno;
    }

    private Usuario buscarUsuarioPorEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
    }

    private Turno buscarPorId(Long id) {
        return turnoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Turno no encontrado"));
    }
}
