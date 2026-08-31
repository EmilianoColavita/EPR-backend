package com.epr.backend.service.impl;

import com.epr.backend.dto.request.AsignarRutinaRequest;
import com.epr.backend.dto.request.DiaRutinaRequest;
import com.epr.backend.dto.request.RutinaRequest;
import com.epr.backend.dto.request.SeleccionarDiaRequest;
import com.epr.backend.dto.response.RutinaListItemResponse;
import com.epr.backend.dto.response.RutinaMiaResponse;
import com.epr.backend.dto.response.RutinaResponse;
import com.epr.backend.entity.AsignacionRutina;
import com.epr.backend.entity.DiaRutina;
import com.epr.backend.entity.Rol;
import com.epr.backend.entity.Rutina;
import com.epr.backend.entity.Usuario;
import com.epr.backend.exception.BadRequestException;
import com.epr.backend.exception.ResourceNotFoundException;
import com.epr.backend.mapper.RutinaMapper;
import com.epr.backend.repository.AsignacionRutinaRepository;
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
    private final AsignacionRutinaRepository asignacionRutinaRepository;
    private final UsuarioRepository usuarioRepository;

    @Override
    public List<RutinaListItemResponse> listar() {
        return rutinaRepository.findAll().stream()
                .map(rutina -> RutinaMapper.toListItem(
                        rutina,
                        asignacionRutinaRepository.countByRutinaIdAndActivaTrue(rutina.getId())))
                .toList();
    }

    @Override
    @Transactional
    public RutinaResponse crear(RutinaRequest request) {
        Rutina rutina = Rutina.builder()
                .nombre(request.nombre())
                .descripcion(request.descripcion())
                .build();

        agregarDias(rutina, request.dias());

        return RutinaMapper.toResponse(rutinaRepository.save(rutina));
    }

    @Override
    public RutinaResponse obtenerPorId(Long rutinaId) {
        return RutinaMapper.toResponse(buscarPorId(rutinaId));
    }

    @Override
    @Transactional
    public RutinaResponse actualizar(Long rutinaId, RutinaRequest request) {
        Rutina rutina = buscarPorId(rutinaId);

        rutina.setNombre(request.nombre());
        rutina.setDescripcion(request.descripcion());
        rutina.getDias().clear();
        agregarDias(rutina, request.dias());
        rutina = rutinaRepository.save(rutina);

        List<AsignacionRutina> asignacionesActivas = asignacionRutinaRepository.findByRutinaIdAndActivaTrue(rutinaId);
        for (AsignacionRutina asignacion : asignacionesActivas) {
            asignacion.setUltimoDiaEntrenado(null);
        }
        asignacionRutinaRepository.saveAll(asignacionesActivas);

        return RutinaMapper.toResponse(rutina);
    }

    @Override
    @Transactional
    public RutinaMiaResponse asignar(Long rutinaId, AsignarRutinaRequest request) {
        Rutina rutina = buscarPorId(rutinaId);

        Usuario alumno = usuarioRepository.findById(request.alumnoId())
                .orElseThrow(() -> new ResourceNotFoundException("Alumno no encontrado"));

        if (alumno.getRol() != Rol.ALUMNO) {
            throw new BadRequestException("La rutina solo puede asignarse a un usuario con rol ALUMNO");
        }

        asignacionRutinaRepository.findByAlumnoIdAndActivaTrue(alumno.getId()).ifPresent(anterior -> {
            anterior.setActiva(false);
            asignacionRutinaRepository.save(anterior);
        });

        AsignacionRutina asignacion = AsignacionRutina.builder()
                .rutina(rutina)
                .alumno(alumno)
                .activa(true)
                .build();
        asignacion = asignacionRutinaRepository.save(asignacion);

        return RutinaMapper.toMiaResponse(asignacion, calcularDiaSugerido(rutina, null));
    }

    @Override
    public RutinaMiaResponse obtenerPorAlumno(Long alumnoId) {
        AsignacionRutina asignacion = buscarAsignacionActivaPorAlumno(alumnoId);
        return RutinaMapper.toMiaResponse(asignacion, calcularDiaSugerido(asignacion.getRutina(), asignacion.getUltimoDiaEntrenado()));
    }

    @Override
    public RutinaMiaResponse obtenerMia(String emailAlumno) {
        AsignacionRutina asignacion = buscarAsignacionActivaPorAlumno(buscarAlumnoPorEmail(emailAlumno).getId());
        return RutinaMapper.toMiaResponse(asignacion, calcularDiaSugerido(asignacion.getRutina(), asignacion.getUltimoDiaEntrenado()));
    }

    @Override
    @Transactional
    public RutinaMiaResponse seleccionarDia(String emailAlumno, SeleccionarDiaRequest request) {
        AsignacionRutina asignacion = buscarAsignacionActivaPorAlumno(buscarAlumnoPorEmail(emailAlumno).getId());

        DiaRutina dia = asignacion.getRutina().getDias().stream()
                .filter(d -> d.getId().equals(request.diaId()))
                .findFirst()
                .orElseThrow(() -> new BadRequestException("El día indicado no pertenece a tu rutina"));

        asignacion.setUltimoDiaEntrenado(dia);
        asignacionRutinaRepository.save(asignacion);

        return RutinaMapper.toMiaResponse(asignacion, calcularDiaSugerido(asignacion.getRutina(), dia));
    }

    private void agregarDias(Rutina rutina, List<DiaRutinaRequest> diasRequest) {
        if (diasRequest == null) {
            return;
        }
        for (DiaRutinaRequest diaRequest : diasRequest) {
            rutina.addDia(RutinaMapper.toEntity(diaRequest));
        }
    }

    private Long calcularDiaSugerido(Rutina rutina, DiaRutina ultimo) {
        List<DiaRutina> dias = rutina.getDias();
        if (dias.isEmpty()) {
            return null;
        }

        if (ultimo == null) {
            return dias.get(0).getId();
        }

        int index = -1;
        for (int i = 0; i < dias.size(); i++) {
            if (dias.get(i).getId().equals(ultimo.getId())) {
                index = i;
                break;
            }
        }

        if (index == -1) {
            return dias.get(0).getId();
        }

        return dias.get((index + 1) % dias.size()).getId();
    }

    private Usuario buscarAlumnoPorEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
    }

    private AsignacionRutina buscarAsignacionActivaPorAlumno(Long alumnoId) {
        return asignacionRutinaRepository.findByAlumnoIdAndActivaTrue(alumnoId)
                .orElseThrow(() -> new ResourceNotFoundException("El alumno no tiene una rutina asignada"));
    }

    private Rutina buscarPorId(Long id) {
        return rutinaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rutina no encontrada"));
    }
}
