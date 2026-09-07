package com.epr.backend.service;

import com.epr.backend.dto.request.PagoRequest;
import com.epr.backend.dto.request.RechazarComprobanteRequest;
import com.epr.backend.dto.response.ComprobantePagoArchivoResponse;
import com.epr.backend.dto.response.ComprobantePagoResponse;
import com.epr.backend.entity.EstadoComprobante;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface ComprobantePagoService {

    ComprobantePagoResponse subir(String emailAlumno, MultipartFile archivo, Long planCuotaId,
                                   BigDecimal monto, LocalDate fecha);

    List<ComprobantePagoResponse> listarMios(String emailAlumno);

    ComprobantePagoArchivoResponse descargarMio(String emailAlumno, Long comprobanteId);

    List<ComprobantePagoResponse> listarPorAlumno(Long alumnoId);

    ComprobantePagoArchivoResponse descargarPorAlumno(Long alumnoId, Long comprobanteId);

    List<ComprobantePagoResponse> listarPorEstado(EstadoComprobante estado);

    ComprobantePagoResponse confirmar(Long comprobanteId, PagoRequest request);

    ComprobantePagoResponse rechazar(Long comprobanteId, RechazarComprobanteRequest request);
}
