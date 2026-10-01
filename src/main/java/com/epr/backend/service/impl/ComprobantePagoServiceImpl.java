package com.epr.backend.service.impl;

import com.epr.backend.dto.projection.ComprobantePagoResumen;
import com.epr.backend.dto.request.PagoRequest;
import com.epr.backend.dto.request.RechazarComprobanteRequest;
import com.epr.backend.dto.response.ComprobantePagoArchivoResponse;
import com.epr.backend.dto.response.ComprobantePagoResponse;
import com.epr.backend.dto.response.PagoResponse;
import com.epr.backend.entity.ComprobantePago;
import com.epr.backend.entity.EstadoComprobante;
import com.epr.backend.entity.Pago;
import com.epr.backend.entity.PlanCuota;
import com.epr.backend.entity.Rol;
import com.epr.backend.entity.Usuario;
import com.epr.backend.exception.BadRequestException;
import com.epr.backend.exception.ResourceNotFoundException;
import com.epr.backend.mapper.ComprobantePagoMapper;
import com.epr.backend.repository.ComprobantePagoRepository;
import com.epr.backend.repository.PagoRepository;
import com.epr.backend.repository.PlanCuotaRepository;
import com.epr.backend.repository.UsuarioRepository;
import com.epr.backend.security.RateLimiter;
import com.epr.backend.service.ComprobantePagoService;
import com.epr.backend.service.CuotaService;
import com.epr.backend.service.NotificacionService;
import com.epr.backend.util.ArchivoSubido;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ComprobantePagoServiceImpl implements ComprobantePagoService {

    private static final Set<String> CONTENT_TYPES_PERMITIDOS = Set.of(
            ArchivoSubido.PDF, ArchivoSubido.JPEG, ArchivoSubido.PNG
    );
    private static final long TAMANIO_MAXIMO_BYTES = 15L * 1024 * 1024;

    private final ComprobantePagoRepository comprobantePagoRepository;
    private final UsuarioRepository usuarioRepository;
    private final PlanCuotaRepository planCuotaRepository;
    private final PagoRepository pagoRepository;
    private final CuotaService cuotaService;
    private final NotificacionService notificacionService;
    private final RateLimiter rateLimiter;

    @Value("${epr.rate-limit.comprobantes.window-minutes}")
    private long ventanaMinutos;

    @Value("${epr.rate-limit.comprobantes.max-per-usuario}")
    private int maxPorUsuario;

    @Override
    @Transactional
    public ComprobantePagoResponse subir(String emailAlumno, MultipartFile archivo, Long planCuotaId,
                                          BigDecimal monto, LocalDate fecha) {
        // Evita que un alumno llene la base subiendo archivos en loop.
        rateLimiter.exigir("comprobantes:usuario:" + emailAlumno.toLowerCase(Locale.ROOT), maxPorUsuario,
                Duration.ofMinutes(ventanaMinutos));

        Usuario alumno = buscarAlumnoPorEmail(emailAlumno);
        ArchivoSubido.Contenido contenido = ArchivoSubido.leerYValidar(
                archivo, CONTENT_TYPES_PERMITIDOS, TAMANIO_MAXIMO_BYTES, "El archivo debe ser PDF, JPG o PNG");

        PlanCuota planCuota = null;
        if (planCuotaId != null) {
            planCuota = planCuotaRepository.findById(planCuotaId)
                    .orElseThrow(() -> new ResourceNotFoundException("Plan de cuota no encontrado"));
        }

        ComprobantePago comprobante = ComprobantePago.builder()
                .alumno(alumno)
                .nombreArchivo(ArchivoSubido.nombreSeguro(archivo.getOriginalFilename(), "comprobante"))
                .contentType(contenido.contentType())
                .archivo(contenido.bytes())
                .fecha(fecha)
                .planCuota(planCuota)
                .monto(monto)
                .estado(EstadoComprobante.PENDIENTE)
                .build();

        return ComprobantePagoMapper.toResponse(comprobantePagoRepository.save(comprobante));
    }

    @Override
    public List<ComprobantePagoResponse> listarMios(String emailAlumno) {
        return comprobantePagoRepository.listarResumenPorAlumnoEmail(emailAlumno).stream()
                .map(ComprobantePagoMapper::toResponse)
                .toList();
    }

    @Override
    public ComprobantePagoArchivoResponse descargarMio(String emailAlumno, Long comprobanteId) {
        ComprobantePago comprobante = comprobantePagoRepository.findById(comprobanteId)
                .orElseThrow(() -> new ResourceNotFoundException("Comprobante no encontrado"));

        if (!comprobante.getAlumno().getEmail().equalsIgnoreCase(emailAlumno)) {
            throw new AccessDeniedException("No tenés permiso para acceder a este comprobante");
        }

        return ComprobantePagoMapper.toArchivoResponse(comprobante);
    }

    @Override
    public List<ComprobantePagoResponse> listarPorAlumno(Long alumnoId) {
        buscarAlumno(alumnoId);
        return comprobantePagoRepository.listarResumenPorAlumnoId(alumnoId).stream()
                .map(ComprobantePagoMapper::toResponse)
                .toList();
    }

    @Override
    public ComprobantePagoArchivoResponse descargarPorAlumno(Long alumnoId, Long comprobanteId) {
        ComprobantePago comprobante = comprobantePagoRepository.findByIdAndAlumnoId(comprobanteId, alumnoId)
                .orElseThrow(() -> new ResourceNotFoundException("Comprobante no encontrado"));
        return ComprobantePagoMapper.toArchivoResponse(comprobante);
    }

    @Override
    public List<ComprobantePagoResponse> listarPorEstado(EstadoComprobante estado) {
        List<ComprobantePagoResumen> comprobantes = estado != null
                ? comprobantePagoRepository.listarResumenPorEstado(estado)
                : comprobantePagoRepository.listarResumen();

        return comprobantes.stream()
                .map(ComprobantePagoMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public ComprobantePagoResponse confirmar(Long comprobanteId, PagoRequest request) {
        ComprobantePago comprobante = comprobantePagoRepository.findById(comprobanteId)
                .orElseThrow(() -> new ResourceNotFoundException("Comprobante no encontrado"));

        if (comprobante.getEstado() != EstadoComprobante.PENDIENTE) {
            throw new BadRequestException("El comprobante ya fue procesado");
        }

        PagoResponse pagoResponse = cuotaService.registrarPago(comprobante.getAlumno().getId(), request);
        Pago pago = pagoRepository.getReferenceById(pagoResponse.id());

        comprobante.setEstado(EstadoComprobante.CONFIRMADO);
        comprobante.setPago(pago);
        comprobante.setPlanCuota(pago.getPlanCuota());
        comprobante.setFecha(pago.getFecha());
        comprobante.setMonto(pago.getMonto());

        ComprobantePagoResponse response = ComprobantePagoMapper.toResponse(comprobantePagoRepository.save(comprobante));

        notificacionService.crear(comprobante.getAlumno(), "Pago confirmado",
                "Tu pago fue confirmado. Tu cuenta está al día.", "/panel/alumno/pagos");

        return response;
    }

    @Override
    @Transactional
    public ComprobantePagoResponse rechazar(Long comprobanteId, RechazarComprobanteRequest request) {
        ComprobantePago comprobante = comprobantePagoRepository.findById(comprobanteId)
                .orElseThrow(() -> new ResourceNotFoundException("Comprobante no encontrado"));

        if (comprobante.getEstado() != EstadoComprobante.PENDIENTE) {
            throw new BadRequestException("El comprobante ya fue procesado");
        }

        String notaRechazo = request != null ? request.nota() : null;
        comprobante.setEstado(EstadoComprobante.RECHAZADO);
        comprobante.setNotaRechazo(notaRechazo);

        ComprobantePagoResponse response = ComprobantePagoMapper.toResponse(comprobantePagoRepository.save(comprobante));

        String mensaje = (notaRechazo != null && !notaRechazo.isBlank())
                ? "Tu comprobante fue rechazado. Motivo: " + notaRechazo
                : "Tu comprobante fue rechazado. Consultá con el administrador para más información.";
        notificacionService.crear(comprobante.getAlumno(), "Comprobante rechazado", mensaje, "/panel/alumno/pagos");

        return response;
    }

    private Usuario buscarAlumnoPorEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Alumno no encontrado"));
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
