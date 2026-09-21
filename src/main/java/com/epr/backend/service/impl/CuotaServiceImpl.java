package com.epr.backend.service.impl;

import com.epr.backend.dto.request.PagoRequest;
import com.epr.backend.dto.response.CuentaAlumnoResponse;
import com.epr.backend.dto.response.EstadoCuentaResponse;
import com.epr.backend.dto.response.PagoResponse;
import com.epr.backend.dto.response.ResumenCuotasResponse;
import com.epr.backend.entity.Cuota;
import com.epr.backend.entity.EstadoBeca;
import com.epr.backend.entity.Pago;
import com.epr.backend.entity.PlanCuota;
import com.epr.backend.entity.Rol;
import com.epr.backend.entity.Usuario;
import com.epr.backend.exception.BadRequestException;
import com.epr.backend.exception.ResourceNotFoundException;
import com.epr.backend.mapper.PagoMapper;
import com.epr.backend.mapper.PlanCuotaMapper;
import com.epr.backend.repository.BecaRepository;
import com.epr.backend.repository.CuotaRepository;
import com.epr.backend.repository.PagoRepository;
import com.epr.backend.repository.PlanCuotaRepository;
import com.epr.backend.repository.UsuarioRepository;
import com.epr.backend.service.CuotaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CuotaServiceImpl implements CuotaService {

    private final CuotaRepository cuotaRepository;
    private final PlanCuotaRepository planCuotaRepository;
    private final PagoRepository pagoRepository;
    private final UsuarioRepository usuarioRepository;
    private final BecaRepository becaRepository;

    @Override
    public EstadoCuentaResponse obtenerMiEstado(String email) {
        boolean becado = becaRepository.findByAlumnoEmailAndEstado(email, EstadoBeca.ACTIVA).isPresent();
        return cuotaRepository.findByAlumnoEmail(email)
                .map(cuota -> new EstadoCuentaResponse(cuota.isAlDia(), cuota.getFechaVencimiento(), becado))
                .orElseGet(() -> EstadoCuentaResponse.sinCuota(becado));
    }

    @Override
    public CuentaAlumnoResponse obtenerCuenta(Long alumnoId) {
        buscarAlumno(alumnoId);
        boolean becado = becaRepository.findByAlumnoIdAndEstado(alumnoId, EstadoBeca.ACTIVA).isPresent();
        return cuotaRepository.findByAlumnoId(alumnoId)
                .map(cuota -> new CuentaAlumnoResponse(
                        cuota.getPlanCuotaActual() != null ? PlanCuotaMapper.toResponse(cuota.getPlanCuotaActual()) : null,
                        cuota.getFechaVencimiento(),
                        cuota.isAlDia(),
                        becado))
                .orElseGet(() -> new CuentaAlumnoResponse(null, null, false, becado));
    }

    @Override
    public List<PagoResponse> listarPagos(Long alumnoId) {
        buscarAlumno(alumnoId);
        return pagoRepository.findByAlumnoIdOrderByFechaDesc(alumnoId).stream()
                .map(PagoMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public PagoResponse registrarPago(Long alumnoId, PagoRequest request) {
        Usuario alumno = buscarAlumno(alumnoId);

        PlanCuota planCuota = planCuotaRepository.findById(request.planCuotaId())
                .orElseThrow(() -> new ResourceNotFoundException("Plan de cuota no encontrado"));
        if (!planCuota.isActivo()) {
            throw new BadRequestException("El plan seleccionado no está activo");
        }

        LocalDate fecha = request.fecha() != null ? request.fecha() : LocalDate.now();

        Pago pago = Pago.builder()
                .alumno(alumno)
                .planCuota(planCuota)
                .fecha(fecha)
                .monto(request.monto())
                .build();
        pago = pagoRepository.save(pago);

        Cuota cuenta = cuotaRepository.findByAlumnoId(alumnoId)
                .orElseGet(() -> Cuota.builder().alumno(alumno).build());
        cuenta.setPlanCuotaActual(planCuota);
        cuenta.setFechaVencimiento(fecha.plusDays(planCuota.getDuracionDias()));
        cuotaRepository.save(cuenta);

        return PagoMapper.toResponse(pago);
    }

    @Override
    public ResumenCuotasResponse obtenerResumen() {
        List<Usuario> alumnosActivos = usuarioRepository.findByRolAndActivo(Rol.ALUMNO, true);
        List<Long> alumnoIds = alumnosActivos.stream().map(Usuario::getId).toList();

        Set<Long> becados = becaRepository.findByAlumnoIdInAndEstado(alumnoIds, EstadoBeca.ACTIVA).stream()
                .map(beca -> beca.getAlumno().getId())
                .collect(Collectors.toSet());
        List<Long> alumnoIdsNoBecados = alumnoIds.stream().filter(id -> !becados.contains(id)).toList();

        Map<Long, Cuota> cuentaPorAlumno = cuotaRepository.findByAlumnoIdIn(alumnoIdsNoBecados).stream()
                .collect(Collectors.toMap(cuota -> cuota.getAlumno().getId(), Function.identity()));

        long alDia = alumnoIdsNoBecados.stream()
                .filter(id -> {
                    Cuota cuota = cuentaPorAlumno.get(id);
                    return cuota != null && cuota.isAlDia();
                })
                .count();

        return new ResumenCuotasResponse(alDia, alumnoIdsNoBecados.size() - alDia);
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
