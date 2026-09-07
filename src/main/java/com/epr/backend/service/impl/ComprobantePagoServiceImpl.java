package com.epr.backend.service.impl;

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
import com.epr.backend.service.ComprobantePagoService;
import com.epr.backend.service.CuotaService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ComprobantePagoServiceImpl implements ComprobantePagoService {

    private static final Set<String> CONTENT_TYPES_PERMITIDOS = Set.of(
            "application/pdf", "image/jpeg", "image/png"
    );

    private final ComprobantePagoRepository comprobantePagoRepository;
    private final UsuarioRepository usuarioRepository;
    private final PlanCuotaRepository planCuotaRepository;
    private final PagoRepository pagoRepository;
    private final CuotaService cuotaService;

    @Override
    @Transactional
    public ComprobantePagoResponse subir(String emailAlumno, MultipartFile archivo, Long planCuotaId,
                                          BigDecimal monto, LocalDate fecha) {
        Usuario alumno = buscarAlumnoPorEmail(emailAlumno);
        validarArchivo(archivo);

        PlanCuota planCuota = null;
        if (planCuotaId != null) {
            planCuota = planCuotaRepository.findById(planCuotaId)
                    .orElseThrow(() -> new ResourceNotFoundException("Plan de cuota no encontrado"));
        }

        ComprobantePago comprobante = ComprobantePago.builder()
                .alumno(alumno)
                .nombreArchivo(archivo.getOriginalFilename())
                .contentType(archivo.getContentType())
                .archivo(leerBytes(archivo))
                .fecha(fecha)
                .planCuota(planCuota)
                .monto(monto)
                .estado(EstadoComprobante.PENDIENTE)
                .build();

        return ComprobantePagoMapper.toResponse(comprobantePagoRepository.save(comprobante));
    }

    @Override
    public List<ComprobantePagoResponse> listarMios(String emailAlumno) {
        return comprobantePagoRepository.findByAlumnoEmailOrderByFechaSubidaDesc(emailAlumno).stream()
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
        return comprobantePagoRepository.findByAlumnoIdOrderByFechaSubidaDesc(alumnoId).stream()
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
        List<ComprobantePago> comprobantes = estado != null
                ? comprobantePagoRepository.findByEstadoOrderByFechaSubidaDesc(estado)
                : comprobantePagoRepository.findAllByOrderByFechaSubidaDesc();

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

        return ComprobantePagoMapper.toResponse(comprobantePagoRepository.save(comprobante));
    }

    @Override
    @Transactional
    public ComprobantePagoResponse rechazar(Long comprobanteId, RechazarComprobanteRequest request) {
        ComprobantePago comprobante = comprobantePagoRepository.findById(comprobanteId)
                .orElseThrow(() -> new ResourceNotFoundException("Comprobante no encontrado"));

        if (comprobante.getEstado() != EstadoComprobante.PENDIENTE) {
            throw new BadRequestException("El comprobante ya fue procesado");
        }

        comprobante.setEstado(EstadoComprobante.RECHAZADO);
        comprobante.setNotaRechazo(request != null ? request.nota() : null);

        return ComprobantePagoMapper.toResponse(comprobantePagoRepository.save(comprobante));
    }

    private void validarArchivo(MultipartFile archivo) {
        if (archivo == null || archivo.isEmpty()) {
            throw new BadRequestException("El archivo es requerido");
        }
        String contentType = archivo.getContentType();
        if (contentType == null || !CONTENT_TYPES_PERMITIDOS.contains(contentType.toLowerCase())) {
            throw new BadRequestException("El archivo debe ser PDF, JPG o PNG");
        }
    }

    private byte[] leerBytes(MultipartFile archivo) {
        try {
            return archivo.getBytes();
        } catch (IOException e) {
            throw new BadRequestException("No se pudo leer el archivo");
        }
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
